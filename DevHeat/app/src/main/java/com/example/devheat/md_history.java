package com.example.devheat;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import io.noties.markwon.Markwon;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.devheat.database.DatabaseHelper;

import java.util.List;

public class md_history extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private LinearLayout layout;
    private SharedPreferences sharedThemesPref, MyPrefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.md_history);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.md_history), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        MyPrefs = getSharedPreferences("MyPrefs",MODE_PRIVATE);


        int id = MyPrefs.getInt("loggedID",-1);
        Toast.makeText(md_history.this, "userID = "+id, Toast.LENGTH_SHORT).show();
        dbHelper = new DatabaseHelper(this);
        List<String> mdContents = dbHelper.getAllMdContentList(id);

        layout = findViewById(R.id.layoutMD);

        for (String content : mdContents) {
            TextView textView = new TextView(this);
            textView.setText(content);
            textView.setTextColor(getResources().getColor(android.R.color.black));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 0, 0, 16); // 16dp de margen inferior
            textView.setLayoutParams(params);

            Markwon markwon = Markwon.create(this);

            // Renderiza el contenido Markdown en el TextView
            markwon.setMarkdown(textView, content);

            layout.addView(textView);
        }




    }
}
