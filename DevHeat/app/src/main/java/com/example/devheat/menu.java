package com.example.devheat;

import static android.os.Process.sendSignal;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.devheat.database.DatabaseHelper;

public class menu extends AppCompatActivity {

    Button closeMenu, deleteSP, deleteDB;
    private DatabaseHelper dbHelper;
    private Switch switchLM;


    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.menu);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.menu), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

    closeMenu = findViewById(R.id.closeMenu);
    closeMenu.setOnClickListener(new View.OnClickListener(){

        @Override
        public void onClick(View v){
            finish();
        }

    });

    deleteSP = findViewById(R.id.deleteSP);
    deleteSP.setOnClickListener(new View.OnClickListener(){

            @Override
                public void onClick(View v){

        }

    });

    dbHelper = new DatabaseHelper(this);
    SQLiteDatabase db = dbHelper.getWritableDatabase();
    deleteDB = findViewById(R.id.deleteDB);
    deleteDB.setOnClickListener(new View.OnClickListener(){
        @Override
        public void onClick(View v){
        dbHelper.deleteDB(db);
        }
    });

    switchLM = findViewById(R.id.switchLM);
    switchLM.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            SharedPreferences sharedPref = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();

            if (isChecked) {
                    Toast.makeText(menu.this, "cheked", Toast.LENGTH_SHORT).show();
                    switchLM.setText("Dark mode");
                    editor.putBoolean("isDarkMode", true);
                    editor.apply();
                } else {
                Toast.makeText(menu.this, "Uncheked", Toast.LENGTH_SHORT).show();
                switchLM.setText("Light mode");
                editor.putBoolean("isDarkMode", false);
                editor.apply();
                }
        }
    });

    }
}
