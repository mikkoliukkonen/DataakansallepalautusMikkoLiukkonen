package com.example.dataakansalleliukkonenmikko;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Locale;

public class CompareFragment extends Fragment {

    private EditText editTextCompareMunicipalityOne;
    private EditText editTextCompareMunicipalityTwo;
    private Button buttonCompare;
    private TextView textViewCompareResult;

    private DataRetriever dataRetriever;
    private SearchHistoryManager searchHistoryManager;

    public CompareFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_compare, container, false);

        editTextCompareMunicipalityOne = view.findViewById(R.id.editTextCompareMunicipalityOne);
        editTextCompareMunicipalityTwo = view.findViewById(R.id.editTextCompareMunicipalityTwo);
        buttonCompare = view.findViewById(R.id.buttonCompare);
        textViewCompareResult = view.findViewById(R.id.textViewCompareResult);

        dataRetriever = new DataRetriever();
        searchHistoryManager = new SearchHistoryManager(requireContext());

        buttonCompare.setOnClickListener(v -> compareMunicipalities());

        return view;
    }

    private void compareMunicipalities() {
        String firstName = editTextCompareMunicipalityOne.getText().toString().trim();
        String secondName = editTextCompareMunicipalityTwo.getText().toString().trim();

        if (firstName.isEmpty() || secondName.isEmpty()) {
            textViewCompareResult.setText("Kirjoita molemmat kunnat.");
            return;
        }

        textViewCompareResult.setText("Haetaan vertailutietoja...");

        new Thread(() -> {
            try {
                MunicipalityData firstData = dataRetriever.getMunicipalityData(firstName);
                MunicipalityData secondData = dataRetriever.getMunicipalityData(secondName);

                String comparison = createComparisonText(firstData, secondData);

                if (getActivity() == null) {
                    return;
                }

                requireActivity().runOnUiThread(() -> {
                    textViewCompareResult.setText(comparison);

                    searchHistoryManager.addMunicipality(firstData.getName());
                    searchHistoryManager.addMunicipality(secondData.getName());
                });

            } catch (Exception e) {
                if (getActivity() == null) {
                    return;
                }

                requireActivity().runOnUiThread(() -> textViewCompareResult.setText(
                        "Vertailu epäonnistui.\n\nVirhe:\n" + e.getMessage()
                ));
            }
        }).start();
    }

    private String createComparisonText(MunicipalityData first, MunicipalityData second) {
        String biggerPopulation = getBiggerText(
                first.getName(),
                second.getName(),
                first.getPopulation(),
                second.getPopulation()
        );

        String biggerEmploymentRate = getBiggerText(
                first.getName(),
                second.getName(),
                first.getEmploymentRate(),
                second.getEmploymentRate()
        );

        String biggerTemperature = getBiggerText(
                first.getName(),
                second.getName(),
                first.getTemperature(),
                second.getTemperature()
        );

        String biggerWorkplaceSelfSufficiency = getBiggerText(
                first.getName(),
                second.getName(),
                first.getWorkplaceSelfSufficiency(),
                second.getWorkplaceSelfSufficiency()
        );

        return String.format(
                Locale.US,
                "%s vs %s\n\n" +

                        "Väkiluku:\n" +
                        "%s: %d\n" +
                        "%s: %d\n" +
                        "Suurempi: %s\n\n" +

                        "Väestönlisäys:\n" +
                        "%s: %d\n" +
                        "%s: %d\n\n" +

                        "Työpaikkaomavaraisuus:\n" +
                        "%s: %.1f %%\n" +
                        "%s: %.1f %%\n" +
                        "Korkeampi: %s\n\n" +

                        "Työllisyysaste:\n" +
                        "%s: %.1f %%\n" +
                        "%s: %.1f %%\n" +
                        "Korkeampi: %s\n\n" +

                        "Lämpötila:\n" +
                        "%s: %.1f °C\n" +
                        "%s: %.1f °C\n" +
                        "Lämpimämpi: %s\n\n" +

                        "Sää:\n" +
                        "%s: %s\n" +
                        "%s: %s",

                first.getName(), second.getName(),

                first.getName(), first.getPopulation(),
                second.getName(), second.getPopulation(),
                biggerPopulation,

                first.getName(), first.getPopulationChange(),
                second.getName(), second.getPopulationChange(),

                first.getName(), first.getWorkplaceSelfSufficiency(),
                second.getName(), second.getWorkplaceSelfSufficiency(),
                biggerWorkplaceSelfSufficiency,

                first.getName(), first.getEmploymentRate(),
                second.getName(), second.getEmploymentRate(),
                biggerEmploymentRate,

                first.getName(), first.getTemperature(),
                second.getName(), second.getTemperature(),
                biggerTemperature,

                first.getName(), first.getWeatherDescription(),
                second.getName(), second.getWeatherDescription()
        );
    }

    private String getBiggerText(String firstName, String secondName, double firstValue, double secondValue) {
        if (firstValue > secondValue) {
            return firstName;
        } else if (secondValue > firstValue) {
            return secondName;
        } else {
            return "Sama arvo";
        }
    }
}