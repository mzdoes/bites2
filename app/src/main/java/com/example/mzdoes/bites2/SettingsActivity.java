package com.example.mzdoes.bites2;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SettingsActivity extends AppCompatActivity {

    // ANDROID WIDGETS
    private RadioGroup           languageSettings, countrySettings;
    private FloatingActionButton settingsApplyButton;

    // INSTANCE VARIABLES
    private String               chosenLanguage, chosenCountry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        chosenLanguage = chosenCountry = "";
        try {
            chosenLanguage = Utility.readString(getApplicationContext(), KeySettings.LANGUAGE_KEY);
            chosenCountry = Utility.readString(getApplicationContext(), KeySettings.COUNTRY_KEY);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        languageSettings = findViewById(R.id.radioGroup_languageSettings);
        countrySettings = findViewById(R.id.radioGroup_countrySettings);
        settingsApplyButton = findViewById(R.id.floatingActionButton_settingsApply);

        setWidgets();
    }

    private void setWidgets() {
        languageSettings.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                if (radioGroup.indexOfChild(findViewById(checkedId)) == 0) {chosenLanguage = "";}
                else if (radioGroup.indexOfChild(findViewById(checkedId)) == 1) {chosenLanguage = "en";}
                else if (radioGroup.indexOfChild(findViewById(checkedId)) == 2) {chosenLanguage = "es";}
                else if (radioGroup.indexOfChild(findViewById(checkedId)) == 3) {chosenLanguage = "fr";}

                Toast.makeText(SettingsActivity.this, chosenLanguage, Toast.LENGTH_SHORT).show();
            }
        });

        countrySettings.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                if (radioGroup.indexOfChild(findViewById(checkedId)) == 0) {chosenCountry = "";}
                else if (radioGroup.indexOfChild(findViewById(checkedId)) == 1) {chosenCountry = "ca";}
                else if (radioGroup.indexOfChild(findViewById(checkedId)) == 2) {chosenCountry = "fr";}
                else if (radioGroup.indexOfChild(findViewById(checkedId)) == 3) {chosenCountry = "gb";}
                else if (radioGroup.indexOfChild(findViewById(checkedId)) == 4) {chosenCountry = "mx";}
                else if (radioGroup.indexOfChild(findViewById(checkedId)) == 5) {chosenCountry = "us";}

                Toast.makeText(SettingsActivity.this, chosenCountry, Toast.LENGTH_SHORT).show();
            }
        });

        settingsApplyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Utility.saveString(getApplicationContext(), KeySettings.LANGUAGE_KEY, chosenLanguage);
                    Utility.saveString(getApplicationContext(), KeySettings.COUNTRY_KEY, chosenCountry);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Intent i = new Intent(SettingsActivity.this, MainActivity.class);
                setResult(RESULT_OK, i);
                i.putExtra(KeySettings.LANGUAGE_KEY, chosenLanguage);
                i.putExtra(KeySettings.COUNTRY_KEY, chosenCountry);
                overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
                finish();
            }
        });
    }

    private String getAbbreviation(String string) {
        Matcher m = Pattern.compile("\\(([^)]+)\\)").matcher(string);
        if (m.matches()) { return m.group(1); }
        else { return ""; }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
    }
}
