package itproject.neon_client;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.FrameLayout;

import org.artoolkit.ar.base.ARActivity;
import org.artoolkit.ar.base.rendering.ARRenderer;


public class ARSimple extends ARActivity
{
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 133;

    public ARRenderer supplyRenderer()
    {
        return new ARRenderer();
    }

    public FrameLayout supplyFrameLayout()
    {
        return (FrameLayout)this.findViewById(R.id.mainLayout);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String message = intent.getStringExtra(ProfilePageActivity.EXTRA_MESSAGE);

        if (!checkCameraPermission())
        {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_CAMERA);
        }

    }

    private boolean checkCameraPermission()
    {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                                                                == PackageManager.PERMISSION_GRANTED;
    }
}
