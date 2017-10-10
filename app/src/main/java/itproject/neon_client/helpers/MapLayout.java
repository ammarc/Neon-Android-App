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


public class MapLayout extends RelativeLayout
{
    private GoogleMap mMap;
    // this is the offset between the bottom of the info
    // window and the the actual Google Maps marker
    private int bottomOffsetInPixels;
    private Marker marker;
    private View infoWindow;


    /*
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

    public void initialize(GoogleMap mMap, int bottomOffsetInPixels)
    {
        this.mMap= mMap;
        this.bottomOffsetInPixels = bottomOffsetInPixels;
    }

    public void setMarkerWithInfoWindow(Marker marker, View infoWindow)
    {
        this.marker = marker;
        this.infoWindow = infoWindow;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent e)
    {
        boolean toReturn = false;
        //Log.e("MapLayout", "Dispatching touch event like a boss!");

        //Log.e("MapLayout", marker.isInfoWindowShown() + " for info window");

        // Being wary of any null pointers
        if (marker != null && marker.isInfoWindowShown() && mMap != null && infoWindow != null)
        {
            Point point = mMap.getProjection().toScreenLocation(marker.getPosition());
            MotionEvent copyEv = MotionEvent.obtain(e);

            // Adjusting the position of the marker
            copyEv.offsetLocation(-point.x + (infoWindow.getWidth() / 2),
                    -point.y + infoWindow.getHeight() + bottomOffsetInPixels);

            toReturn = infoWindow.dispatchTouchEvent(e);
            Button infoButton = (Button)infoWindow.findViewById(R.id.button);
            infoButton.dispatchTouchEvent(e);
            Log.e("MapLayout", "The button to press is: " + infoButton.toString());
            Log.e("MapLayout", "Dispatching touch event that will make me look like a boss!");

        }

        // If the infoWindow is interacted with in the touch event, then just return true.
        // Otherwise pass this event to the super class
        return toReturn || super.dispatchTouchEvent(e);
    }
}
