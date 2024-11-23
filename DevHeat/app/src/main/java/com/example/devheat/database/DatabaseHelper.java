package com.example.devheat.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    public DatabaseHelper(Context context) {
        super(context, "users.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE users ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "name TEXT, "
                + "password TEXT)";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS users");
        onCreate(db);
    }

    public void deleteDB(SQLiteDatabase db){
        db.execSQL("DELETE FROM users");
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