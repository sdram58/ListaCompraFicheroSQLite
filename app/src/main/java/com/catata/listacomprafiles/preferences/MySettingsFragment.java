package com.catata.listacomprafiles.preferences;
import android.os.Bundle;
import android.util.Log;

import androidx.preference.EditTextPreference;
import androidx.preference.PreferenceFragmentCompat;

import com.catata.listacomprafiles.R;

public class MySettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings, rootKey);

/*        EditTextPreference editTextPreference = findPreference("edit_text_preference_1");

        Log.i("PREFERENCIA",editTextPreference.getText());*/
    }
}