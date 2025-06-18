package com.example.devheat

import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.Toast

import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

import java.io.FileOutputStream
import java.io.IOException
import java.nio.charset.StandardCharsets
import androidx.appcompat.app.AlertDialog
import android.content.ClipboardManager

import com.example.devheat.database.DatabaseHelper;

class Template : AppCompatActivity() {

    private lateinit var spinnerMK: Spinner
    private lateinit var btnMenuTemplate: ImageButton
    private lateinit var btnAddCathegory: Button
    private lateinit var buttonClipboard: Button
    private lateinit var btnClearOptions: Button
    private lateinit var btnSave: Button
    private lateinit var containerLayout: LinearLayout
    private var spinners: MutableList<Spinner> = mutableListOf()
    private var editTexts: MutableList<EditText> = mutableListOf()
    private var ist: ItemSelectedListener = ItemSelectedListener()
    private var path: String = ""
    private var textToCopy: String = "";
    private var numTextContainers: Int = 0
    private var TextContainers: MutableList<EditText> = mutableListOf()
    private var type = IntArray(9) { 0 }
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var sharedThemesPref: SharedPreferences
    private lateinit var MyPrefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.template)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.template)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        dbHelper = DatabaseHelper(this)
        MyPrefs = getSharedPreferences("MyPrefs", MODE_PRIVATE)

        var adapter: ArrayAdapter<CharSequence>  = ArrayAdapter . createFromResource (
                this,
        R.array.arrayMKTypes,
        android.R.layout.simple_spinner_item)

        containerLayout = findViewById(R.id.containerLayout)
        btnAddCathegory = findViewById(R.id.btnAddCathegory)

        btnAddCathegory.setOnClickListener {


        var spinner: Spinner  = Spinner(this)
                spinner.adapter = adapter

                var paramsS: LinearLayout.LayoutParams  = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                paramsS.setMargins(0, 0, 0, 16)
                spinner.setLayoutParams(paramsS)
                spinner.setPadding(8, 8, 8, 8);
                spinner.setOnItemSelectedListener(ist)

                containerLayout.addView(spinner);
                spinners.add(spinner);

        }



        buttonClipboard = findViewById(R.id.buttonClipboard);
        buttonClipboard.setOnClickListener {

            getContent()
            textToCopy = ""

            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Simple text", textToCopy)
            clipboard.setPrimaryClip(clip)

            val id = MyPrefs.getInt("loggedID", -1)
            val inserted = dbHelper.insertMdFile(id, textToCopy)

            Toast.makeText(applicationContext, "Saved to clipboard", Toast.LENGTH_SHORT).show()

        }

        btnMenuTemplate = findViewById(R.id.btnMenuTemplate);
        btnMenuTemplate.setOnClickListener {
            var intent = Intent(this, menu::class.java)
            startActivity(intent)
        }


        btnClearOptions = findViewById(R.id.btnClearOptions)
        btnClearOptions.setOnClickListener{
                containerLayout.removeAllViews();
                numTextContainers = 0;
            }

        var ADbuilder = AlertDialog.Builder(this)
            .setTitle("Location")
            .setMessage("Where do you want to save it?")
            .setPositiveButton("Default path") { _, _ ->
                Toast.makeText(this, "Default path", Toast.LENGTH_SHORT).show()
                saveFile(path)
                val inserted = dbHelper.insertMdFile(0, textToCopy)
            }

            .setNegativeButton("Other path") { _, _ ->
                Toast.makeText(this, "Select a path", Toast.LENGTH_SHORT).show();
                var intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                try {
                    startActivityForResult(intent, 123);
                    saveFile(path);
                    var inserted: Boolean = dbHelper.insertMdFile (0, textToCopy);
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(this, "Path not found", Toast.LENGTH_SHORT).show();
                }

            }
            .setNeutralButton("Save as Starred") { _, _ ->

                var editor = MyPrefs.edit();
                editor.putString("starredBio", textToCopy);
                editor.apply()

                Toast.makeText(this, "Starred", Toast.LENGTH_SHORT).show()

            }

        btnSave = findViewById(R.id.btnSave)
        btnSave.setOnClickListener {
            var dialog = ADbuilder.create ()
            dialog.show()
        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 123 && resultCode == RESULT_OK) {
            var treeUri: Uri? = data?.getData()
            var path: String? = treeUri?.getPath()

            Toast.makeText(this, "Selected path: " + path, Toast.LENGTH_LONG).show()
        }
    }

    fun saveFile(path: String) {
        var fos: FileOutputStream? = null
        getContent();
        try {

            fos = openFileOutput("nuevoArchivo.txt", Context.MODE_PRIVATE);
            fos.write(textToCopy.toByteArray(Charsets.UTF_8))
            Toast.makeText(this@Template, "File created", Toast.LENGTH_SHORT).show();
            fos.close();

        } catch (e: IOException) {
            Toast.makeText(this@Template, "Error while creating the file", Toast.LENGTH_SHORT).show();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (e: IOException) {
                    Toast.makeText(this, "Error while creating the file", Toast.LENGTH_SHORT).show();
                }

            }
        }
    }

    inner class ItemSelectedListener: AdapterView.OnItemSelectedListener {
        override fun onItemSelected(
            parent: AdapterView<*>, view: View, position: Int, id: Long
        ) {
            var context = view.getContext()

            var selectedItem: String = parent.getItemAtPosition(position).toString()

            if(selectedItem.equals("Text")){
                var editText: EditText = EditText(context)
                var paramsE: LinearLayout.LayoutParams =  LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                );
                editText.setId(numTextContainers)
                type[numTextContainers] = 0
                numTextContainers ++
                paramsE.setMargins(0, 0, 0, 16)
                editText.setLayoutParams(paramsE)
                editText.setPadding(8, 8, 8, 8)
                editText.setHint("Enter your text here")
                editTexts.add(editText)
                TextContainers.add(editText)
                containerLayout.addView(editText)

            } else if (selectedItem.equals("1st level Heading")){
                var editText: EditText = EditText(context)
                editText.setId(numTextContainers)
                type[numTextContainers] = 1
                numTextContainers++
                var paramsE: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                paramsE.setMargins(0, 0, 0, 16)
                editText.setLayoutParams(paramsE)
                editText.setPadding(8, 8, 8, 8)
                editText.setHint("Your 1st level heading")
                editTexts.add(editText)
                TextContainers.add(editText)
                containerLayout.addView(editText)

            }else if(selectedItem.equals("2nd level heading")){
                var editText: EditText =  EditText(context)
                editText.setId(numTextContainers)
                type[numTextContainers] = 2
                numTextContainers++
                var paramsE: LinearLayout.LayoutParams  = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                paramsE.setMargins(0, 0, 0, 16)
                editText.setLayoutParams(paramsE);
                editText.setPadding(8, 8, 8, 8);
                editText.setHint("Your 2nd level heading");
                editTexts.add(editText);
                TextContainers.add(editText);
                containerLayout.addView(editText);

            }else if(selectedItem.equals("3rd level heading")){
                var editText: EditText =  EditText(context)
                editText.setId(numTextContainers);
                type[numTextContainers] = 3;
                numTextContainers ++;
                var paramsE: LinearLayout.LayoutParams =  LinearLayout.LayoutParams(
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
            }else if(selectedItem.equals("Text + link")){
                var editText: EditText =  EditText(context)
                editText.setId(numTextContainers);
                type[numTextContainers] = 61;
                numTextContainers ++;
                var  paramsE: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                paramsE.setMargins(0, 0, 0, 16);
                editText.setLayoutParams(paramsE);
                editText.setPadding(8, 8, 8, 8);
                editText.setHint("Your text here");
                editTexts.add(editText);
                TextContainers.add(editText);
                containerLayout.addView(editText);
                //----------------------------------------------------
                var editText2: EditText =  EditText(context)
                editText2.setId(numTextContainers);
                type[numTextContainers] = 62;
                numTextContainers ++;
                editText2.setLayoutParams(paramsE);
                editText2.setPadding(8, 8, 8, 8);
                editText2.setHint("Your link");
                editTexts.add(editText2)
                TextContainers.add(editText2)
                containerLayout.addView(editText2)
            }else if(selectedItem.equals("HR")){
                var editText: EditText =  EditText(context)
                editText.setId(numTextContainers)
                type[numTextContainers] = 7
                numTextContainers ++
                var paramsE: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                paramsE.setMargins(0, 0, 0, 16)
                editText.setLayoutParams(paramsE);
                editText.setPadding(8, 8, 8, 8)
                editText.setText("HR")
                editText.setEnabled(false)
                editTexts.add(editText);
                TextContainers.add(editText);
                containerLayout.addView(editText);

            }else if(selectedItem.equals("!Note text")){
                var editText: EditText =  EditText(context)
                editText.setId(numTextContainers)
                type[numTextContainers] = 9
                numTextContainers++
                var paramsE: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                paramsE.setMargins(0, 0, 0, 16)
                editText.setLayoutParams(paramsE)
                editText.setPadding(8, 8, 8, 8)
                editText.setHint("Your note text")
                editTexts.add(editText)
                TextContainers.add(editText)
                containerLayout.addView(editText)

            }

        }


        override fun onNothingSelected(
            parent: AdapterView<*> // ? en vez de *
        ) {
            Toast.makeText(this@Template, "Nothing selected", Toast.LENGTH_SHORT).show();
        }

    }

    public  fun getContent(){

        for (x in 0 until TextContainers.size){
            var editText: EditText = TextContainers[x];

            var pre: String = "";
            var post: String= "";

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

            var line: String = pre + editText.getText().toString() + post;

            if(type[x] == 7){
                line = "\n --- \n";
            }

            textToCopy = textToCopy + line;
        }

    }
}