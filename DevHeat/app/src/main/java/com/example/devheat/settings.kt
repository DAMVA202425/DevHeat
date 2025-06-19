package com.example.devheat

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat



import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.Drawable;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDelegate;

import com.example.devheat.database.DatabaseHelper;

class settings : AppCompatActivity() {

    private lateinit var sharedThemesPref: SharedPreferences
    private lateinit var MyPrefs: SharedPreferences
    private lateinit var deleteSP: Button
    private lateinit var deleteDB: Button
    private lateinit var btnMdHistory: Button
    private lateinit var closeMenu: ImageButton
    private lateinit var ibtnSaveToken: ImageButton
    private lateinit var imageLightMode: ImageView
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var switchLM: Switch;
    private lateinit var etToken: EditText
    private var isDarkTheme: Boolean = false;
    private var isChecked: Boolean = false;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedThemesPref = getSharedPreferences("ThemePrefs", MODE_PRIVATE);
        MyPrefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        isDarkTheme = sharedThemesPref.getBoolean("isDarkTheme", false);

        enableEdgeToEdge()

        AppCompatDelegate.setDefaultNightMode(
            if (isDarkTheme) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )

        setContentView(R.layout.settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.settings)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initializeUI()
    }


    private fun initializeUI() {
        imageLightMode = findViewById(R.id.imageLightMode);
        if(isDarkTheme){
            var drawableResourceId: Int = getResources().getIdentifier("night", "drawable", getPackageName());
            var drawable: Drawable = getResources().getDrawable(drawableResourceId);
            imageLightMode.setImageDrawable(drawable);
            imageLightMode.setImageTintList(ColorStateList.valueOf(Color.WHITE));
        }else{
            var drawableResourceId: Int = getResources().getIdentifier("sun", "drawable", getPackageName());
            var drawable: Drawable = getResources().getDrawable(drawableResourceId);
            imageLightMode.setImageDrawable(drawable);
            imageLightMode.setImageTintList(ColorStateList.valueOf(Color.BLACK));
        }

        closeMenu = findViewById(R.id.closeMenu);
        closeMenu.setOnClickListener{ finish() }

        etToken = findViewById(R.id.etToken);

        ibtnSaveToken = findViewById(R.id.ibtnSaveToken);
        ibtnSaveToken.setOnClickListener{
            var token: String = etToken.getText().toString();

            var loggedID: Int = MyPrefs.getInt("loggedID",-1);
            dbHelper = DatabaseHelper(this);

            dbHelper.updateToken(loggedID, token);
            Toast.makeText(this@settings, "Token saved.", Toast.LENGTH_SHORT).show();

        }


        deleteSP = findViewById(R.id.deleteSP)  //Deletes [sharedThemesPref] and [MyPrefs] shared preferences
        deleteSP.setOnClickListener{

            var themesEditor = sharedThemesPref.edit();
            themesEditor.clear(); themesEditor.apply();

            MyPrefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            var MyPrefsEditor = MyPrefs.edit();
            MyPrefsEditor.clear(); MyPrefsEditor.apply();

        }

        deleteDB = findViewById(R.id.deleteDB);
        deleteDB.setOnClickListener{
            dbHelper = DatabaseHelper(this);
            var db = dbHelper.getReadableDatabase(); //Truncates users
            dbHelper.onUpgrade(db,0,1);   // and history of created Marckdown files

            Toast.makeText(this,"Database restored succesfully",Toast.LENGTH_SHORT).show();
        }


        btnMdHistory = findViewById(R.id.btnMdHistory)
        btnMdHistory.setOnClickListener{
            intent = Intent(this@settings, md_history::class.java);
            startActivity(intent);
        }


        switchLM = findViewById(R.id.switchLM);
        switchLM.setChecked(isDarkTheme);
        updateSwitchText();

        switchLM.setOnCheckedChangeListener{ _, isChecked ->
            isDarkTheme = isChecked
            var editor = sharedThemesPref.edit()
            editor.putBoolean("isDarkTheme", isDarkTheme)
            editor.apply()


            AppCompatDelegate.setDefaultNightMode(
                if (isDarkTheme) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            )

            updateSwitchText()
        }
    }



    private fun updateSwitchText() {
        if (isDarkTheme) {
            switchLM.setText("Dark mode");
            Log.i("Light theme", "Light theme changed");
        } else {
            switchLM.setText("Light mode");

        }
    }

}