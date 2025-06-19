package com.example.devheat.resources

import android.os.AsyncTask
import android.util.Log
import org.json.JSONObject
import java.io.BufferedWriter
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class UpdateGitHubBio : AsyncTask<String, Void, Boolean>() {

    override fun doInBackground(vararg params: String): Boolean {
        val token = params[0]
        val username = params[1] // Aunque no se usa en la llamada, lo dejamos si se necesita más adelante
        val biography = params[2]

        return try {
            val url = URL("https://api.github.com/user")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "PATCH"
            connection.setRequestProperty("Authorization", "Bearer $token")
            connection.setRequestProperty("Content-Type", "application/json")
            connection.setRequestProperty("Accept", "application/vnd.github+json")
            connection.setRequestProperty("X-GitHub-Api-Version", "2022-11-28")
            connection.doOutput = true

            val jsonBody = JSONObject().apply {
                put("bio", biography)
            }

            BufferedWriter(OutputStreamWriter(connection.outputStream)).use { writer ->
                writer.write(jsonBody.toString())
            }

            val responseCode = connection.responseCode
            responseCode == 200
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override fun onPostExecute(success: Boolean) {
        if (success) {
            Log.w("acierto", "Biography changed successfully!")
        } else {
            Log.e("error", "Error al actualizar la biografía")
        }
    }
}
