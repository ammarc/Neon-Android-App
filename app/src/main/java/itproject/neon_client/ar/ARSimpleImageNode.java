package itproject.neon_client.ar;

import android.util.Log;

import com.jme3.math.Vector3f;

import java.util.Arrays;

import eu.kudan.kudan.ARImageNode;

public class ARSimpleImageNode extends ARImageNode
{
    public static final String TAG = "ARSimpleNode";
    private float orientationMatrix[];
    private float radiansToRotateBy;
    private float currentAngleRadians;
    private float yaw;
    private float newYaw;
    private int initialSettingFlag;

    @Override
    public void preRender()
    {
        super.preRender();
        // update after getting the phone's position in space

        radiansToRotateBy = orientationMatrix[0];
        if (initialSettingFlag == 1)
        {
            currentAngleRadians += radiansToRotateBy;
            initialSettingFlag = 0;
        }
        else
        {
            currentAngleRadians += radiansToRotateBy + (newYaw - yaw);
        }
        // Log.e(TAG, "Current angle is " + Math.toDegrees(currentAngleRadians));
        yaw = newYaw;

        float degreesToRotateBy = (float)Math.toDegrees(radiansToRotateBy);
        // Log.e(TAG, "Current angle is: " + currentAngleRadians);
        // Log.e(TAG, "Now rotating by degrees: " + degreesToRotateBy);

        // this reduces the jitters in the arrow
        if (Math.abs(degreesToRotateBy) > 1)
        {
            rotateByDegrees(-degreesToRotateBy, 0.0f, 0.0f, 1.0f);
        }
    }

    public ARSimpleImageNode(String assetName)
    {
        super(assetName);
        orientationMatrix = new float[3];
        Arrays.fill(orientationMatrix, 0.0f);
        radiansToRotateBy = 0.0f;
        currentAngleRadians = 0.0f;
        initialSettingFlag = -1;
    }

    // add a method to set the matrix so that the render can be easily done
    public void updateOrientationMatrix(float[] updatedRotationMatrix, float newYaw)
    {
        for (int i = 0; i < updatedRotationMatrix.length; i++)
        {
            if (i == 0)
            {
                orientationMatrix[0] = newYaw -  currentAngleRadians;
            }
           else
            {
                orientationMatrix[i] = updatedRotationMatrix[i];
            }
        }
        this.newYaw = newYaw;

        if (initialSettingFlag == -1)
            initialSettingFlag = 1;
    }

    public void resetToTrackNewLocation()
    {
        initialSettingFlag = -1;
    }
}
