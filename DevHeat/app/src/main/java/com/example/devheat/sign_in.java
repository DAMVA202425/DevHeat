package com.example.devheat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.devheat.database.DatabaseHelper;
import com.example.devheat.resources.GetGitHubPP;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

public class sign_in extends AppCompatActivity {

    //private DatabaseHelper dbHelper;
    private Button btnSignIn;
    private EditText etUserName, etPassword;
    private TextView textView3;
    private String user, password;
    private Bundle extras;
    private SharedPreferences sharedPref;
    private ImageView ivPPsignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.sign_in);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.sign_in), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sharedPref = getSharedPreferences("MyPrefs",MODE_PRIVATE);
        user = sharedPref.getString("user", null);
        ivPPsignIn = findViewById(R.id.ivPPsignIn);
        String imageURL = null;
        try {

            String token ="none";
            Toast.makeText(this, "user = " + user, Toast.LENGTH_SHORT).show();
            imageURL = new GetGitHubPP().execute(token, user).get();
            Log.w("imageURL","imageURL = "+ imageURL);
            if(imageURL != null && !imageURL.isEmpty()){
                Picasso.get()
                        .load(imageURL)
                        .into(ivPPsignIn);
            }else{
                ivPPsignIn.setImageResource(R.drawable.account_circle_outline);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        Intent intent = getIntent();

        String userName = intent.getStringExtra("userName");
        String userPassword = intent.getStringExtra("userPassword");
        int userID = intent.getIntExtra("userID",-1);

        textView3 = findViewById(R.id.textView3);
        extras = getIntent().getExtras();

        if(extras != null){
            textView3.setText("Welcome, " + userName+" !");
        }

        etUserName = findViewById(R.id.etUserName);
        etPassword = findViewById(R.id.etPassword);

        btnSignIn = findViewById(R.id.btnSignIn);
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user = etUserName.getText().toString();
                password = etPassword.getText().toString();

                if(user.equals(userName) && password.equals(userPassword)){
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putInt("loggedID",userID);
                    Toast.makeText(sign_in.this, "userID = "+userID,Toast.LENGTH_SHORT).show();
                    editor.apply();
                    Intent intent = new Intent(sign_in.this, template.class);
                    startActivity(intent);

                }else{
                    Toast.makeText(sign_in.this, "Incorrect password or username.",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
