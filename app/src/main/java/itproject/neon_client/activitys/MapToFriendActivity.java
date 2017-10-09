package itproject.neon_client.activitys;

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
import org.json.JSONObject;
import org.json.JSONArray;

import java.io.DataOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import itproject.neon_client.helper.LoggedInUser;
import itproject.neon_client.R;

import static android.content.ContentValues.TAG;

public class MapToFriendActivity extends AppCompatActivity implements OnMapReadyCallback {

    protected Location mLastLocation;
    protected FusedLocationProviderApi mFusedLocactionProviderApi;
    protected LocationManager mLocationManager;
    protected Context context;
    GoogleApiClient mGoogleApiClient;
    LocationRequest locationRequest;
    protected static String directory = "http://13.65.209.193:3000";
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_to_friend);
        Toolbar toolbar = (Toolbar) findViewById(R.id.map_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

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
    
    static double get_latitude(String to_user, String from_user) throws JSONException {
        String path = directory + "/gps/friends?user=" + from_user;
        JSONArray friends_locations = get(path);
        
        for (int i = 0; i < friends_locations.length(); i ++) {
            if (friends_locations.getJSONObject(i).getString("username").equals(to_user)) {
                return friends_locations.getJSONObject(i).getDouble("latitude");
            }
        }
        return 0;
    }
    
    static double get_longitude(String to_user, String from_user) throws JSONException {
        String path = directory+ "/gps/friends?user=" + from_user;
        JSONArray friends_locations = get(path);
        
        for (int i = 0; i < friends_locations.length(); i ++) {
            if (friends_locations.getJSONObject(i).getString("username").equals(to_user)) {
                return friends_locations.getJSONObject(i).getDouble("longitude");
            }
        }
        return 0;
    }
    
    public static JSONArray post_location(String username, double latitude, double longitude) {
        try {
            JSONObject post_message = new JSONObject();
            post_message.put("username", username);
            post_message.put("latitude", latitude);
            post_message.put("longitude", longitude);
            String path = directory+ "/gps";
            
            URL url = new URL(path);
            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Accept", "application/json");
            httpURLConnection.connect();
            
            DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
            wr.writeBytes(post_message.toString());
            wr.flush();
            wr.close();
            
            if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                httpURLConnection.disconnect();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public static JSONArray get(String path) {
        try {
            URL url = new URL(path);
            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.connect();
            System.out.println("connected");
            System.out.println(httpURLConnection.getResponseCode());
            if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                String server_response = readStream(httpURLConnection.getInputStream());
                System.out.println(server_response);
                JSONArray response_json_array;
                response_json_array = new JSONArray(server_response);
                return response_json_array;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    private static String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuffer response = new StringBuffer();
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return response.toString();
    }

}
