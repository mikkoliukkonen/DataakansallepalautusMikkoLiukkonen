package com.example.dataakansalleliukkonenmikko;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.Normalizer;
import java.util.Locale;

public class StatisticsRetriever {

    private static final String POPULATION_URL =
            "https://pxdata.stat.fi/PxWeb/api/v1/fi/StatFin/synt/statfin_synt_pxt_12dy.px";

    private static final String WORKPLACE_SELF_SUFFICIENCY_URL =
            "https://pxdata.stat.fi/PxWeb/api/v1/fi/StatFin/tyokay/statfin_tyokay_pxt_125s.px";

    private static final String EMPLOYMENT_RATE_URL =
            "https://pxdata.stat.fi/PxWeb/api/v1/fi/StatFin/tyokay/statfin_tyokay_pxt_115x.px";

    public StatisticsData getStatisticsData(String municipalityName) throws Exception {
        String municipalityCode = getMunicipalityCode(municipalityName);

        int population = getPopulation(municipalityCode);
        int populationChange = getPopulationChange(municipalityCode);
        double workplaceSelfSufficiency = getWorkplaceSelfSufficiency(municipalityCode);
        double employmentRate = getEmploymentRate(municipalityCode);

        return new StatisticsData(
                population,
                populationChange,
                workplaceSelfSufficiency,
                employmentRate
        );
    }

    private String getMunicipalityCode(String municipalityName) throws Exception {
        String metadata = getJsonFromUrl(POPULATION_URL);

        JSONObject root = new JSONObject(metadata);
        JSONArray variables = root.getJSONArray("variables");

        for (int i = 0; i < variables.length(); i++) {
            JSONObject variable = variables.getJSONObject(i);

            if (variable.getString("code").equals("Alue")) {
                JSONArray values = variable.getJSONArray("values");
                JSONArray valueTexts = variable.getJSONArray("valueTexts");

                String searchedName = normalize(municipalityName);

                for (int j = 0; j < valueTexts.length(); j++) {
                    String currentName = normalize(valueTexts.getString(j));

                    if (currentName.equals(searchedName)) {
                        return values.getString(j);
                    }
                }
            }
        }

        throw new Exception("Municipality was not found: " + municipalityName);
    }

    private int getPopulation(String municipalityCode) throws Exception {
        String query = createQuery(
                "2024",
                municipalityCode,
                new String[]{"vaesto"}
        );

        JSONObject response = postJson(POPULATION_URL, query);
        JSONArray values = response.getJSONArray("value");

        return values.getInt(0);
    }

    private int getPopulationChange(String municipalityCode) throws Exception {
        String query = createQuery(
                "2024",
                municipalityCode,
                new String[]{"valisays"}
        );

        JSONObject response = postJson(POPULATION_URL, query);
        JSONArray values = response.getJSONArray("value");

        return values.getInt(0);
    }

    private double getWorkplaceSelfSufficiency(String municipalityCode) throws Exception {
        String query = createQuery(
                "2023",
                municipalityCode,
                new String[]{"tyopaikkaomavaraisuus"}
        );

        JSONObject response = postJson(WORKPLACE_SELF_SUFFICIENCY_URL, query);
        JSONArray values = response.getJSONArray("value");

        return values.getDouble(0);
    }

    private double getEmploymentRate(String municipalityCode) throws Exception {
        String query = createQuery(
                "2024",
                municipalityCode,
                new String[]{"tyollisyysaste"}
        );

        JSONObject response = postJson(EMPLOYMENT_RATE_URL, query);
        JSONArray values = response.getJSONArray("value");

        return values.getDouble(0);
    }

    private String createQuery(String year, String municipalityCode, String[] dataCodes) {
        StringBuilder dataValues = new StringBuilder();

        for (int i = 0; i < dataCodes.length; i++) {
            dataValues.append("\"").append(dataCodes[i]).append("\"");

            if (i < dataCodes.length - 1) {
                dataValues.append(",");
            }
        }

        return "{"
                + "\"query\":["
                + "{"
                + "\"code\":\"Vuosi\","
                + "\"selection\":{"
                + "\"filter\":\"item\","
                + "\"values\":[\"" + year + "\"]"
                + "}"
                + "},"
                + "{"
                + "\"code\":\"Alue\","
                + "\"selection\":{"
                + "\"filter\":\"item\","
                + "\"values\":[\"" + municipalityCode + "\"]"
                + "}"
                + "},"
                + "{"
                + "\"code\":\"Tiedot\","
                + "\"selection\":{"
                + "\"filter\":\"item\","
                + "\"values\":[" + dataValues + "]"
                + "}"
                + "}"
                + "],"
                + "\"response\":{"
                + "\"format\":\"JSON-stat2\""
                + "}"
                + "}";
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
                throw new Exception("Statistics Finland metadata error: " + responseCode + " " + error);
            }

            return readResponse(connection.getInputStream());

        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private JSONObject postJson(String urlString, String jsonBody) throws Exception {
        HttpURLConnection connection = null;

        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);

            OutputStream outputStream = connection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

            writer.write(jsonBody);
            writer.flush();
            writer.close();
            outputStream.close();

            int responseCode = connection.getResponseCode();

            if (responseCode < 200 || responseCode >= 300) {
                String error = readResponse(connection.getErrorStream());
                throw new Exception("Statistics Finland API error: " + responseCode + " " + error);
            }

            String response = readResponse(connection.getInputStream());

            return new JSONObject(response);

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

    private String normalize(String text) {
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD);
        normalized = normalized.replaceAll("\\p{M}", "");
        normalized = normalized.toLowerCase(Locale.ROOT).trim();

        return normalized;
    }
}