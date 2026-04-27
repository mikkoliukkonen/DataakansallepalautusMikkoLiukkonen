package com.example.dataakansalleliukkonenmikko;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.URL;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private EditText editTextMunicipality;
    private Button buttonSearch;
    private TextView textViewResult;
    private RecyclerView recyclerViewData;
    private LinearLayout layoutSearchHistory;
    private ImageView imageViewWeatherIcon;

    private DataRetriever dataRetriever;
    private DataItemAdapter dataItemAdapter;
    private List<DataItem> dataItems;
    private SearchHistoryManager searchHistoryManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextMunicipality = findViewById(R.id.editTextMunicipality);
        buttonSearch = findViewById(R.id.buttonSearch);
        textViewResult = findViewById(R.id.textViewResult);
        recyclerViewData = findViewById(R.id.recyclerViewData);
        layoutSearchHistory = findViewById(R.id.layoutSearchHistory);
        imageViewWeatherIcon = findViewById(R.id.imageViewWeatherIcon);

        dataRetriever = new DataRetriever();
        searchHistoryManager = new SearchHistoryManager(this);

        dataItems = new ArrayList<>();
        dataItemAdapter = new DataItemAdapter(dataItems);

        recyclerViewData.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewData.setAdapter(dataItemAdapter);

        buttonSearch.setOnClickListener(v -> {
            String municipalityName = editTextMunicipality.getText().toString().trim();
            searchMunicipalityData(municipalityName);
        });

        updateSearchHistoryButtons();
    }

    private void searchMunicipalityData(String municipalityName) {
        textViewResult.setText("Haetaan tietoja...");

        if (imageViewWeatherIcon != null) {
            imageViewWeatherIcon.setVisibility(View.GONE);
            imageViewWeatherIcon.setImageDrawable(null);
        }

        new Thread(() -> {
            try {
                MunicipalityData data = dataRetriever.getMunicipalityData(municipalityName);

                Bitmap weatherIcon = loadWeatherIcon(data.getWeatherIconCode());

                ArrayList<DataItem> newDataItems = new ArrayList<>();

                newDataItems.add(new DataItem(
                        "Väkiluku",
                        String.valueOf(data.getPopulation())
                ));

                newDataItems.add(new DataItem(
                        "Väestönlisäys",
                        String.valueOf(data.getPopulationChange())
                ));

                newDataItems.add(new DataItem(
                        "Työpaikkaomavaraisuus",
                        data.getWorkplaceSelfSufficiency() + " %"
                ));

                newDataItems.add(new DataItem(
                        "Työllisyysaste",
                        data.getEmploymentRate() + " %"
                ));

                newDataItems.add(new DataItem(
                        "Lämpötila",
                        data.getTemperature() + " °C"
                ));

                newDataItems.add(new DataItem(
                        "Sää",
                        data.getWeatherDescription()
                ));

                runOnUiThread(() -> {
                    textViewResult.setText(data.getName());
                    dataItemAdapter.updateData(newDataItems);

                    if (weatherIcon != null) {
                        imageViewWeatherIcon.setImageBitmap(weatherIcon);
                        imageViewWeatherIcon.setVisibility(View.VISIBLE);
                    } else {
                        imageViewWeatherIcon.setImageDrawable(null);
                        imageViewWeatherIcon.setVisibility(View.GONE);
                    }

                    searchHistoryManager.saveMunicipality(data.getName());
                    updateSearchHistoryButtons();
                });

            } catch (Exception e) {
                runOnUiThread(() -> {
                    textViewResult.setText("Tietojen haku epäonnistui.");
                    dataItemAdapter.updateData(createErrorItems(e));

                    if (imageViewWeatherIcon != null) {
                        imageViewWeatherIcon.setImageDrawable(null);
                        imageViewWeatherIcon.setVisibility(View.GONE);
                    }
                });
            }
        }).start();
    }

    private void updateSearchHistoryButtons() {
        layoutSearchHistory.removeAllViews();

        List<String> history = searchHistoryManager.getHistory();

        if (history.isEmpty()) {
            TextView emptyText = new TextView(this);
            emptyText.setText("Ei vielä hakuja");
            emptyText.setTextSize(14);
            layoutSearchHistory.addView(emptyText);
            return;
        }

        for (String municipalityName : history) {
            Button historyButton = new Button(this);
            historyButton.setText(municipalityName);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            params.setMargins(0, 0, 12, 0);
            historyButton.setLayoutParams(params);

            historyButton.setOnClickListener(v -> {
                editTextMunicipality.setText(municipalityName);
                searchMunicipalityData(municipalityName);
            });

            layoutSearchHistory.addView(historyButton);
        }
    }

    private List<DataItem> createDataItems(MunicipalityData data) {
        List<DataItem> items = new ArrayList<>();

        items.add(new DataItem("Väkiluku", String.valueOf(data.getPopulation())));
        items.add(new DataItem("Väestönlisäys", String.valueOf(data.getPopulationChange())));

        items.add(new DataItem(
                "Työpaikkaomavaraisuus",
                String.format(Locale.US, "%.1f %%", data.getWorkplaceSelfSufficiency())
        ));

        items.add(new DataItem(
                "Työllisyysaste",
                String.format(Locale.US, "%.1f %%", data.getEmploymentRate())
        ));

        items.add(new DataItem(
                "Lämpötila",
                String.format(Locale.US, "%.1f °C", data.getTemperature())
        ));

        items.add(new DataItem("Sää", data.getWeatherDescription()));

        return items;
    }
    private Bitmap loadWeatherIcon(String iconCode) {
        try {
            if (iconCode == null || iconCode.isEmpty()) {
                return null;
            }

            String iconUrl = "https://openweathermap.org/img/wn/" + iconCode + "@2x.png";
            InputStream inputStream = new URL(iconUrl).openStream();
            return BitmapFactory.decodeStream(inputStream);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private List<DataItem> createErrorItems(Exception e) {
        List<DataItem> items = new ArrayList<>();

        items.add(new DataItem("Virhe", e.getMessage()));

        return items;
    }
}
