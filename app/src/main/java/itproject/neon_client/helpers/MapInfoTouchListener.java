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
    private View changedView;

    public MapInfoTouchListener(View view)
    {
        this.view = view;
    }

    public void setMarker(Marker marker)
    {
        this.marker = marker;
    }

    @Override
    public boolean onTouch(View vv, MotionEvent event)
    {
        int[] viewLocationOnScreen = new int[2];

        view.getLocationOnScreen(viewLocationOnScreen);

        if (0 <= event.getX() && event.getX() <= view.getWidth() && 0 <= event.getY()
                && event.getY() <= vv.getHeight())
        {
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN: startPress(vv); break;

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

    private void startPress(View v) {
        if (!pressed) {
            pressed = true;
            handler.removeCallbacks(confirmClickRunnable);
            v.setVisibility(View.INVISIBLE);
            changedView = v;
            if (marker != null)
                marker.showInfoWindow();
        }
    }

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
