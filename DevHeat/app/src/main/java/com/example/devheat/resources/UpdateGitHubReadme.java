package com.example.devheat.resources;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Base64;

import org.json.JSONObject;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateGitHubReadme extends AsyncTask<String, Void, Boolean> {

    private static final String TAG = "UpdateGitHubReadme";

    @Override
    protected Boolean doInBackground(String... params) {
        String token = params[0];
        String username = params[1];
        String newContent = params[2];
        try {
            // Primero, obtenemos el SHA del archivo actual
            URL getUrl = new URL("https://api.github.com/repos/" + username + "/" + username + "/contents/README.md");
            HttpURLConnection getConnection = (HttpURLConnection) getUrl.openConnection();
            getConnection.setRequestMethod("GET");
            getConnection.setRequestProperty("Authorization", "token " + token);
            getConnection.setRequestProperty("Accept", "application/vnd.github.v3+json");

            int getResponseCode = getConnection.getResponseCode();
            if (getResponseCode != 200) {
                Log.e(TAG, "Error al obtener el README: " + getResponseCode);
                return false;
            }

            JSONObject getResponse = new JSONObject(new String(getConnection.getInputStream().readAllBytes()));
            String sha = getResponse.getString("sha");

            // Ahora, actualizamos el contenido
            URL updateUrl = new URL("https://api.github.com/repos/" + username + "/" + username + "/contents/README.md");
            HttpURLConnection updateConnection = (HttpURLConnection) updateUrl.openConnection();
            updateConnection.setRequestMethod("PUT");
            updateConnection.setRequestProperty("Authorization", "token " + token);
            updateConnection.setRequestProperty("Content-Type", "application/json");
            updateConnection.setRequestProperty("Accept", "application/vnd.github.v3+json");
            updateConnection.setDoOutput(true);

            JSONObject jsonBody = new JSONObject();
            jsonBody.put("message", "Update README");
            jsonBody.put("content", Base64.encodeToString(newContent.getBytes(), Base64.NO_WRAP));
            jsonBody.put("sha", sha);

            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(updateConnection.getOutputStream()));
            writer.write(jsonBody.toString());
            writer.close();

            int updateResponseCode = updateConnection.getResponseCode();
            return updateResponseCode == 200;
        } catch (Exception e) {
            Log.e(TAG, "Error al actualizar el README", e);
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean success) {
        if (success) {
            Log.d(TAG, "README actualizado con éxito");
        } else {
            Log.e(TAG, "Error al actualizar el README");
        }
    }
}
