package com.example.smartpantrymanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.PantryViewHolder> {

    private final List<PantryItem> pantryItems;

    public PantryAdapter(List<PantryItem> pantryItems) {
        this.pantryItems = pantryItems;
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