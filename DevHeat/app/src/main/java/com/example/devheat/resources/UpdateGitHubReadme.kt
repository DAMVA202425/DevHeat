package com.example.devheat.resources

import android.os.AsyncTask
import android.util.Base64
import android.util.Log
import org.json.JSONObject
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class UpdateGitHubReadme : AsyncTask<String, Void, Boolean>() {

    companion object {
        private const val TAG = "UpdateGitHubReadme"
    }

    override fun doInBackground(vararg params: String): Boolean {
        val token = params[0]
        val username = params[1]
        val newContent = params[2]

        return try {
            // 1. Obtener SHA del README.md actual
            val getUrl = URL("https://api.github.com/repos/$username/$username/contents/README.md")
            val getConnection = getUrl.openConnection() as HttpURLConnection
            getConnection.requestMethod = "GET"
            getConnection.setRequestProperty("Authorization", "token $token")
            getConnection.setRequestProperty("Accept", "application/vnd.github.v3+json")

            val getResponseCode = getConnection.responseCode
            if (getResponseCode != 200) {
                Log.e(TAG, "Error al obtener el README: $getResponseCode")
                return false
            }

            val getResponse = JSONObject(getConnection.inputStream.bufferedReader().readText())
            val sha = getResponse.getString("sha")

            // 2. Actualizar el contenido con PUT
            val updateUrl = URL("https://api.github.com/repos/$username/$username/contents/README.md")
            val updateConnection = updateUrl.openConnection() as HttpURLConnection
            updateConnection.requestMethod = "PUT"
            updateConnection.setRequestProperty("Authorization", "token $token")
            updateConnection.setRequestProperty("Content-Type", "application/json")
            updateConnection.setRequestProperty("Accept", "application/vnd.github.v3+json")
            updateConnection.doOutput = true

            val encodedContent = Base64.encodeToString(newContent.toByteArray(), Base64.NO_WRAP)
            val jsonBody = JSONObject().apply {
                put("message", "Update README")
                put("content", encodedContent)
                put("sha", sha)
            }

            BufferedWriter(OutputStreamWriter(updateConnection.outputStream)).use { writer ->
                writer.write(jsonBody.toString())
            }

            val updateResponseCode = updateConnection.responseCode
            updateResponseCode == 200
        } catch (e: Exception) {
            Log.e(TAG, "Error al actualizar el README", e)
            false
        }
    }

    override fun onPostExecute(success: Boolean) {
        if (success) {
            Log.d(TAG, "README actualizado con éxito")
        } else {
            Log.e(TAG, "Error al actualizar el README")
        }
    }
}
