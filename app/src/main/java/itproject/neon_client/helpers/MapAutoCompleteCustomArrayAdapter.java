package itproject.neon_client.helpers;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import itproject.neon_client.R;
import itproject.neon_client.activities.MainActivity;

/**
 * In this class you can customize the appearance of the suggestion drop-down.
 */

public class MapAutoCompleteCustomArrayAdapter extends ArrayAdapter<String>
{
    Context mContext;
    int layoutResourceId;
    String autoCompleteData[];

    public MapAutoCompleteCustomArrayAdapter (Context mContext, int layoutResourceId, String[] data)
    {
        super(mContext, layoutResourceId, data);

        Log.e("Adapter", "In the Adapter constructor");

        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.autoCompleteData = data;
    }

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

        Log.e("Adapter", "object item is " + objectItem);

        // get the TextView and then set the text and tag values
        TextView textViewItem = (TextView) view.findViewById(R.id.search_box);
        textViewItem.setText(objectItem);

        return view;
    }
}
