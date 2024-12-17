package com.example.devheat.resources;
import android.os.AsyncTask;
import android.util.Log;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetGitHubPP extends AsyncTask<String, Void, String> {

    String token, username;

    @Override
    protected String doInBackground(String... params) {
        token = params[0];
        username = params[1];
        Log.w("username ","username = " + username);
        try {
            URL url = new URL("https://api.github.com/user");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Bearer " + token);
            connection.setRequestProperty("Accept", "application/vnd.github+json");
            connection.setRequestProperty("X-GitHub-Api-Version", "2022-11-28");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                JSONObject jsonResponse = new JSONObject(response.toString());
                return jsonResponse.optString("avatar_url", "");
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(String avatarUrl) {
        if (avatarUrl != null && !avatarUrl.isEmpty()) {
            Log.d("Success", "URL de la foto de perfil obtenida: " + avatarUrl);
        } else {
            Log.e("Error", "No se pudo obtener la URL de la foto de perfil");
        }
    }
}
