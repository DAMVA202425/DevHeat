package com.example.devheat;

import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.devheat.database.DatabaseHelper;

public class menu extends AppCompatActivity {
    private SharedPreferences sharedPref;
    private Button deleteSP, deleteDB;
    private ImageButton closeMenu;
    private DatabaseHelper dbHelper;
    private Switch switchLM;
    private boolean isDarkTheme = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve the theme preference
        sharedPref = getSharedPreferences("ThemePrefs", MODE_PRIVATE);
        isDarkTheme = sharedPref.getBoolean("isDarkTheme", false);

        // Set the theme before setting the content view
        AppCompatDelegate.setDefaultNightMode(
                isDarkTheme ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );

        setContentView(R.layout.menu);

        // Initialize UI components
        initializeUI();
    }

    private void initializeUI() {

        closeMenu = findViewById(R.id.closeMenu);
        ((View) closeMenu).setOnClickListener(v -> finish());



        deleteSP = findViewById(R.id.deleteSP);
        deleteSP.setOnClickListener(v -> {
            // Implement delete SharedPreferences logic here
        });

        dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        deleteDB = findViewById(R.id.deleteDB);
        deleteDB.setOnClickListener(v -> dbHelper.deleteDB(db));

        switchLM = findViewById(R.id.switchLM);
        switchLM.setChecked(isDarkTheme);
        updateSwitchText();

        switchLM.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isDarkTheme = isChecked;
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean("isDarkTheme", isDarkTheme);
            editor.apply();

            // Change theme without recreating the activity
            AppCompatDelegate.setDefaultNightMode(
                    isDarkTheme ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
            );

            updateSwitchText();
        });
    }

    private void updateSwitchText() {
        if (isDarkTheme) {
            switchLM.setText("Dark mode");
            //Toast.makeText(this, "Checked", Toast.LENGTH_SHORT).show();
        } else {
            switchLM.setText("Light mode");
            //Toast.makeText(this, "Unchecked", Toast.LENGTH_SHORT).show();
        }
    }
}