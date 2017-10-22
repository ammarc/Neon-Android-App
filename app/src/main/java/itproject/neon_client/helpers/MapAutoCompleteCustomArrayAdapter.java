package itproject.neon_client.helpers;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import itproject.neon_client.R;
import itproject.neon_client.activities.MainActivity;

/**
 * This class is used to customize the appearance of the suggestion drop-down.
 */
public class MapAutoCompleteCustomArrayAdapter extends ArrayAdapter<String>
{
    public static final String TAG = "Adapter";
    private Context mContext;
    private int layoutResourceId;
    private String autoCompleteData[];

    /**
     * The constructor for this class
     * @param mContext the context under which the adapter was created
     * @param layoutResourceId the ID of the custom row view to display
     * @param data the array of data to be displayed in each row
     */
    public MapAutoCompleteCustomArrayAdapter (Context mContext, int layoutResourceId, String[] data)
    {
        super(mContext, layoutResourceId, data);

        Log.i(TAG, "In the Adapter constructor");

        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.autoCompleteData = data;
    }

    /**
     * This is used by Android to get the length of the data to display
     * @return the length of the array to display
     */
    @Override
    public int getCount() {
        Log.i(TAG, "Count is " + autoCompleteData.length);
        return autoCompleteData.length;
    }

    /**
     * This method is used by Android to get the view to display, therefore it is inflated
     * here if it is null and the value of the item to display is set.
     * @param position view's position
     * @param view the current view
     * @param parent the parent's view group
     * @return the view of the row to display
     */
    @Override
    public View getView(int position, View view, ViewGroup parent)
    {
        if (view == null)
        {
            // inflate the view if it is null
            LayoutInflater inflater = ((MainActivity) mContext).getLayoutInflater();
            view = inflater.inflate(layoutResourceId, parent, false);
        }

        // object item based on the position
        String objectItem = autoCompleteData[position];

        // get the TextView and then set the text and tag values
        TextView textViewItem = (TextView)view.findViewById(R.id.auto_complete_view_item);
        textViewItem.setText(objectItem);

        return view;
    }
}
