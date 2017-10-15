package itproject.neon_client.activities;

import android.animation.ObjectAnimator;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;

import java.util.List;

import itproject.neon_client.R;
import itproject.neon_client.helpers.FriendHelper;
import itproject.neon_client.helpers.LoggedInUser;

import static itproject.neon_client.R.*;
import static itproject.neon_client.R.color.grey;
import static itproject.neon_client.R.drawable.ic_account_circle_black_24dp;
import static itproject.neon_client.R.drawable.ic_add_black_24dp;
import static itproject.neon_client.R.drawable.ic_arrow_forward_black_24dp;
import static itproject.neon_client.R.drawable.ic_done_black_24dp;

public class FindFriendsActivity extends AppCompatActivity {

    private static final String TAG = "testing";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_find_friends);
        Toolbar toolbar = (Toolbar) findViewById(id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Find Friends");


        LinearLayout ListLayout = (LinearLayout) findViewById(id.search_display);

        for (String user : FriendHelper.allUsers()) {

            final String user_final = user;

            if (FriendHelper.getFriendshipStatus(LoggedInUser.getUsername(),user) == -1) {

                final LinearLayout ListElement = new LinearLayout(this);
                ListElement.setPadding(16, 16, 16, 16);
                ListElement.setOrientation(LinearLayout.HORIZONTAL);

                ImageView dp = new ImageView(this);
                dp.setImageResource(ic_account_circle_black_24dp);
                dp.setLayoutParams(new LinearLayout.LayoutParams(200, 200));
                dp.setPadding(16, 16, 16, 16);

                LinearLayout ListElementNames = new LinearLayout(this);
                ListElementNames.setOrientation(LinearLayout.VERTICAL);
                ListElementNames.setLayoutParams(new LinearLayout.LayoutParams(210, 200, 1));
                ListElementNames.setPadding(16, 16, 16, 16);
                ListElementNames.setMinimumWidth(220);

                TextView name = new TextView(this);
                try {
                    name.setText(FriendHelper.getUserFullName(user));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                name.setPadding(0, 16, 0, 0);
                TextView username = new TextView(this);
                username.setText(user);

                ListElementNames.addView(name, 0);
                ListElementNames.addView(username, 1);

                final ImageButton addButton = new ImageButton(this);
                addButton.setMaxHeight(2);
                addButton.setBackgroundResource(ic_arrow_forward_black_24dp);
                addButton.setLayoutParams(new LinearLayout.LayoutParams(150, 150));
                addButton.setPadding(16, 32, 16, 16);
                addButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            String returned_message = FriendHelper.addFriend(user_final,LoggedInUser.getUsername());
                            Log.i(TAG,"friendship requested: " + user_final);
                            Log.i(TAG,"message from db: " + returned_message);
                            addButton.setBackgroundResource(ic_done_black_24dp);
                            ListElement.setBackgroundColor(getResources().getColor(grey));
                        } catch (JSONException e) {
                            Log.i(TAG,"friendship add failed: " + user_final);
                            e.printStackTrace();
                        }
                    }
                });

                ListElement.addView(dp, 0);
                ListElement.addView(ListElementNames, 1);
                ListElement.addView(addButton, 2);

                ListLayout.addView(ListElement);
            }
        }

    }
    
}
