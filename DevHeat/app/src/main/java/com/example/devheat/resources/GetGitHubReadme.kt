package com.example.devheat.resources

import android.os.AsyncTask
import android.util.Base64
import android.util.Log
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class GetGitHubReadme : AsyncTask<String, Void, String?>() {

    companion object {
        private const val TAG = "GetGitHubReadme"
    }

    override fun doInBackground(vararg params: String): String? {
        val token = params[0]
        val username = params[1]
        var connection: HttpURLConnection? = null
        var reader: BufferedReader? = null

        return try {
            val url = URL("https://api.github.com/repos/$username/$username/readme")
            connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.setRequestProperty("Authorization", "token $token")
            connection.setRequestProperty("Accept", "application/vnd.github.v3+json")

            val responseCode = connection.responseCode
            Log.d(TAG, "Response Code: $responseCode")

            if (responseCode == HttpURLConnection.HTTP_OK) {
                reader = BufferedReader(InputStreamReader(connection.inputStream))
                val response = StringBuilder()
                var line: String?

                while (reader.readLine().also { line = it } != null) {
                    response.append(line)
                }

                val jsonResponse = JSONObject(response.toString())
                val content = jsonResponse.optString("content", "")
                if (content.isNotEmpty()) {
                    val decodedBytes = Base64.decode(content, Base64.DEFAULT)
                    String(decodedBytes, charset("UTF-8"))
                } else {
                    null
                }
            } else {
                Log.e(TAG, "Error response: ${connection.responseMessage}")
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error en la solicitud", e)
            null
        } finally {
            try {
                reader?.close()
            } catch (e: Exception) {
                Log.e(TAG, "Error al cerrar el reader", e)
            }
            connection?.disconnect()
        }
    }

    override fun onPostExecute(readmeContent: String?) {
        if (!readmeContent.isNullOrEmpty()) {
            Log.d(TAG, "Contenido del README obtenido: ${readmeContent.take(100)}...")
            // Aquí puedes actualizar tu TextView con el contenido del README
            // Por ejemplo: textView.text = readmeContent
        } else {
            Log.e(TAG, "No se pudo obtener el contenido del README")
        }
    }
}
