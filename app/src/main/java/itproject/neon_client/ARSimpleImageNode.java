package itproject.neon_client;

import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import eu.kudan.kudan.ARImageNode;

import static android.content.ContentValues.TAG;

public class ARSimpleImageNode extends ARImageNode
{
    @Override
    public void preRender()
    {
        super.preRender();
        // update after getting the phone's position in space
        //float rotationMatrix[] = new float[3];
        //float inclination[] = new float[3];
        //SensorManager.getRotationMatrix(rotationMatrix, inclination, new float[3],new float[3]);
        //Log.e(TAG, "setup: I found the rotation matrix to be" + rotationMatrix[0] + " " + rotationMatrix[1] + " " + rotationMatrix[2]);
    }

    public ARSimpleImageNode(String assetName)
    {
        super(assetName);
    }

    // add a method to set the matrix so that the render can be easily done
}
