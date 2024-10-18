package net.androidbootcamp.campmoab.Reservations.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.androidbootcamp.campmoab.Classes.CustomSpinnerClass;
import net.androidbootcamp.campmoab.R;

import java.util.ArrayList;
import java.util.List;

public class GuestSpinnerAdapter extends ArrayAdapter<String> {
    private final Context context;
    private List<String> ageGroups;
    private List<String> ageGroupTitles;
    private ArrayList<Long> groupQty;
    private OnGuestCountChangeListener countChangeListener;
    private CustomSpinnerClass customSpinner;
    private LayoutInflater inflater;

    public GuestSpinnerAdapter(Context context, List<String> ageGroups, List<String> ageGroupTitles, CustomSpinnerClass customSpinner) {
        super(context, R.layout.activity_reservation_spinner_age_groups, ageGroups);
        this.context = context;
        this.ageGroups = ageGroups != null ? ageGroups : new ArrayList<>();
        this.ageGroupTitles = ageGroupTitles != null ? ageGroupTitles : new ArrayList<>();
        this.groupQty = new ArrayList<>();
        this.customSpinner = customSpinner;

        // Initialize quantities for each age group
        for (int i = 0; i < ageGroups.size(); i++) {
            // Set the first age group's quantity to 1, others to 0
            groupQty.add((long) (i == 0 ? 1 : 0));
        }

        inflater = LayoutInflater.from(context);
    }

    public interface OnGuestCountChangeListener {
        void onGuestCountChanged(int totalGuests);
    }

    public void setOnGuestCountChangeListener(OnGuestCountChangeListener listener) {
        this.countChangeListener = listener;
    }

    @NonNull
    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Inflate the rounded_dropdown_background container layout
        View dropdownView = inflater.inflate(R.layout.activity_reservation_spinner_container, parent, false);

        // Add the item view to the rounded_dropdown_background layout (container)
        LinearLayout container = dropdownView.findViewById(R.id.ageGroupContainerLayout); // Ensure container has the right ID

        // Clear any existing views to avoid duplication
        container.removeAllViews();

        for (int i = 0; i < ageGroups.size(); i++) {
            // Inflate the item layout for each age group
            View itemView = inflater.inflate(R.layout.activity_reservation_spinner_age_groups, parent, false);

            ViewHolder viewHolder = new ViewHolder();

            // Set up the individual item views (e.g., age group, quantity, buttons)
            viewHolder.txtAgeGroupTitle = itemView.findViewById(R.id.txtAgeGroupTitle);
            viewHolder.txtAgeGroup = itemView.findViewById(R.id.txtAgeGroup);
            viewHolder.txtQuantity = itemView.findViewById(R.id.txtQuantity);
            viewHolder.btnAdd = itemView.findViewById(R.id.btnAdd);
            viewHolder.btnSubtract = itemView.findViewById(R.id.btnSubtract);

            String ageGroupTitle = ageGroupTitles.get(i);
            String ageGroup = ageGroups.get(i);
            viewHolder.txtAgeGroupTitle.setText(ageGroupTitle);  // Set the title for the age group
            viewHolder.txtAgeGroup.setText(ageGroup);            // Set the age group name
            viewHolder.txtQuantity.setText(String.valueOf(groupQty.get(i)));  // Display quantity

            // Create a final copy of i to use inside the click listeners
            final int index = i;

            // Set button click listeners inside the loop
            viewHolder.btnAdd.setOnClickListener(v -> {
                Long currentQuantity = groupQty.get(index); // Use i here to ensure correct item is modified
                groupQty.set(index, currentQuantity + 1);
                viewHolder.txtQuantity.setText(String.valueOf(groupQty.get(index)));
                notifyTotalGuestCountChanged();
            });

            viewHolder.btnSubtract.setOnClickListener(v -> {
                Long currentQuantity = groupQty.get(index); // Use i here for the correct item
                if (index == 0 && currentQuantity <= 1) {
                    return; // Prevent subtraction if it's the first age group with quantity 1
                }
                if (currentQuantity > 0) {
                    groupQty.set(index, currentQuantity - 1);
                    viewHolder.txtQuantity.setText(String.valueOf(groupQty.get(index)));
                    notifyTotalGuestCountChanged();
                }
            });

            // Add the item view to the rounded_dropdown_background container layout
            container.addView(itemView);
        }

        // Inflate and set up the SAVE button
        Button btnSave = dropdownView.findViewById(R.id.btnSave);
        btnSave.setOnClickListener(v -> {
            Log.d("GuestSpinnerAdapter", "Save button clicked");

            customSpinner.performClosedEvent();
        });

        Button btnClear = dropdownView.findViewById(R.id.btnClear);
        btnClear.setOnClickListener(v -> {
            // Reset all quantities to default values
            for (int i = 0; i < groupQty.size(); i++) {
                groupQty.set(i, (long) (i == 0 ? 1 : 0)); // Reset to 1 for the first, 0 for others
            }

            // Notify the adapter that the data has changed
            notifyTotalGuestCountChanged();
            notifyDataSetChanged(); // Add this line to ensure the UI reflects changes

            //Log.d("GuestSpinnerAdapter", "Clear button clicked");
        });

        return dropdownView;
    }

    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        // Use a simple TextView for the main spinner view
        TextView textView = (TextView) LayoutInflater.from(context).inflate(android.R.layout.simple_spinner_item, parent, false);

        if (view == null) {
            //Log.e("GuestSpinnerAdapter", "Inflated view is null!");
        }

        return textView;
    }


    public void notifyTotalGuestCountChanged() {
        int totalGuests = 0;

        for (Long quantity : groupQty) {
            totalGuests += quantity;
        }

        if (countChangeListener != null) {
            countChangeListener.onGuestCountChanged(totalGuests);
        }
    }

    public ArrayList<Long> getGroupQty() {
        Log.d("GuestSpinnerAdapter", "Returning groupQty: " + groupQty);
        return new ArrayList<>(groupQty);
    }

    public ArrayList<Long> setGroupQty(ArrayList<Long> groupQty) {
        this.groupQty.clear();
        this.groupQty.addAll(groupQty);
        return new ArrayList<>(groupQty);
    }

    public Integer getTotalGuests() {
        int totalGuests = 0;

        for (Long quantity : groupQty) {
            totalGuests += quantity;
        }

        return totalGuests;
    }

    @Override
    public int getCount() {
        return 1; // Only one view for the rounded_dropdown_background
    }

    // Define the ViewHolder class
    private static class ViewHolder {
        TextView txtAgeGroupTitle;
        TextView txtAgeGroup;
        TextView txtQuantity;
        Button btnAdd;
        Button btnSubtract;
    }
}
