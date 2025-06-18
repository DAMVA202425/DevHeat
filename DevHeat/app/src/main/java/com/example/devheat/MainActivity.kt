package com.example.devheat

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.devheat.database.DatabaseHelper

class MainActivity : AppCompatActivity() {

    private lateinit var btnCreateU: Button
    private lateinit var btnMenu: Button
    private lateinit var menuMain: ImageButton
    private lateinit var layout: LinearLayout
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var sharedThemesPref: SharedPreferences
    private lateinit var MyPrefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        sharedThemesPref = getSharedPreferences("ThemePrefs", MODE_PRIVATE)
        val isDarkTheme: Boolean = sharedThemesPref.getBoolean("isDarkTheme", false)

        AppCompatDelegate.setDefaultNightMode(
            if(isDarkTheme) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )

        initializeUI()

    }

    private fun initializeUI() {
        MyPrefs = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        var editor = MyPrefs.edit()

        btnCreateU = findViewById(R.id.btnCreateU)
        btnCreateU.setOnClickListener() {
            val intent = Intent(this, NewUser::class.java)
            startActivity(intent)
        }

        layout = findViewById(R.id.layout1)

        dbHelper = DatabaseHelper(this)

        var users: List<String> = listOf()
        users = dbHelper.getAllUserNamesList()

        for (user in users) {
            var button: Button = Button(this)
            button.setText(user)
            button.setBackgroundResource(android.R.drawable.btn_default)
            button.setTextColor(getResources().getColor(android.R.color.black))


            var userPassword = dbHelper.getPassword(user)
            Log.i("user", "user = " + user)

            var params: LinearLayout.LayoutParams = LinearLayout.LayoutParams (
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0,0,0,0)
            button.setLayoutParams(params)

            button.setOnClickListener() {
                val id: Int = dbHelper.getID(user)
                editor.putString("user", user)
                editor.putInt("loggedID",id);
                editor.apply();

                var intent: Intent =  Intent(this, SignIn::class.java)
                intent.putExtra("userName", user);
                intent.putExtra("userPassword", userPassword);
                intent.putExtra("userID", id);
                startActivity(intent);
            }

            layout.addView(button)

        }

    }

}