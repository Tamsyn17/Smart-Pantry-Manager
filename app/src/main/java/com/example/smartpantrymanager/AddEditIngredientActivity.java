package com.example.smartpantrymanager;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddEditIngredientActivity extends AppCompatActivity {

    private EditText etName;
    private EditText etQuantity;
    private EditText etUnit;
    private EditText etExpiryDate;

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_ingredient);

        etName = findViewById(R.id.etName);
        etQuantity = findViewById(R.id.etQuantity);
        etUnit = findViewById(R.id.etUnit);
        etExpiryDate = findViewById(R.id.etExpiryDate);

        Button btnSave = findViewById(R.id.btnSave);

        databaseHelper = new DatabaseHelper(this);

        btnSave.setOnClickListener(v -> saveIngredient());
    }

    private void saveIngredient() {

        String name = etName.getText().toString().trim();
        String quantityText = etQuantity.getText().toString().trim();
        String unit = etUnit.getText().toString().trim();
        String expiryDate = etExpiryDate.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            etName.setError("Ingredient name is required");
            etName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(quantityText)) {
            etQuantity.setError("Quantity is required");
            etQuantity.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(unit)) {
            etUnit.setError("Unit is required");
            etUnit.requestFocus();
            return;
        }

        double quantity;

        try {
            quantity = Double.parseDouble(quantityText);
        } catch (NumberFormatException e) {
            etQuantity.setError("Enter a valid quantity");
            etQuantity.requestFocus();
            return;
        }

        if (quantity <= 0) {
            etQuantity.setError("Quantity must be greater than 0");
            etQuantity.requestFocus();
            return;
        }

        PantryItem item = new PantryItem(
                name,
                quantity,
                unit,
                expiryDate
        );

        long result = databaseHelper.addPantryItem(item);

        if (result != -1) {
            Toast.makeText(
                    this,
                    "Ingredient saved successfully",
                    Toast.LENGTH_SHORT
            ).show();

            finish();

        } else {
            Toast.makeText(
                    this,
                    "Failed to save ingredient",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }
}