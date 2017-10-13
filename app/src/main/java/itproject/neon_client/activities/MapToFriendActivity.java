package itproject.neon_client.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;

import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;

import itproject.neon_client.helpers.LoggedInUser;
import itproject.neon_client.R;

import static android.content.ContentValues.TAG;
import static itproject.neon_client.helpers.MapHelper.get_latitude;
import static itproject.neon_client.helpers.MapHelper.get_longitude;
import static itproject.neon_client.helpers.MapHelper.post_location;

public class MapToFriendActivity extends AppCompatActivity implements OnMapReadyCallback {

    protected Location mLastLocation;
    protected FusedLocationProviderApi mFusedLocactionProviderApi;
    protected LocationManager mLocationManager;
    protected Context context;
    GoogleApiClient mGoogleApiClient;
    LocationRequest locationRequest;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_to_friend);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Melbourne and move the camera
        LatLng melbUni = new LatLng(-37.7964, 144.9612);
        mMap.addMarker(new MarkerOptions().position(melbUni).title("Marker in Melb Uni"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(melbUni));
        //mMap.setMinZoomPreference(10);
        //mMap.setMaxZoomPreference(20);
    }

    public void onConnected(Bundle arg0) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mFusedLocactionProviderApi.requestLocationUpdates(mGoogleApiClient, locationRequest, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                mLastLocation = location;
                LatLng myLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                post_location(LoggedInUser.getUser().username, mLastLocation.getLatitude(), mLastLocation.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
                mMap.moveCamera(CameraUpdateFactory.zoomBy(15));

                Log.d(TAG, "location changed");
            }
        });
    }

    void friend_marker(GoogleMap map, String to_user) throws JSONException {
        double latitude = get_latitude(to_user, LoggedInUser.getUser().username);
        double longitude = get_longitude(to_user, LoggedInUser.getUser().username);

        map.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .title(to_user));
    }
}
