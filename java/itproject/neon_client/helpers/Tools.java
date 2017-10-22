package itproject.neon_client.helpers;

import android.content.Context;
import android.widget.Toast;

public class Tools  {
    public static void exceptionToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
}
