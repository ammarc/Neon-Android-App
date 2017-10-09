package itproject.neon_client.activitys;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import static itproject.neon_client.R.*;
import static itproject.neon_client.R.drawable.ic_account_circle_black_24dp;
import static itproject.neon_client.R.drawable.ic_add_black_24dp;

public class FindFriendsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_find_friends);
        Toolbar toolbar = (Toolbar) findViewById(id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Find FriendHelper");


        LinearLayout ListLayout = (LinearLayout) findViewById(id.search_display);

        for (String user : MainActivity.all_users) {

            LinearLayout ListElement = new LinearLayout(this);
            ListElement.setPadding(16,16,16,16);
            ListElement.setOrientation(LinearLayout.HORIZONTAL);

            ImageView dp = new ImageView(this);
            dp.setImageResource(ic_account_circle_black_24dp);
            dp.setLayoutParams(new LinearLayout.LayoutParams(200,200));
            dp.setPadding(16,16,16,16);

            LinearLayout ListElementNames = new LinearLayout(this);
            ListElementNames.setOrientation(LinearLayout.VERTICAL);
            ListElementNames.setLayoutParams(new LinearLayout.LayoutParams(210,200,1));
            ListElementNames.setPadding(16,16,16,16);
            ListElementNames.setMinimumWidth(220);

            TextView name = new TextView(this);
            name.setText("full name");
            name.setPadding(0,16,0,0);
            TextView username = new TextView(this);
            username.setText(user);

            ListElementNames.addView(name,0);
            ListElementNames.addView(username,1);

            ImageButton addButton = new ImageButton(this);
            addButton.setBackgroundResource(ic_add_black_24dp);
            addButton.setLayoutParams(new LinearLayout.LayoutParams(150,150));
            addButton.setPadding(16,16,16,16);

            // todo add friend here OnCLick

            ListElement.addView(dp,0);
            ListElement.addView(ListElementNames,1);
            ListElement.addView(addButton,2);

            ListLayout.addView(ListElement);
        }

    }
}
