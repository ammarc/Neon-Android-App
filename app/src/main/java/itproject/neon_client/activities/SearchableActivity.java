package itproject.neon_client.activities;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import itproject.neon_client.R;


public class SearchableActivity extends Activity
{
    ArrayList<MarkerOptions> listToSearch;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_searchable);

        // Get the intent, verify the action and get the query
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            doMySearch(query);
        }
    }


    public MarkerOptions doMySearch(String query)
    {
        for (MarkerOptions m : listToSearch)
        {
            if (m.getTitle().equals(query))
                return m;
        }
        return null;
    }
}
