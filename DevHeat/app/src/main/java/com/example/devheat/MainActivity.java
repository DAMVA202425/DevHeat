package com.example.devheat;

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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.devheat.database.DatabaseHelper;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button btnCreateU, btnMenu;
    private ImageButton menuMain;
    private LinearLayout layout;
    private DatabaseHelper dbHelper;
    private boolean isDarkTheme;
    private SharedPreferences sharedThemesPref, MyPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //sharedThemesPref = getSharedPreferences("ThemePrefs", MODE_PRIVATE);
        //isDarkMode = sharedThemesPref.getBoolean("isDarkMode", false);
        //setTheme(isDarkMode ? R.style.Theme_DevHeat_Dark : R.style.Theme_DevHeat_Light);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Retrieve the theme preference
        sharedThemesPref = getSharedPreferences("ThemePrefs", MODE_PRIVATE);
        isDarkTheme = sharedThemesPref.getBoolean("isDarkTheme", false);

        // Set the theme before setting the content view
        AppCompatDelegate.setDefaultNightMode(
                isDarkTheme ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );

        initializeUI();

    }

    @Override
    protected void onResume(){
        super.onResume();
        layout.removeAllViews();
        dbHelper = new DatabaseHelper(this);
        dbHelper.getAllUsers();

        List<String> users = dbHelper.getAllUserNamesList();
        for(String user : users) {
            Button button = new Button(this);
            button.setText(user);
            button.setBackgroundResource(android.R.drawable.btn_default);
            button.setTextColor(getResources().getColor(android.R.color.black));
            String userPassword = dbHelper.getPassword(user);


            Log.e("user", "user = " + user);
            //Log.e("id", "id = " + id);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 0, 0, 0);
            button.setLayoutParams(params);
            button.setOnClickListener(v -> {
                MyPrefs = getSharedPreferences("MyPrefs",MODE_PRIVATE);
                SharedPreferences.Editor editor = MyPrefs.edit();
                int id = dbHelper.getID(user);
                editor.putString("user", user);
                editor.putInt("loggedID",id);
                editor.apply();

                Intent intent = new Intent(MainActivity.this, sign_in.class);
                intent.putExtra("userName", user);
                intent.putExtra("userPassword", userPassword);
                intent.putExtra("userID", id);
                startActivity(intent);

            });
            layout.addView(button);

        }
    }

    private void initializeUI(){
        MyPrefs = getSharedPreferences("MyPrefs",MODE_PRIVATE);
        SharedPreferences.Editor editor = MyPrefs.edit();

        btnCreateU = findViewById(R.id.btnCreateU);
        btnCreateU.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, new_user.class);
            startActivity(intent);
        });
        layout = findViewById(R.id.layout1);

        dbHelper = new DatabaseHelper(this);

        List<String> users = dbHelper.getAllUserNamesList();
        for (String user : users) {
            Button button = new Button(this);
            button.setText(user);
            button.setBackgroundResource(android.R.drawable.btn_default);
            button.setTextColor(getResources().getColor(android.R.color.black));

            String userPassword = dbHelper.getPassword(user);

            Log.e("user", "user = " + user);
            //Log.e("id", "id = " + id);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 0, 0, 0);
            button.setLayoutParams(params);

            button.setOnClickListener(v -> {
                int id = dbHelper.getID(user);
                editor.putString("user", user);
                editor.putInt("loggedID",id);
                editor.apply();

                Intent intent = new Intent(MainActivity.this, sign_in.class);
                intent.putExtra("userName", user);
                intent.putExtra("userPassword", userPassword);
                intent.putExtra("userID", id);
                startActivity(intent);
            });

            layout.addView(button);
        }


    }
    }
