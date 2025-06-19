package com.example.devheat

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.ContactsContract.Data
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

import com.example.devheat.database.DatabaseHelper
import com.example.devheat.resources.GetGitHubBio
import com.example.devheat.resources.GetGitHubPP
import com.example.devheat.resources.GetGitHubReadme
import com.example.devheat.resources.UpdateGitHubBio
import com.example.devheat.resources.UpdateGitHubReadme
import com.squareup.picasso.Picasso
import io.noties.markwon.Markwon
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
    private var imageURL: String? = null
    private var token: String? = null
    var biography: String? = null
    var starredBio: String? = null
    private var user: String? = null

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

        MyPrefs = getSharedPreferences("MyPrefs", MODE_PRIVATE)

        tvUserMenu = findViewById(R.id.tvUserMenu);
        tvUserMenu.setText(MyPrefs.getString("user",null));

        etBiography = findViewById(R.id.etBiography);
        tvPrincMD = findViewById(R.id.tvPrincMD);

        ivProfilePicture = findViewById(R.id.ivProfilePicture)

        dbHelper = DatabaseHelper(this)

        token = dbHelper.getToken(MyPrefs.getInt("loggedID",-1));
        user = MyPrefs.getString("user", null)
        starredBio = MyPrefs.getString("starredBio", null)


        try {
            imageURL = GetGitHubPP().execute(token, user).get();
        } catch (e: Exception) {
            Log.e("Image error", e.printStackTrace().toString())
        }

        try {
            biography = GetGitHubBio().execute(token, user).get();
        } catch (e: Exception) {
            Log.e("Biography error", "No biography found");
        }

        try {
            var readme: String? = GetGitHubReadme().execute(token, user).get()
            var markwon: Markwon = Markwon.create(this)
            val SC_StarredBio = starredBio //For SmartCast
            if(!SC_StarredBio.isNullOrEmpty()){
                markwon.setMarkdown(tvPrincMD, starredBio.toString());
            } else if (!readme.isNullOrEmpty()) {
                markwon.setMarkdown(tvPrincMD, readme);
            }


        } catch (e: Exception) {
            Log.e("Main Readme error", "No readme found");
        }

        etBiography.setText(biography);



        btnChangeState = findViewById(R.id.btnChangeState);
        btnChangeState.setOnClickListener{
            var biography2 = etBiography.getText().toString();

            if(token != null && user!=null){
                UpdateGitHubBio().execute(token, user, biography2);
                Toast.makeText(this@menu,"Biography changed!", Toast.LENGTH_SHORT).show();
            }
        }

        btnChangeMD = findViewById(R.id.btnChangeMD);
        btnChangeMD.setOnClickListener{
            val SCstarredBio = starredBio

            if(!SCstarredBio.isNullOrEmpty()){
                UpdateGitHubReadme().execute(token, user, starredBio);
                Toast.makeText(this@menu, "Readme from Github changed!", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this@menu, "You need to save a new bio first.", Toast.LENGTH_SHORT).show();
            }

        }
    }

    override fun onResume() {
        super.onResume()
        try{
            token = dbHelper.getToken(MyPrefs.getInt("loggedID",-1))
            imageURL = GetGitHubPP().execute(token, user).get()
        }catch(e: Exception){

        }

        val url = imageURL

        if(!url.isNullOrEmpty()){
            Picasso.get()
                .load(imageURL)
                .into(ivProfilePicture)
        }else{
            ivProfilePicture.setImageResource(R.drawable.account_circle_outline);
        }
    }
}

