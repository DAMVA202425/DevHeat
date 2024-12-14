package com.example.devheat;

import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import androidx.appcompat.app.AlertDialog;
import android.content.ClipboardManager;

import com.example.devheat.database.DatabaseHelper;


public class template extends AppCompatActivity {

    private Spinner spinnerMK;
    private ImageButton btnMenuTemplate;
    private Button btnAddCathegory, buttonClipboard, btnClearOptions, btnSave;
    private LinearLayout containerLayout;
    private List<Spinner> spinners = new ArrayList<>();
    private List<EditText> editTexts = new ArrayList<>();
    private ItemSelectedListener ist = new ItemSelectedListener();
    private AlertDialog.Builder ADbuilder;
    private String path = "", textToCopy = "";
    private int numTextContainers = 0;
    private List <EditText> TextContainers = new ArrayList<>();
    private int[] type = new int[9];
    private DatabaseHelper dbHelper;
    private SharedPreferences sharedPref;

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

        dbHelper = new DatabaseHelper(this);
        sharedPref = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        //spinnerMK = findViewById(R.id.spinnerMK);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.arrayMKTypes,
                android.R.layout.simple_spinner_item);
        //spinnerMK.setAdapter(adapter);
        //spinnerMK.setOnItemSelectedListener(ist);

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
                getContent();
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Simple text", textToCopy);
                clipboard.setPrimaryClip(clip);
                //Toast.makeText(getApplicationContext(), "Text copied to clipboard", Toast.LENGTH_SHORT).show();
                sharedPref = getSharedPreferences("MyPrefs",MODE_PRIVATE);
                int id = sharedPref.getInt("loggedID",-1);
                boolean inserted = dbHelper.insertMdFile(id, textToCopy);
                Toast.makeText(getApplicationContext(), "Saved to clipboard", Toast.LENGTH_SHORT).show();

            }
        });

        btnMenuTemplate = findViewById(R.id.btnMenuTemplate);
        ((View) btnMenuTemplate).setOnClickListener(v -> {
            Intent intent = new Intent(template.this, menu.class);
            startActivity(intent);
        });




        btnClearOptions = findViewById(R.id.btnClearOptions);
        btnClearOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                containerLayout.removeAllViews();
                numTextContainers = 0;
            }
        });

        ADbuilder = new AlertDialog.Builder(this);
        ADbuilder.setTitle("Location");
        ADbuilder.setMessage("Where do you want to save it?");
        ADbuilder.setPositiveButton("Default path", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(template.this, "Ruta predeterminada", Toast.LENGTH_SHORT).show();
                saveFile(path);
                boolean inserted = dbHelper.insertMdFile(0, textToCopy);
            }
        });
        ADbuilder.setNegativeButton("Other path", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(template.this, "Select a path", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                try {
                    startActivityForResult(intent, 123);
                    saveFile(path);
                    boolean inserted = dbHelper.insertMdFile(0, textToCopy);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(template.this, "Path not found", Toast.LENGTH_SHORT).show();
                }
            }
        });
        ADbuilder.setNeutralButton("Save as Starred", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("starredBio", textToCopy);
                editor.apply();


                Toast.makeText(template.this, "Starred", Toast.LENGTH_SHORT).show();
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


        btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dialog = ADbuilder.create();
                dialog.show();
            }

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123 && resultCode == RESULT_OK) {
            Uri treeUri = data.getData();
            String path = treeUri.getPath();
            // Ahora 'path' contiene la ruta seleccionada
            Toast.makeText(this, "Selected path: " + path, Toast.LENGTH_LONG).show();
        }
    }


    public void saveFile(String path) {
        FileOutputStream fos = null;
        getContent();
        try {
            fos = openFileOutput(path + "nuevoArchivo.txt", Context.MODE_PRIVATE);

            fos.write(textToCopy.getBytes(StandardCharsets.UTF_8));
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
                editText.setId(numTextContainers);
                type[numTextContainers] = 0;
                numTextContainers ++;
                paramsE.setMargins(0, 0, 0, 16);
                editText.setLayoutParams(paramsE);
                editText.setPadding(8, 8, 8, 8);
                editText.setHint("Enter your text here");
                editTexts.add(editText);
                TextContainers.add(editText);
                containerLayout.addView(editText);

            } else if (itemSeleccionado.equals("1st level Heading")){
                EditText editText = new EditText(context);
                editText.setId(numTextContainers);
                type[numTextContainers] = 1;
                numTextContainers ++;
                LinearLayout.LayoutParams paramsE = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                paramsE.setMargins(0, 0, 0, 16);
                editText.setLayoutParams(paramsE);
                editText.setPadding(8, 8, 8, 8);
                editText.setHint("Your 1st level heading");
                editTexts.add(editText);
                TextContainers.add(editText);
                containerLayout.addView(editText);

            }else if(itemSeleccionado.equals("2nd level heading")){
                EditText editText = new EditText(context);
                editText.setId(numTextContainers);
                type[numTextContainers] = 2;
                numTextContainers ++;
                LinearLayout.LayoutParams paramsE = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                paramsE.setMargins(0, 0, 0, 16);
                editText.setLayoutParams(paramsE);
                editText.setPadding(8, 8, 8, 8);
                editText.setHint("Your 2nd level heading");
                editTexts.add(editText);
                TextContainers.add(editText);
                containerLayout.addView(editText);

            }else if(itemSeleccionado.equals("3rd level heading")){
                EditText editText = new EditText(context);
                editText.setId(numTextContainers);
                type[numTextContainers] = 3;
                numTextContainers ++;
                LinearLayout.LayoutParams paramsE = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                paramsE.setMargins(0, 0, 0, 16);
                editText.setLayoutParams(paramsE);
                editText.setPadding(8, 8, 8, 8);
                editText.setHint("Your 3rd level heading");
                editTexts.add(editText);
                TextContainers.add(editText);
                containerLayout.addView(editText);
            }else if(itemSeleccionado.equals("Link")){
                EditText editText = new EditText(context);
                editText.setId(numTextContainers);
                type[numTextContainers] = 4;
                numTextContainers ++;
                LinearLayout.LayoutParams paramsE = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                paramsE.setMargins(0, 0, 0, 16);
                editText.setLayoutParams(paramsE);
                editText.setPadding(8, 8, 8, 8);
                editText.setHint("Your link here");
                editTexts.add(editText);
                TextContainers.add(editText);
                containerLayout.addView(editText);
            }else if(itemSeleccionado.equals("Text + link")){
                EditText editText = new EditText(context);
                editText.setId(numTextContainers);
                type[numTextContainers] = 61;
                numTextContainers ++;
                LinearLayout.LayoutParams paramsE = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                paramsE.setMargins(0, 0, 0, 16);
                editText.setLayoutParams(paramsE);
                editText.setPadding(8, 8, 8, 8);
                editText.setHint("Your text here");
                editTexts.add(editText);
                TextContainers.add(editText);
                containerLayout.addView(editText);
                //----------------------------------------------------
                EditText editText2 = new EditText(context);
                editText2.setId(numTextContainers);
                type[numTextContainers] = 62;
                numTextContainers ++;
                editText2.setLayoutParams(paramsE);
                editText2.setPadding(8, 8, 8, 8);
                editText2.setHint("Your link");
                editTexts.add(editText2);
                TextContainers.add(editText2);
                containerLayout.addView(editText2);
            }else if(itemSeleccionado.equals("HR")){
                EditText editText = new EditText(context);
                editText.setId(numTextContainers);
                type[numTextContainers] = 7;
                numTextContainers ++;
                LinearLayout.LayoutParams paramsE = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                paramsE.setMargins(0, 0, 0, 16);
                editText.setLayoutParams(paramsE);
                editText.setPadding(8, 8, 8, 8);
                editText.setText("HR");
                editText.setEnabled(false);
                editTexts.add(editText);
                TextContainers.add(editText);
                containerLayout.addView(editText);

            }else if(itemSeleccionado.equals("!Note text")){
                EditText editText = new EditText(context);
                editText.setId(numTextContainers);
                type[numTextContainers] = 9;
                numTextContainers ++;
                LinearLayout.LayoutParams paramsE = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                paramsE.setMargins(0, 0, 0, 16);
                editText.setLayoutParams(paramsE);
                editText.setPadding(8, 8, 8, 8);
                editText.setHint("Your note text");
                editTexts.add(editText);
                TextContainers.add(editText);
                containerLayout.addView(editText);

            }

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            Toast.makeText(template.this, "Nothing selected", Toast.LENGTH_SHORT).show();
        }

    }

    public  void getContent(){

        for (int x = 0; x< TextContainers.size(); x++){
            EditText editText = TextContainers.get(x);

            String pre = "";
            String post = "";

            if (type[x] == 0){
                pre = "";
                post = "\n";
            } else if (type[x] == 1) {
                pre = "# ";
                post = "\n";
            } else if (type[x] == 2) {
                pre = "## ";
                post = "\n";
            } else if(type[x] == 3){
                pre = "### ";
                post = "\n";
            }  else if(type[x] == 61){
                pre = "["; post = "]";
            }  else if(type[x] == 62){
                pre = "("; post = ")\n";
            } else if(type[x] == 9){
                pre = "> [!NOTE] \n> ";
                post = "\n";
            }

            String line = pre + editText.getText().toString() + post;

              if(type[x] == 7){
                  line = "\n --- \n";
            }

            textToCopy = textToCopy + line;
        }

    }

}

