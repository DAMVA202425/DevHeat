package com.example.devheat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
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
    private boolean isDarkMode;
    private SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //sharedPreferences = getSharedPreferences("ThemePrefs", MODE_PRIVATE);
        //isDarkMode = sharedPreferences.getBoolean("isDarkMode", false);
        //setTheme(isDarkMode ? R.style.Theme_DevHeat_Dark : R.style.Theme_DevHeat_Light);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });



        menuMain = findViewById(R.id.menuMain);
        ((View) menuMain).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, menu.class);
            startActivity(intent);
        });



        btnCreateU = findViewById(R.id.btnCreateU);
        btnCreateU.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(MainActivity.this, new_user.class);
                startActivity(intent);
            }
        });
        layout = findViewById(R.id.layout1);
        dbHelper = new DatabaseHelper(this);
        List<String> users = dbHelper.getAllUserNamesList();
        for(String user : users){
            Button button = new Button(this);
            button.setText(user);
            button.setBackgroundResource(android.R.drawable.btn_default);
            button.setTextColor(getResources().getColor(android.R.color.black));
            String userPassword = dbHelper.getPassword(user);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 0, 0, 0); // 16dp de margen inferior
            button.setLayoutParams(params);
            button.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, sign_in.class);
                intent.putExtra("userName", user);            //Cada usuario hará
                intent.putExtra("userPassword",userPassword); //login con su nombre
                startActivity(intent);                              //y su contraseña

            });
            layout.addView(button);

        }
    }
}