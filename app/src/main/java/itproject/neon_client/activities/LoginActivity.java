package itproject.neon_client.activities;

import android.content.Intent;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

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

import itproject.neon_client.helpers.LoggedInUser;
import itproject.neon_client.R;
import itproject.neon_client.user_data.AppDatabase;
import itproject.neon_client.user_data.User;

/**
 * A login screen that offers login via facebook.
 */
public class LoginActivity extends AppCompatActivity {

    public static final String TAG = "profile";
    CallbackManager callbackManager;
    ProfileTracker profileTracker;

    LoginButton fbLoginButton;
    Button signUpButton;
    Button signInButton;

    static AppDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(TAG, "on create");

        setContentView(R.layout.activity_login);

        fbLoginButton = (LoginButton) findViewById(R.id.fb_login_button);
        signUpButton = (Button) findViewById(R.id.sign_up_button);
        signInButton = (Button) findViewById(R.id.fb_sign_in_button);


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

        Log.i(TAG, "*");

        if (Profile.getCurrentProfile() == null) {
            signInButton.setVisibility(View.INVISIBLE);
            signUpButton.setVisibility(View.INVISIBLE);
        }
        else {
            signInButton.setVisibility(View.VISIBLE);
            signUpButton.setVisibility(View.VISIBLE);
        }

        Log.i(TAG,"!");

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUp();
            }
        });
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                facebookSignIn();
            }
        });

        /* mock data */
        database = AppDatabase.getDatabase(getApplicationContext());

        database.userDao().removeAllUsers();

        // add some data
        /*List<User> users = database.userDao().getAllUser();
        if (users.size()==0) {
            database.userDao().addUser(new User(1, "harryP", "harry", "potter", "0411854930", "hazP@account", Profile.getCurrentProfile().getId()));
            database.userDao().addUser(new User(2, "ginny_weasley", "ginny", "weasley", "0447893029", "gweasley@gmail", "0"));
            database.userDao().addUser(new User(3, "hermione", "hermione", "granger", "0478986543", "hgranger@hotmail", "0"));
        }*/

    }

    public void goToCamera(View view) {

        startActivity(new Intent(LoginActivity.this, NeonARActivity.class));

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
            Log.i(TAG, "Name of current logged in user is " + Profile.getCurrentProfile().getFirstName());

            for (User user : database.userDao().getAllUser())
            {
                if (Profile.getCurrentProfile().getId().compareTo(user.fb_id) == 0)
                {
                    LoggedInUser.setUser(user);
                    Log.e(TAG, "current user is " + user);
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                }
            }

            // TODO error message if they don't have account
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "on activity result");
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroy() {
        AppDatabase.destroyInstance();
        super.onDestroy();
    }
}
