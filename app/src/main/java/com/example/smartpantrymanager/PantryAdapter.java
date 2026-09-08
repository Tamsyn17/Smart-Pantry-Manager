package com.example.smartpantrymanager;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.PantryViewHolder> {

    private final List<PantryItem> pantryItems;
    private final DatabaseHelper databaseHelper;

    public PantryAdapter(List<PantryItem> pantryItems, Context context) {
        this.pantryItems = pantryItems;
        this.databaseHelper = new DatabaseHelper(context);
    }

    @NonNull
    @Override
    public PantryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pantry, parent, false);

        return new PantryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PantryViewHolder holder, int position) {

        PantryItem item = pantryItems.get(position);

        holder.tvIngredientName.setText(item.getName());

        String quantityText =
                "Quantity: " + item.getQuantity() + " " + item.getUnit();

        holder.tvQuantity.setText(quantityText);

        if (item.getExpiryDate() == null || item.getExpiryDate().isEmpty()) {
            holder.tvExpiryDate.setText("Expiry: Not specified");
        } else {
            holder.tvExpiryDate.setText("Expiry: " + item.getExpiryDate());
        }

        // Normal tap = edit
        holder.itemView.setOnClickListener(v -> {

            Context context = v.getContext();

            Intent intent = new Intent(
                    context,
                    AddEditIngredientActivity.class
            );

            intent.putExtra("item_id", item.getId());
            intent.putExtra("item_name", item.getName());
            intent.putExtra("item_quantity", item.getQuantity());
            intent.putExtra("item_unit", item.getUnit());
            intent.putExtra("item_expiry", item.getExpiryDate());

            context.startActivity(intent);
        });

        // Long press = delete
        holder.itemView.setOnLongClickListener(v -> {

            Context context = v.getContext();

            new AlertDialog.Builder(context)
                    .setTitle("Delete Ingredient")
                    .setMessage("Are you sure you want to delete " + item.getName() + "?")
                    .setNegativeButton("Cancel", null)
                    .setPositiveButton("Delete", (dialog, which) -> {

                        int rowsDeleted =
                                databaseHelper.deletePantryItem(item.getId());

                        if (rowsDeleted > 0) {

                            int currentPosition =
                                    holder.getAdapterPosition();

                            if (currentPosition != RecyclerView.NO_POSITION) {
                                pantryItems.remove(currentPosition);
                                notifyItemRemoved(currentPosition);
                            }

                            Toast.makeText(
                                    context,
                                    "Ingredient deleted successfully",
                                    Toast.LENGTH_SHORT
                            ).show();

                        } else {

                            Toast.makeText(
                                    context,
                                    "Failed to delete ingredient",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    })
                    .show();

            return true;
        });
    }

    @Override
    public int getItemCount() {
        return pantryItems.size();
    }

    public static class PantryViewHolder extends RecyclerView.ViewHolder {

        TextView tvIngredientName;
        TextView tvQuantity;
        TextView tvExpiryDate;

        public PantryViewHolder(@NonNull View itemView) {
            super(itemView);

            tvIngredientName = itemView.findViewById(R.id.tvIngredientName);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvExpiryDate = itemView.findViewById(R.id.tvExpiryDate);
        }
    }
}