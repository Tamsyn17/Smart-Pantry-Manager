package com.example.smartpantrymanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerViewPantry;
    private TextView tvEmptyPantry;
    private PantryAdapter pantryAdapter;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbarMain = findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbarMain);

        recyclerViewPantry = findViewById(R.id.recyclerViewPantry);
        tvEmptyPantry = findViewById(R.id.tvEmptyPantry);

        Button btnSettings = findViewById(R.id.btnSettings);
        Button btnSuggestedRecipes = findViewById(R.id.btnSuggestedRecipes);
        Button btnAddIngredient = findViewById(R.id.btnAddIngredient);

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
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPantryItems();
    }

    private void loadPantryItems() {

        List<PantryItem> pantryItems =
                databaseHelper.getAllPantryItems();

        if (pantryItems.isEmpty()) {

            recyclerViewPantry.setVisibility(View.GONE);
            tvEmptyPantry.setVisibility(View.VISIBLE);

        } else {

            recyclerViewPantry.setVisibility(View.VISIBLE);
            tvEmptyPantry.setVisibility(View.GONE);
        }

        pantryAdapter =
                new PantryAdapter(pantryItems, this);

        recyclerViewPantry.setAdapter(pantryAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(
                R.menu.main_menu,
                menu
        );

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(
            @NonNull MenuItem item) {

        int itemId = item.getItemId();

        if (itemId == R.id.menuPantry) {

            return true;

        } else if (itemId == R.id.menuSuggestedRecipes) {

            Intent intent = new Intent(
                    MainActivity.this,
                    SuggestedRecipesActivity.class
            );

            startActivity(intent);
            return true;

        } else if (itemId == R.id.menuSettings) {

            Intent intent = new Intent(
                    MainActivity.this,
                    SettingsActivity.class
            );

            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}