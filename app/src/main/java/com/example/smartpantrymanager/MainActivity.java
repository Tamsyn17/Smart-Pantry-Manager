package com.example.smartpantrymanager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerViewPantry;
    private TextView tvEmptyPantry;
    private EditText etSearchPantry;

    private PantryAdapter pantryAdapter;
    private DatabaseHelper databaseHelper;

    private List<PantryItem> allPantryItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbarMain = findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbarMain);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
        }

        recyclerViewPantry = findViewById(R.id.recyclerViewPantry);
        tvEmptyPantry = findViewById(R.id.tvEmptyPantry);
        etSearchPantry = findViewById(R.id.etSearchPantry);

        Button btnSettings = findViewById(R.id.btnSettings);
        Button btnSuggestedRecipes = findViewById(R.id.btnSuggestedRecipes);
        Button btnAddIngredient = findViewById(R.id.btnAddIngredient);

        TextView tvBottomRecipes = findViewById(R.id.tvBottomRecipes);
        TextView tvBottomSettings = findViewById(R.id.tvBottomSettings);

        databaseHelper = new DatabaseHelper(this);

        recyclerViewPantry.setLayoutManager(
                new LinearLayoutManager(this)
        );

        btnSettings.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    SettingsActivity.class
            );
            startActivity(intent);
        });

        btnSuggestedRecipes.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    SuggestedRecipesActivity.class
            );
            startActivity(intent);
        });

        btnAddIngredient.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    AddEditIngredientActivity.class
            );
            startActivity(intent);
        });

        tvBottomRecipes.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    SuggestedRecipesActivity.class
            );
            startActivity(intent);
        });

        tvBottomSettings.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    SettingsActivity.class
            );
            startActivity(intent);
        });

        etSearchPantry.addTextChangedListener(
                new TextWatcher() {

                    @Override
                    public void beforeTextChanged(
                            CharSequence s,
                            int start,
                            int count,
                            int after) {
                    }

                    @Override
                    public void onTextChanged(
                            CharSequence s,
                            int start,
                            int before,
                            int count) {

                        filterPantryItems(
                                s.toString()
                        );
                    }

                    @Override
                    public void afterTextChanged(
                            Editable s) {
                    }
                }
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPantryItems();
    }

    private void loadPantryItems() {

        allPantryItems =
                databaseHelper.getAllPantryItems();

        String searchText =
                etSearchPantry.getText()
                        .toString()
                        .trim();

        if (searchText.isEmpty()) {

            showPantryItems(
                    new ArrayList<>(allPantryItems)
            );

        } else {

            filterPantryItems(searchText);
        }
    }

    private void filterPantryItems(
            String searchText) {

        List<PantryItem> filteredItems =
                new ArrayList<>();

        String query =
                searchText
                        .trim()
                        .toLowerCase(Locale.ROOT);

        if (query.isEmpty()) {

            filteredItems.addAll(
                    allPantryItems
            );

        } else {

            for (PantryItem item : allPantryItems) {

                String ingredientName =
                        item.getName()
                                .toLowerCase(Locale.ROOT);

                if (ingredientName.contains(query)) {

                    filteredItems.add(item);
                }
            }
        }

        showPantryItems(filteredItems);
    }

    private void showPantryItems(
            List<PantryItem> pantryItems) {

        if (pantryItems.isEmpty()) {

            recyclerViewPantry.setVisibility(
                    View.GONE
            );

            tvEmptyPantry.setVisibility(
                    View.VISIBLE
            );

        } else {

            recyclerViewPantry.setVisibility(
                    View.VISIBLE
            );

            tvEmptyPantry.setVisibility(
                    View.GONE
            );
        }

        pantryAdapter =
                new PantryAdapter(
                        pantryItems,
                        this
                );

        recyclerViewPantry.setAdapter(
                pantryAdapter
        );
    }

    @Override
    public boolean onCreateOptionsMenu(
            Menu menu) {

        getMenuInflater().inflate(
                R.menu.main_menu,
                menu
        );

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(
            @NonNull MenuItem item) {

        int itemId =
                item.getItemId();

        if (itemId == R.id.menuPantry) {

            return true;

        } else if (
                itemId == R.id.menuSuggestedRecipes) {

            Intent intent =
                    new Intent(
                            MainActivity.this,
                            SuggestedRecipesActivity.class
                    );

            startActivity(intent);

            return true;

        } else if (
                itemId == R.id.menuSettings) {

            Intent intent =
                    new Intent(
                            MainActivity.this,
                            SettingsActivity.class
                    );

            startActivity(intent);

            return true;
        }

        return super.onOptionsItemSelected(
                item
        );
    }
}