package itproject.neon_client;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;


import com.facebook.Profile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import itproject.neon_client.mock_data.UserDao;
import itproject.neon_client.mock_data.User;


/**
 * Created by kit on 5/9/17.
 */

public class ProfilePageActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "itproject.neon_client.MESSAGE";

    private static List<String> friends = new ArrayList<>(Arrays.asList("Ron_Weasley", "Hermione_Granger", "Luna_Lovegood","Neville_Longbottom"));
    private static List<String> friend_requests = new ArrayList<>(Arrays.asList("Harry_Potter", "Ginny_Weasley"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);

        TextView user_info_display = (TextView) findViewById(R.id.user_info_display);

        user_info_display.setText("Welcome " + LoggedInUser.getUser().firstname + " " + LoggedInUser.getUser().lastname);

        init();
    }

    public void cameraClick (View view) {
        // Do something in response to button
        Intent intent = new Intent(this, ARSimple.class);
        startActivity(intent);
    }

    /** Called when the user taps the Send button */
    public void chat(String friend) {
        // Do something in response to button
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra(EXTRA_MESSAGE, friend);
        startActivity(intent);
    }


    public void init() {
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

        TableLayout friend_requests_table = (TableLayout) findViewById(R.id.friend_requests_table);
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
        }
    }



}
