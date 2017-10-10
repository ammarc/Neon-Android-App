package itproject.neon_client.helpers;

import android.graphics.Point;
import android.util.Log;
import android.view.View;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.google.android.gms.maps.model.Marker;

import static android.content.ContentValues.TAG;

public abstract class MapInfoTouchListener implements OnTouchListener {
    private final View view;
    private final Handler handler = new Handler();

    private Marker marker;
    private boolean pressed = false;

    public MapInfoTouchListener(View view)
    {
        this.view = view;
    }

    public void setMarker(Marker marker)
    {
        Log.e(TAG, "setMarker: Marker is set for the onTouchListener");
        this.marker = marker;
    }

    @Override
    public boolean onTouch(View vv, MotionEvent event)
    {
        int[] viewLocationOnScreen = new int[2];

        Log.e(TAG, "views x and y are " + view.getX() + " " + view.getY());

        Log.e(TAG, "views width value is " + view.getWidth());
        Log.e(TAG, "views height value is " + view.getHeight());

        Log.e(TAG, "vvs height value is " + vv.getHeight());
        Log.e(TAG, "vvs width value is " + vv.getWidth());

        view.getLocationOnScreen(viewLocationOnScreen);
        Log.e(TAG, "Location of the event on the screen is " + event.getRawX() + " " + event.getRawY());

        Log.e(TAG, "vvs width and height are " + vv.getWidth() + " " + vv.getHeight());

        Log.e(TAG, "vvs x is " + vv.getX() + " views X is " + view.getX() + " views width is " + view.getWidth());
        Log.e(TAG, "vvs y is " + vv.getY() + " views Y is " + view.getY() + " views height is " + view.getHeight());
        Log.e(TAG, "Views x bounds are: " + (vv.getX() + view.getX()) + " " + (vv.getX() + view.getWidth() + view.getX()));

        //if (view.getX() + vv.getX() <= event.getRawX() && view.getWidth() + view.getX() + vv.getX() >= event.getRawX())// &&
                //view.getY() + vv.getY() <= event.getY() && event.getY() <= view.getHeight() + view.getY() + vv.getY())
            if (0 <= event.getX() && event.getX() <= view.getWidth() &&
                    0 <= event.getY() && event.getY() <= vv.getHeight())
        {
            Log.e(TAG, "onTouch: Button's onTouch is called");
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN: startPress(); break;

                // We need to delay releasing of the view a little so it shows the pressed
                // state on the screen
                case MotionEvent.ACTION_UP: handler.postDelayed(confirmClickRunnable, 150); break;

                case MotionEvent.ACTION_CANCEL: endPress(); break;
                default: break;
            }
        }
        else {
            // If the touch goes outside of the view's area
            // (like when moving finger out of the pressed button)
            // just release the press
            endPress();
        }
        return false;
    }

    private void startPress() {
        if (!pressed) {
            pressed = true;
            handler.removeCallbacks(confirmClickRunnable);
            if (marker != null)
                marker.showInfoWindow();
        }
    }

    private boolean endPress() {
        if (pressed) {
            this.pressed = false;
            handler.removeCallbacks(confirmClickRunnable);
            if (marker != null)
                marker.showInfoWindow();
            return true;
        }
        else
            return false;
    }

    private final Runnable confirmClickRunnable = new Runnable() {
        public void run() {
            if (endPress()) {
                onClickConfirmed(view, marker);
            }
        }
    };

    /**
     * This is called after a successful click
     */
    protected abstract void onClickConfirmed(View v, Marker marker);
}
