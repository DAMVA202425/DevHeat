package com.example.devheat;

import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import androidx.appcompat.app.AlertDialog;
import android.content.ClipboardManager;
import android.content.ClipData;


public class template extends AppCompatActivity {

    private Spinner spinnerMK;
    private Button btnAddCathegory, buttonClipboard, btnClearOptions, btnSave;
    private LinearLayout containerLayout;
    private List<Spinner> spinners = new ArrayList<>();
    private List<EditText> editTexts = new ArrayList<>();
    private ItemSelectedListener ist = new ItemSelectedListener();
    private AlertDialog.Builder ADbuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.template);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.template), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        spinnerMK = findViewById(R.id.spinnerMK);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.arrayMKTypes,
                android.R.layout.simple_spinner_item);
        spinnerMK.setAdapter(adapter);
        spinnerMK.setOnItemSelectedListener(ist);

        containerLayout = findViewById(R.id.containerLayout);
        btnAddCathegory = findViewById(R.id.btnAddCathegory);

        btnAddCathegory.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Spinner spinner = new Spinner(template.this);
                spinner.setAdapter(adapter);

                LinearLayout.LayoutParams paramsS = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                paramsS.setMargins(0, 0, 0, 16);
                spinner.setLayoutParams(paramsS);
                spinner.setPadding(8, 8, 8, 8);
                spinner.setOnItemSelectedListener(ist);

                containerLayout.addView(spinner);
                //containerLayout.addView(editText);
                spinners.add(spinner);
            }
        });

        buttonClipboard = findViewById(R.id.buttonClipboard);
        buttonClipboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                String textToCopy = "todo";
                ClipData clip = ClipData.newPlainText("Simple text", textToCopy);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getApplicationContext(), "Text copied to clipboard", Toast.LENGTH_SHORT).show();
            }
        });

        btnClearOptions = findViewById(R.id.btnClearOptions);
        btnClearOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                containerLayout.removeAllViews();
            }
        });

        ADbuilder = new AlertDialog.Builder(this);
        ADbuilder.setTitle("Ubication");
        ADbuilder.setMessage("¿Where do you want to save it?");
        ADbuilder.setPositiveButton("Default path", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(template.this, "Predetermined path", Toast.LENGTH_SHORT).show();
                saveFile("");
            }
        });
        ADbuilder.setNegativeButton("Another path", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(template.this, "Another path", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*"); // Permite seleccionar cualquier tipo de archivo
                startActivityForResult(intent, 123);
            }
        });

        btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dialog = ADbuilder.create();
                dialog.show();
            }

        });
    }

    public void saveFile(String path) {
        FileOutputStream fos = null;
        String algo = "contenido archivo";
        try {
            fos = openFileOutput("nuevoArchivo.txt", Context.MODE_PRIVATE);

            fos.write(algo.getBytes(StandardCharsets.UTF_8));
            Toast.makeText(template.this, "File created", Toast.LENGTH_SHORT).show();
            fos.close();

        } catch (IOException e) {
            Toast.makeText(template.this, "Error while creating the file", Toast.LENGTH_SHORT).show();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    Toast.makeText(template.this, "Error while creating the file", Toast.LENGTH_SHORT).show();
                }

            }
        }
    }


    public class ItemSelectedListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            Context context = view.getContext();

            String itemSeleccionado = parent.getItemAtPosition(position).toString();

            if(itemSeleccionado.equals("Text")){
                EditText editText = new EditText(context);
                LinearLayout.LayoutParams paramsE = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                paramsE.setMargins(0, 0, 0, 16);
                editText.setLayoutParams(paramsE);
                editText.setPadding(8, 8, 8, 8);
                editText.setHint("Enter your text here");
                editTexts.add(editText);
                containerLayout.addView(editText);

            } else if (itemSeleccionado.equals("1st level Heading")){
                EditText editText = new EditText(context);
                LinearLayout.LayoutParams paramsE = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                paramsE.setMargins(0, 0, 0, 16);
                editText.setLayoutParams(paramsE);
                editText.setPadding(8, 8, 8, 8);
                editText.setHint("Your 1st level heading");
                editTexts.add(editText);
                containerLayout.addView(editText);

            }else if(itemSeleccionado.equals("2nd level heading")){
                EditText editText = new EditText(context);
                LinearLayout.LayoutParams paramsE = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                paramsE.setMargins(0, 0, 0, 16);
                editText.setLayoutParams(paramsE);
                editText.setPadding(8, 8, 8, 8);
                editText.setHint("Your 2nd level heading");
                editTexts.add(editText);
                containerLayout.addView(editText);

            }else if(itemSeleccionado.equals("3rd level heading")){
                EditText editText = new EditText(context);
                LinearLayout.LayoutParams paramsE = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                paramsE.setMargins(0, 0, 0, 16);
                editText.setLayoutParams(paramsE);
                editText.setPadding(8, 8, 8, 8);
                editText.setHint("Your 3rd level heading");
                editTexts.add(editText);
                containerLayout.addView(editText);
            }

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            Toast.makeText(template.this, "Nothing selected", Toast.LENGTH_SHORT).show();
        }
    }

}

