package com.example.smartpantrymanager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.PantryViewHolder> {

    private final List<PantryItem> pantryItems;
    private final DatabaseHelper databaseHelper;
    private final SharedPreferences sharedPreferences;

    private static final String PREFS_NAME = "pantry_settings";
    private static final String KEY_EXPIRY_WARNING_DAYS = "expiry_warning_days";

    public PantryAdapter(List<PantryItem> pantryItems, Context context) {
        this.pantryItems = pantryItems;

        this.databaseHelper =
                new DatabaseHelper(context);

        this.sharedPreferences =
                context.getSharedPreferences(
                        PREFS_NAME,
                        Context.MODE_PRIVATE
                );
    }

    @NonNull
    @Override
    public PantryViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(
                        R.layout.item_pantry,
                        parent,
                        false
                );

        return new PantryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull PantryViewHolder holder,
            int position) {

        PantryItem item =
                pantryItems.get(position);

        holder.tvIngredientName.setText(
                item.getName()
        );

        String quantityText =
                "Quantity: "
                        + item.getQuantity()
                        + " "
                        + item.getUnit();

        holder.tvQuantity.setText(quantityText);

        displayExpiryStatus(
                holder,
                item
        );

        // Normal tap = edit ingredient
        holder.itemView.setOnClickListener(v -> {

            Context context =
                    v.getContext();

            Intent intent = new Intent(
                    context,
                    AddEditIngredientActivity.class
            );

            intent.putExtra(
                    "item_id",
                    item.getId()
            );

            intent.putExtra(
                    "item_name",
                    item.getName()
            );

            intent.putExtra(
                    "item_quantity",
                    item.getQuantity()
            );

            intent.putExtra(
                    "item_unit",
                    item.getUnit()
            );

            intent.putExtra(
                    "item_expiry",
                    item.getExpiryDate()
            );

            context.startActivity(intent);
        });

        // Long press = delete ingredient
        holder.itemView.setOnLongClickListener(v -> {

            Context context =
                    v.getContext();

            new AlertDialog.Builder(context)
                    .setTitle("Delete Ingredient")
                    .setMessage(
                            "Are you sure you want to delete "
                                    + item.getName()
                                    + "?"
                    )
                    .setNegativeButton(
                            "Cancel",
                            null
                    )
                    .setPositiveButton(
                            "Delete",
                            (dialog, which) -> {

                                int rowsDeleted =
                                        databaseHelper
                                                .deletePantryItem(
                                                        item.getId()
                                                );

                                if (rowsDeleted > 0) {

                                    int currentPosition =
                                            holder.getAdapterPosition();

                                    if (currentPosition
                                            != RecyclerView.NO_POSITION) {

                                        pantryItems.remove(
                                                currentPosition
                                        );

                                        notifyItemRemoved(
                                                currentPosition
                                        );
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
                            }
                    )
                    .show();

            return true;
        });
    }

    private void displayExpiryStatus(
            PantryViewHolder holder,
            PantryItem item) {

        String expiryDate =
                item.getExpiryDate();

        if (expiryDate == null
                || expiryDate.trim().isEmpty()) {

            holder.tvExpiryDate.setText(
                    "Expiry: Not specified"
            );

            return;
        }

        int warningDays =
                sharedPreferences.getInt(
                        KEY_EXPIRY_WARNING_DAYS,
                        7
                );

        long daysUntilExpiry =
                calculateDaysUntilExpiry(
                        expiryDate
                );

        String expiryText;

        if (daysUntilExpiry == Long.MIN_VALUE) {

            expiryText =
                    "Expiry: "
                            + expiryDate
                            + "\nStatus: Invalid expiry date";

        } else if (daysUntilExpiry < 0) {

            expiryText =
                    "Expiry: "
                            + expiryDate
                            + "\nStatus: Expired";

        } else if (daysUntilExpiry == 0) {

            expiryText =
                    "Expiry: "
                            + expiryDate
                            + "\nStatus: Expires today";

        } else if (daysUntilExpiry <= warningDays) {

            expiryText =
                    "Expiry: "
                            + expiryDate
                            + "\nStatus: Expiring soon ("
                            + daysUntilExpiry
                            + " days remaining)";

        } else {

            expiryText =
                    "Expiry: "
                            + expiryDate
                            + "\nStatus: Fresh";
        }

        holder.tvExpiryDate.setText(
                expiryText
        );
    }

    private long calculateDaysUntilExpiry(
            String expiryDate) {

        SimpleDateFormat dateFormat =
                new SimpleDateFormat(
                        "yyyy-MM-dd",
                        Locale.getDefault()
                );

        dateFormat.setLenient(false);

        try {

            Date expiry =
                    dateFormat.parse(
                            expiryDate
                    );

            String todayText =
                    dateFormat.format(
                            new Date()
                    );

            Date today =
                    dateFormat.parse(
                            todayText
                    );

            if (expiry == null
                    || today == null) {

                return Long.MIN_VALUE;
            }

            long difference =
                    expiry.getTime()
                            - today.getTime();

            return TimeUnit.MILLISECONDS
                    .toDays(difference);

        } catch (ParseException e) {

            return Long.MIN_VALUE;
        }
    }

    @Override
    public int getItemCount() {
        return pantryItems.size();
    }

    public static class PantryViewHolder
            extends RecyclerView.ViewHolder {

        TextView tvIngredientName;
        TextView tvQuantity;
        TextView tvExpiryDate;

        public PantryViewHolder(
                @NonNull View itemView) {

            super(itemView);

            tvIngredientName =
                    itemView.findViewById(
                            R.id.tvIngredientName
                    );

            tvQuantity =
                    itemView.findViewById(
                            R.id.tvQuantity
                    );

            tvExpiryDate =
                    itemView.findViewById(
                            R.id.tvExpiryDate
                    );
        }
    }
}