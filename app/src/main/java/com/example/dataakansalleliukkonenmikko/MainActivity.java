package com.example.dataakansalleliukkonenmikko;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class MainActivity extends AppCompatActivity {
    private Button buttonOpenSearch;
    private Button buttonOpenCompare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonOpenSearch = findViewById(R.id.buttonOpenSearch);
        buttonOpenCompare = findViewById(R.id.buttonOpenCompare);

        buttonOpenSearch.setOnClickListener(v -> {
            openFragment(new SearchFragment());
        });

        buttonOpenCompare.setOnClickListener(v -> {
            openFragment(new CompareFragment());
        });

        if (savedInstanceState == null) {
            openFragment(new SearchFragment());
        }
    }

    private void openFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }
}
