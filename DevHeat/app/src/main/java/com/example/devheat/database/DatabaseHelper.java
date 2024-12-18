package com.example.devheat.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    public DatabaseHelper(Context context) {
        super(context, "users.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE1 = "CREATE TABLE users ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "name TEXT, "
                + "password TEXT, "
                + "token TEXT)";
        String CREATE_TABLE2 = " CREATE TABLE mdFiles ("
                + "id INTEGER, "
                + "mdContent TEXT);";

        db.execSQL(CREATE_TABLE1);
        db.execSQL(CREATE_TABLE2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS mdFiles");
        onCreate(db);
    }

    public void deleteDB(SQLiteDatabase db){
        db.execSQL("DELETE FROM users");
        db.execSQL("DELETE FROM mdFiles");
    }

    public boolean insertMdFile(int id, String content) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("id", id);
        contentValues.put("mdContent", content);
        long result = db.insert("mdFiles", null, contentValues);
        db.close();
        return result != -1;  // Devuelve true si la inserción fue exitosa
    }



    // Método para insertar un estudiante
    public boolean insertUser(String name, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("password", password);
        long result = db.insert("users", null, contentValues);
        db.close();
        return result != -1;  // Devuelve true si la inserción fue exitosa
    }

    public String getPassword(String name){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT password FROM users where name = ?", new String[]{String.valueOf(name)});

        // Crea un StringBuilder para construir la cadena de salida
        StringBuilder builder = new StringBuilder();
        if (cursor.getCount() == 0) {
            // Si no hay usuarios, agrega un mensaje al StringBuilder
            builder.append("There are no users");
        } else {
            // Si hay registros, itera sobre cada uno de ellos
            while (cursor.moveToNext()) {
                // Obtiene la contraseña del usuario desde la tercera columna (índice 2)
                String password  = cursor.getString(0);
                builder.append(password);
            }
        }
        // Cierra el cursor para liberar recursos
        cursor.close();
        db.close();
        return builder.toString();
    }

    public int getID(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        int id = -1; // Inicializa id como null

        try {
            cursor = db.rawQuery("SELECT id FROM users WHERE name = ? LIMIT 1", new String[]{name});

            if (cursor.moveToFirst()) {
                id = cursor.getInt(0);
            } else {
                Log.d("Debug", "No user found with name: " + name);
            }
        } catch (Exception e) {
            Log.e("Error", "Exception while retrieving ID: ", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return id; // Devuelve el ID o null si no se encontró
    }

    public boolean updateToken(int id, String token) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("token", token);
        // Actualiza la fila donde el "id" coincide
        int rowsAffected = db.update("users", contentValues, "id = ?", new String[]{String.valueOf(id)});
        return rowsAffected > 0;  // Devuelve true si se actualizó al menos una fila
    }

    public String getToken(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String token = null;

        try {
            Cursor cursor = db.rawQuery("SELECT token FROM users WHERE id = ? LIMIT 1", new String[]{String.valueOf(id)});
            Log.d("Debug", "SELECT token FROM users WHERE id = " + id);

            if (cursor.moveToFirst()) {
                token = cursor.getString(0);
                Log.d("Debug", "token from select = " + token);
                // Verifica si el token está vacío
                if (token == null || token.trim().isEmpty()) {
                    Log.d("Debug", "Token is empty for user ID: " + id);
                    return null; // O puedes devolver una cadena específica
                }
            }

            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }

        return token; // Devuelve null si no se encontró un token
    }





    // Método para leer todos los estudiantes
    public String getAllStudents() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users", null);
        // Crea un StringBuilder para construir la cadena de salida
        StringBuilder builder = new StringBuilder();
        if (cursor.getCount() == 0) {
            // Si no hay estudiantes, agrega un mensaje al StringBuilder
            builder.append("There is no users.");
        } else {
            // Si hay registros, itera sobre cada uno de ellos
            while (cursor.moveToNext()) {
                // Obtiene el ID del estudiante desde la primera columna (índice 0)
                int id = cursor.getInt(0);
                // Obtiene el nombre del estudiante desde la segunda columna (índice 1)
                String name = cursor.getString(1);
                // Obtiene la edad del estudiante desde la tercera columna (índice 2)
                String password  = cursor.getString(2);
                builder.append("ID: ").append(id)
                        .append(", Name: ").append(name)
                        .append(", Password: ").append(password)
                        .append("\n");
            }
        }
        // Cierra el cursor para liberar recursos
        cursor.close();
        db.close();
        return builder.toString();
    }

    public String getAllStudents_V2() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users", null);
        // Crea un StringBuilder para construir la cadena de salida
        StringBuilder builder = new StringBuilder();

        if (cursor.getCount() == 0) {
            // Si no hay usuarios, agrega un mensaje al StringBuilder
            builder.append("There are no users.");
        } else {
            // Mueve el cursor a la primera fila
            if (cursor.moveToFirst()) {
                do {
                    // Obtiene el ID del estudiante desde la primera columna (índice 0)
                    int id = cursor.getInt(0);
                    // Obtiene el nombre del estudiante desde la segunda columna (índice 1)
                    String name = cursor.getString(1);
                    // Obtiene la edad del estudiante desde la tercera columna (índice 2)
                    String password = cursor.getString(2);
                    builder.append("ID: ").append(id)
                            .append(", Name: ").append(name)
                            .append(", Password: ").append(password)
                            .append("\n");
                } while (cursor.moveToNext()); // Continúa moviéndose a la siguiente fila
            }
        }
        // Cierra el cursor para liberar recursos
        cursor.close();
        db.close();
        return builder.toString();
    }

    public String getAllUserNames() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT name FROM users", null);
        // Crea un StringBuilder para construir la cadena de salida
        StringBuilder builder = new StringBuilder();

        if (cursor.getCount() == 0) {
            // Si no hay usuarios, agrega un mensaje al StringBuilder
            builder.append("There are no users.");
        } else {
            // Mueve el cursor a la primera fila
            if (cursor.moveToFirst()) {
                do {
                    // Obtiene el ID del estudiante desde la primera columna (índice 0)
                    int id = cursor.getInt(0);
                    // Obtiene el nombre del estudiante desde la segunda columna (índice 1)
                    String name = cursor.getString(1);
                    // Obtiene la edad del estudiante desde la tercera columna (índice 2)
                    String password = cursor.getString(2);
                    builder
                            .append(", Name: ").append(name)
                            .append("\n");
                } while (cursor.moveToNext()); // Continúa moviéndose a la siguiente fila
            }
        }
        // Cierra el cursor para liberar recursos
        cursor.close();
        db.close();
        return builder.toString();
    }

    public List<String> getAllUserNamesList() {
        List<String> userNames = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT name FROM users", null);

        if (cursor.moveToFirst()) {
            do {
                // El índice 0 corresponde a la columna "name" en la consulta
                String name = cursor.getString(0);
                userNames.add(name);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return userNames;
    }



    public List<String> getAllMdContentList(int id) {
        List<String> mdContents = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT mdContent FROM mdFiles WHERE id = ?", new String[]{String.valueOf(id)});

        if (cursor.moveToFirst()) {
            do {
                String content = cursor.getString(0);
                mdContents.add(content);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return mdContents;
    }

    public String getAllUsers() {
        SQLiteDatabase db = this.getReadableDatabase();
        StringBuilder builder = new StringBuilder();

        Cursor cursor = null;
        try {
            // Ejecutar la consulta para obtener todos los usuarios
            cursor = db.rawQuery("SELECT * FROM users", null);

            // Comprobar si hay resultados
            if (cursor.moveToFirst()) {
                do {
                    // Obtener los valores de cada columna usando getColumnIndexOrThrow
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                    String password = cursor.getString(cursor.getColumnIndexOrThrow("password"));
                    String token = cursor.getString(cursor.getColumnIndexOrThrow("token"));

                    // Agregar cada usuario al StringBuilder
                    builder.append("ID: ").append(id)
                            .append(", Name: ").append(name)
                            .append(", Password: ").append(password)
                            .append(", Token: ").append(token)
                            .append("\n");
                } while (cursor.moveToNext());
            } else {
                builder.append("No users found.");
            }

            // Mostrar el contenido en logcat
            Log.i("DatabaseContent", builder.toString());
        } catch (Exception e) {
            Log.e("DatabaseError", "Error reading database", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return builder.toString();
    }







    // Método para actualizar un estudiante
    public boolean updateStudent(int id, String newName, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", newName);
        contentValues.put("password", newPassword);
        // Actualiza la fila donde el "id" coincide
        int rowsAffected = db.update("students", contentValues, "id = ?", new String[]{String.valueOf(id)});
        db.close();
        return rowsAffected > 0;  // Devuelve true si se actualizó al menos una fila
    }

    public boolean deleteStudent(int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Elimina la fila donde el "id" coincide
        int rowsDeleted = db.delete("students", "id = ?", new String[]{String.valueOf(id)});
        db.close();
        return rowsDeleted > 0;  // Devuelve true si se eliminó al menos una fila
    }
    public Cursor getUserById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM users WHERE id = ?", new String[]{String.valueOf(id)});
    }
    public Cursor getUserByNameAndPassword(String name, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM users WHERE name = ? AND password = ?", new String[]{name, password});
    }

}