package itproject.neon_client;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by brycer on 9/10/17.
 */

public class Tools  {
    public static void exceptionToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
}