package itproject.neon_client.helpers;

import android.content.Context;
import android.util.AttributeSet;
import android.support.v7.widget.AppCompatAutoCompleteTextView;

/**
 * This class describes the custom view for how the auto-complete
 * window will be displayed in the search box in the map.
 */
public class MapSearchAutoCompleteView extends AppCompatAutoCompleteTextView
{
    public MapSearchAutoCompleteView (Context context)
    {
        super(context);
    }

    public MapSearchAutoCompleteView (Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public MapSearchAutoCompleteView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    @Override
    protected void performFiltering(final CharSequence text, final int keyCode) {
        String filterText = "";
        super.performFiltering(filterText, keyCode);
    }
}
