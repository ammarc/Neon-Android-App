package itproject.neon_client.activities;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;

import itproject.neon_client.Services.GPS_Service;
import itproject.neon_client.helpers.LoggedInUser;
import itproject.neon_client.helpers.MapHelper;

/**
 * Created by lachlanthomas on 16/10/17.
 */

public class TO_ADD {

    private BroadcastReceiver receiver;

    //insert following into onCreate()
    if (!runtime_permissions()) {
        start_location_updates();
    }

    //initialised the receiver and posts new locations to database once received
    public void onResume() {
        super.onResume();
        if (receiver == null) {
            receiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    MapHelper.post_location(LoggedInUser.getUsername(), (double) intent.getExtras().get("latitude"), (double) intent.getExtras().get("longitude"));
                }
            };
        }
        registerReceiver(receiver, new IntentFilter("location_update"));
    }

    // checks for location permissions, if not granted then requests them
    private boolean runtime_permissions() {
        if(Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);

            return true;
        }
        return false;
    }

    //if location permissions are granted start constant location updates, if not granted then request permissions
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                start_location_updates();
            } else {
                runtime_permissions();
            }
        }
    }

    //start lsitening for new locations
    private void start_location_updates() {
        Intent i = new Intent(getApplicationContext(), GPS_Service.class);
        startService(i);
    }

    //stop listening for new locations
    private void stop_location_updates() {
        Intent i = new Intent(getApplicationContext(), GPS_Service.class);
        stopService(i);
    }

    //close receiver
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
    }
}
