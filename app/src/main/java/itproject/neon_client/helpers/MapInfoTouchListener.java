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

/**
 * This abstract class is used to listen to events on the map i.e. the user
 * touching on a marker and then respond to those events by displaying a custom
 * marker info bubble above the marker touched.
 */
public abstract class MapInfoTouchListener implements OnTouchListener {
    private final View view;
    private final Handler handler = new Handler();

    private Marker marker;
    private boolean pressed = false;
    private View changedView;

    /**
     * The constructor for this class
     * @param view the view of map
     */
    public MapInfoTouchListener(View view)
    {
        this.view = view;
    }

    /**
     * This is used to set a marker
     * @param marker the marker to be set
     */
    public void setMarker(Marker marker)
    {
        this.marker = marker;
    }

    /**
     * This method is used to listen to an event that is triggered when a user clicks
     * on a marker
     * @param v the view from which the marker was clicked
     * @param event the motion event describing the click
     * @return true if the listener has consumed the event, false otherwise
     */
    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
        int[] viewLocationOnScreen = new int[2];

        view.getLocationOnScreen(viewLocationOnScreen);

        if (0 <= event.getX() && event.getX() <= view.getWidth() && 0 <= event.getY()
                && event.getY() <= v.getHeight())
        {
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN: startPress(v); break;

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

    /**
     * This is triggered when the user first pressed the button, so that the info bubble
     * can be displayed
     * @param v the view from where the press started
     */
    private void startPress(View v)
    {
        if (!pressed)
        {
            pressed = true;
            handler.removeCallbacks(confirmClickRunnable);
            v.setVisibility(View.INVISIBLE);
            changedView = v;

            if (marker != null)
                marker.showInfoWindow();
        }
    }

    /**
     * This is used to tell us when the press ended so that we can adjust the visibility
     * of the info bubble appropriately
     * @return true if the press was started before, false otherwise
     */
    private boolean endPress() {
        if (pressed) {
            this.pressed = false;
            handler.removeCallbacks(confirmClickRunnable);
            if(changedView != null) {
                changedView.setVisibility(View.VISIBLE);
                changedView = null;
            }
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
