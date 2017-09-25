package itproject.neon_client;


import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

import eu.kudan.kudan.ARActivity;
import eu.kudan.kudan.ARArbiTrack;
import eu.kudan.kudan.ARGyroPlaceManager;
import eu.kudan.kudan.ARImageNode;
import eu.kudan.kudan.ARImageTrackable;
import eu.kudan.kudan.ARImageTracker;
import eu.kudan.kudan.ARRenderer;

import static android.content.ContentValues.TAG;

public class ARSimple extends ARActivity// implements GestureDetector.OnGestureListener
{
    private ARSetup setupObject;
    private GestureDetectorCompat gestureDetect;
    private ARRenderer arRenderer;

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setupObject = new ARSetup();
        setupObject.setupAR();
        arRenderer.initialise();
        // Create gesture recogniser to start and stop arbitrack
        // gestureDetect = new GestureDetectorCompat(this,this);
    }


    @Override
    public void setup()
    {
        super.setup();

        // Initialise ArbiTrack.
        ARArbiTrack arbiTrack = ARArbiTrack.getInstance();
        arbiTrack.initialise();

        // Initialise gyro placement.
        ARGyroPlaceManager gyroPlaceManager = ARGyroPlaceManager.getInstance();
        gyroPlaceManager.initialise();

        // Create a node to be used as the target.
        ARImageNode targetNode = new ARImageNode("arrow.png");

        // Add it to the Gyro Placement Manager's world so that it moves with the device's Gyroscope.
        gyroPlaceManager.getWorld().addChild(targetNode);

        // Rotate and scale the node to ensure it is displayed correctly.
        targetNode.rotateByDegrees(90.0f, 1.0f, 0.0f, 0.0f);
        targetNode.rotateByDegrees(180.0f, 0.0f, 1.0f, 0.0f);

        targetNode.scaleByUniform(0.3f);

        // Set the ArbiTracker's target node.
        arbiTrack.setTargetNode(targetNode);

        // To be placed in the setupContent method
        // Create a node to be tracked.
        ARImageNode trackingNode = new ARImageNode("Cow Tracking.png");

        // Rotate the node to ensure it is displayed correctly.
        trackingNode.rotateByDegrees(90.0f, 1.0f, 0.0f, 0.0f);
        trackingNode.rotateByDegrees(180.0f, 0.0f, 1.0f, 0.0f);

        // Add the node as a child of the ArbiTracker's world.
        arbiTrack.getWorld().addChild(trackingNode);
    }

    /*@Override
    public boolean onTouchEvent(MotionEvent event)
    {
        gestureDetect.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e)
    {
        ARArbiTrack arbiTrack = ARArbiTrack.getInstance();

        // If arbitrack is tracking, stop tracking so that its world is no longer rendered, and make the target node visible.
        if (arbiTrack.getIsTracking())
        {
            arbiTrack.stop();
            arbiTrack.getTargetNode().setVisible(true);
        }

        // If it's not tracking, start tracking and hide the target node.
        else
        {
            arbiTrack.start();
            arbiTrack.getTargetNode().setVisible(false);
        }

        return false;
    }

    // We also need to implement the other overrides of the GestureDetector, though we don't need them for this sample.
    @Override
    public boolean onDown(MotionEvent e)
    {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e)
    {
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY)
    {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e)
    {
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
    {
        return false;
    }*/
}
