package itproject.neon_client;

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

/**
 * Created by soe on 25/9/17.
 */

public class NewProfileActivity extends AppCompatActivity {

    private static int id = 0;
    private EditText username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        TextView user_info_display = (TextView) findViewById(R.id.user_welcome);
        username = (EditText) findViewById(R.id.username);

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
        Editable usernameString = username.getText();
        Log.i("profile","username = " + usernameString);
        //User newUser = new User(id++,"")
    }
}
