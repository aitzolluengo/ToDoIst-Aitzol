package com.tzolas.todoist_aitzol.ui;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.preference.PreferenceManager;

import com.google.android.material.navigation.NavigationView;
import com.tzolas.todoist_aitzol.R;
import com.tzolas.todoist_aitzol.ui.completedtask.CompletedTasksFragment;
import com.tzolas.todoist_aitzol.ui.settings.SettingsFragment;
import com.tzolas.todoist_aitzol.ui.tasklist.TaskListFragment;

import java.util.Locale;



public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String language = prefs.getString("pref_language", "default");
        setAppLocale(language);
        applySavedLocale();

        boolean isDarkMode = prefs.getBoolean("pref_dark_mode", false);
        setDarkMode(isDarkMode);


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new TaskListFragment())
                    .commit();
            navigationView.setCheckedItem(R.id.nav_tasks);
        }
    }
    private void applySavedLocale() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String languageCode = prefs.getString("pref_language", "default");

        if (!languageCode.equals("default")) {
            Locale locale = new Locale(languageCode);
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.setLocale(locale);
            getResources().updateConfiguration(config, getResources().getDisplayMetrics());
        }
    }


    private void setAppLocale(String languageCode) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String currentLanguage = prefs.getString("pref_language", "default");

        // ⚠️ Solo recrear si el idioma ha cambiado
        if (!currentLanguage.equals(languageCode)) {
            Locale locale = languageCode.equals("default") ? Locale.getDefault() : new Locale(languageCode);
            Locale.setDefault(locale);

            Configuration config = new Configuration();
            config.setLocale(locale);

            getApplicationContext().createConfigurationContext(config);

            // Guarda el nuevo idioma en SharedPreferences
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("pref_language", languageCode);
            editor.apply();

            recreate(); // ✅ Ahora solo se reinicia si realmente cambió el idioma
        }
    }




    private void setDarkMode(boolean isEnabled) {
        int nightMode = isEnabled ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO;
        AppCompatDelegate.setDefaultNightMode(nightMode);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_tasks) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new TaskListFragment())
                    .commit();
        } else if (id == R.id.nav_completed) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new CompletedTasksFragment())
                    .commit();
        } else if (id == R.id.nav_settings) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new SettingsFragment())
                    .commit();
            // Implementar fragmento de configuración
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
