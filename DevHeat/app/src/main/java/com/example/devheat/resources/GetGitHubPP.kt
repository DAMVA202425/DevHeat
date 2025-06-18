package com.example.devheat.resources

import android.os.AsyncTask
import android.util.Log
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class GetGitHubPP : AsyncTask<String?, Void?, String?>() {
    var token: String? = null
    var username: String? = null

    override fun doInBackground(vararg params: String?): String? {
        token = params[0]
        username = params[1]
        Log.w("username ", "username = $username")
        try {
            val url = URL("https://api.github.com/user")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.setRequestProperty("Authorization", "Bearer $token")
            connection.setRequestProperty("Accept", "application/vnd.github+json")
            connection.setRequestProperty("X-GitHub-Api-Version", "2022-11-28")

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val `in` = BufferedReader(InputStreamReader(connection.inputStream))
                var inputLine: String?
                val response = StringBuilder()
                while ((`in`.readLine().also { inputLine = it }) != null) {
                    response.append(inputLine)
                }
                `in`.close()

                val jsonResponse = JSONObject(response.toString())
                return jsonResponse.optString("avatar_url", "")
            } else {
                return null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    override fun onPostExecute(avatarUrl: String?) {
        if (avatarUrl != null && !avatarUrl.isEmpty()) {
            Log.d("Success", "URL de la foto de perfil obtenida: $avatarUrl")
        } else {
            Log.e("Error", "No se pudo obtener la URL de la foto de perfil")
        }
    }
}