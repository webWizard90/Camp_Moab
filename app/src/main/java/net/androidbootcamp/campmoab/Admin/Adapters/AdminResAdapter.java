package net.androidbootcamp.campmoab.Admin.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import net.androidbootcamp.campmoab.Admin.AdminEditRes;
import net.androidbootcamp.campmoab.Classes.AdminBookingClass;
import net.androidbootcamp.campmoab.Classes.BookingClass;
import net.androidbootcamp.campmoab.R;
import net.androidbootcamp.campmoab.Classes.UserClass;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// FirebaseRecyclerAdapter is a class provided by
// FirebaseUI. it provides functions to bind, adapt and show
// database contents in a Recycler View
public class AdminResAdapter extends RecyclerView.Adapter<AdminResAdapter.ViewHolder> {
    Context context;
    ArrayList<AdminBookingClass> adminBookingClassArrayList;
    ArrayList<UserClass> userClassArrayList;
    ArrayList<String> uid1;
    ArrayList<String> uid2;
    private static final String RESERVATIONS = "Reservations";
    //ArrayList<AdminBookingClass> adminResClassArrayList;

    public AdminResAdapter(ArrayList<AdminBookingClass> adminBookingClassArrayList, ArrayList<UserClass> userClassArrayList, ArrayList<String> uid1, ArrayList<String> uid2, Context context) {
        this.adminBookingClassArrayList = adminBookingClassArrayList;
        this.userClassArrayList = userClassArrayList;
        this.uid1 = uid1;
        this.uid2 = uid2;
        this.context = context;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.admin_res_card, parent, false);

        return new AdminResAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        BookingClass bookingClass = adminBookingClassArrayList.get(position);
        UserClass userClass = userClassArrayList.get(position);
        SpannableString spannableString;
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        TextView guestCounts;

        holder.email.setText(userClass.getEmail());
        holder.resId.setText(uid2.get(position));
        holder.nameRes.setText(userClass.getFirstName() + " " + userClass.getLastName());
        Log.d("AdminResAdapter", "User Name" + holder.nameRes.getText().toString());
        holder.resDate.setText(bookingClass.getArrivalDate() + " - " + bookingClass.getDepartureDate());
        holder.linearLayoutField.getContext();

        List<String> ageGroups = Arrays.asList("Adults: ", "Children: ", "Infants: ", "Service Animals: ");

        // Loop through the group quantities and display them with the correct age group
        for (int i = 0; i < 4; i++) {
            Long groupQty = bookingClass.getGroupQty().get(i);

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

        // Handle notes
        holder.notes.setText(bookingClass.getNotes() != null && !bookingClass.getNotes().isEmpty() ? bookingClass.getNotes() : "N/A");

        // Update confirmation status
        updateConfirmationStatus(holder, bookingClass);

        holder.placedResDate.setText(bookingClass.getDateBooked());
        //holder.notes.setText(bookingClass.getNotes());

        // Email status update
        spannableString = new SpannableString(holder.emailStatusUpdate.getText().toString());
        spannableString.setSpan(new UnderlineSpan(), 0, spannableString.length(), 0);
        holder.emailStatusUpdate.setText(spannableString);

        // Email click listener
        holder.emailStatusUpdate.setOnClickListener(v -> sendEmail(userClass, bookingClass, holder));

        // Confirmation status click listener
        holder.confirmationStatus.setOnClickListener(v -> confirmRes(position));

        // Delete reservation listener
        holder.deleteRez.setOnClickListener(v -> deleteReservation(position));

        // Modify reservation listener
        holder.modifyRez.setOnClickListener(v -> editReservation(position));
    }

    @Override
    public int getItemCount() {
        //return bookingClassArrayList == null ? 0 : bookingClassArrayList.size());
        return userClassArrayList == null ? 0 : userClassArrayList.size();
    }

    private void updateConfirmationStatus(ViewHolder holder, BookingClass bookingClass) {
        holder.confirmationStatus.setText(bookingClass.getStatus());
        if ("Confirmed".equals(bookingClass.getStatus())) {
            holder.confirmationStatus.setTextColor(Color.parseColor("#1A661D"));
            holder.checkmark.setImageResource(R.drawable.checkmark_circle_green);
        } else if ("Pending".equals(bookingClass.getStatus())) {
            holder.confirmationStatus.setTextColor(Color.parseColor("#C35C13"));
            holder.checkmark.setImageResource(R.drawable.checkmark_circle_orange);
        } else {
            holder.confirmationStatus.setTextColor(Color.parseColor("#8E1818"));
            holder.checkmark.setImageResource(R.drawable.checkmark_circle_red);
        }
    }

    private void sendEmail(UserClass userClass, BookingClass bookingClass, ViewHolder holder) {
        Intent email = new Intent(Intent.ACTION_SEND);
        email.putExtra(Intent.EXTRA_EMAIL, new String[]{userClass.getEmail(), "campmoabteam@gmail.com"});
        email.putExtra(Intent.EXTRA_SUBJECT, "Camp Moab Reservation Update");
        email.putExtra(Intent.EXTRA_TEXT, generateEmailBody(userClass, bookingClass, holder));
        email.setType("message/rfc822");
        try {
            context.startActivity(Intent.createChooser(email, "Choose an Email client :"));
        } catch (Exception e) {
            Toast.makeText(context, "Error: " + e.toString(), Toast.LENGTH_LONG).show();
            Log.i("Error", e.toString());
        }
    }

    private String generateEmailBody(UserClass userClass, BookingClass bookingClass, ViewHolder holder) {
        return "Hello " + userClass.getFirstName() + ",\n\n" +
                "We are contacting you to inform you that your reservation has been " + bookingClass.getStatus() + ". " +
                "Please log in to your account to view the changes.\n\n" +
                "If you have any questions, please contact us at campmoabteam@gmail.com." +
                "\n\nReservation Details:\n" +
                "Reservation ID: " + holder.resId.getText() + "\n" +
                "Name: " + holder.nameRes.getText() + "\n" +
                "Arrival Date: " + bookingClass.getArrivalDate() + "\n" +
                "Departure Date: " + bookingClass.getDepartureDate() + "\n" +
                "Guests: " + holder.linearLayoutField.toString() + "\n" +
                "Notes: " + holder.notes.getText() + "\n\n" +
                "Thank you,\n" +
                "The Camp Moab Team";
    }

    //Method to change confirmation status on click
    public void confirmRes(int position) {
        BookingClass bookingClass = adminBookingClassArrayList.get(position);
        Query query = FirebaseDatabase.getInstance().getReference()
                            .child(RESERVATIONS).child(uid1.get(position)).child(uid2.get(position));
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (bookingClass.getStatus().equals("Pending")) {
                        ((DatabaseReference) query).child("confirmationStatus").setValue("Confirmed");
                        bookingClass.setStatus("Confirmed");
                        //set color of text to green
                    } else if (bookingClass.getStatus().equals("Confirmed")) {
                        ((DatabaseReference) query).child("confirmationStatus").setValue("Cancelled");
                        bookingClass.setStatus("Cancelled");
                    } else if (bookingClass.getStatus().equals("Cancelled")) {
                        ((DatabaseReference) query).child("confirmationStatus").setValue("Pending");
                        bookingClass.setStatus("Pending");
                    }
                    //notify DataSet changed only for the confirmation status
                    notifyItemChanged(position, "confirmationStatus");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    //Method to remove values of the reservation
    //based on the logged in user
    public void deleteReservation(int position) {
        //BookingClass bookingClass = bookingClassArrayList.get(position);
        Query query1 = FirebaseDatabase.getInstance().getReference()
                .child(RESERVATIONS).child(uid1.get(position)).child(uid2.get(position));
        query1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    ((DatabaseReference) query1).removeValue();
                    notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    //Method to edit reservation
    public void editReservation(int position) {
        //Get all information from the reservation position and pass it to the edit reservation activity
        BookingClass bookingClass = adminBookingClassArrayList.get(position);
        UserClass userClass = userClassArrayList.get(position);
        Intent intent = new Intent(context, AdminEditRes.class);
        intent.putExtra("firstName", userClass.getFirstName());
        intent.putExtra("lastName", userClass.getLastName());
        intent.putExtra("arrivalDate", bookingClass.getArrivalDate());
        intent.putExtra("departureDate", bookingClass.getDepartureDate());
        // for each groupQty in the list, add it to the intent
        for (int i = 0; i < bookingClass.getGroupQty().size(); i++) {
            intent.putExtra("groupQty" + i, bookingClass.getGroupQty().get(i));
        }
        intent.putExtra("additionalInfo", bookingClass.getNotes());
        intent.putExtra("confirmationStatus", bookingClass.getStatus());
        intent.putExtra("dateBooked", bookingClass.getDateBooked());
        //get the uid1 and uid2 from the position
        intent.putExtra("uid1", uid1.get(position).toString());
        intent.putExtra("email", userClass.getEmail());
        //Log.D("AdminResAdapter", "UID1:" + uid1.get(position).toString());
        intent.putExtra("uid2", uid2.get(position).toString());
        Log.d("AdminResAdapter","UID2: " + uid2.get(position).toString());

        context.startActivity(intent);
    }


    //*** Sub Class to create references of the views in Card view ***\\
        // ("activity_reservations_card.menu") \\
        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView resDate, notes, confirmationStatus, placedResDate, nameRes, email, resId, emailStatusUpdate;
            ImageView modifyRez, deleteRez, checkmark;
            LinearLayout linearLayoutField;

            public ViewHolder(View itemView)
            {
                super(itemView);

                linearLayoutField = (LinearLayout)itemView.findViewById(R.id.guestLinearList);
                email = (TextView)itemView.findViewById(R.id.email);
                resId = (TextView)itemView.findViewById(R.id.ResID);
                resDate = (TextView)itemView.findViewById(R.id.dateReserved);
                notes = (TextView)itemView.findViewById(R.id.txtNotes);
                modifyRez = (ImageView)itemView.findViewById(R.id.modifyRez);
                deleteRez = (ImageView)itemView.findViewById(R.id.deleteRez);
                confirmationStatus = (TextView) itemView.findViewById(R.id.status);
                checkmark = (ImageView) itemView.findViewById(R.id.checkmark);
                placedResDate = (TextView) itemView.findViewById(R.id.placedReservationDate);
                nameRes = (TextView) itemView.findViewById(R.id.reservedBy);
                emailStatusUpdate = (TextView) itemView.findViewById(R.id.emailStatusUpdate);
            }
        }

}
