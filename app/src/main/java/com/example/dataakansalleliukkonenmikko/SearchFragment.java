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

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

public class SearchFragment extends Fragment {
    private EditText editTextMunicipality;
    private Button buttonSearch;
    private LinearLayout layoutSearchHistory;
    private TextView textViewResult;
    private ImageView imageViewWeatherIcon;
    private RecyclerView recyclerViewData;

    private DataRetriever dataRetriever;
    private SearchHistoryManager searchHistoryManager;
    private DataItemAdapter dataItemAdapter;
    private ArrayList<DataItem> dataItems;

    public SearchFragment() {
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        editTextMunicipality = view.findViewById(R.id.editTextMunicipality);
        buttonSearch = view.findViewById(R.id.buttonSearch);
        layoutSearchHistory = view.findViewById(R.id.layoutSearchHistory);
        textViewResult = view.findViewById(R.id.textViewResult);
        imageViewWeatherIcon = view.findViewById(R.id.imageViewWeatherIcon);
        recyclerViewData = view.findViewById(R.id.recyclerViewData);

        dataRetriever = new DataRetriever();
        searchHistoryManager = new SearchHistoryManager(requireContext());

        dataItems = new ArrayList<>();
        dataItemAdapter = new DataItemAdapter(dataItems);

        recyclerViewData.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerViewData.setAdapter(dataItemAdapter);

        updateSearchHistoryButtons();

        buttonSearch.setOnClickListener(v -> {
            String municipalityName = editTextMunicipality.getText().toString().trim();

            if (!municipalityName.isEmpty()) {
                searchMunicipality(municipalityName);
            }
        });

        return view;
    }

    private void searchMunicipality(String municipalityName) {
        textViewResult.setText("Haetaan tietoja...");
        imageViewWeatherIcon.setVisibility(View.GONE);
        dataItems.clear();
        dataItemAdapter.notifyDataSetChanged();

        new Thread(() -> {
            MunicipalityData municipalityData = dataRetriever.getMunicipalityData(municipalityName);

            System.out.println("ICON CODE: " + municipalityData.getWeatherIconCode());

            Bitmap weatherIcon = loadWeatherIcon(municipalityData.getWeatherIconCode());

            requireActivity().runOnUiThread(() -> {
                textViewResult.setText(municipalityData.getName());

                dataItems.clear();

                dataItems.add(new DataItem(
                        "Väkiluku",
                        String.valueOf(municipalityData.getPopulation())
                ));

                dataItems.add(new DataItem(
                        "Väestönlisäys",
                        String.valueOf(municipalityData.getPopulationChange())
                ));

                dataItems.add(new DataItem(
                        "Työpaikkaomavaraisuus",
                        municipalityData.getWorkplaceSelfSufficiency() + " %"
                ));

                dataItems.add(new DataItem(
                        "Työllisyysaste",
                        municipalityData.getEmploymentRate() + " %"
                ));

                dataItems.add(new DataItem(
                        "Lämpötila",
                        municipalityData.getTemperature() + " °C"
                ));

                dataItems.add(new DataItem(
                        "Sää",
                        municipalityData.getWeatherDescription()
                ));

                dataItemAdapter.notifyDataSetChanged();

                if (weatherIcon != null) {
                    imageViewWeatherIcon.setImageBitmap(weatherIcon);
                    imageViewWeatherIcon.setVisibility(View.VISIBLE);
                } else {
                    imageViewWeatherIcon.setImageDrawable(null);
                    imageViewWeatherIcon.setVisibility(View.GONE);
                }

                searchHistoryManager.addMunicipality(municipalityData.getName());
                updateSearchHistoryButtons();
            });
        }).start();
    }

    private void updateSearchHistoryButtons() {
        layoutSearchHistory.removeAllViews();

        ArrayList<String> history = searchHistoryManager.getSearchHistory();

        for (String municipality : history) {
            Button historyButton = new Button(requireContext());
            historyButton.setText(municipality);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            params.setMargins(0, 0, 12, 0);
            historyButton.setLayoutParams(params);

            historyButton.setOnClickListener(v -> {
                editTextMunicipality.setText(municipality);
                searchMunicipality(municipality);
            });

            layoutSearchHistory.addView(historyButton);
        }
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
}
