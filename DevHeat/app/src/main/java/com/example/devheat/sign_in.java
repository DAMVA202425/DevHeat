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

    private DatabaseHelper dbHelper;
    private Button btnSignIn;
    private EditText etUserName, etPassword;
    private TextView textView3;
    private String user, password, token, imageURL;
    private Bundle extras;
    private SharedPreferences sharedThemesPref, MyPrefs;
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

        MyPrefs = getSharedPreferences("MyPrefs",MODE_PRIVATE);
        dbHelper = new DatabaseHelper(this);
        token = dbHelper.getToken(MyPrefs.getInt("loggedID",-1));
        user = MyPrefs.getString("user", null);
        ivPPsignIn = findViewById(R.id.ivPPsignIn);
        dbHelper = new DatabaseHelper(this);

        try {
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
                    SharedPreferences.Editor editor = MyPrefs.edit();
                    editor.putInt("loggedID",userID);
                    editor.apply();
                    Intent intent = new Intent(sign_in.this, template.class);
                    startActivity(intent);

                }else{
                    Toast.makeText(sign_in.this, "Incorrect password or username.",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public void onResume(){
        super.onResume();  //probar con user
        if (dbHelper == null) {
            dbHelper = new DatabaseHelper(this);
        }
        try{

            token = dbHelper.getToken(MyPrefs.getInt("loggedID",-1));
            Log.d("Debug", "Token: " + token);
            imageURL = new GetGitHubPP().execute(token, user).get();
            Log.d("Debug", "ImageURL: " + imageURL);
        }catch(Exception e){
            Log.e("error url", "imageURL = "+ imageURL +" y token = " + token+ "user = "+ user);
            Log.e("Error", "Excepción capturada: " + e.getMessage());
        }

        if(imageURL != null && !imageURL.isEmpty()){
            Picasso.get()
                    .load(imageURL)
                    .into(ivPPsignIn);
        }else{
            ivPPsignIn.setImageResource(R.drawable.account_circle_outline);

        }
    }

    @Override
    public void onStop(){
        super.onStop();
        imageURL = null;
        token = null;
        Log.e("Error", "onStop launched ");
        Picasso.get().invalidate(imageURL);
    }

}
