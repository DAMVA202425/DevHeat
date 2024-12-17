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
import com.example.devheat.resources.GetGitHubReadme;
import com.example.devheat.resources.UpdateGitHubBio;
import com.example.devheat.resources.UpdateGitHubReadme;
import com.squareup.picasso.Picasso;
import io.noties.markwon.Markwon;

public class menu extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private LinearLayout layout;
    private SharedPreferences sharedThemesPref, MyPrefs;
    private Button btnOpenSettings, btnMdHistory;
    private ImageButton closeMenu, btnChangeState, btnChangeMD;
    private EditText etBiography;
    private TextView tvUserMenu, tvPrincMD;
    private ImageView ivProfilePicture;
    String imageURL, token, user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.menu), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        MyPrefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);


        btnOpenSettings = findViewById(R.id.btnOpenSettings);
        btnOpenSettings.setOnClickListener(new View.OnClickListener(){
           @Override
           public void onClick(View v){

               Intent intent = new Intent(menu.this, settings.class);
               startActivity(intent);

           }
        });

        closeMenu = findViewById(R.id.closeMenu);
        closeMenu.setOnClickListener(v -> finish());

        btnMdHistory = findViewById(R.id.btnMdHistory);
        btnMdHistory.setOnClickListener(v -> {
           Intent intent = new Intent(menu.this, md_history.class);
       });

        tvUserMenu = findViewById(R.id.tvUserMenu);
        tvUserMenu.setText(MyPrefs.getString("user",null));

        etBiography = findViewById(R.id.etBiography);
        tvPrincMD = findViewById(R.id.tvPrincMD);

        ivProfilePicture = findViewById(R.id.ivProfilePicture);

        dbHelper = new DatabaseHelper(this);

        token = dbHelper.getToken(MyPrefs.getInt("loggedID",-1));
        user = MyPrefs.getString("user", null);
        String starredBio = MyPrefs.getString("starredBio", null);
        String biography = null;

        try {
            imageURL = new GetGitHubPP().execute(token, user).get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            biography = new GetGitHubBio().execute(token, "jotaaloud").get();
        } catch (Exception e) {
            Log.e("Biography error", "No biography found");
        }



        try {
            String readme= new GetGitHubReadme().execute(token, user).get();
            Markwon markwon = Markwon.create(this);
            if(starredBio != null && !(starredBio.isEmpty())){
                markwon.setMarkdown(tvPrincMD, starredBio);
            } else if (readme != null && !(readme.isEmpty())) {
                markwon.setMarkdown(tvPrincMD, readme);
            }


        } catch (Exception e) {
            Log.e("Main Readme error", "No readme found");
        }

        etBiography.setText(biography);

        if(imageURL != null && !imageURL.isEmpty()){
            Picasso.get()
                    .load(imageURL)
                    .into(ivProfilePicture);
        }else{
            ivProfilePicture.setImageResource(R.drawable.account_circle_outline);
        }

        btnChangeState = findViewById(R.id.btnChangeState);
        btnChangeState.setOnClickListener(v -> {
            String biography2 = etBiography.getText().toString();

            if(token != null && user!=null){
                new UpdateGitHubBio().execute(token, user, biography2);
                Toast.makeText(menu.this,"Biography changed!", Toast.LENGTH_SHORT).show();
            }

        });

        btnChangeMD = findViewById(R.id.btnChangeMD);
        btnChangeMD.setOnClickListener(v -> {

            if(starredBio != null && !(starredBio.isEmpty())){
                new UpdateGitHubReadme().execute(token, user, starredBio);
                Toast.makeText(menu.this, "Readme from Github changed!", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(menu.this, "You need to save a new bio first.", Toast.LENGTH_SHORT).show();
            }

        });

    }

    @Override
    public void onResume(){
        super.onResume();

        try{
            token = dbHelper.getToken(MyPrefs.getInt("loggedID",-1));
            imageURL = new GetGitHubPP().execute(token, user).get();
        }catch(Exception e){

        }

        if(imageURL != null && !imageURL.isEmpty()){
            Picasso.get()
                    .load(imageURL)
                    .into(ivProfilePicture);
        }else{
            ivProfilePicture.setImageResource(R.drawable.account_circle_outline);
        }
    }


}