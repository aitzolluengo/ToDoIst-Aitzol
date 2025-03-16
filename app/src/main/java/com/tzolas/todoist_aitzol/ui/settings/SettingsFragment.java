package com.tzolas.todoist_aitzol.ui.settings;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreferenceCompat;
import com.tzolas.todoist_aitzol.R;
import java.util.Locale;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        // ✅ Modo Oscuro
        SwitchPreferenceCompat darkModePref = findPreference("pref_dark_mode");
        if (darkModePref != null) {
            darkModePref.setOnPreferenceChangeListener((preference, newValue) -> {
                boolean isDarkMode = (boolean) newValue;
                setDarkMode(isDarkMode);
                return true;
            });
        }

        // ✅ Cambio de idioma
        ListPreference languagePref = findPreference("pref_language");
        if (languagePref != null) {
            languagePref.setOnPreferenceChangeListener((preference, newValue) -> {
                setAppLocale((String) newValue);
                return true;
            });
        }
    }

    private void setDarkMode(boolean isEnabled) {
        int nightMode = isEnabled ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO;
        AppCompatDelegate.setDefaultNightMode(nightMode);
    }

    private void setAppLocale(String languageCode) {

        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(requireContext()).edit();
        editor.putString("pref_language", languageCode);
        editor.apply();


        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);


        Context context = requireActivity().getBaseContext().getApplicationContext().createConfigurationContext(config);


        requireActivity().finish();
        requireActivity().startActivity(requireActivity().getIntent());
    }



}
