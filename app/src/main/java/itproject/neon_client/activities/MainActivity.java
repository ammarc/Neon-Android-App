package itproject.neon_client.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.widget.ProfilePictureView;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import itproject.neon_client.helpers.FriendHelper;
import itproject.neon_client.helpers.LoggedInUser;
import itproject.neon_client.R;
import itproject.neon_client.chat.ChatActivity;
import itproject.neon_client.helpers.MapAutoCompleteCustomArrayAdapter;
import itproject.neon_client.helpers.MapHelper;
import itproject.neon_client.helpers.MapInfoTouchListener;
import itproject.neon_client.helpers.MapLayout;
import itproject.neon_client.helpers.MapSearchAutoCompleteTextChangedListener;
import itproject.neon_client.helpers.MapSearchAutoCompleteView;

import static itproject.neon_client.R.drawable.ic_account_circle_black_24dp;



public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, LocationListener {

    // where 20 is offset between info bottom edge and content's bottom edge
    // and 39 is the marker height
    public static final int MARKER_HEIGHT = 39;
    public static final int BALLOON_BOTTOM_EDGE_OFFSET = 20;
    private static final String TAG = "MainActivity";
    private static final int LOCATION_MIN_TIME = 10000;
    private static final int LOCATION_MIN_DISTANCE = 1;
    private static final long MAP_UPDATE_DELAY = 20;
    public static final String EXTRA_MESSAGE = "itproject.neon_client.MESSAGE";
    public static final int MAP_ZOOM_VIEW = 15;

    private ViewGroup infoWindow;
    private TextView infoTitle;
    private TextView infoSnippet;
    private Button chatButton;
    private Button cameraButton;
    private Button mapButton;
    private MapInfoTouchListener chatButtonListener;
    private MapInfoTouchListener cameraButtonListener;
    private MapInfoTouchListener mapButtonListener;
    private MapLayout mapLayout;
    private Location userLocation;
    private LatLngBounds.Builder builder;
    private CameraUpdate cameraUpdate;
    private boolean mLocationPermissionGranted;
    private LocationManager mLocationManager;
    private boolean isMapReady;
    private final ScheduledExecutorService mapUpdateExecutor = Executors.newSingleThreadScheduledExecutor();

    private ArrayList<Marker> listOfAllMarkers;

    private MapSearchAutoCompleteView mapSearchAutoCompleteView;
    private ArrayAdapter<String> autoCompleteArrayAdapter;

    private GoogleMap mMap;
    private Menu sideMenu;
    private final int MENU_DYNAMIC = 2131755500;
    private int friend_insert_counter = 0;

    public static List<String> friendsList;
    public static List<String> friend_requests;

    public MapSearchAutoCompleteView getMapSearchAutoCompleteView()
    {
        return mapSearchAutoCompleteView;
    }

    public String[] getListOfAllMarkers()
    {
        String[] toReturn = new String[listOfAllMarkers.size()];
        for(int i = 0; i < listOfAllMarkers.size(); i++)
            toReturn[i] = listOfAllMarkers.get(i).getTitle();
        return toReturn;
    }

    public ArrayAdapter<String> getAutoCompleteArrayAdapter()
    {
        return autoCompleteArrayAdapter;
    }

    public void setAutoCompleteArrayAdapter(ArrayAdapter<String> autoCompleteArrayAdapter)
    {
        this.autoCompleteArrayAdapter = autoCompleteArrayAdapter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        listOfAllMarkers = new ArrayList<>();
        mLocationPermissionGranted = false;
        isMapReady = false;

        getLocationPermission();

        try
        {
            friendsList = FriendHelper.getFriendList(LoggedInUser.getUsername());
            friend_requests = FriendHelper.getPendingFriends(LoggedInUser.getUsername());
        }
        catch (JSONException e)
        {
            Log.e(TAG, e.getMessage());
        }

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(LoggedInUser.getUsername());

        mapSearchAutoCompleteView = (MapSearchAutoCompleteView) findViewById(R.id.search_box);

        mapSearchAutoCompleteView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                RelativeLayout rl = (RelativeLayout) view;
                TextView tv = (TextView) rl.getChildAt(0);
                mapSearchAutoCompleteView.setText(tv.getText().toString());
            }
        });

        mapSearchAutoCompleteView.addTextChangedListener(new MapSearchAutoCompleteTextChangedListener(this));

        // this is initially empty
        String[] userNameList = {};
        if (friendsList != null)
        {
            int index = 0;
            for (Object user : friendsList.toArray()) {
                index++;
            }
            userNameList = new String[index+1];
            int newIndex = 0;
            for (Object user : friendsList.toArray()) {
                userNameList[newIndex++] = user.toString();
                if (newIndex == index) {
                    break;
                }
            }
        }

        // set the custom ArrayAdapter
        autoCompleteArrayAdapter = new MapAutoCompleteCustomArrayAdapter(this, R.layout.auto_complete_view_row, userNameList);
        mapSearchAutoCompleteView.setAdapter(autoCompleteArrayAdapter);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View navigationBar = navigationView.getHeaderView(0);
        sideMenu = navigationView.getMenu();

        /* dp */
        ImageView userDp = (ImageView) navigationBar.findViewById(R.id.user_dp);



        /* user info */
        TextView user_name = (TextView) navigationBar.findViewById(R.id.user_name);
        try {
            user_name.setText(FriendHelper.getUserFullName(LoggedInUser.getUsername()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        TextView user_username = (TextView) navigationBar.findViewById(R.id.user_username);
        user_username.setText(LoggedInUser.getUsername());

        /* map */
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map_main_view);
        mapFragment.getMapAsync(this);


        this.infoWindow = (ViewGroup)getLayoutInflater().inflate(R.layout.info_balloon, null);
        this.infoTitle = (TextView)infoWindow.findViewById(R.id.friend_name);

        this.chatButton = (Button)infoWindow.findViewById(R.id.button_chat);

        // Setting custom OnTouchListener which deals with the pressed state
        // so it shows up
        this.chatButtonListener = new MapInfoTouchListener(infoWindow)
        {
            @Override
            protected void onClickConfirmed(View v, Marker marker) {
                // Here we can perform some action triggered after clicking the button
                //chat(marker.getTitle());
                chatButton.setBackgroundColor(getResources().getColor(R.color.dark_gray));
                chatButton.setBackgroundColor(getResources().getColor(R.color.white));
                chat(marker.getTitle());
            }
        };
        this.chatButton.setOnTouchListener(chatButtonListener);

        mapUpdateExecutor.scheduleWithFixedDelay(new Runnable()
        {
            @Override
            public void run()
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        refreshMap();
                    }
                });
            }
        }, 0, MAP_UPDATE_DELAY, TimeUnit.SECONDS);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main_drawer, menu);

        MenuItem friends_view = sideMenu.findItem(R.id.nav_friends);
        MenuItem friend_requests_view = sideMenu.findItem(R.id.nav_friend_requests);

        Menu subm = friends_view.getSubMenu(); // get my MenuItem with placeholder submenu
        subm.clear(); // delete place holder

        for (String friend : friendsList)
        {
            subm.add(0, MENU_DYNAMIC+friend_insert_counter, friend_insert_counter, friend); // id is idx+ my constant
            final MenuItem new_friend = subm.findItem(MENU_DYNAMIC+friend_insert_counter);
            new_friend.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    String friend_username = (String) new_friend.getTitle();
                    chat(friend_username);
                    return true;
                }
            });
            friend_insert_counter++;
        }

        subm = friend_requests_view.getSubMenu(); // get my MenuItem with placeholder submenu
        subm.clear(); // delete place holder

        for (String friend : friend_requests)
        {
            subm.add(0, MENU_DYNAMIC+friend_insert_counter, friend_insert_counter, friend); // id is idx+ my constant
            final MenuItem new_friend = subm.findItem(MENU_DYNAMIC+friend_insert_counter);
            new_friend.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    String friend_username = (String) new_friend.getTitle();
                    chat(friend_username);
                    return true;
                }
            });
            friend_insert_counter++;
        }

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Log.i("profile","id clicked in list = " + item.getItemId());

        if (id == R.id.nav_friends_show) {

            Log.i("profile","nav_friends_show");
            MenuItem friends_show = sideMenu.findItem(R.id.nav_friends);
            if (friends_show.isVisible()) {
                friends_show.setVisible(false);
            } else {
                friends_show.setVisible(true);
            }
            return true;

        } else if (id == R.id.nav_friend_requests_show) {

            Log.i("profile","nav_friend_requests_show");
            MenuItem friend_requests = sideMenu.findItem(R.id.nav_friend_requests);
            if (friend_requests.isVisible()) {
                friend_requests.setVisible(false);
            } else {
                friend_requests.setVisible(true);
            }
            return true;

        } else if (id == R.id.nav_logout) {

            startActivity(new Intent(MainActivity.this, LoginActivity.class));

        } else if (id == R.id.nav_find_friends) {

            startActivity(new Intent(MainActivity.this,FindFriendsActivity.class));

        } else if (id >= MENU_DYNAMIC && id <= MENU_DYNAMIC + friend_insert_counter) {
            // clicked on a friend to chat, or friend to accept
            return true;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    /** Called when the user taps the Send button */
    public void chat(String friend) {
        // Do something in response to button
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra(EXTRA_MESSAGE, friend);
        startActivity(intent);
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
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        try
        {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    LOCATION_MIN_TIME, LOCATION_MIN_DISTANCE, this);
        }
        catch (SecurityException e)
        {
            Log.e(TAG, "Location access error: " + e.getMessage());
        }
        builder = new LatLngBounds.Builder();
        updateLocation();

        // Let's add a couple of markers
        for (String friend : friendsList)
        {
            Log.i(TAG, "Now adding a marker for friend: " + friend);
            try
            {
                listOfAllMarkers.add(mMap.addMarker(new MarkerOptions().title(friend).
                        position(new LatLng(MapHelper.get_latitude(friend, LoggedInUser.getUsername())
                                , MapHelper.get_longitude(friend, LoggedInUser.getUsername())))));
            }
            catch (JSONException e)
            {
                Log.e(TAG, e.getMessage());
            }
        }


        mapLayout = (MapLayout) findViewById(R.id.map_container);
        mapLayout.initialize(mMap, getPixelsFromDp(this, MARKER_HEIGHT + BALLOON_BOTTOM_EDGE_OFFSET));

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                // Setting up the infoWindow with current's marker info
                infoTitle.setText(marker.getTitle());
                //infoSnippet.setText(marker.getSnippet());
                chatButtonListener.setMarker(marker);

                // We must call this to set the current marker and infoWindow references
                // to the MapWrapperLayout
                mapLayout.setMarkerWithInfoWindow(marker, infoWindow);
                return infoWindow;
            }
        });

        // show the user's location on the main map
        if (userLocation != null)
        {
            LatLng userLatLng = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());
            MapHelper.post_location(LoggedInUser.getUsername(), userLocation.getLatitude(), userLocation.getLongitude());
            builder.include(userLatLng);
            // LatLngBounds bounds = builder.build();
            cameraUpdate = CameraUpdateFactory.newLatLng(userLatLng);
            mMap.moveCamera(cameraUpdate);
            cameraUpdate = CameraUpdateFactory.zoomTo(MAP_ZOOM_VIEW);
            mMap.animateCamera(cameraUpdate);
        }

        isMapReady = true;
    }

    public void refreshMap()
    {
        if (!isMapReady)
            return;
        for (Marker marker : this.listOfAllMarkers)
        {
            Log.e(TAG, "Refreshing map of friend: " + marker.getTitle());
            try {
                marker.setPosition(new LatLng(MapHelper.get_latitude(marker.getTitle(),
                        LoggedInUser.getUsername()),
                        MapHelper.get_longitude(marker.getTitle(), LoggedInUser.getUsername())));
                // Post a new location every couple of seconds
                if (userLocation != null)
                    MapHelper.post_location(LoggedInUser.getUsername(), userLocation.getLatitude(), userLocation.getLongitude());
            }
            catch (JSONException e)
            {
                Log.e(TAG, "JSON exception: " + e.getMessage());
            }
        }
    }



    private void updateLocation()
    {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                Criteria criteria = new Criteria();
                userLocation = mLocationManager.getLastKnownLocation(mLocationManager.
                        getBestProvider(criteria, false));
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                userLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            updateLocation();
        } else {
            mLocationPermissionGranted = false;
            Log.i(TAG,"location permission not granted");
        }
    }

    public static int getPixelsFromDp(Context context, float dp)
    {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dp * scale + 0.5f);
    }

    public void onMapSearch(View view)
    {
        EditText locationSearch = (EditText) findViewById(R.id.search_box);
        String query = locationSearch.getText().toString();
        Marker marker = null;

        if (query != null || !query.equals(""))
        {
            for (Marker m : listOfAllMarkers)
            {
                if (m.getTitle().equals(query))
                {
                    marker = m;
                    break;
                }
            }

            //Address address = addressList.get(0);
            //LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
            //mMap.addMarker(new MarkerOptions().position(latLng).title("Marker"));
            if (marker != null)
            {
                mMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
                marker.showInfoWindow();
            }
            else
            {
                Toast.makeText(this.getApplicationContext(), "No user exists with that name", Toast.LENGTH_SHORT).show();
            }
        }

        // putting keyboard down
        view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private class FacebookDisplayPictureTask extends AsyncTask<String,Void,Bitmap> {

        @Override
        protected Bitmap doInBackground(String... fb_ids) {

            String user_fb_id = fb_ids[0];

            Bitmap bitmap = null;
            Log.i(TAG,"fb id is " + user_fb_id + " in async task");

            try {
                URL img_url = new URL("http://graph.facebook.com/"+user_fb_id+"/picture?type=large");

                bitmap = BitmapFactory.decodeStream((InputStream)img_url.getContent());

                if (bitmap == null) {
                    Log.i(TAG,"bitmap is null in async task");

                } else {
                    Log.i(TAG,"bitmap is not null in async task");
                }

                return bitmap;

                /*(Bitmap mIcon = BitmapFactory.decodeStream(img_value.openConnection().getInputStream());
                if (mIcon == null) {
                    Log.i(TAG,"bitmap is null in async task");

                } else {
                    Log.i(TAG,"bitmap is not null in async task");
                }*/


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    @Override
    public void onLocationChanged(Location location)
    {
        // userLocation = location;
        // can be used to update location info on screen
        // Log.i(TAG, "Posting location from main");
        // MapHelper.post_location(LoggedInUser.getUsername(), userLocation.getLatitude(),
                // userLocation.getLongitude());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onProviderDisabled(String provider) {}

}