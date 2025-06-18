package com.example.devheat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.devheat.database.DatabaseHelper;
import com.example.devheat.resources.GetGitHubBio;
import com.example.devheat.resources.GetGitHubPP;
//import com.example.devheat.resources.GetGitHubReadme;
//import com.example.devheat.resources.UpdateGitHubBio;
//import com.example.devheat.resources.UpdateGitHubReadme;
import com.squareup.picasso.Picasso;
//import io.noties.markwon.Markwon;
import androidx.activity.enableEdgeToEdge

class menu : AppCompatActivity() {

    private lateinit var  dbHelper: DatabaseHelper
    private lateinit var layout: LinearLayout
    private lateinit var sharedThemesPref: SharedPreferences
    private lateinit var MyPrefs: SharedPreferences
    private lateinit var btnOpenSettings: Button
    private lateinit var btnMdHistory: Button
    private lateinit var closeMenu: ImageButton
    private lateinit var btnChangeState: ImageButton
    private lateinit var btnChangeMD: ImageButton
    private lateinit var  etBiography: EditText;
    private lateinit var tvUserMenu: TextView
    private lateinit var tvPrincMD: TextView
    private lateinit var ivProfilePicture: ImageView
    private lateinit var imageURL: String
    private lateinit var token: String
    private lateinit var user: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.menu)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.menu)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        btnOpenSettings = findViewById(R.id.btnOpenSettings);
        btnOpenSettings.setOnClickListener {

            intent = Intent(this@menu , settings::class.java);
            startActivity(intent)
        }

        closeMenu = findViewById(R.id.closeMenu);
        closeMenu.setOnClickListener { finish()}

        btnMdHistory = findViewById(R.id.btnMdHistory);
        btnMdHistory.setOnClickListener{
            intent = Intent(this@menu, md_history::class.java);
        }
    }

    /*override fun onResume() {
        super.onResume()
        try{
            //token = dbHelper.getToken(MyPrefs.getInt("loggedID",-1));
            //imageURL = GetGitHubPP().execute(token, user).get();
        }catch(e: Exception){

        }

        if(imageURL != null && !imageURL.isEmpty()){
            Picasso.get()
                .load(imageURL)
                .into(ivProfilePicture);
        }else{
            ivProfilePicture.setImageResource(R.drawable.account_circle_outline);
        }
    }*/
}

