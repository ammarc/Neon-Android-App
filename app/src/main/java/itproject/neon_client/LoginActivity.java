package itproject.neon_client;

import android.content.Intent;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

import java.util.ArrayList;
import java.util.List;

import eu.kudan.kudan.ARAPIKey;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via facebook.
 */
public class LoginActivity extends AppCompatActivity {

    CallbackManager callbackManager;
    ProfileTracker profileTracker;

    LoginButton fbLoginButton;
    Button signUpButton;
    Button signInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i("profile", "on create");

        setContentView(R.layout.activity_login);

        fbLoginButton = (LoginButton) findViewById(R.id.fb_login_button);
        signUpButton = (Button) findViewById(R.id.sign_up_button);
        signInButton = (Button) findViewById(R.id.fb_sign_in_button);

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        callbackManager = CallbackManager.Factory.create();

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
                                    Log.i("fb profile: ", profile2.getFirstName() + " " + profile2.getLastName());
                                    profileTracker.stopTracking();
                                }
                            };

                            signInButton.setVisibility(View.VISIBLE);
                            signUpButton.setVisibility(View.VISIBLE);


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

        if (Profile.getCurrentProfile() == null) {
            signInButton.setVisibility(View.INVISIBLE);
            signUpButton.setVisibility(View.INVISIBLE);
        }
        else {
            signInButton.setVisibility(View.VISIBLE);
            signUpButton.setVisibility(View.VISIBLE);
        }

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sign_up();
            }
        });
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                facebook_sign_in();
            }
        });
    }

    private void sign_up() {
        if (Profile.getCurrentProfile() == null) {
            Log.i("profile","null");
        }
        else {
            Log.i("profile",Profile.getCurrentProfile().getFirstName());
            startActivity(new Intent(LoginActivity.this, NewProfileActivity.class));
        }
    }

    private void facebook_sign_in() {
        if (Profile.getCurrentProfile() == null) {
            Log.i("profile","null");
        }
        else {
            Log.i("profile",Profile.getCurrentProfile().getFirstName());
            startActivity(new Intent(LoginActivity.this, ProfilePageActivity.class));
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("profile", "on activity result");
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}

