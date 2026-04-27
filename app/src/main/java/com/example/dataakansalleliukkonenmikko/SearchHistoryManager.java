package com.example.dataakansalleliukkonenmikko;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;

public class SearchHistoryManager {
    private static final String PREFS_NAME = "search_history_prefs";
    private static final String KEY_HISTORY = "search_history";
    private static final int MAX_HISTORY_SIZE = 5;

    private SharedPreferences sharedPreferences;

    public SearchHistoryManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void addMunicipality(String municipalityName) {
        ArrayList<String> history = getSearchHistory();

        String formattedName = municipalityName.trim().toUpperCase();

        history.remove(formattedName);
        history.add(0, formattedName);

        while (history.size() > MAX_HISTORY_SIZE) {
            history.remove(history.size() - 1);
        }

        saveSearchHistory(history);
    }

    public void saveMunicipality(String municipalityName) {
        addMunicipality(municipalityName);
    }

    public ArrayList<String> getSearchHistory() {
        String savedHistory = sharedPreferences.getString(KEY_HISTORY, "");

        ArrayList<String> history = new ArrayList<>();

        if (!savedHistory.isEmpty()) {
            String[] municipalities = savedHistory.split(";");

            for (String municipality : municipalities) {
                if (!municipality.trim().isEmpty()) {
                    history.add(municipality);
                }
            }
        }

        return history;
    }

    public ArrayList<String> getHistory() {
        return getSearchHistory();
    }

    private void saveSearchHistory(ArrayList<String> history) {
        StringBuilder stringBuilder = new StringBuilder();

        for (String municipality : history) {
            stringBuilder.append(municipality).append(";");
        }

        sharedPreferences.edit()
                .putString(KEY_HISTORY, stringBuilder.toString())
                .apply();
    }
}