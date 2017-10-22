package itproject.neon_client.activities;


import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import eu.kudan.kudan.ARArbiTrack;
import eu.kudan.kudan.ARGyroPlaceManager;
import itproject.neon_client.ar.ARSetup;
import itproject.neon_client.ar.ARSimpleImageNode;
import itproject.neon_client.helpers.LoggedInUser;
import itproject.neon_client.helpers.MapHelper;

/**
 * This is the activity that gets launched when the AR is initiated. It takes sensor
 * readings, gets data from the backend and also posts it and it also passes sensor data
 * off to the image node to have the correct orientation on screen.
 */
public class NeonARActivity extends eu.kudan.kudan.ARActivity implements SensorEventListener,
        LocationListener
{
    public static final String UI_POINTER_LOCATION = "arrow.png";
    private boolean hasAccel = false;
    private boolean hasGravity = false;
    private boolean hasCompass = false;
    private static final int LOCATION_MIN_TIME = 10000;
    private static final int LOCATION_MIN_DISTANCE = 1;
    private static final long MAP_UPDATE_DELAY = 20;
    private static final double TEST_LOCATION_LATITUDE = -37.798649;
    private static final double TEST_LOCATION_LONGITUDE= 144.960338;
    private static final int INITIAL_SENSOR_ACTIVITY_NUM = 500;
    private static final int RENDER_LIMIT = 1000;
    private static final float FILTER_THRESHOLD = 0.25f;
    private static final String TAG = "NeonARActivity";
    public static final String EXTRA_AR_MESSAGE = "itproject.neon_client.AR_MESSAGE";
    private final ScheduledExecutorService locationUpdateExecutor = Executors.
                                                            newSingleThreadScheduledExecutor();
    private SensorManager sensorManager;
    private Sensor sensorGravity;
    private Sensor sensorMagnetic;
    private LocationManager locationManager;
    private Location currentLocation;
    private Location destLocation;
    private GeomagneticField geomagneticField;
    private ARSimpleImageNode targetNode;
    private boolean initialArrowPosSet;
    private float initialArrowAngleRadians;
    private int numSensorChanged;
    private ARGyroPlaceManager gyroPlaceManager;
    private ARArbiTrack arbiTrack;
    private String friendUsername;

    private float[] gravity;
    // magnetic data
    private float[] geomagnetic;
    // Rotation data
    private float[] rotation;
    private float[] orientation;
    // smoothed values
    private float[] smoothed;
    private int numRendered;

    /**
     * When the activity is created, the API key is first read and then another thread is spun
     * to get an updated friend's location periodically. The initial values of the variables
     * are also set here.
     * @param savedInstance the saved instance from Android
     */
    @Override
    public void onCreate(Bundle savedInstance)
    {
        super.onCreate(savedInstance);
        // read API key from where it's stored on file
        try
        {
            InputStream inputStream = this.getApplicationContext().getAssets().open("APIKey.txt");
            Scanner s = new Scanner(inputStream).useDelimiter("\\A");
            ARSetup.setupAR(s.hasNext() ? s.next() : "");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        friendUsername = getIntent().getStringExtra(EXTRA_AR_MESSAGE);
        initialPropertySet();
        locationUpdateExecutor.scheduleWithFixedDelay(new Runnable()
        {
            @Override
            public void run()
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateFriendLocation();
                    }
                });
            }
        }, 0, MAP_UPDATE_DELAY, TimeUnit.SECONDS);

        try {
            if (currentLocation != null)
            {
                destLocation = new Location(currentLocation);
                destLocation.setLatitude(MapHelper.get_latitude(friendUsername,
                                                                LoggedInUser.getUsername()));
                destLocation.setLongitude(MapHelper.get_longitude(friendUsername,
                                                                LoggedInUser.getUsername()));
            }
        }
        catch (JSONException e)
        {
            Log.e(TAG, "Got JSON exception: " + e.getMessage());
        }
    }

    /**
     * This is the method that is called by Kudan when first setting up the AR. This is used
     * to set up AR objects like ArbiTrack and ARGyroPlaceManager. This is also where the arrow
     * is tilted so as to appear laying flat on the user's screen
     */
    @Override
    public void setup()
    {
        super.setup();

        // Initialise gyro placement.
        gyroPlaceManager = ARGyroPlaceManager.getInstance();
        gyroPlaceManager.initialise();
        // Initialise ArbiTrack.
        arbiTrack = ARArbiTrack.getInstance();
        arbiTrack.initialise();

        // Create a node to be used as the target.
        targetNode = new ARSimpleImageNode(UI_POINTER_LOCATION);

        // Add it to the Gyro Placement Manager's world so that it moves with the device's Gyroscope.
        gyroPlaceManager.getWorld().removeAllChildren();
        gyroPlaceManager.getWorld().addChild(targetNode);

        // Rotate and scale the node to ensure it is displayed correctly.
        targetNode.rotateByDegrees(90.0f, 1.0f, 0.0f, 0.0f);
        targetNode.rotateByDegrees(90.0f, 0.0f, 0.0f, 1.0f);

        // Rotate the arrow by an initial reading
        if (initialArrowPosSet)
        {
            Log.i(TAG, "Rotating by " + Math.toDegrees(initialArrowAngleRadians));
            targetNode.rotateByDegrees(-(float)Math.toDegrees(initialArrowAngleRadians),
                                                                            0.0f, 0.0f, 1.0f);
        }

        targetNode.scaleByUniform(0.3f);

        // Set the ArbiTracker's target node.
        arbiTrack.setTargetNode(targetNode);
    }

    /**
     * After creation of this activity, we need to get the sensor data to find the orientation
     * of the phone in 3d space. Here all of the sensors and their corresponding listeners are
     * set up.
     */
    @Override
    public void onStart()
    {
        super.onStart();

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (hasGravity)
            sensorGravity = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        else
            sensorGravity = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorMagnetic = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        // listen to these sensors
        sensorManager.registerListener(this, sensorGravity, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(this, sensorMagnetic, SensorManager.SENSOR_DELAY_UI);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // get the current user's location
        requestLocationData();

        try {
            if (currentLocation != null) {
                destLocation = new Location(currentLocation);
                destLocation.setLatitude(MapHelper.get_latitude(friendUsername,
                                                                    LoggedInUser.getUsername()));
                destLocation.setLongitude(MapHelper.get_longitude(friendUsername,
                                                                    LoggedInUser.getUsername()));
            }
        }
        catch (JSONException e)
        {
            Log.e(TAG, "Got JSON exception: " + e.getMessage());
        }

    }

    /**
     * When the activity is stopped, we need to do some house-keeping and remove
     * the sensor listeners for efficiency reasons like battery conservation
     */
    @Override
    protected void onStop()
    {
        super.onStop();
        // remove listeners
        sensorManager.unregisterListener(this, sensorGravity);
        sensorManager.unregisterListener(this, sensorMagnetic);
        locationManager.removeUpdates(this);
    }

    /**
     * This is essentially used to update our location with the newly
     * recorded location provided by Android
     * @param location the newly recorded location
     */
    @Override
    public void onLocationChanged(Location location)
    {
        currentLocation = location;
        // could be used to show location updates to user
        geomagneticField = new GeomagneticField(
                (float) currentLocation.getLatitude(),
                (float) currentLocation.getLongitude(),
                (float) currentLocation.getAltitude(),
                System.currentTimeMillis());
    }

    /**
     * This method is used to get sensor event changes including the accelerometer
     * and the magnetic field sensor. Those raw values are smoothed and their values
     * are offset between the true North and the bearing between the user and their
     * friend.
     * @param event the sensor event which recorded a change
     */
    @Override
    public void onSensorChanged(SensorEvent event)
    {
        if(numRendered == RENDER_LIMIT)
        {
            targetNode.resetToTrackNewLocation();
            numRendered = 0;
        }
        else
            numRendered++;

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER
                            || event.sensor.getType() == Sensor.TYPE_GRAVITY)
        {
            // we need to use a low pass filter to make data smoothed
            smoothed = lowPassFilter(event.values, gravity);
            for (int i = 0; i < smoothed.length; i++)
                gravity[i] = smoothed[i];

        }
        else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
        {
            // we need to use a low pass filter to make data smoothed
            smoothed = lowPassFilter(event.values, geomagnetic);
            for (int i = 0; i < smoothed.length; i++)
                geomagnetic[i] = smoothed[i];
        }

        // get rotation matrix to get gravity and magnetic data
        SensorManager.getRotationMatrix(rotation, null, gravity, geomagnetic);

        // get bearing to target
        SensorManager.getOrientation(rotation, orientation);

        // the first ~500 values aren't used as it was found that it takes about that
        // many readings for the readings to be fairly stable and thus accurate
        if (numSensorChanged <= INITIAL_SENSOR_ACTIVITY_NUM)
            numSensorChanged++;

        if (!initialArrowPosSet)
        {
            initialArrowAngleRadians = orientation[0];
            initialArrowPosSet = true;
        }
        else if (targetNode != null && numSensorChanged > INITIAL_SENSOR_ACTIVITY_NUM-1)
        {
            targetNode.updateOrientationValue(orientation[0] -
                                (float) Math.toRadians(currentLocation.bearingTo(destLocation)));
        }
    }

    /**
     * This method sets the initial values of all the attributes of this class or initializes
     * them, where appropriate
     */
    public void initialPropertySet()
    {
        gravity = new float[9];
        // magnetic data
        geomagnetic = new float[9];
        // Rotation data
        rotation = new float[9];
        orientation = new float[3];
        // smoothed values
        smoothed = new float[3];
        numRendered = 0;
        PackageManager manager = getPackageManager();
        initialArrowPosSet = false;
        initialArrowAngleRadians = 0.0f;

        this.setup();
        if (manager.hasSystemFeature(PackageManager.FEATURE_SENSOR_ACCELEROMETER))
            hasAccel = true;
        if (manager.hasSystemFeature(PackageManager.FEATURE_SENSOR_GYROSCOPE))
            hasGravity = true;
        if (manager.hasSystemFeature(PackageManager.FEATURE_SENSOR_COMPASS))
            hasCompass = true;
    }

    /**
     * After the activity is resumed we need to make sure all of our sensor-data arrays
     * are re-initialized to prevent old readings to influence our perception of the phone's
     * orientation
     */
    @Override
    public void onResume()
    {
        super.onResume();
        gravity = new float[9];
        geomagnetic = new float[9];
        rotation = new float[9];
        orientation = new float[3];
        smoothed = new float[3];
        sensorManager.registerListener(this, sensorGravity, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(this, sensorMagnetic, SensorManager.SENSOR_DELAY_UI);
        numSensorChanged = 0;
    }

    /**
     * We need to un-register this class as a listener whenever this activity
     * is paused for a better in-app performance
     */
    @Override
    public void onPause()
    {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    /**
     * This low pass filter is used to smooth raw values from the sensor. It essentially
     * adjusts the output arrays' values with the difference between the current value
     * and the newly recorded value such that the output array reflects a change in line
     * with what was recorded but doesn't change them directly to the inputs' values for
     * more stable values.
     * @param input the raw-valued sensor data array
     * @param output the current values stored in that array
     * @return the adjusted values to reflect a change in the output caused by the input
     */
    public float[] lowPassFilter(float[] input, float[] output)
    {
        if (output == null)
            return input;

        for (int i = 0; i < input.length; i++)
        {
            output[i] = output[i] + FILTER_THRESHOLD * (input[i] - output[i]);
        }
        return output;
    }

    /**
     * This method is used to update our version of the friend's location from the database.
     * At the same time, it is also used to post our location to the server.
     */
    public void updateFriendLocation()
    {
        try
        {
            destLocation = new Location(currentLocation);
            destLocation.setLongitude(MapHelper.get_longitude(friendUsername,
                                                                    LoggedInUser.getUsername()));
            destLocation.setLatitude(MapHelper.get_latitude(friendUsername,
                                                                    LoggedInUser.getUsername()));
            targetNode.resetToTrackNewLocation();
            if (currentLocation != null)
                MapHelper.post_location(LoggedInUser.getUsername(),
                        currentLocation.getLatitude(), currentLocation.getLongitude());
        }
        catch (JSONException e)
        {
            Log.e(TAG, "Got JSON exception: " + e.getMessage());
        }
    }

    /**
     * This method is used to request the current location of the phone. It first tries to get
     * that data from the GPS and then if that fails, it tries to get it from the network
     * provider. It also creates a toast to inform the user, if there is problem with getting
     * the correct permissions.
     */
    public void requestLocationData()
    {
        Location gpsLocation;

        try
        {
            // 1 is a integer which will return the result in onRequestPermissionsResult
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.
                    ACCESS_FINE_LOCATION}, 1);

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    LOCATION_MIN_TIME, LOCATION_MIN_DISTANCE, this);
            // get last known position
            gpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (gpsLocation != null)
            {
                currentLocation = gpsLocation;
            }
            else
            {
                // try with network provider
                Location networkLocation = locationManager
                        .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                if (networkLocation != null)
                {
                    currentLocation = networkLocation;
                }
                else
                {
                    // throw an error
                    Log.e(TAG, "Couldn't find any location services");
                    throw new SecurityException();
                }

                // set current location
                onLocationChanged(currentLocation);
            }
        }
        catch (SecurityException e)
        {
            // let the user know about the lack of permission
            Toast.makeText(getApplicationContext(), "You do not have permission to access the " +
                    "location", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onProviderDisabled(String provider) {}

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

}
