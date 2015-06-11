package com.dailymotion.sampleapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import com.dailymotion.sampleapp.R;

public class ThePreferenceActivity extends PreferenceActivity {
    final Preference.OnPreferenceChangeListener mListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    updatePreferences();
                }
            });
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);

        updatePreferences();

        findPreference(getString(R.string.keyPlayerBaseUrl)).setOnPreferenceChangeListener(mListener);
        findPreference(getString(R.string.keyForceVideoId)).setOnPreferenceChangeListener(mListener);
        findPreference(getString(R.string.keyVideoId)).setOnPreferenceChangeListener(mListener);
        findPreference(getString(R.string.keyExtraParameters)).setOnPreferenceChangeListener(mListener);
    }

    private void updatePreferences() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        Preference pref = findPreference(getString(R.string.keyPlayerBaseUrl));
        pref.setSummary(prefs.getString(getString(R.string.keyPlayerBaseUrl), ""));

        boolean forceVideoId = ((CheckBoxPreference)findPreference(getString(R.string.keyForceVideoId))).isChecked();

        pref = findPreference(getString(R.string.keyVideoId));
        if (forceVideoId) {
            pref.setEnabled(true);
        } else {
            pref.setEnabled(false);
        }

        pref.setSummary(prefs.getString(getString(R.string.keyVideoId), ""));

        pref = findPreference(getString(R.string.keyExtraParameters));
        pref.setSummary(prefs.getString(getString(R.string.keyExtraParameters), ""));

    }
}
