package itproject.neon_client;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;


import java.util.ArrayList;
import java.util.List;

/**
 * A login screen that offers login via email/password.
 */

public class LoginActivity extends Activity
{

    private CallbackManager callbackManager;

    /** Called when the user taps the Send button */
    public void loginButton(View view) {
        // Do something in response to button
        Intent intent = new Intent(this, SecondPageActivity.class);
        startActivity(intent);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
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
}
