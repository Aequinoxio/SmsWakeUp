package com.example.utente.smswakeup;

import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.annotation.BoolRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SimpleSettingsActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    public static String KEY_BOOL = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.activity_simple_settings);

        KEY_BOOL = getString(R.string.shared_wakeUpOnlyFromNumber);
        addPreferencesFromResource(R.xml.preferences);

        setSummary();
    }


    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                          String key) {

        Preference connectionPref = findPreference(key);

        // Set summary to be the user-description for the selected value
        String s;

        switch (key){
            case ("msgBody"):
                s=getString(R.string.msgWakeUp);
                break;
            case ("msgFromNumber"):
                s=getString(R.string.msgFromNumber);
                break;
            case ("secsWait"):
                s=getString(R.string.shared_secsWait);
                break;
            default:
                s="";
        }

        if (!key.equals(KEY_BOOL)) {
            connectionPref.setSummary(sharedPreferences.getString(key, s));
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    private void setSummary(){
        Preference connectionPref = findPreference(ApplicationSettings.getMsgBodyKey());
        connectionPref.setSummary(ApplicationSettings.getMsgBody());

        connectionPref = findPreference(ApplicationSettings.getMsgFromNumberKey());
        connectionPref.setSummary(ApplicationSettings.getMsgFromNumber());

        connectionPref = findPreference(ApplicationSettings.getSecsWaitSoundKey());
        connectionPref.setSummary(String.valueOf(ApplicationSettings.getSecsWaitSound()));
    }
}
