package itproject.neon_client;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.Profile;
import com.facebook.ProfileTracker;

/**
 * Created by kit on 5/9/17.
 */

public class SecondPageActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "itproject.neon_client.MESSAGE";

    ProfileTracker profileTracker;
    private String firstName, lastName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_page);

        // todo : something is not syncing, name down the bottom isn't working

        TextView user_info_display = (TextView) findViewById(R.id.user_info_display);
        Profile profile = Profile.getCurrentProfile();

        if(profile == null) {
            profileTracker = new ProfileTracker() {
                @Override
                protected void onCurrentProfileChanged(Profile profile, Profile profile2) {
                    // profile2 is the new profile
                    firstName = profile2.getFirstName();
                    lastName = profile2.getLastName();
                    Log.i("fb profile 2: ", firstName + " " + lastName);
                    profileTracker.stopTracking();
                }
            };
        }

        if (firstName == null) {
            Log.i("profile firstname ","null");
        }
        else {
            Log.i("profile okkk ", firstName + " " + lastName);
        }

        //Log.i("second page firstName: ", firstName);

        //Log.i("second page profile: ", profile.getFirstName());
        //user_info_display.setText(profile.getFirstName() + " " + profile.getLastName());

        user_info_display.setText("heyyyyy");
    }

    /** Called when the user taps the Send button */
    public void sendMessage(View view) {
        // Do something in response to button
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        EditText editText = (EditText) findViewById(R.id.editText);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

}
