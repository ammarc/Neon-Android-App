package itproject.neon_client;

import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.facebook.Profile;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {

    private GoogleMap mMap;

    public static final String EXTRA_MESSAGE = "itproject.neon_client.MESSAGE";

    private static List<String> friends = new ArrayList<>(Arrays.asList("Ron_Weasley", "Hermione_Granger", "Luna_Lovegood","Neville_Longbottom"));
    private static List<String> friend_requests = new ArrayList<>(Arrays.asList("Harry_Potter", "Ginny_Weasley"));


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View navbar = navigationView.getHeaderView(0);

        /* dp */
        ImageView userDp = (ImageView) navbar.findViewById(R.id.user_dp);
        //Bitmap dpBitmap = getFacebookProfilePicture(LoggedInUser.getUser().fb_id); // ToDo fb profile picture

        /* user info */
        TextView user_name = (TextView) navbar.findViewById(R.id.user_name);
        user_name.setText(LoggedInUser.getUser().fullname);
        TextView user_username = (TextView) navbar.findViewById(R.id.user_username);
        user_username.setText(LoggedInUser.getUser().username);

        //friendsInit();


        /* map */
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        /*SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);*/
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
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) { //TODO set actions
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_friends) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_logout) {

            startActivity(new Intent(MainActivity.this, LoginActivity.class));

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
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


    /* puts friends in table */
    public void friendsInit() {
        TableLayout friends_table = (TableLayout) findViewById(R.id.friends_table);
        for (final String friend : friends) {
            TableRow tbrow = new TableRow(this);
            TextView t1v = new TextView(this);
            t1v.setText(friend);
            t1v.setMinHeight(100);
            t1v.setTextSize(15);
            t1v.setTextColor(Color.WHITE);
            t1v.setGravity(Gravity.LEFT);
            tbrow.addView(t1v);
            tbrow.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // Code here executes on main thread after user presses button
                    chat(friend);
                }
            });
            friends_table.addView(tbrow);
        }

        /*TableLayout friend_requests_table = (TableLayout) findViewById(R.id.friend_requests_table);
        for (final String friend : friend_requests) {
            TableRow tbrow = new TableRow(this);
            TextView t1v = new TextView(this);
            t1v.setText(friend);
            t1v.setMinHeight(100);
            t1v.setTextSize(15);
            t1v.setTextColor(Color.WHITE);
            t1v.setGravity(Gravity.LEFT);
            tbrow.addView(t1v);
            tbrow.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // Code here executes on main thread after user presses button
                    chat(friend);
                }
            });
            friend_requests_table.addView(tbrow);
        }*/
    }

    /** Called when the user taps the Send button */
    public void chat(String friend) {
        // Do something in response to button
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra(EXTRA_MESSAGE, friend);
        startActivity(intent);
    }
}
