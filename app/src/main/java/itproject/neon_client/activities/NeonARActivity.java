package itproject.neon_client.activities;


import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Surface;
import android.widget.Toast;

import org.json.JSONException;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import eu.kudan.kudan.ARActivity;
import eu.kudan.kudan.ARArbiTrack;
import eu.kudan.kudan.ARCameraStream;
import eu.kudan.kudan.ARGyroPlaceManager;
import itproject.neon_client.ar.ARSetup;
import itproject.neon_client.ar.ARSimpleImageNode;
import itproject.neon_client.helpers.LoggedInUser;
import itproject.neon_client.helpers.MapHelper;

import static eu.kudan.kudan.ARArbiTrack.deinitialise;

public class NeonARActivity extends eu.kudan.kudan.ARActivity implements SensorEventListener, LocationListener
{
    public static final String UI_POINTER_LOCATION = "arrow.png";
    private ARSetup setupObject;
    private boolean hasAccel = false;
    private boolean hasGravity = false;
    private boolean hasCompass = false;
    private static final int LOCATION_MIN_TIME = 0;
    private static final int LOCATION_MIN_DISTANCE = 0;
    private static final long MAP_UPDATE_DELAY = 10;
    // private static final double TEST_LOCATION_LATITUDE = -37.798649;
    // private static final double TEST_LOCATION_LONGITUDE= 144.960338;
    // private static final int LOCATION_MIN_TIME = 30 * 1000;
    // private static final int LOCATION_MIN_DISTANCE = 10;
    private static final int INITIAL_SENSOR_ACTIVITY_NUM = 500;
    private static final int RENDER_LIMIT = 1000;
    private static final float FILTER_THRESHOLD = 0.25f;
    private static final String TAG = "NeonARActivity";
    public static final String EXTRA_AR_MESSAGE = "itproject.neon_client.AR_MESSAGE";
    private final ScheduledExecutorService locationUpdateExecutor = Executors.newSingleThreadScheduledExecutor();
    // sensor manager
    private SensorManager sensorManager;
    // sensor gravity
    private Sensor sensorGravity;
    private Sensor sensorMagnetic;
    private LocationManager locationManager;
    private Location currentLocation;
    private Location destLocation;
    private GeomagneticField geomagneticField;
    private double bearing = 0;
    private ARSimpleImageNode targetNode;
    private boolean initialArrowPosSet;
    private float initialArrowAngleRadians;
    private int numSensorChanged;
    private ARGyroPlaceManager gyroPlaceManager;
    private ARArbiTrack arbiTrack;
    private boolean locationPostedToDatabase;
    private String friendUsername;

    private float[] gravity;
    // magnetic data
    private float[] geomagnetic;
    // Rotation data
    private float[] rotation;
    private float[] orientation;
    // smoothed values
    private float[] smoothed;
    private int renders;


    @Override
    public void onCreate(Bundle savedInstance)
    {
        super.onCreate(savedInstance);
        // Initialise gyro placement.
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

        if (Build.MODEL.equals("LGE Nexus 5X"))
        {
            // rotate camera 180°
            ARCameraStream cameraStream = ARCameraStream.getInstance();
            cameraStream.rotateCameraPreview(180);
        }
    }

    @Override
    public void setup()
    {
        super.setup();

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
            // Log.e(TAG, "Rotating by " + Math.toDegrees(initialArrowAngleRadians));
            targetNode.rotateByDegrees(-(float)Math.toDegrees(initialArrowAngleRadians), 0.0f, 0.0f, 1.0f);
        }

        targetNode.scaleByUniform(0.3f);

        // Set the ArbiTracker's target node.
        arbiTrack.setTargetNode(targetNode);
    }

    @Override
    public void onStart()
    {
        super.onStart();

        Location gpsLocation;

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

        // request location data
        try
        {
            // 1 is a integer which will return the result in onRequestPermissionsResult
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);

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
            Toast.makeText(getApplicationContext(), "You do not have permission to access the location"
                    , Toast.LENGTH_SHORT).show();
        }

        try {
            destLocation = new Location(currentLocation);
            destLocation.setLatitude(MapHelper.get_latitude(friendUsername, LoggedInUser.getUsername()));
            destLocation.setLongitude(MapHelper.get_longitude(friendUsername, LoggedInUser.getUsername()));
        }
        catch (JSONException e)
        {
            Log.e(TAG, "Got JSON exception: " + e.getMessage());
        }

    }

    @Override
    protected void onStop()
    {
        super.onStop();
        // remove listeners
        sensorManager.unregisterListener(this, sensorGravity);
        sensorManager.unregisterListener(this, sensorMagnetic);
        locationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location)
    {
        currentLocation = location;
        // can be used to update location info on screen
        geomagneticField = new GeomagneticField(
                (float) currentLocation.getLatitude(),
                (float) currentLocation.getLongitude(),
                (float) currentLocation.getAltitude(),
                System.currentTimeMillis());
        Log.i(TAG, "Posting location from AR");
        MapHelper.post_location(LoggedInUser.getUsername(), currentLocation.getLatitude(),
                currentLocation.getLongitude());
    }

    @Override
    public void onSensorChanged(SensorEvent event)
    {
        if(renders == RENDER_LIMIT) {
            // initialPropertySet();
            targetNode.resetToTrackNewLocation();
            // TODO: this maybe causing the arrow to spin
            targetNode = new ARSimpleImageNode(UI_POINTER_LOCATION);

            renders = 0;
        }

        else
        {
            renders++;
        }
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER
                            || event.sensor.getType() == Sensor.TYPE_GRAVITY)
        {
            // we need to use a low pass filter to make data smoothed
            smoothed = lowPassFilter(event.values, gravity);
            gravity[0] = smoothed[0];
            gravity[1] = smoothed[1];
            gravity[2] = smoothed[2];

        }
        else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
        {
            smoothed = lowPassFilter(event.values, geomagnetic);
            geomagnetic[0] = smoothed[0];
            geomagnetic[1] = smoothed[1];
            geomagnetic[2] = smoothed[2];
        }

        // get rotation matrix to get gravity and magnetic data
        SensorManager.getRotationMatrix(rotation, null, gravity, geomagnetic);

        // get bearing to target
        SensorManager.getOrientation(rotation, orientation);

        // east degrees of true North
        bearing = orientation[0];
        // convert from radians to degrees
        bearing = Math.toDegrees(bearing);

        // fix difference between true North and magnetic North
        if (geomagneticField != null)
            bearing += geomagneticField.getDeclination();

        // bearing must be in 0-360
        if (bearing < 0)
            bearing += 360;

        if (numSensorChanged <= INITIAL_SENSOR_ACTIVITY_NUM)
        {
            numSensorChanged++;
        }

        if (!initialArrowPosSet)
        {
            // Log.e(TAG, "Setting initial arrow direction as " + Math.toDegrees(orientation[0]));
            initialArrowAngleRadians = orientation[0];
            initialArrowPosSet = true;
        }
        else if (targetNode != null && numSensorChanged > INITIAL_SENSOR_ACTIVITY_NUM-1)
        {
            if (!locationPostedToDatabase)
            {
                MapHelper.post_location(LoggedInUser.getUsername(), currentLocation.getLatitude(),
                            currentLocation.getLongitude());
                locationPostedToDatabase = true;
            }
            // targetNode.updateOrientationMatrix(orientation, orientation[0]);
            Log.i(TAG, "The angle to dest is " + currentLocation.bearingTo(destLocation) +
                       " with src: " + currentLocation.toString() + " and dest: " + destLocation.toString());
            targetNode.updateOrientationMatrix(orientation, orientation[0] -
                                (float) Math.toRadians(currentLocation.bearingTo(destLocation)));
        }
    }

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
        renders = 0;
        PackageManager manager = getPackageManager();
        initialArrowPosSet = false;
        initialArrowAngleRadians = 0.0f;

        locationPostedToDatabase = false;
        this.setup();
        if (manager.hasSystemFeature(PackageManager.FEATURE_SENSOR_ACCELEROMETER))
            hasAccel = true;
        if (manager.hasSystemFeature(PackageManager.FEATURE_SENSOR_GYROSCOPE))
            hasGravity = true;
        if (manager.hasSystemFeature(PackageManager.FEATURE_SENSOR_COMPASS))
            hasCompass = true;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onProviderDisabled(String provider) {}

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    @Override
    public void onResume()
    {
        super.onResume();
        gravity = new float[3];
        geomagnetic = new float[3];
        rotation = new float[9];
        orientation = new float[3];
        smoothed = new float[3];
        setupObject = new ARSetup();
        sensorManager.registerListener(this, sensorGravity, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(this, sensorMagnetic, SensorManager.SENSOR_DELAY_UI);
        numSensorChanged = 0;
    }

    @Override
    public void onPause()
    {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

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

    public void updateFriendLocation()
    {
        try
        {
            destLocation = new Location(currentLocation);
            destLocation.setLongitude(MapHelper.get_longitude(friendUsername, LoggedInUser.getUsername()));
            destLocation.setLatitude(MapHelper.get_latitude(friendUsername, LoggedInUser.getUsername()));
            targetNode.resetToTrackNewLocation();
        }
        catch (JSONException e)
        {
            Log.e(TAG, "Got JSON exception: " + e.getMessage());
        }
    }

}
