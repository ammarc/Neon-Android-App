package itproject.neon_client;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.Profile;

import itproject.neon_client.mock_data.UserDao;
import itproject.neon_client.mock_data.User;
import itproject.neon_client.mock_data.AppDatabase;

/**
 * Created by soe on 25/9/17.
 */

public class NewProfileActivity extends AppCompatActivity {

    private static int id = 3;
    private EditText username, phone_number, email;
    private AppDatabase database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        /*mock data*/
        database = AppDatabase.getDatabase(getApplicationContext());

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

        id++;

        User newUser = new User(id,usernameString,Profile.getCurrentProfile().getFirstName(),Profile.getCurrentProfile().getLastName(),
                phoneString, emailString, 1, Profile.getCurrentProfile().getId());

        database.userDao().addUser(newUser);
        User user = database.userDao().getUser(id).get(0);
        LoggedInUser.setUser(newUser);
        Log.i("profile","user " + LoggedInUser.getUser().username + " id " + LoggedInUser.getUser().id);
        startActivity(new Intent(NewProfileActivity.this, ProfilePageActivity.class));
    }
}