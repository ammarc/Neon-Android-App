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

import eu.kudan.kudan.ARArbiTrack;
import eu.kudan.kudan.ARGyroPlaceManager;
import itproject.neon_client.ar.ARSetup;
import itproject.neon_client.ar.ARSimpleImageNode;

import static android.content.ContentValues.TAG;

public class NeonARActivity extends eu.kudan.kudan.ARActivity implements SensorEventListener, LocationListener
{
    private ARSetup setupObject;
    private boolean hasAccel = false;
    private boolean hasGravity = false;
    private boolean hasCompass = false;
    // TODO: set the following two constants back to their original values
    private static final int LOCATION_MIN_TIME = 0;
    private static final int LOCATION_MIN_DISTANCE = 0;
    // TODO: these are test coordinates (South Lawn), we need to change them to the friend's location
    private static final double TEST_LOCATION_LATITUDE = -37.798649;
    private static final double TEST_LOCATION_LONGITUDE= 144.960338;
    // private static final int LOCATION_MIN_TIME = 30 * 1000;
    // private static final int LOCATION_MIN_DISTANCE = 10;
    private static final int INITIAL_SENSOR_ACTIVITY_NUM = 500;
    private static final int RENDER_LIMIT = 1000;
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

    private float[] gravity;
    // magnetic data
    private float[] geomagnetic;
    // Rotation data
    private float[] rotation;
    private float[] orientation;
    // smoothed values
    private float[] smoothed;
    private int renders;
    private int count = 0;


    @Override
    public void onCreate(Bundle savedInstance)
    {
        super.onCreate(savedInstance);
        // Initialise gyro placement.
        initialPropertySet();
    }

    @Override
    public void setup()
    {
        super.setup();

        gyroPlaceManager = ARGyroPlaceManager.getInstance();
        gyroPlaceManager.initialise();
        // Initialise ArbiTrack.
        ARArbiTrack arbiTrack = ARArbiTrack.getInstance();
        arbiTrack.initialise();


        // Create a node to be used as the target.
        targetNode = new ARSimpleImageNode("arrow.png");

        // Add it to the Gyro Placement Manager's world so that it moves with the device's Gyroscope.
        gyroPlaceManager.getWorld().removeAllChildren();
        gyroPlaceManager.getWorld().addChild(targetNode);

        // Rotate and scale the node to ensure it is displayed correctly.
        targetNode.rotateByDegrees(90.0f, 1.0f, 0.0f, 0.0f);
        targetNode.rotateByDegrees(90.0f, 0.0f, 0.0f, 1.0f);

        // Rotate the arrow by an initial reading
        if (initialArrowPosSet)
        {
            Log.e(TAG, "Rotating by " + Math.toDegrees(initialArrowAngleRadians));
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
        sensorManager.registerListener(this, sensorGravity, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, sensorMagnetic, SensorManager.SENSOR_DELAY_NORMAL);

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

        destLocation = new Location(currentLocation);
        destLocation.setLatitude(TEST_LOCATION_LATITUDE);
        destLocation.setLongitude(TEST_LOCATION_LONGITUDE);

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
        // Log.e(TAG, "I am in onLocationChanged");
        currentLocation = location;
        // Log.e(TAG, "Found the current location to be " + currentLocation);
        // can be used to update location info on screen
        geomagneticField = new GeomagneticField(
                (float) currentLocation.getLatitude(),
                (float) currentLocation.getLongitude(),
                (float) currentLocation.getAltitude(),
                System.currentTimeMillis());
    }

    @Override
    public void onSensorChanged(SensorEvent event)
    {
        if(renders == RENDER_LIMIT) {
            initialPropertySet();

            renders = 0;
        }

        else {
            renders++;
        }
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER
                            || event.sensor.getType() == Sensor.TYPE_GRAVITY)
        {
            // we need to use a low pass filter to make data smoothed
            // smoothed = LowPassFilter.filter(event.values, gravity);
            gravity[0] = event.values[0];
            gravity[1] = event.values[1];
            gravity[2] = event.values[2];

        }
        else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
        {
            // smoothed = LowPassFilter.filter(event.values, geomagnetic);
            geomagnetic[0] = event.values[0];
            geomagnetic[1] = event.values[1];
            geomagnetic[2] = event.values[2];
        }

        // get rotation matrix to get gravity and magnetic data
        SensorManager.getRotationMatrix(rotation, null, gravity, geomagnetic);

        // get bearing to target
        SensorManager.getOrientation(rotation, orientation);

        // east degrees of true North
        bearing = orientation[0];
        // convert from radians to degrees
        bearing = Math.toDegrees(bearing);

        // fix difference between true North and magnetical North
        if (geomagneticField != null)
            bearing += geomagneticField.getDeclination();

        // bearing must be in 0-360
        if (bearing < 0)
            bearing += 360;

        if(count%1000 == 0)
            Log.e(TAG, "Pitch is " + Math.toDegrees(orientation[1]));
        count++;

        if (numSensorChanged <= INITIAL_SENSOR_ACTIVITY_NUM)
        {
            numSensorChanged++;
            if (numSensorChanged != INITIAL_SENSOR_ACTIVITY_NUM)
            {
                Toast.makeText(getApplicationContext(), "Setting up...", Toast.LENGTH_SHORT);
            }
            else
            {
                Toast.makeText(getApplicationContext(), "Done!", Toast.LENGTH_LONG);
            }
        }

        if (!initialArrowPosSet)
        {
            // Log.e(TAG, "Setting initial arrow direction as " + Math.toDegrees(orientation[0]));
            initialArrowAngleRadians = orientation[0];
            initialArrowPosSet = true;
        }
        else if (targetNode != null && numSensorChanged > INITIAL_SENSOR_ACTIVITY_NUM-1)
        {
            // targetNode.updateOrientationMatrix(orientation, orientation[0]);
            Log.e(TAG, "The angle to dest is " + currentLocation.bearingTo(destLocation));
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
        sensorManager.registerListener(this, sensorGravity, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, sensorMagnetic, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        sensorManager.unregisterListener(this);
    }
}
