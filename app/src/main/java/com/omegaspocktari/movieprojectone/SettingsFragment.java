package com.omegaspocktari.movieprojectone;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;

/**
 * Fragment for settings
 *
 * Created by ${Michael} on 4/13/2017.
 */

public class SettingsFragment extends PreferenceFragmentCompat implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    /**
     * Called during {@link #onCreate(Bundle)} to supply the preferences for this fragment.
     * Subclasses are expected to call {@link #setPreferenceScreen(PreferenceScreen)} either
     * directly or via helper methods such as {@link #addPreferencesFromResource(int)}.
     *
     * @param savedInstanceState If the fragment is being re-created from
     *                           a previous saved state, this is the state.
     * @param rootKey            If non-null, this preference fragment should be rooted at the
     *                           {@link PreferenceScreen} with this key.
     */
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        // Add preferences from the general preferences
        addPreferencesFromResource(R.xml.pref_general);

        // Interface for accessing and modifying preference data
        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();

        // Acquire preference screen
        PreferenceScreen preferenceScreen = getPreferenceScreen();

        // get the amount of preferences and then move through them, getting the preference
        // and applying it as the preference's summary with @setPreferenceSummary method.
        int count = preferenceScreen.getPreferenceCount();
        for (int i = 0; i < count; i++) {
            Preference preference = preferenceScreen.getPreference(i);
            if(!(preference instanceof CheckBoxPreference)) {
                String value = sharedPreferences.getString(preference.getKey(), "");
                setPreferenceSummary(preference, value);
            }
        }
    }

    /**
     * Called when a shared preference is changed, added, or removed. This
     * may be called even if a preference is set to its existing value.
     * <p>
     * <p>This callback will be run on your main thread.
     *
     * @param sharedPreferences The {@link SharedPreferences} that received
     *                          the change.
     * @param key               The key of the preference that was changed, added, or
     */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        // Update preferences if they are changed after initial @onCreatePreferences
        Preference preference = findPreference(key);
        if (null != preference) {
            if (!(preference instanceof CheckBoxPreference)) {
                setPreferenceSummary(preference, sharedPreferences.getString(key, ""));
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Register preferences change listener
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        // Unregister preference change listener
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    // Method to generate preferences summary for ListPreference
    private void setPreferenceSummary(Preference preference, Object value) {
        String stringValue = value.toString();

        // Specifically target ListPreference
        if (preference instanceof ListPreference) {

            // Find the correct index of the given string value
            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(stringValue);

            // Error Checking
            if (prefIndex >= 0) {
                // This shows the labels of the ListPreference
                preference.setSummary(listPreference.getEntries()[prefIndex]);
            } else {
                // For values that aren't ListPreference show the given label
                preference.setSummary(stringValue);
            }
        }
    }


}
