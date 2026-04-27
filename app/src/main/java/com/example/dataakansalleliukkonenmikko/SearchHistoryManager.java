package com.example.dataakansalleliukkonenmikko;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SearchHistoryManager {

    private static final String PREFS_NAME = "search_history_prefs";
    private static final String KEY_HISTORY = "municipality_history";
    private static final int MAX_HISTORY_SIZE = 5;

    private SharedPreferences sharedPreferences;

    public SearchHistoryManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void saveMunicipality(String municipalityName) {
        List<String> history = getHistory();

        history.remove(municipalityName);
        history.add(0, municipalityName);

        if (history.size() > MAX_HISTORY_SIZE) {
            history = history.subList(0, MAX_HISTORY_SIZE);
        }

        String joinedHistory = String.join(";", history);

        sharedPreferences.edit()
                .putString(KEY_HISTORY, joinedHistory)
                .apply();
    }

    public List<String> getHistory() {
        String historyString = sharedPreferences.getString(KEY_HISTORY, "");

        List<String> history = new ArrayList<>();

        if (!historyString.isEmpty()) {
            history.addAll(Arrays.asList(historyString.split(";")));
        }

        return history;
    }

    public void clearHistory() {
        sharedPreferences.edit()
                .remove(KEY_HISTORY)
                .apply();
    }
}