package dk.dtu.lbs.fragments;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import dk.dtu.lbs.activities.R;

/**
 * Created by Bahram on 08-11-2015.
 */
public class SettingsFragment extends PreferenceFragment {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
    }
}
