package com.example.devheat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.devheat.database.DatabaseHelper;

public class settings extends AppCompatActivity {
    private SharedPreferences sharedThemesPref, MyPrefs;
    private Button deleteSP, deleteDB, btnMdHistory;
    private ImageButton closeMenu, ibtnSaveToken;
    private ImageView imageLightMode;
    private DatabaseHelper dbHelper;
    private Switch switchLM;
    private EditText etToken;
    private boolean isDarkTheme = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve the theme preference
        sharedThemesPref = getSharedPreferences("ThemePrefs", MODE_PRIVATE);
        MyPrefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        isDarkTheme = sharedThemesPref.getBoolean("isDarkTheme", false);

        // Set the theme before setting the content view
        AppCompatDelegate.setDefaultNightMode(
                isDarkTheme ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );

        setContentView(R.layout.settings);

        // Initialize UI components
        initializeUI();
    }

    private void initializeUI() {
        imageLightMode = findViewById(R.id.imageLightMode);
        if(isDarkTheme){
            int drawableResourceId = getResources().getIdentifier("night", "drawable", getPackageName());
            Drawable drawable = getResources().getDrawable(drawableResourceId);
            imageLightMode.setImageDrawable(drawable);
            imageLightMode.setImageTintList(ColorStateList.valueOf(Color.WHITE));
        }else{
            int drawableResourceId = getResources().getIdentifier("sun", "drawable", getPackageName());
            Drawable drawable = getResources().getDrawable(drawableResourceId);
            imageLightMode.setImageDrawable(drawable);
            imageLightMode.setImageTintList(ColorStateList.valueOf(Color.BLACK));
        }

        closeMenu = findViewById(R.id.closeMenu);
        ((View) closeMenu).setOnClickListener(v -> finish());

        etToken = findViewById(R.id.etToken);

        ibtnSaveToken = findViewById(R.id.ibtnSaveToken);
        ibtnSaveToken.setOnClickListener(v -> {
            String token = etToken.getText().toString();

            int loggedID = MyPrefs.getInt("loggedID",-1);
            dbHelper = new DatabaseHelper(this);

            dbHelper.updateToken(loggedID, token);
            Toast.makeText(settings.this, "Token saved.", Toast.LENGTH_SHORT).show();

        });


        deleteSP = findViewById(R.id.deleteSP);  //Deletes [sharedThemesPref] and [MyPrefs] shared preferences
        deleteSP.setOnClickListener(v -> {

            SharedPreferences.Editor themesEditor = sharedThemesPref.edit();
            themesEditor.clear(); themesEditor.apply();

            MyPrefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            SharedPreferences.Editor MyPrefsEditor = MyPrefs.edit();
            MyPrefsEditor.clear(); MyPrefsEditor.apply();

        });

        deleteDB = findViewById(R.id.deleteDB);
        deleteDB.setOnClickListener(v -> {
            dbHelper = new DatabaseHelper(this);
            SQLiteDatabase db = dbHelper.getReadableDatabase(); //Truncates users
            dbHelper.onUpgrade(db,0,1);   // and history of created Marckdown files

            Toast.makeText(this,"Database restored succesfully",Toast.LENGTH_SHORT).show();
        });


        btnMdHistory = findViewById(R.id.btnMdHistory);
        btnMdHistory.setOnClickListener(v -> {
            Intent intent = new Intent(settings.this, md_history.class);
            startActivity(intent);
        });


        switchLM = findViewById(R.id.switchLM);
        switchLM.setChecked(isDarkTheme);
        updateSwitchText();

        switchLM.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isDarkTheme = isChecked;
            SharedPreferences.Editor editor = sharedThemesPref.edit();
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
            Log.i("Light theme", "Light theme changed");
        } else {
            switchLM.setText("Light mode");

        }
    }

}