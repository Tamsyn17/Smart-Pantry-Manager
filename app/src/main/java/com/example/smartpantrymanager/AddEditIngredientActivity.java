package com.example.smartpantrymanager;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class AddEditIngredientActivity extends AppCompatActivity {

    private EditText etName;
    private EditText etQuantity;
    private EditText etUnit;
    private EditText etExpiryDate;

    private DatabaseHelper databaseHelper;

    private boolean isEditMode = false;
    private int itemId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_ingredient);

        TextView tvTitle = findViewById(R.id.tvTitle);
        etName = findViewById(R.id.etName);
        etQuantity = findViewById(R.id.etQuantity);
        etUnit = findViewById(R.id.etUnit);
        etExpiryDate = findViewById(R.id.etExpiryDate);

        Button btnSave = findViewById(R.id.btnSave);

        databaseHelper = new DatabaseHelper(this);

        if (getIntent().hasExtra("item_id")) {

            isEditMode = true;

            itemId = getIntent().getIntExtra("item_id", -1);

            String name = getIntent().getStringExtra("item_name");
            double quantity = getIntent().getDoubleExtra("item_quantity", 0);
            String unit = getIntent().getStringExtra("item_unit");
            String expiryDate = getIntent().getStringExtra("item_expiry");

            tvTitle.setText("Edit Pantry Ingredient");
            btnSave.setText("Update Ingredient");

            etName.setText(name);
            etQuantity.setText(String.valueOf(quantity));
            etUnit.setText(unit);
            etExpiryDate.setText(expiryDate);
        }

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

        if (!expiryDate.isEmpty() && !isValidDate(expiryDate)) {
            etExpiryDate.setError("Use date format yyyy-MM-dd");
            etExpiryDate.requestFocus();
            return;
        }

        if (isEditMode) {

            PantryItem item = new PantryItem(
                    itemId,
                    name,
                    quantity,
                    unit,
                    expiryDate
            );

            int rowsUpdated =
                    databaseHelper.updatePantryItem(item);

            if (rowsUpdated > 0) {

                Toast.makeText(
                        this,
                        "Ingredient updated successfully",
                        Toast.LENGTH_SHORT
                ).show();

                finish();

            } else {

                Toast.makeText(
                        this,
                        "Failed to update ingredient",
                        Toast.LENGTH_SHORT
                ).show();
            }

        } else {

            PantryItem item = new PantryItem(
                    name,
                    quantity,
                    unit,
                    expiryDate
            );

            long result =
                    databaseHelper.addPantryItem(item);

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

    private boolean isValidDate(String dateText) {

        SimpleDateFormat dateFormat =
                new SimpleDateFormat(
                        "yyyy-MM-dd",
                        Locale.getDefault()
                );

        dateFormat.setLenient(false);

        try {

            dateFormat.parse(dateText);

            return true;

        } catch (ParseException e) {

            return false;
        }
    }
}