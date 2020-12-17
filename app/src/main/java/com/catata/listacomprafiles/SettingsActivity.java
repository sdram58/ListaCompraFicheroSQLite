package com.catata.listacomprafiles;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.catata.listacomprafiles.preferences.MySettingsFragment;

public class SettingsActivity extends AppCompatActivity {
    public static final String KEY_MONEDA = "key_moneda";
    public static final String KEY_ALMACENAMIENTO = "key_almacenamiento";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        setTitle("Settings");

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, new MySettingsFragment())
                .commit();
    }
}