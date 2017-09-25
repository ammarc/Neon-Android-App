package itproject.neon_client;

import android.app.Application;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import eu.kudan.kudan.ARAPIKey;

/**
 * A login screen that offers login via facebook.
 */
public class LoginActivity extends FragmentActivity {


    CallbackManager callbackManager;
    LoginButton fbLoginButton;
    ProfileTracker profileTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_login);

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        callbackManager = CallbackManager.Factory.create();
        fbLoginButton = (LoginButton) findViewById(R.id.fb_login_button);
        Button loggedInButton = (Button) findViewById(R.id.logged_in_button);

        if (isLoggedIn()) {

            loggedInButton.setVisibility(View.VISIBLE);

            User.setFirstName(Profile.getCurrentProfile().getFirstName());
            User.setLastName(Profile.getCurrentProfile().getLastName());

            String message = "Continue as " + Profile.getCurrentProfile().getFirstName();
            loggedInButton.setText(message);
        }

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {

                    @Override
                    public void onSuccess(LoginResult loginResult) {

                        // App code
                        if (Profile.getCurrentProfile() == null) {
                            profileTracker = new ProfileTracker() {
                                @Override
                                protected void onCurrentProfileChanged(Profile profile, Profile profile2) {
                                    // profile2 is the new profile
                                    User.setFirstName(profile2.getFirstName());
                                    User.setLastName(profile2.getLastName());
                                    Log.i("fb profile: ", profile2.getFirstName() + " " + profile2.getLastName());
                                    profileTracker.stopTracking();
                                }
                            };

                            // no need to call startTracking() on mProfileTracker
                            // because it is called by its constructor, internally.
                        }
                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                    }
                });

    }

    /** Called when the user taps the continue as -- button */
    public void ContinueLoggedIn(View view) {
        // Do something in response to button
        Button loggedInButton = (Button) findViewById(R.id.logged_in_button);
        Intent intent = new Intent(this, ProfilePageActivity.class);
        startActivity(intent);
        loggedInButton.setVisibility(View.INVISIBLE);
    }

    public boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }


    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        {
            /*Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                User.setDp(bitmap);
                Log.i("profile dp", "worked");
            } catch (IOException e) {
                Log.i("profile dp", "didn't work");
                e.printStackTrace();
            }*/
        }
        callbackManager.onActivityResult(requestCode, resultCode, data);
        startActivity(new Intent(LoginActivity.this, ProfilePageActivity.class));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //profileTracker.stopTracking();
    }
}

