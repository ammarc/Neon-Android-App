package itproject.neon_client;


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
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.Toast;

import eu.kudan.kudan.ARActivity;
import eu.kudan.kudan.ARArbiTrack;
import eu.kudan.kudan.ARGyroPlaceManager;
import eu.kudan.kudan.ARImageNode;
import eu.kudan.kudan.ARImageTrackable;
import eu.kudan.kudan.ARImageTracker;
import eu.kudan.kudan.ARRenderer;
import eu.kudan.kudan.ARRendererListener;

import static android.content.ContentValues.TAG;

public class ARSimple extends ARActivity implements SensorEventListener, LocationListener {
    private ARSetup setupObject;
    private GestureDetectorCompat gestureDetect;
    private ARRenderer arRenderer;
    private SensorEvent sensorEvent;
    private boolean hasAccel = false;
    private boolean hasGyro = false;
    private boolean hasCompass = false;
    private static final int LOCATION_MIN_TIME = 30 * 1000;
    private static final int LOCATION_MIN_DISTANCE = 10;
    // Gravity for accelerometer data
    private float[] gravity = new float[3];
    // magnetic data
    private float[] geomagnetic = new float[3];
    // Rotation data
    private float[] rotation = new float[9];
    // orientation (azimuth, pitch, roll)
    private float[] orientation = new float[3];
    // smoothed values
    private float[] smoothed = new float[3];
    // sensor manager
    private SensorManager sensorManager;
    // sensor gravity
    private Sensor sensorGravity;
    private Sensor sensorMagnetic;
    private LocationManager locationManager;
    private Location currentLocation;
    private GeomagneticField geomagneticField;
    private double bearing = 0;

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setupObject = new ARSetup();
        setupObject.setupAR();
        PackageManager manager = getPackageManager();

        // arRenderer.initialise();
        // Create gesture recogniser to start and stop arbitrack
        // gestureDetect = new GestureDetectorCompat(this,this);
        if (manager.hasSystemFeature(PackageManager.FEATURE_SENSOR_ACCELEROMETER))
            hasAccel = true;
        if (manager.hasSystemFeature(PackageManager.FEATURE_SENSOR_GYROSCOPE))
            hasGyro = true;
        if (manager.hasSystemFeature(PackageManager.FEATURE_SENSOR_COMPASS))
            hasCompass = true;
    }

    @Override
    public void setup() {
        super.setup();


        // Initialise ArbiTrack.
        ARArbiTrack arbiTrack = ARArbiTrack.getInstance();
        arbiTrack.initialise();

        // Initialise gyro placement.
        ARGyroPlaceManager gyroPlaceManager = ARGyroPlaceManager.getInstance();
        gyroPlaceManager.initialise();

        // Create a node to be used as the target.
        ARSimpleImageNode targetNode = new ARSimpleImageNode("arrow.png");

        // Add it to the Gyro Placement Manager's world so that it moves with the device's Gyroscope.
        gyroPlaceManager.getWorld().addChild(targetNode);

        // Rotate and scale the node to ensure it is displayed correctly.
        targetNode.rotateByDegrees(90.0f, 1.0f, 0.0f, 0.0f);
        targetNode.rotateByDegrees(90.0f, 0.0f, 0.0f, 1.0f);

        targetNode.scaleByUniform(0.3f);

        // Set the ArbiTracker's target node.
        arbiTrack.setTargetNode(targetNode);

        // To be placed in the setupContent method
        // Create a node to be tracked.
        /*ARImageNode trackingNode = new ARImageNode("Cow Tracking.png");

        // Rotate the node to ensure it is displayed correctly.
        trackingNode.rotateByDegrees(90.0f, 1.0f, 0.0f, 0.0f);
        trackingNode.rotateByDegrees(180.0f, 0.0f, 1.0f, 0.0f);

        // Add the node as a child of the ArbiTracker's world.
        arbiTrack.getWorld().addChild(trackingNode);*/
    }

    @Override
    public void onStart()
    {
        super.onStart();

        Location gpsLocation;

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorGravity = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorMagnetic = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        // listen to these sensors
        sensorManager.registerListener(this, sensorGravity, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, sensorMagnetic, SensorManager.SENSOR_DELAY_NORMAL);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // request location data
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    LOCATION_MIN_TIME, LOCATION_MIN_DISTANCE, this);
            // get last known position
            gpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (gpsLocation != null) {
                currentLocation = gpsLocation;
            } else {
                // try with network provider
                Location networkLocation = locationManager
                        .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                if (networkLocation != null) {
                    currentLocation = networkLocation;
                } else {
                    // throw an error
                    throw new SecurityException();
                }

                // set current location
                onLocationChanged(currentLocation);
            }
        } catch (SecurityException e) {
            // let the user know about the lack of permission
            Toast.makeText(getApplicationContext(), "You do not have permission to access the location"
                    , Toast.LENGTH_LONG).show();
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
    }

    @Override
    public void onSensorChanged(SensorEvent event)
    {

        // get accelerometer data
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
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

        // log the bearing
        Log.e(TAG, "matrix is " + Math.toDegrees(orientation[0]) + " " + Math.toDegrees(orientation[1]) + " " + Math.toDegrees(orientation[2]));
        Log.e(TAG, "the bearing is " + bearing);
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
