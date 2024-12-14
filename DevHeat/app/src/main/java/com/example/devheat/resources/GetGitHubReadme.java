package com.example.devheat.resources;
import android.os.AsyncTask;
import android.util.Log;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import android.util.Base64;

public class GetGitHubReadme extends AsyncTask<String, Void, String> {

    private static final String TAG = "GetGitHubReadme";

    @Override
    protected String doInBackground(String... params) {
        String token = params[0];
        String username = params[1];
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            URL url = new URL("https://api.github.com/repos/" + username + "/" + username + "/readme");
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "token " + token);
            connection.setRequestProperty("Accept", "application/vnd.github.v3+json");

            int responseCode = connection.getResponseCode();
            Log.d(TAG, "Response Code: " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                JSONObject jsonResponse = new JSONObject(response.toString());
                String content = jsonResponse.optString("content", "");
                if (!content.isEmpty()) {
                    byte[] decodedBytes = Base64.decode(content, Base64.DEFAULT);
                    return new String(decodedBytes, "UTF-8");
                }
            } else {
                Log.e(TAG, "Error response: " + connection.getResponseMessage());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error en la solicitud", e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e) {
                    Log.e(TAG, "Error al cerrar el reader", e);
                }
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(String readmeContent) {
        if (readmeContent != null && !readmeContent.isEmpty()) {
            Log.d(TAG, "Contenido del README obtenido: " + readmeContent.substring(0, Math.min(readmeContent.length(), 100)) + "...");
            // Aquí puedes actualizar tu TextView con el contenido del README
            // Por ejemplo: textView.setText(readmeContent);
        } else {
            Log.e(TAG, "No se pudo obtener el contenido del README");
        }
    }
}


