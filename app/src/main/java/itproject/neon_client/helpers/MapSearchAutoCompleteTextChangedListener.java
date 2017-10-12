package itproject.neon_client.helpers;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

import java.util.ArrayList;
import java.util.regex.Pattern;

import itproject.neon_client.R;
import itproject.neon_client.activities.MainActivity;

/**
 * Each time the user types a character, it queries the user name list
 * and updates the customized ArrayAdapter.
 */

public class MapSearchAutoCompleteTextChangedListener implements TextWatcher
{
    Context context;
    ArrayList<String> suggestionsList;
    public static String TAG = "MapSearchAutoComplete";

    public MapSearchAutoCompleteTextChangedListener(Context context)
    {
        this.context = context;
        this.suggestionsList = new ArrayList<>();
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
        String[] objects = mainActivity.getListOfAllMarkers();
        for(int i = 0; i < objects.length; i++) {
            // if they match the pattern, then add them
            if(objects[i].toLowerCase().contains(userInput.toString().toLowerCase()))
                suggestionsList.add(objects[i]);
        }

        //Log.e("onTextChanged", "onTextChanged: " + suggestionsList.toString());

        // update the adapter


        String[] suggestionsArr = new String[suggestionsList.size()];
        suggestionsArr = suggestionsList.toArray(suggestionsArr);

        mainActivity.setAutoCompleteArrayAdapter(new MapAutoCompleteCustomArrayAdapter(mainActivity,
                                                        R.layout.auto_complete_view_row, suggestionsArr));

        mainActivity.getMapSearchAutoCompleteView().setAdapter(mainActivity.getAutoCompleteArrayAdapter());

        // clear the array as we are already done with it
        suggestionsList.clear();
    }
}
