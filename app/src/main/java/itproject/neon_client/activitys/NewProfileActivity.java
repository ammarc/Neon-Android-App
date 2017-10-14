package itproject.neon_client.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.Profile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import itproject.neon_client.helper.FriendHelper;
import itproject.neon_client.helper.LoggedInUser;
import itproject.neon_client.R;
import itproject.neon_client.mock_data.User;

/**
 * Created by kit on 25/9/17.
 */

public class NewProfileActivity extends AppCompatActivity {

    private int id = 0;
    private EditText username, phone_number, email;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_create_profile);

        Log.i("profile","new profile");

        TextView user_info_display = (TextView) findViewById(R.id.user_welcome);
        username = (EditText) findViewById(R.id.username);
        phone_number = (EditText) findViewById(R.id.phone_number);
        email = (EditText) findViewById(R.id.email);

        user_info_display.setText("Welcome " + Profile.getCurrentProfile().getFirstName() + " " + Profile.getCurrentProfile().getLastName());

        Button setUsername = (Button) findViewById(R.id.set_username);
        setUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createProfile();
            }
        });
    }

    private void createProfile() {

        String usernameString = username.getText().toString();
        String phoneString = phone_number.getText().toString();
        String emailString = email.getText().toString();

        if (usernameString.length() < 4) {
            username.setError("username is invalid");
            return;
        }
        if (phoneString.length() != 10) {
            phone_number.setError("phone number is invalid");
            return;
        }
        if (!emailString.contains("@")) {
            email.setError("email is invalid");
            return;
        }


        try {
            if (FriendHelper.user_exists(usernameString)) {
                Log.i("profile",usernameString + " username is taken sozzzzzzzle");
            }
            else {
                Log.i("profile",usernameString + " doesn't exist");
                JSONArray result = FriendHelper.add_user(usernameString,Profile.getCurrentProfile().getFirstName(),Profile.getCurrentProfile().getLastName(),
                        phoneString, emailString, Profile.getCurrentProfile().getId());
                if(result!=null){
                    Log.i("profile", result.toString());
                }
                else{
                    Log.i("profile", "no response from server!");
                }
                Log.i("profile", "user added!");
            }
        } catch (JSONException e) {
        }


        LoggedInUser.setUsername(usernameString);


        startActivity(new Intent(NewProfileActivity.this, MainActivity.class));
    }
}
