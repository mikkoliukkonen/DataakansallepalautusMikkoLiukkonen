package com.example.dataakansalleliukkonenmikko;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class WeatherRetriever {
    private static final String API_KEY = "ebf1824a427a09c0a8e22f26236b074f";

    public WeatherData getWeatherData(String municipalityName) {
        try {
            String encodedMunicipality = URLEncoder.encode(municipalityName + ",FI", "UTF-8");

            String urlString =
                    "https://api.openweathermap.org/data/2.5/weather?q="
                            + encodedMunicipality
                            + "&appid="
                            + API_KEY
                            + "&units=metric"
                            + "&lang=fi";

            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream())
            );

            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            reader.close();

            JSONObject jsonObject = new JSONObject(response.toString());

            JSONObject mainObject = jsonObject.getJSONObject("main");
            double temperature = mainObject.getDouble("temp");

            JSONArray weatherArray = jsonObject.getJSONArray("weather");
            JSONObject weatherObject = weatherArray.getJSONObject(0);

            String description = weatherObject.getString("description");
            String iconCode = weatherObject.getString("icon");

            return new WeatherData(temperature, description, iconCode);

        } catch (Exception e) {
            e.printStackTrace();
            return new WeatherData(0, "Säätietoa ei saatavilla", "");
        }
    }
}