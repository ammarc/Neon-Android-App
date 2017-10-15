package itproject.neon_client.activities;

import android.content.Intent;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

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

import org.json.JSONException;

import itproject.neon_client.helpers.FriendHelper;
import itproject.neon_client.helpers.LoggedInUser;
import itproject.neon_client.R;

/**
 * A login screen that offers login via facebook.
 */
public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "testing";
    CallbackManager callbackManager;
    ProfileTracker profileTracker;

    LoginButton fbLoginButton;
    Button signUpButton;
    Button signInButton;

    private ProgressBar spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(TAG, "on create");

        setContentView(R.layout.activity_login);

        fbLoginButton = (LoginButton) findViewById(R.id.fb_login_button);
        signUpButton = (Button) findViewById(R.id.sign_up_button);
        signInButton = (Button) findViewById(R.id.fb_sign_in_button);
        spinner = (ProgressBar)findViewById(R.id.progressBar);
        spinner.setVisibility(View.GONE);

        final TextView fb_logout_message = findViewById(R.id.not_the_logged_in_person_message);


        /* facebook and login */

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(LoginActivity.this);

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
                            fb_logout_message.setText("Logged in as " + Profile.getCurrentProfile().getFirstName() + " " + Profile.getCurrentProfile().getLastName() + "\nNot you?");


                            // no need to call startTracking() on mProfileTracker
                            // because it is called by its constructor, internally.
                        }
                    }

                    @Override
                    public void onCancel() {
                        // App code
                        Log.i(TAG,"fb cancel");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                        Log.i(TAG,"fb error");
                    }
                });

        if (Profile.getCurrentProfile() == null) {
            Log.i(TAG,"current profile is null");
            signInButton.setVisibility(View.INVISIBLE);
            signUpButton.setVisibility(View.INVISIBLE);
            fb_logout_message.setVisibility(View.INVISIBLE);
        }
        else {
            signUpButton.setVisibility(View.VISIBLE);
            signInButton.setVisibility(View.INVISIBLE);
            fb_logout_message.setVisibility(View.VISIBLE);

            if (FriendHelper.allUsers() == null) {
                Log.i(TAG,"allusers() is null");
            } else {
                for (String user : FriendHelper.allUsers()) {
                    try {
                        if (FriendHelper.getUserFacebookID(user).equals(Profile.getCurrentProfile().getId())) {
                            signInButton.setVisibility(View.VISIBLE);
                            signUpButton.setVisibility(View.INVISIBLE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

        }

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUp();
            }
        });
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spinner.setVisibility(View.VISIBLE);
                facebookSignIn();
            }
        });

    }

    private void signUp() {
        Log.i(TAG,"sign up");
        if (Profile.getCurrentProfile() == null) {
            Log.i(TAG,"null");
        }
        else {
            Log.i(TAG,Profile.getCurrentProfile().getFirstName());
            startActivity(new Intent(LoginActivity.this, NewProfileActivity.class));
        }
    }

    private void facebookSignIn()
    {
        Log.i(TAG,"fb login");
        if (Profile.getCurrentProfile() == null)
        {
            Log.i(TAG,"null");
        }
        else
        {
            Log.i(TAG, "Name of current fb user is " + Profile.getCurrentProfile().getFirstName());

            for (String user : FriendHelper.allUsers())
            {
                try {
                    if (Profile.getCurrentProfile().getId().equals(FriendHelper.getUserFacebookID(user)))
                    {
                        LoggedInUser.setUsername(user);
                        Log.i(TAG, "current user is " + user);
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            Log.i(TAG,"user doesn't have an account");
            Snackbar mySnackbar = Snackbar.make(findViewById(R.id.coordinator_layout), R.string.dont_have_account, Snackbar.LENGTH_LONG);
            mySnackbar.show();
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "on activity result");
        finish();
        startActivity(getIntent());
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
