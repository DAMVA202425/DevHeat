package com.example.devheat.database

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.ContentValues
import android.util.Log

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, "users.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        val createTable1 = """
            CREATE TABLE users (
                id INTEGER PRIMARY KEY AUTOINCREMENT, 
                name TEXT, 
                password TEXT, 
                token TEXT
            )
        """.trimIndent()

        val createTable2 = """
            CREATE TABLE mdFiles (
                id INTEGER, 
                mdContent TEXT
            )
        """.trimIndent()

        db.execSQL(createTable1)
        db.execSQL(createTable2)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS users")
        db.execSQL("DROP TABLE IF EXISTS mdFiles")
        onCreate(db)
    }

    fun deleteDB(db: SQLiteDatabase) {
        db.execSQL("DELETE FROM users")
        db.execSQL("DELETE FROM mdFiles")
    }

    fun insertMdFile(id: Int, content: String): Boolean {
        val db = writableDatabase
        val contentValues = ContentValues().apply {
            put("id", id)
            put("mdContent", content)
        }
        val result = db.insert("mdFiles", null, contentValues)
        db.close()
        return result != -1L
    }

    fun insertUser(name: String, password: String): Boolean {
        val db = writableDatabase
        val contentValues = ContentValues().apply {
            put("name", name)
            put("password", password)
        }
        val result = db.insert("users", null, contentValues)
        db.close()
        return result != -1L
    }

    fun getPassword(name: String): String {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT password FROM users WHERE name = ?", arrayOf(name))
        val builder = StringBuilder()

        if (cursor.count == 0) {
            builder.append("There are no users")
        } else {
            while (cursor.moveToNext()) {
                builder.append(cursor.getString(0))
            }
        }

        cursor.close()
        db.close()
        return builder.toString()
    }

    fun getID(name: String): Int {
        val db = readableDatabase
        var cursor: Cursor? = null
        var id = -1

        try {
            cursor = db.rawQuery("SELECT id FROM users WHERE name = ? LIMIT 1", arrayOf(name))
            if (cursor.moveToFirst()) {
                id = cursor.getInt(0)
            } else {
                Log.d("Debug", "No user found with name: $name")
            }
        } catch (e: Exception) {
            Log.e("Error", "Exception while retrieving ID: ", e)
        } finally {
            cursor?.close()
            db.close()
        }

        return id
    }

    fun updateToken(id: Int, token: String): Boolean {
        val db = writableDatabase
        val contentValues = ContentValues().apply {
            put("token", token)
        }
        val rowsAffected = db.update("users", contentValues, "id = ?", arrayOf(id.toString()))
        db.close()
        return rowsAffected > 0
    }

    fun getToken(id: Int): String? {
        val db = readableDatabase
        var token: String? = null

        try {
            val cursor = db.rawQuery("SELECT token FROM users WHERE id = ? LIMIT 1", arrayOf(id.toString()))
            Log.d("Debug", "SELECT token FROM users WHERE id = $id")

            if (cursor.moveToFirst()) {
                token = cursor.getString(0)
                Log.d("Debug", "token from select = $token")
                if (token.isNullOrBlank()) {
                    Log.d("Debug", "Token is empty for user ID: $id")
                    return null
                }
            }
            cursor.close()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            db.close()
        }

        return token
    }

    fun getAllStudents(): String {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM users", null)
        val builder = StringBuilder()

        if (cursor.count == 0) {
            builder.append("There is no users.")
        } else {
            while (cursor.moveToNext()) {
                val id = cursor.getInt(0)
                val name = cursor.getString(1)
                val password = cursor.getString(2)
                builder.append("ID: $id, Name: $name, Password: $password\n")
            }
        }

        cursor.close()
        db.close()
        return builder.toString()
    }

    fun getAllStudentsV2(): String {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM users", null)
        val builder = StringBuilder()

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(0)
                val name = cursor.getString(1)
                val password = cursor.getString(2)
                builder.append("ID: $id, Name: $name, Password: $password\n")
            } while (cursor.moveToNext())
        } else {
            builder.append("There are no users.")
        }

        cursor.close()
        db.close()
        return builder.toString()
    }

    fun getAllUserNames(): String {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT name FROM users", null)
        val builder = StringBuilder()

        if (cursor.moveToFirst()) {
            do {
                val name = cursor.getString(0)
                builder.append("Name: $name\n")
            } while (cursor.moveToNext())
        } else {
            builder.append("There are no users.")
        }

        cursor.close()
        db.close()
        return builder.toString()
    }

    fun getAllUserNamesList(): List<String> {
        val userNames = mutableListOf<String>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT name FROM users", null)

        if (cursor.moveToFirst()) {
            do {
                val name = cursor.getString(0)
                userNames.add(name)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return userNames
    }

    fun getAllMdContentList(id: Int): List<String> {
        val mdContents = mutableListOf<String>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT mdContent FROM mdFiles WHERE id = ?", arrayOf(id.toString()))

        if (cursor.moveToFirst()) {
            do {
                val content = cursor.getString(0)
                mdContents.add(content)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return mdContents
    }

    fun getAllUsers(): String {
        val db = readableDatabase
        val builder = StringBuilder()

        try {
            val cursor = db.rawQuery("SELECT * FROM users", null)
            if (cursor.moveToFirst()) {
                do {
                    val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                    val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                    val password = cursor.getString(cursor.getColumnIndexOrThrow("password"))
                    val token = cursor.getString(cursor.getColumnIndexOrThrow("token"))

                    builder.append("ID: $id, Name: $name, Password: $password, Token: $token\n")
                } while (cursor.moveToNext())
            } else {
                builder.append("No users found.")
            }
            Log.i("DatabaseContent", builder.toString())
        } catch (e: Exception) {
            Log.e("DatabaseError", "Error reading database", e)
        } finally {
            db.close()
        }

        return builder.toString()
    }

    fun updateStudent(id: Int, newName: String, newPassword: String): Boolean {
        val db = writableDatabase
        val contentValues = ContentValues().apply {
            put("name", newName)
            put("password", newPassword)
        }
        val rowsAffected = db.update("students", contentValues, "id = ?", arrayOf(id.toString()))
        db.close()
        return rowsAffected > 0
    }

    fun deleteStudent(id: Int): Boolean {
        val db = writableDatabase
        val rowsDeleted = db.delete("students", "id = ?", arrayOf(id.toString()))
        db.close()
        return rowsDeleted > 0
    }

    fun getUserById(id: Int): Cursor {
        return readableDatabase.rawQuery("SELECT * FROM users WHERE id = ?", arrayOf(id.toString()))
    }

    fun getUserByNameAndPassword(name: String, password: String): Cursor {
        return readableDatabase.rawQuery("SELECT * FROM users WHERE name = ? AND password = ?", arrayOf(name, password))
    }
}
