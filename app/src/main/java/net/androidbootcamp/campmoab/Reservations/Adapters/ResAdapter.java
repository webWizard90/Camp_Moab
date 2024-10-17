package net.androidbootcamp.campmoab.Reservations.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import net.androidbootcamp.campmoab.Classes.ReservationClass;
import net.androidbootcamp.campmoab.Reservations.EditReservation;
import net.androidbootcamp.campmoab.Classes.DateClass;
import net.androidbootcamp.campmoab.Classes.FirebaseHelperClass;
import net.androidbootcamp.campmoab.R;
import net.androidbootcamp.campmoab.Classes.UserClass;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ResAdapter extends RecyclerView.Adapter<ResAdapter.ViewHolder>{
    private static final String RESERVATIONS = "Reservations";
    Context context;
    ArrayList<ReservationClass> reservationClassArrayList;
    ArrayList<UserClass> userClassArrayList;
    ArrayList<String> UIDs;
    ArrayList<String> resIDs;
    String userID;
    //String resID;
    private final boolean isAdmin;

    // Constructor for Users
    public ResAdapter(ArrayList<ReservationClass> reservationClassArrayList, String userID, ArrayList<String> resIDs, Context context, boolean isAdmin) {
        this.reservationClassArrayList = reservationClassArrayList;
        this.userID = userID;
        this.resIDs = resIDs;
        this.context = context;
        this.isAdmin = isAdmin;
    }

    // Constructor for Admins
    public ResAdapter(ArrayList<ReservationClass> reservationClassArrayList, ArrayList<UserClass> userClassArrayList, ArrayList<String> UIDs, ArrayList<String> resIDs, Context context, boolean isAdmin) {
        this.reservationClassArrayList = reservationClassArrayList;
        this.userClassArrayList = userClassArrayList;
        this.UIDs = UIDs;
        this.resIDs = resIDs;
        this.context = context;
        this.isAdmin = isAdmin;
    }

    @NonNull
    @Override
    public ResAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_reservations_card, parent, false);
        return new ResAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        TextView guestCounts;
        DateClass dateClass = new DateClass();
        ReservationClass reservationClass = reservationClassArrayList.get(position);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        if (isAdmin) {
            // Check if position is valid
            if (userClassArrayList != null && !userClassArrayList.isEmpty()) {
                if (position >= 0 && position < userClassArrayList.size()) {
                    UserClass userClass = userClassArrayList.get(position);
                    if (userClass != null) {
                        if (holder.name != null && holder.email != null) {
                            holder.name.setText(userClass.getFirstName() + " " + userClass.getLastName());
                            holder.email.setText(userClass.getEmail());

                            //Log.d("ResAdapter", "user class is not null, showing name and email");
                        }

                        holder.adminLinearLayout.setVisibility(View.VISIBLE);

                        //Log.d("ResAdapter", "isAdmin: " + isAdmin);
                    }
                }
            }
        } else {
            holder.adminLinearLayout.setVisibility(View.GONE);
        }

        /*if (holder.resID != null && position < resIDs.size()) {
            holder.resID.setText(resIDs.get(position));
            Log.d("ResAdapter", "Setting resID: " + resIDs.get(position) + " at position: " + position);
        }*/
        //holder.resID.setText(resIDs.get(position));

        if (position < resIDs.size()) {
            holder.resID.setText(resIDs.get(position));
            Log.d("ResAdapter", "Setting resID: " + resIDs.get(position) + " at position: " + position);

        } else {
            Log.e("ResAdapter", "Invalid position: " + position + " for resIDs size: " + resIDs.size());
        }


        holder.resDate.setText(reservationClass.getArrivalDate() + " - " + reservationClass.getDepartureDate());

        List<String> ageGroups = Arrays.asList("Adults: ", "Children: ", "Infants: ", "Service Animals: ");
        // Loop through the group quantities and display them with the correct age group
        for (int i = 0; i < 4; i++) {
            Long groupQty = reservationClass.getGroupQty().get(i);

            // Only create and add the TextView if the quantity is greater than 0
            if (groupQty > 0) {
                guestCounts = new TextView(holder.linearLayoutField.getContext());
                guestCounts.setText(ageGroups.get(i) + groupQty);
                guestCounts.setTextSize(18);
                guestCounts.setTextColor(Color.BLACK);
                layoutParams.bottomMargin = -5;
                layoutParams.topMargin = -8;
                guestCounts.setLayoutParams(layoutParams);
                holder.linearLayoutField.addView(guestCounts);
            }
        }

        //Get text of reservation and add to appropriate view in Card view
        if (reservationClass.getNotes() != null && !reservationClass.getNotes().isEmpty()) {
            holder.notesTitle.setVisibility(View.VISIBLE);
            holder.notes.setVisibility(View.VISIBLE);
            holder.lineView6.setVisibility(View.VISIBLE);
            holder.notes.setText(reservationClass.getNotes());
        } else {
            holder.notesTitle.setVisibility(View.GONE);
            holder.notes.setVisibility(View.GONE);
            holder.lineView6.setVisibility(View.GONE);
        }

        // Check if the reservation has started
        LocalDate currentDate = LocalDate.now();
        // Parse the reservation arrival date using the custom formatter
        LocalDate reservationArrivalDate = dateClass.parseStringToDate(reservationClass.getArrivalDate());

        if (currentDate.isAfter(reservationArrivalDate)) {
            // Hide the delete and modify buttons if the reservation has already started and the user is not an admin
            holder.deleteRez.setVisibility(View.GONE);
            holder.modifyRez.setVisibility(View.GONE);
        } else if (currentDate.isAfter(reservationArrivalDate) && isAdmin) {
            // Show the delete and modify buttons if the reservation has already started and the user is an admin
            // Delete reservation
            holder.deleteRez.setVisibility(View.VISIBLE);
            holder.deleteRez.setOnClickListener(v -> deleteReservation(position));

            // Modify reservation
            holder.modifyRez.setVisibility(View.VISIBLE);
            holder.modifyRez.setOnClickListener(v -> editReservation(position));
        } else {
            // Show the delete and modify buttons if the reservation has not started
            // Delete reservation
            holder.deleteRez.setVisibility(View.VISIBLE);
            holder.deleteRez.setOnClickListener(v -> deleteReservation(position));

            // Modify reservation
            holder.modifyRez.setVisibility(View.VISIBLE);
            holder.modifyRez.setOnClickListener(v -> editReservation(position));
        }

        holder.status.setText(reservationClass.getStatus());
    }

    public void deleteReservation(int position) {
        // Check if the position is valid for both lists
        if (position < 0 || position >= reservationClassArrayList.size() || position >= userClassArrayList.size()) {
            Log.e("ResAdapter", "Invalid position: " + position);
            return; // Exit if the position is invalid
        }

        //Get the reservation ID and the user ID
        String resID = resIDs.get(position);
        String userID = UIDs.get(position);

        //Delete the reservation from the database or shared preferences
        FirebaseHelperClass firebaseHelper = new FirebaseHelperClass();
        DatabaseReference ref = firebaseHelper.getRef();

        // Reference to the Firebase database
        Query query = ref.child(RESERVATIONS).child(userID).child(resID);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Are you sure you want to delete?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    ((DatabaseReference) query).removeValue();
                                    notifyDataSetChanged();
                                }
                            }


                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                })

                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        builder.create();
        builder.show();
    }

    public void editReservation(int position) {    // Log the sizes of the lists before attempting to access elements
        Intent intent = new Intent(context, EditReservation.class);
        //ReservationClass bookingClass = reservationClassArrayList.toArray(new ReservationClass[0])[position];

        if (isAdmin) {
            // Ensure the position is valid for all lists
            if (position < reservationClassArrayList.size() && position < UIDs.size() && position < resIDs.size()) {

                intent.putExtra("UID", UIDs.get(position));
                intent.putExtra("resID", resIDs.get(position));
                intent.putExtra("isAdmin", true);

                Log.d("ResAdapter", "Position: " + position
                        + ", reservationClassArrayList size: " + reservationClassArrayList.size()
                        + ", UIDs size: " + UIDs.size()
                        + ", resIDs size: " + resIDs.size());

            }
        } else {
            // Ensure the position is valid for all lists
            if (position < reservationClassArrayList.size() && position < resIDs.size()) {
                intent.putExtra("resID", resIDs.get(position));
                intent.putExtra("isAdmin", false);

                Log.d("ResAdapter", "Position: " + position
                        + ", reservationClassArrayList size: " + reservationClassArrayList.size()
                        + ", resIDs size: " + resIDs.size());
            }
        }

        Log.d("ResAdapter", "Position: " + position
                + ", ResID: " + resIDs.get(position)
                + ", Notes: " + reservationClassArrayList.get(position).getNotes()
                + ", Arrival: " + reservationClassArrayList.get(position).getArrivalDate()
                + ", Departure: " + reservationClassArrayList.get(position).getDepartureDate());



        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return Math.min(resIDs.size(), reservationClassArrayList.size());  // Ensure equal sizes
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView resDate, notes, notesTitle, status, name, email, resID;
        ImageView modifyRez, deleteRez;
        LinearLayout linearLayoutField, adminLinearLayout;
        View lineView6;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            linearLayoutField = (LinearLayout)itemView.findViewById(R.id.guestLinearList);
            adminLinearLayout = (LinearLayout)itemView.findViewById(R.id.adminLinearLayout);
            lineView6 = (View)itemView.findViewById(R.id.view6);
            name = (TextView)itemView.findViewById(R.id.name);
            email = (TextView)itemView.findViewById(R.id.email);
            resID = (TextView)itemView.findViewById(R.id.resID);
            resDate = (TextView)itemView.findViewById(R.id.dateReserved);
            notes = (TextView)itemView.findViewById(R.id.txtNotes);
            notesTitle = (TextView)itemView.findViewById(R.id.txtNotesTitle);
            status = (TextView)itemView.findViewById(R.id.reservationStatus);
            modifyRez = (ImageView)itemView.findViewById(R.id.modifyRez);
            deleteRez = (ImageView)itemView.findViewById(R.id.deleteRez);
        }
    }
}
