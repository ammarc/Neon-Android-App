package itproject.neon_client.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.Profile;

import org.json.JSONArray;
import org.json.JSONException;

import itproject.neon_client.helpers.LoggedInUser;
import itproject.neon_client.helpers.FriendHelper;
import itproject.neon_client.R;
import itproject.neon_client.user_data.User;

public class NewProfileActivity extends AppCompatActivity {

    private static final String TAG = "testing";
    private EditText username, phone_number, email;

    private ProgressBar spinner;
    Button setUsernameButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_create_profile);

        spinner = (ProgressBar)findViewById(R.id.newUserProgressBar);
        spinner.setVisibility(View.GONE);

        Log.i(TAG,"new profile");

        TextView user_info_display = (TextView) findViewById(R.id.user_welcome);
        username = (EditText) findViewById(R.id.username);
        phone_number = (EditText) findViewById(R.id.phone_number);
        email = (EditText) findViewById(R.id.email);

        user_info_display.setText("Welcome\n" + Profile.getCurrentProfile().getFirstName() + " " + Profile.getCurrentProfile().getLastName());

        setUsernameButton = (Button) findViewById(R.id.set_username);
        setUsernameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spinner.setVisibility(View.VISIBLE);
                createProfile();
            }
        });
    }

    private void createProfile() {

        String usernameString = username.getText().toString();
        String phoneString = phone_number.getText().toString();
        String emailString = email.getText().toString();

        if (usernameString.length() < 4) {
            username.setError("username is too short");
            spinner.setVisibility(View.GONE);
            return;
        }
        if (usernameString.matches("(.*) (.*)")) {
            username.setError("username can't have spaces");
            spinner.setVisibility(View.GONE);
            return;
        }
        if (phoneString.length() != 10) {
            phone_number.setError("phone number is invalid");
            spinner.setVisibility(View.GONE);
            return;
        }
        if (!emailString.contains("@")) {
            email.setError("email is invalid");
            spinner.setVisibility(View.GONE);
            return;
        }


        try {
            if (FriendHelper.userExists(usernameString)) {
                Log.i(TAG,usernameString + " exists!");
                username.setError("username is taken");
            }
            else {
                for (String user : FriendHelper.allUsers()) {
                    if (Profile.getCurrentProfile().getId().equals(FriendHelper.getUserFacebookID(user))) {
                        // todo remove user
                    }
                }
                JSONArray result = FriendHelper.addUser(usernameString,Profile.getCurrentProfile().getFirstName(),Profile.getCurrentProfile().getLastName(),
                        phoneString, emailString, Profile.getCurrentProfile().getId());
                Log.i(TAG, "user added!");
            }
        } catch (JSONException e) {
        }

        LoggedInUser.setUsername(usernameString);

        Intent mainActivityIntent = new Intent(NewProfileActivity.this, MainActivity.class);
        Log.e(TAG, "Current profile is " + usernameString);

        startActivity(mainActivityIntent);
    }
}
