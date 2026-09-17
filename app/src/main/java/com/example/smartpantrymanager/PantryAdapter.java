package com.example.smartpantrymanager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

        // -----------------------------------------
        // INGREDIENT IMAGE
        // -----------------------------------------

        String ingredientName =
                item.getName()
                        .trim()
                        .toLowerCase(Locale.ROOT);

        if (ingredientName.equals("butter")) {

            holder.imgIngredient.setImageResource(
                    R.drawable.butter
            );

        } else if (ingredientName.equals("egg")
                || ingredientName.equals("eggs")) {

            holder.imgIngredient.setImageResource(
                    R.drawable.eggs
            );

        } else if (ingredientName.equals("rice")) {

            holder.imgIngredient.setImageResource(
                    R.drawable.rice
            );

        } else {

            // Default image for ingredients
            // that do not have their own picture yet
            holder.imgIngredient.setImageResource(
                    R.drawable.ic_launcher_foreground
            );
        }

        // -----------------------------------------
        // INGREDIENT INFORMATION
        // -----------------------------------------

        holder.tvIngredientName.setText(
                item.getName()
        );

        String quantityText =
                "Quantity: "
                        + item.getQuantity()
                        + " "
                        + item.getUnit();

        holder.tvQuantity.setText(
                quantityText
        );

        displayExpiryStatus(
                holder,
                item
        );

        // -----------------------------------------
        // NORMAL TAP = EDIT INGREDIENT
        // -----------------------------------------

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

        // -----------------------------------------
        // LONG PRESS = DELETE INGREDIENT
        // -----------------------------------------

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

    // -----------------------------------------
    // EXPIRY STATUS
    // -----------------------------------------

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

            setStatusBadge(
                    holder,
                    "No expiry date",
                    "#746E7C",
                    "#F3EEFA"
            );

            return;
        }

        holder.tvExpiryDate.setText(
                "Expiry: " + expiryDate
        );

        int warningDays =
                sharedPreferences.getInt(
                        KEY_EXPIRY_WARNING_DAYS,
                        7
                );

        long daysUntilExpiry =
                calculateDaysUntilExpiry(
                        expiryDate
                );

        if (daysUntilExpiry == Long.MIN_VALUE) {

            setStatusBadge(
                    holder,
                    "Invalid date",
                    "#B85C5C",
                    "#FDECEC"
            );

        } else if (daysUntilExpiry < 0) {

            setStatusBadge(
                    holder,
                    "!  Expired",
                    "#B85C5C",
                    "#FDECEC"
            );

        } else if (daysUntilExpiry == 0) {

            setStatusBadge(
                    holder,
                    "!  Expires today",
                    "#B85C5C",
                    "#FDECEC"
            );

        } else if (daysUntilExpiry <= warningDays) {

            setStatusBadge(
                    holder,
                    "◷  Expiring soon",
                    "#C28B3C",
                    "#FFF3DF"
            );

        } else {

            setStatusBadge(
                    holder,
                    "🌿  Fresh",
                    "#4E8B72",
                    "#E8F5EE"
            );
        }
    }

    // -----------------------------------------
    // STATUS BADGE DESIGN
    // -----------------------------------------

    private void setStatusBadge(
            PantryViewHolder holder,
            String text,
            String textColor,
            String backgroundColor) {

        holder.tvExpiryStatus.setText(
                text
        );

        holder.tvExpiryStatus.setTextColor(
                Color.parseColor(textColor)
        );

        GradientDrawable badgeBackground =
                new GradientDrawable();

        badgeBackground.setColor(
                Color.parseColor(backgroundColor)
        );

        badgeBackground.setCornerRadius(
                30f
        );

        holder.tvExpiryStatus.setBackground(
                badgeBackground
        );
    }

    // -----------------------------------------
    // CALCULATE DAYS UNTIL EXPIRY
    // -----------------------------------------

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

    // -----------------------------------------
    // VIEW HOLDER
    // -----------------------------------------

    public static class PantryViewHolder
            extends RecyclerView.ViewHolder {

        ImageView imgIngredient;
        TextView tvIngredientName;
        TextView tvQuantity;
        TextView tvExpiryDate;
        TextView tvExpiryStatus;

        public PantryViewHolder(
                @NonNull View itemView) {

            super(itemView);

            imgIngredient =
                    itemView.findViewById(
                            R.id.imgIngredient
                    );

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

            tvExpiryStatus =
                    itemView.findViewById(
                            R.id.tvExpiryStatus
                    );
        }
    }
}