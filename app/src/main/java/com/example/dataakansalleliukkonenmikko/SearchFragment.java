package com.example.dataakansalleliukkonenmikko;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SearchFragment extends Fragment {

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

    public SearchFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_search, container, false);

        editTextMunicipality = view.findViewById(R.id.editTextMunicipality);
        buttonSearch = view.findViewById(R.id.buttonSearch);
        textViewResult = view.findViewById(R.id.textViewResult);
        recyclerViewData = view.findViewById(R.id.recyclerViewData);
        layoutSearchHistory = view.findViewById(R.id.layoutSearchHistory);
        imageViewWeatherIcon = view.findViewById(R.id.imageViewWeatherIcon);

        dataRetriever = new DataRetriever();
        searchHistoryManager = new SearchHistoryManager(requireContext());

        dataItems = new ArrayList<>();
        dataItemAdapter = new DataItemAdapter(dataItems);

        recyclerViewData.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerViewData.setAdapter(dataItemAdapter);

        buttonSearch.setOnClickListener(v -> {
            String municipalityName = editTextMunicipality.getText().toString().trim();
            searchMunicipalityData(municipalityName);
        });

        updateSearchHistoryButtons();

        return view;
    }

    private void searchMunicipalityData(String municipalityName) {
        if (municipalityName.isEmpty()) {
            textViewResult.setText("Kirjoita kunnan nimi.");
            dataItemAdapter.updateData(new ArrayList<>());
            imageViewWeatherIcon.setVisibility(View.GONE);
            return;
        }

        textViewResult.setText("Haetaan tietoja...");
        dataItemAdapter.updateData(new ArrayList<>());
        imageViewWeatherIcon.setVisibility(View.GONE);

        new Thread(() -> {
            try {
                MunicipalityData data = dataRetriever.getMunicipalityData(municipalityName);
                List<DataItem> newDataItems = createDataItems(data);
                Bitmap weatherIcon = loadWeatherIcon(data.getWeatherIconCode());

                if (getActivity() == null) {
                    return;
                }

                requireActivity().runOnUiThread(() -> {
                    textViewResult.setText(data.getName());
                    dataItemAdapter.updateData(newDataItems);

                    if (weatherIcon != null) {
                        imageViewWeatherIcon.setImageBitmap(weatherIcon);
                        imageViewWeatherIcon.setVisibility(View.VISIBLE);
                    } else {
                        imageViewWeatherIcon.setVisibility(View.GONE);
                    }

                    searchHistoryManager.saveMunicipality(data.getName());
                    updateSearchHistoryButtons();
                });

            } catch (Exception e) {
                if (getActivity() == null) {
                    return;
                }

                requireActivity().runOnUiThread(() -> {
                    textViewResult.setText("Tietojen haku epäonnistui.");
                    dataItemAdapter.updateData(createErrorItems(e));
                    imageViewWeatherIcon.setVisibility(View.GONE);
                });
            }
        }).start();
    }

    private Bitmap loadWeatherIcon(String iconCode) {
        try {
            String iconUrl = "https://openweathermap.org/img/wn/" + iconCode + "@2x.png";
            InputStream inputStream = new URL(iconUrl).openStream();
            return BitmapFactory.decodeStream(inputStream);
        } catch (Exception e) {
            return null;
        }
    }

    private void updateSearchHistoryButtons() {
        layoutSearchHistory.removeAllViews();

        List<String> history = searchHistoryManager.getHistory();

        if (history.isEmpty()) {
            TextView emptyText = new TextView(requireContext());
            emptyText.setText("Ei vielä hakuja");
            emptyText.setTextSize(14);
            layoutSearchHistory.addView(emptyText);
            return;
        }

        for (String municipalityName : history) {
            Button historyButton = new Button(requireContext());
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
        items.add(new DataItem("Sääikonin koodi", data.getWeatherIconCode()));

        return items;
    }

    private List<DataItem> createErrorItems(Exception e) {
        List<DataItem> items = new ArrayList<>();
        items.add(new DataItem("Virhe", e.getMessage()));
        return items;
    }
}
