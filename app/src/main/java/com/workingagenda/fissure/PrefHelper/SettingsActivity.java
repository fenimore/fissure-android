package com.workingagenda.fissure.PrefHelper;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by fen on 8/3/16.
 */
public class SettingsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();

    }
}
