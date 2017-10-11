package itproject.neon_client.helpers;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

import itproject.neon_client.R;
import itproject.neon_client.activities.MainActivity;

/**
 * Each time the user types a character, it queries the user name list
 * and updates the customized ArrayAdapter.
 */

public class MapSearchAutoCompleteTextChangedListener implements TextWatcher
{
    Context context;

    public MapSearchAutoCompleteTextChangedListener(Context context)
    {
        this.context = context;
    }

    @Override
    public void afterTextChanged(Editable s) {}

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void onTextChanged(CharSequence userInput, int start, int before, int count)
    {
        Log.e("AutoCompleteListener", "User input " + userInput);
        MainActivity mainActivity = ((MainActivity) context);

        // update the adapter
        mainActivity.getAutoCompleteArrayAdapter().notifyDataSetChanged();

        // get suggestions from the database
        String[] myObjs = mainActivity.getListOfAllMarkers();

        Log.e("onTextChanged", "onTextChanged: " + myObjs.toString());

        // update the adapter
        mainActivity.setAutoCompleteArrayAdapter(new MapAutoCompleteCustomArrayAdapter(mainActivity,
                                                        R.layout.auto_complete_view_row, myObjs));

        mainActivity.getMapSearchAutoCompleteView().setAdapter(mainActivity.getAutoCompleteArrayAdapter());
    }
}
