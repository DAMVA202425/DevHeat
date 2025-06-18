package com.example.devheat

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.devheat.database.DatabaseHelper
import com.example.devheat.resources.GetGitHubPP
import com.squareup.picasso.Picasso


class SignIn : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var btnSignIn: Button
    private lateinit var etUserName: EditText
    private lateinit var etPassword: EditText
    private lateinit var textView3: TextView
    private var user: String? = null
    private var token: String? = null
    private lateinit var password: String
    private var imageURL: String ?= null
    private lateinit var extras: Bundle
    private lateinit var sharedThemesPref: SharedPreferences
    private lateinit var MyPrefs: SharedPreferences
    private lateinit var ivPPsignIn: ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.sign_in)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.sign_in)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        MyPrefs = getSharedPreferences("MyPrefs",MODE_PRIVATE);
        dbHelper =  DatabaseHelper(this);
        token = dbHelper.getToken(MyPrefs.getInt("loggedID",-1))
        user = MyPrefs.getString("user", null)

        ivPPsignIn = findViewById(R.id.ivPPsignIn);
        dbHelper = DatabaseHelper(this)

        try {

            imageURL = GetGitHubPP().execute(token, user).get();
            Log.w("imageURL","imageURL = "+ imageURL);
            if(imageURL != null && !imageURL!!.isEmpty()){
                Picasso.get()
                    .load(imageURL)
                    .into(ivPPsignIn);
            }else{
                ivPPsignIn.setImageResource(R.drawable.account_circle_outline);
            }

        } catch(e: Exception) {
            Log.e("ERROR", "Error: " + e.message.toString())
        }

        val intent = intent

        val userName = intent.getStringExtra("userName")
        val userPassword = intent.getStringExtra("userPassword")
        val userID = intent.getIntExtra("userID", -1)

        textView3 = findViewById(R.id.textView3)
        extras = getIntent().extras!!

        if (extras != null) {
            textView3.setText("Welcome, $userName !")
        }

        etUserName = findViewById(R.id.etUserName)
        etPassword = findViewById(R.id.etPassword)

        btnSignIn = findViewById(R.id.btnSignIn)
        btnSignIn.setOnClickListener { view: View? ->
            user = etUserName.text.toString()
            password = etPassword.text.toString()
            if (user == userName && password == userPassword) {
                val editor = MyPrefs.edit()
                editor.putInt("loggedID", userID)
                editor.apply()
                val intent1 = Intent(
                    this@SignIn,
                    Template::class.java
                )
                startActivity(intent1)
            } else {
                Toast.makeText(
                    this@SignIn,
                    "Incorrect password or username.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    public override fun onResume() {
        val intent = intent

        val userName = intent.getStringExtra("userName")


        super.onResume()
        if (dbHelper == null) {
            dbHelper = DatabaseHelper(this)
        }
        try {
            token = dbHelper.getToken(MyPrefs.getInt("loggedID", -1))
            Log.d("Debug", "loggedID: " + MyPrefs.getInt("loggedID", -1))

            Log.d("Debug", "Token: $token")
            imageURL = GetGitHubPP().execute(token, userName).get()
            Log.d("Debug", "ImageURL: $imageURL")
        } catch (e: java.lang.Exception) {
            Log.e("error url", "imageURL = " + imageURL + " y token = " + token + "user = " + user)
            Log.e("Error", "Excepción capturada: " + e.message)
        }

        if (imageURL != null && !imageURL!!.isEmpty()) {
            Picasso.get()
                .load(imageURL)
                .into(ivPPsignIn)
        } else {
            ivPPsignIn.setImageResource(R.drawable.account_circle_outline)
        }
    }

    public override fun onStop() {
        super.onStop()
        imageURL = null
        token = null
        Log.d("Debug", "onStop launched ")
        Picasso.get().invalidate(imageURL)
    }

}