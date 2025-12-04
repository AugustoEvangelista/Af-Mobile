package com.example.medicamentos;

import android.os.AsyncTask;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ChuckApi {

    public interface JokeCallback {
        void onSuccess(String joke);
        void onError(String error);
    }

    public static void getJoke(JokeCallback callback) {
        new AsyncTask<Void, Void, String>() {

            String errorMessage = null;

            @Override
            protected String doInBackground(Void... voids) {
                try {
                    URL url = new URL("https://api.chucknorris.io/jokes/random");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");

                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(conn.getInputStream())
                    );

                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    reader.close();

                    JSONObject json = new JSONObject(result.toString());
                    return json.getString("value");

                } catch (Exception e) {
                    errorMessage = e.getMessage();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String joke) {
                if (joke != null)
                    callback.onSuccess(joke);
                else
                    callback.onError(errorMessage);
            }
        }.execute();
    }
}
