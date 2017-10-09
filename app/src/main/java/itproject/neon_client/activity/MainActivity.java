package itproject.neon_client.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import itproject.neon_client.helper.LoggedInUser;
import itproject.neon_client.R;
import itproject.neon_client.chat.ChatActivity;

import static android.content.ContentValues.TAG;
import static itproject.neon_client.R.id.map;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {

    private GoogleMap mMap;
    private Menu sideMenu;
    private final int MENU_DYNAMIC = 2131755500;
    private int friend_insert_counter = 0;

    public static final String EXTRA_MESSAGE = "itproject.neon_client.MESSAGE";

    public static List<String> friends = new ArrayList<>(Arrays.asList("Ron_Weasley", "Hermione_Granger", "Luna_Lovegood","Neville_Longbottom"));
    public static List<String> friend_requests = new ArrayList<>(Arrays.asList("Harry_Potter", "Ginny_Weasley"));
    public static List<String> all_users = new ArrayList<>(Arrays.asList("draco_m","hagrid_has_scary_pets","he_who_must_not_be_named","ratty","shaggy_dog","lupin_howles"));


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View navbar = navigationView.getHeaderView(0);
        sideMenu = navigationView.getMenu();

        /* dp */
        ImageView userDp = (ImageView) navbar.findViewById(R.id.user_dp);
        //Bitmap dpBitmap = getFacebookProfilePicture(LoggedInUser.getUser().fb_id); // ToDo fb profile picture

        /* user info */
        TextView user_name = (TextView) navbar.findViewById(R.id.user_name);
        user_name.setText(LoggedInUser.getUser().fullname);
        TextView user_username = (TextView) navbar.findViewById(R.id.user_username);
        user_username.setText(LoggedInUser.getUser().username);

        /* map */
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map_main_view);
        mapFragment.getMapAsync(this);
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

        for (String friend : friends)
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

    /**
     * Gets called every time the user presses the menu button.
     * Use if your menu is dynamic.
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        /*menu.clear();
        if(enableAdd)
            menu.add(0, MENU_ADD, Menu.NONE, R.string.your-add-text).setIcon(R.drawable.your-add-icon);
        if(enableList)
            menu.add(0, MENU_LIST, Menu.NONE, R.string.your-list-text).setIcon(R.drawable.your-list-icon);
        if(enableRefresh)
            menu.add(0, MENU_REFRESH, Menu.NONE, R.string.your-refresh-text).setIcon(R.drawable.your-refresh-icon);
        if(enableLogin)
            menu.add(0, MENU_LOGIN, Menu.NONE, R.string.your-login-text).setIcon(R.drawable.your-login-icon);*/

        return super.onPrepareOptionsMenu(menu);
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

    public static Bitmap getFacebookProfilePicture(String userID){
        URL imageURL = null;
        Log.i("profile", "getting dp");
        try {
            imageURL = new URL("https://graph.facebook.com/" + userID + "/picture?type=large");
        } catch (MalformedURLException e) {
            Log.i("profile","dp failure *");
            e.printStackTrace();
        }
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());
        } catch (IOException e) {
            Log.i("profile","dp failure !");
            e.printStackTrace();
        }
        return bitmap;
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

        // Add a marker in Melbounre and move the camera
        LatLng melbUni = new LatLng(-37.7964, 144.9612);
        mMap.addMarker(new MarkerOptions().position(melbUni).title("Marker in Melb Uni"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(melbUni));
        mMap.setMinZoomPreference(10);
        mMap.setMaxZoomPreference(20);

        // New popup menu
        View popUp = getLayoutInflater().inflate(R.layout.activity_main, , false);

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener()
              {
                  @Override
                  public boolean onMarkerClick(Marker marker)
                  {
                      //Log.e(TAG, "The marker was clicked");
                      RecyclerView recyclerView = new RecyclerView(getApplicationContext());
                      //ArrayyList
                      //recyclerView.addFocusables();
                      return true;
                  }
              }
        );
    }
}
