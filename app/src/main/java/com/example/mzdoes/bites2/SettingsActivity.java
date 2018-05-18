package com.example.mzdoes.bites2;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SettingsActivity extends AppCompatActivity {

    /**  SETTINGSACTIVITY INSTANCE VARIABLES  **/
    // ANDROID WIDGETS
    private RadioGroup           languageSettings, countrySettings;
    private FloatingActionButton settingsApplyButton;
    private EditText             topicEditText;

    // INSTANCE VARIABLES
    private String               chosenLanguage, chosenCountry, chosenTopic;


    /** ---  METHODS AND STUFF  ---- **/
    // APP WIDGETS AND TOOLS METHODS
    private void setWidgets() {
        languageSettings.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                if (radioGroup.indexOfChild(findViewById(checkedId)) == 0) {chosenLanguage = "";}
                else if (radioGroup.indexOfChild(findViewById(checkedId)) == 1) {chosenLanguage = "en";}
                else if (radioGroup.indexOfChild(findViewById(checkedId)) == 2) {chosenLanguage = "es";}
                else if (radioGroup.indexOfChild(findViewById(checkedId)) == 3) {chosenLanguage = "fr";}

            }
        });

        if (chosenLanguage.equals("")) { RadioButton b = findViewById(R.id.radioButton_languageAll); b.setChecked(true); }
        else if (chosenLanguage.equals("en")) { RadioButton b = findViewById(R.id.radioButton_languageEN); b.setChecked(true); }
        else if (chosenLanguage.equals("es")) { RadioButton b = findViewById(R.id.radioButton_languageES); b.setChecked(true); }
        else if (chosenLanguage.equals("fr")) { RadioButton b = findViewById(R.id.radioButton_languageFR); b.setChecked(true); }

        countrySettings.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                if (radioGroup.indexOfChild(findViewById(checkedId)) == 0) {chosenCountry = "";}
                else if (radioGroup.indexOfChild(findViewById(checkedId)) == 1) {chosenCountry = "ca";}
                else if (radioGroup.indexOfChild(findViewById(checkedId)) == 2) {chosenCountry = "fr";}
                else if (radioGroup.indexOfChild(findViewById(checkedId)) == 3) {chosenCountry = "gb";}
                else if (radioGroup.indexOfChild(findViewById(checkedId)) == 4) {chosenCountry = "mx";}
                else if (radioGroup.indexOfChild(findViewById(checkedId)) == 5) {chosenCountry = "us";}

            }
        });

        if (chosenCountry.equals("")) { RadioButton b = findViewById(R.id.radioButton_countryAll); b.setChecked(true); }
        else if (chosenCountry.equals("ca")) { RadioButton b = findViewById(R.id.radioButton_countryCA); b.setChecked(true); }
        else if (chosenCountry.equals("fr")) { RadioButton b = findViewById(R.id.radioButton_countryFR); b.setChecked(true); }
        else if (chosenCountry.equals("gb")) { RadioButton b = findViewById(R.id.radioButton_countryGB); b.setChecked(true); }
        else if (chosenCountry.equals("mx")) { RadioButton b = findViewById(R.id.radioButton_countryMX); b.setChecked(true); }
        else if (chosenCountry.equals("us")) { RadioButton b = findViewById(R.id.radioButton_countryUS); b.setChecked(true); }


        topicEditText.setText(chosenTopic);

        settingsApplyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chosenTopic = topicEditText.getText().toString();

                try {
                    Utility.saveString(getApplicationContext(), KeySettings.LANGUAGE_KEY, chosenLanguage);
                    Utility.saveString(getApplicationContext(), KeySettings.COUNTRY_KEY, chosenCountry);
                    Utility.saveString(getApplicationContext(), KeySettings.DEFAULT_TOPIC_KEY, chosenTopic);
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

    // MISCELLANEOUS
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        chosenLanguage = chosenCountry = chosenTopic = "";
        try {
            chosenLanguage = Utility.readString(getApplicationContext(), KeySettings.LANGUAGE_KEY);
            chosenCountry = Utility.readString(getApplicationContext(), KeySettings.COUNTRY_KEY);
            chosenTopic = Utility.readString(getApplicationContext(), KeySettings.DEFAULT_TOPIC_KEY);
        } catch  (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        languageSettings = findViewById(R.id.radioGroup_languageSettings);
        countrySettings = findViewById(R.id.radioGroup_countrySettings);
        topicEditText = findViewById(R.id.editText_defaultTopic);
        settingsApplyButton = findViewById(R.id.floatingActionButton_settingsApply);

        setWidgets();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
    }
}
