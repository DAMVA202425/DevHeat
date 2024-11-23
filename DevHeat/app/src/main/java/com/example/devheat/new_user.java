package com.example.devheat;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.devheat.database.DatabaseHelper;


public class new_user extends AppCompatActivity{
    private Button exitnew_user, btnCreateUser;
    private EditText etNewUsername, etNewPassword, etNewPassword2;
    private DatabaseHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.new_user);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.new_user), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        etNewUsername = findViewById(R.id.etNewUsername);
        etNewPassword = findViewById(R.id.etNewPassword);
        etNewPassword2 = findViewById(R.id.etNewPassword2);
        dbHelper = new DatabaseHelper(this);
        btnCreateUser = findViewById(R.id.btnCreateUser);
        btnCreateUser.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String user = etNewUsername.getText().toString();

                String newPassword = etNewPassword.getText().toString();
                String newPassword2 = etNewPassword2.getText().toString();

                if(newPassword.equals(newPassword2)){
                    boolean inserted = dbHelper.insertUser(user,newPassword);

                    if(inserted){
                        Toast.makeText(new_user.this, "User: "+user+" created.",Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(new_user.this, "Error creating the user.",Toast.LENGTH_SHORT).show();
                    }

                }else{
                    Toast.makeText(new_user.this, "Enter the same password twice",Toast.LENGTH_SHORT).show();
                }


            }

        });


        exitnew_user = findViewById(R.id.exitnew_user);
        exitnew_user.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                finish();
            }
        });
    }

}
