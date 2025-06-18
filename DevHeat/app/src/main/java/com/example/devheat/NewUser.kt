package com.example.devheat


import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge

import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.devheat.database.DatabaseHelper


class NewUser : AppCompatActivity() {


    private lateinit var exitNewUser: Button
    private lateinit var btnCreateUser: Button
    private lateinit var etNewUsername: EditText
    private lateinit var etNewPassword: EditText
    private lateinit var etNewPassword2: EditText
    private lateinit var dbHelper: DatabaseHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.new_user)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.new_user)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        etNewUsername = findViewById(R.id.etNewUsername)
        etNewPassword = findViewById(R.id.etNewPassword)
        etNewPassword2 = findViewById(R.id.etNewPassword2)
        dbHelper = DatabaseHelper(this)


        btnCreateUser = findViewById(R.id.btnCreateUser)
        btnCreateUser.setOnClickListener {
            val user = etNewUsername.text.toString()
            val newPassword = etNewPassword.text.toString()
            val newPassword2 = etNewPassword2.text.toString()


            if (newPassword == newPassword2) {
                val inserted = dbHelper.insertUser(user, newPassword)
                if (inserted) {
                    Toast.makeText(this, "User: $user created.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Error creating the user.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Enter the same password twice", Toast.LENGTH_SHORT).show()
            }
        }


        exitNewUser = findViewById(R.id.exitnew_user)
        exitNewUser.setOnClickListener {
            finish()
        }
    }
}

