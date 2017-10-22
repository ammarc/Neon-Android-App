package itproject.neon_client.ar;

import android.util.Log;

import eu.kudan.kudan.ARImageNode;

/**
 * This class extends the traditional image node class from Kudan
 * and is used to inject our logic so that we can draw our image (an arrow)
 * and rotate such that it is pointing in the correct direction i.e.
 * where our friend is
 */
public class ARSimpleImageNode extends ARImageNode
{
    public static final String TAG = "ARSimpleNode";
    private float orientationMatrixYaw;
    private float radiansToRotateBy;
    private float currentAngleRadians;
    private float yaw;
    private float newYaw;
    private int initialSettingFlag;

    /**
     * This method is called before each time the render method is called,
     * and thus is a great place for us to adjust the orientation of the arrow
     */
    @Override
    public void preRender()
    {
        super.preRender();

        // update after getting the phone's position in space
        radiansToRotateBy = orientationMatrixYaw;
        if (initialSettingFlag == 1)
        {
            currentAngleRadians += radiansToRotateBy;
            initialSettingFlag = 0;
        }
        else
        {
            // after setting the arrow on the screen we need only
            // track the rotation of the phone and offset the arrow
            // by the values read from the sensor
            currentAngleRadians += radiansToRotateBy + (newYaw - yaw);
        }
        yaw = newYaw;

        float degreesToRotateBy = (float)Math.toDegrees(radiansToRotateBy);

        // this reduces the jitters in the arrow
        if (Math.abs(degreesToRotateBy) > 1)
        {
            rotateByDegrees(-degreesToRotateBy, 0.0f, 0.0f, 1.0f);
        }
    }

    /**
     * Constructor for this image node class
     * @param assetName the name of the image asset to project on the camera
     */
    public ARSimpleImageNode(String assetName)
    {
        super(assetName);
        radiansToRotateBy = 0.0f;
        currentAngleRadians = 0.0f;
        initialSettingFlag = -1;
    }

    /**
     * This method updates the yaw, the 0-index value of the orientation matrix
     * so that when the image of the arrow is rendered, the arrow is offset by the
     * correct value to reflect the current orientation of the phone
     * @param newYaw the update 0-index value gotten from the sensors
     */
    public void updateOrientationValue(float newYaw)
    {
        orientationMatrixYaw = newYaw - currentAngleRadians;

        this.newYaw = newYaw;

        if (initialSettingFlag == -1)
            initialSettingFlag = 1;
    }

    /**
     * This method resets the flag used to tell the renderer to draw the arrow
     * to a location without considering the offset. This is used initially, or when
     * the arrow needs to be re-adjusted e.g. after getting a new location from the server
     */
    public void resetToTrackNewLocation()
    {
        initialSettingFlag = -1;
    }

}
