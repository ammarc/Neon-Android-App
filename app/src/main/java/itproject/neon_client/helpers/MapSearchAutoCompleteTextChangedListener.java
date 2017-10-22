package itproject.neon_client.helpers;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

import java.util.ArrayList;

import itproject.neon_client.R;
import itproject.neon_client.activities.MainActivity;

/**
 * Each time the user types a character, the app queries the user name list
 * and updates the customized ArrayAdapter.
 */

/**
 * This class is essentially used to 'listen' to what the user is typing
 * and then show the appropriate suggestions to them
 */
public class MapSearchAutoCompleteTextChangedListener implements TextWatcher
{
    Context context;
    ArrayList<String> suggestionsList;
    public static String TAG = "AutoCompleteListener";

    /**
     * The constructor for this class
     * @param context the context from which this is called
     */
    public MapSearchAutoCompleteTextChangedListener(Context context)
    {
        this.context = context;
        this.suggestionsList = new ArrayList<>();
    }

    /**
     * This method is essentially used to show the custom auto-complete view created to the
     * user after filling it with the list of the usernames that match with what the user
     * is currently typing.
     * @param userInput what the user has input till now
     * @param start the beginning of the characters the user has input
     * @param before the replaced-text's length
     * @param count how much the user has typed
     */
    @Override
    public void onTextChanged(CharSequence userInput, int start, int before, int count)
    {
        Log.i(TAG, "User input " + userInput);
        MainActivity mainActivity = ((MainActivity) context);

        // update the adapter
        mainActivity.getAutoCompleteArrayAdapter().notifyDataSetChanged();

        String[] objects = mainActivity.getListOfAllMarkers();
        for(int i = 0; i < objects.length; i++)
        {
            // if they match the pattern, then add them
            if(objects[i].toLowerCase().contains(userInput.toString().toLowerCase()))
                suggestionsList.add(objects[i]);
        }


        String[] suggestionsArr = new String[suggestionsList.size()];
        suggestionsArr = suggestionsList.toArray(suggestionsArr);

        mainActivity.setAutoCompleteArrayAdapter(new MapAutoCompleteCustomArrayAdapter(mainActivity,
                                                R.layout.auto_complete_view_row, suggestionsArr));

        mainActivity.getMapSearchAutoCompleteView().setAdapter(mainActivity.
                                                            getAutoCompleteArrayAdapter());

        // clear the array as we are already done with it
        suggestionsList.clear();
    }

    @Override
    public void afterTextChanged(Editable s) {}

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
}
