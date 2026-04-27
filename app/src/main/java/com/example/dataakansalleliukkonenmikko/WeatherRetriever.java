package com.example.dataakansalleliukkonenmikko;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class WeatherRetriever {

    private static final String API_KEY = "ebf1824a427a09c0a8e22f26236b074f";

    public WeatherData getWeatherData(String municipalityName) throws Exception {
        String encodedMunicipality = URLEncoder.encode(municipalityName, "UTF-8");

        String urlString =
                "https://api.openweathermap.org/data/2.5/weather?q="
                        + encodedMunicipality
                        + ",FI&units=metric&appid="
                        + API_KEY;

        String json = getJsonFromUrl(urlString);

        JSONObject root = new JSONObject(json);

        JSONObject main = root.getJSONObject("main");
        double temperature = main.getDouble("temp");

        JSONArray weatherArray = root.getJSONArray("weather");
        JSONObject weatherObject = weatherArray.getJSONObject(0);
        String description = weatherObject.getString("description");

        return new WeatherData(temperature, description);
    }

    private String getJsonFromUrl(String urlString) throws Exception {
        HttpURLConnection connection = null;

        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");

            int responseCode = connection.getResponseCode();

            if (responseCode < 200 || responseCode >= 300) {
                String error = readResponse(connection.getErrorStream());
                throw new Exception("OpenWeather API error: " + responseCode + " " + error);
            }

            return readResponse(connection.getInputStream());

        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private String readResponse(InputStream inputStream) throws Exception {
        if (inputStream == null) {
            return "";
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder result = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            result.append(line);
        }

        reader.close();
        return result.toString();
    }
}