package itproject.neon_client;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.Profile;

/**
 * Created by kit on 5/9/17.
 */

public class SecondPageActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "itproject.neon_client.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_page);

        TextView user_info_display = (TextView) findViewById(R.id.user_info_display);
        //Profile profile = Profile.getCurrentProfile();

        //Log.i("second page profile: ", profile.getFirstName());
        //user_info_display.setText(profile.getFirstName() + " " + profile.getLastName());
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
