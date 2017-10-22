package itproject.neon_client.helpers;

import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import itproject.neon_client.R;

/**
 * This is used as a custom may layout so that we can display info bubbles on
 * the marker of our main map
 */
public class MapLayout extends RelativeLayout
{
    private GoogleMap mMap;
    // this is the offset between the bottom of the info
    // window and the the actual Google Maps marker
    private int bottomOffsetInPixels;
    private Marker marker;
    private View infoWindow;


    /**
     * The default constructors.
     */
    public MapLayout(Context context)
    {
        super(context);
    }

    public MapLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public MapLayout(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    /**
     * Initializes the map layout's variables
     * @param mMap the google map object used
     * @param bottomOffsetInPixels the offset to be used to display the bubble above the marker
     */
    public void initialize(GoogleMap mMap, int bottomOffsetInPixels)
    {
        this.mMap= mMap;
        this.bottomOffsetInPixels = bottomOffsetInPixels;
    }

    /**
     * Sets the marker's info window
     * @param marker the marker to be set
     * @param infoWindow the view of the info window to be set
     */
    public void setMarkerWithInfoWindow(Marker marker, View infoWindow)
    {
        this.marker = marker;
        this.infoWindow = infoWindow;
    }

    /**
     * This method is used to trigger an event whenever the user clicks on
     * one of the buttons of the info bubble
     * @param e the click event where the user tapped on the screen
     * @return true if the event was handled by the view, false otherwise
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent e)
    {
        boolean toReturn = false;

        // Being wary of any null pointers
        if (marker != null && marker.isInfoWindowShown() && mMap != null && infoWindow != null)
        {
            Point point = mMap.getProjection().toScreenLocation(marker.getPosition());
            MotionEvent copyEv = MotionEvent.obtain(e);

            // Adjusting the position of the marker
            copyEv.offsetLocation(-point.x + (infoWindow.getWidth() / 2),
                    -point.y + infoWindow.getHeight() + bottomOffsetInPixels);

            toReturn = infoWindow.dispatchTouchEvent(copyEv);
        }

        // If the infoWindow is interacted with in the touch event, then just return true.
        // Otherwise pass this event to the super class
        return toReturn || super.dispatchTouchEvent(e);
    }
}
