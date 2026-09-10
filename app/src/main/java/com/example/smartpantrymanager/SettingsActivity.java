package com.example.smartpantrymanager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    private EditText etExpiryWarningDays;
    private Button btnSaveSettings;

    private SharedPreferences sharedPreferences;

    private static final String PREFS_NAME = "pantry_settings";
    private static final String KEY_EXPIRY_WARNING_DAYS = "expiry_warning_days";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        etExpiryWarningDays =
                findViewById(R.id.etExpiryWarningDays);

        btnSaveSettings =
                findViewById(R.id.btnSaveSettings);

        sharedPreferences =
                getSharedPreferences(
                        PREFS_NAME,
                        MODE_PRIVATE
                );

        loadSettings();

        btnSaveSettings.setOnClickListener(v -> {
            saveSettings();
        });
    }

    private void loadSettings() {

        int warningDays =
                sharedPreferences.getInt(
                        KEY_EXPIRY_WARNING_DAYS,
                        7
                );

        etExpiryWarningDays.setText(
                String.valueOf(warningDays)
        );
    }

    private void saveSettings() {

        String warningDaysText =
                etExpiryWarningDays
                        .getText()
                        .toString()
                        .trim();

        if (warningDaysText.isEmpty()) {

            etExpiryWarningDays.setError(
                    "Please enter the number of warning days"
            );

            etExpiryWarningDays.requestFocus();

            return;
        }

        int warningDays;

        try {

            warningDays =
                    Integer.parseInt(warningDaysText);

        } catch (NumberFormatException e) {

            etExpiryWarningDays.setError(
                    "Please enter a valid number"
            );

            return;
        }

        if (warningDays < 0) {

            etExpiryWarningDays.setError(
                    "Warning days cannot be negative"
            );

            return;
        }

        SharedPreferences.Editor editor =
                sharedPreferences.edit();

        editor.putInt(
                KEY_EXPIRY_WARNING_DAYS,
                warningDays
        );

        editor.apply();

        Toast.makeText(
                this,
                "Settings saved successfully",
                Toast.LENGTH_SHORT
        ).show();
    }
}