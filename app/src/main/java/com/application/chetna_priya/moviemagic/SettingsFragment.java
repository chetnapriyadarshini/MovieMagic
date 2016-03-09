package com.application.chetna_priya.moviemagic;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A placeholder fragment containing a simple view.
 */
public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

    private static final String LOG_TAG = SettingsFragment.class.getSimpleName();

    public SettingsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "CALLED ON CREATEEEEEEEE");
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_general);
        bindPreferenceSummarytoValue(findPreference(getString(R.string.pref_sort_by_key)));
    }

    private void bindPreferenceSummarytoValue(Preference preference) {
        preference.setOnPreferenceChangeListener(this);
        onPreferenceChange(preference, PreferenceManager.getDefaultSharedPreferences(preference.getContext())
                .getString(preference.getKey(), ""));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {

        String summary = newValue.toString();
        if(preference instanceof ListPreference)
        {
            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(summary);
            if(prefIndex >= 0)
            {
                Log.d(LOG_TAG, "Set summary!!!!!!!!!!!!!!! " + prefIndex);
                preference.setSummary(listPreference.getEntries()[prefIndex]);
            }
            else

                Log.d(LOG_TAG," NOTTTTTTTTT Set summary!!!!!!!!!!!!!!! "+prefIndex);
        }
        return true;
    }
}
