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

    String firstName = User.getFirstName();
    String lastName = User.getLastName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_page);

        // todo : something is not syncing, name down the bottom isn't working

        TextView user_info_display = (TextView) findViewById(R.id.user_info_display);

        user_info_display.setText("Welcome " + firstName + " " + lastName);

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
