package net.androidbootcamp.campmoab.Admin.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
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
import net.androidbootcamp.campmoab.Bookings.BookingClass;
import net.androidbootcamp.campmoab.R;
import net.androidbootcamp.campmoab.UserAccountAttributes.UserClass;

import java.util.ArrayList;

// FirebaseRecyclerAdapter is a class provided by
// FirebaseUI. it provides functions to bind, adapt and show
// database contents in a Recycler View
public class AdminResAdapter extends RecyclerView.Adapter<AdminResAdapter.ViewHolder> {
    Context context;
    ArrayList<BookingClass> bookingClassArrayList;
    ArrayList<UserClass> userClassArrayList;
    ArrayList<String> uid1;
    ArrayList<String> uid2;
    //ArrayList<AdminResClass> adminResClassArrayList;

    public AdminResAdapter(ArrayList<BookingClass> bookingClassArrayList, ArrayList<UserClass> userClassArrayList, ArrayList<String> uid1, ArrayList<String> uid2, Context context) {
        this.bookingClassArrayList = bookingClassArrayList;
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
        BookingClass bookingClass = bookingClassArrayList.get(position);
        UserClass userClass = userClassArrayList.get(position);
        SpannableString spannableString;
        ArrayList<String> guestNamesList = new ArrayList<>();
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        TextView guestNames;

        holder.email.setText(userClass.getEmail());
        holder.resId.setText(uid2.get(position));
        holder.nameRes.setText(userClass.getFirstName() + " " + userClass.getLastName());
        Log.v("holder name", holder.nameRes.getText().toString());
        holder.resDate.setText(bookingClass.getArrivalDate() + " - " + bookingClass.getDepartureDate());
        holder.linearLayoutField.getContext();

        Integer numGuests = bookingClass.getGuestNames().size();

        if (numGuests > 0) {
            //clears the linear layout before adding new textviews
            holder.linearLayoutField.removeAllViews();
            for (String name : bookingClass.getGuestNames()) {
                guestNames = new TextView(holder.linearLayoutField.getContext());
                guestNames.setText(name);
                guestNamesList.add(name);
                guestNames.setTextSize(20);
                guestNames.setTextColor(Color.BLACK);
                layoutParams.bottomMargin = -10;
                layoutParams.topMargin = -12;
                guestNames.setLayoutParams(layoutParams);
                holder.linearLayoutField.addView(guestNames);
            }
        } else {
            guestNames = new TextView(holder.linearLayoutField.getContext());
            guestNames.setText("No Guests");
            guestNames.setTextSize(20);
            guestNames.setTextColor(Color.BLACK);
            layoutParams.bottomMargin = -15;
            layoutParams.topMargin = -12;
            guestNames.setLayoutParams(layoutParams);
            holder.linearLayoutField.addView(guestNames);
            Log.d("Guest Name", "Guest name is null");
        }

        //if holder.notes is null, set visibility to gone
        if (bookingClass.getAddTxt() == null || bookingClass.getAddTxt().isEmpty()) {
            holder.notes.setText("N/A");
        } else {
            holder.notes.setText(bookingClass.getAddTxt());
        }

        holder.confirmationStatus.setText(bookingClass.getConfirmationStatus());
        //if text is "Confirmed", set color to green
        if (holder.confirmationStatus.getText().toString().equals("Confirmed")) {
            holder.confirmationStatus.setTextColor(Color.parseColor("#1A661D"));
            holder.checkmark.setImageResource(R.drawable.checkmark_circle_green);
        } else if (holder.confirmationStatus.getText().toString().equals("Pending")) {
            holder.confirmationStatus.setTextColor(Color.parseColor("#C35C13"));
            holder.checkmark.setImageResource(R.drawable.checkmark_circle_orange);
        } else {
            holder.confirmationStatus.setTextColor(Color.parseColor("#8E1818"));
            holder.checkmark.setImageResource(R.drawable.checkmark_circle_red);
        }

        holder.placedResDate.setText(bookingClass.getDateBooked());
        //holder.notes.setText(bookingClass.getAddTxt());

        spannableString = new SpannableString(holder.emailStatusUpdate.getText().toString());
        spannableString.setSpan(new UnderlineSpan(), 0, spannableString.length(), 0);
        holder.emailStatusUpdate.setText(spannableString);
        holder.emailStatusUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //send email to user email and camp moab email
                Intent email = new Intent(Intent.ACTION_SEND);
                email.putExtra(Intent.EXTRA_EMAIL, new String[]{userClass.getEmail(), "campmoabteam@gamil.com"});
                email.putExtra(Intent.EXTRA_SUBJECT, "Camp Moab Reservation Update");
                email.putExtra(Intent.EXTRA_TEXT, "Hello " + userClass.getFirstName() + ",\n\n" +
                        "We are contacting you to inform you that your reservation has been " + bookingClass.getConfirmationStatus() + ". " + "Please log in to your account to view the changes.\n\n" +
                        "If you have any questions, please contact us at campmoabteam@gmail.com." +
                        "\n\n" +
                        "Reservation Details:\n" +
                        "Reservation ID: " + holder.resId.getText() + "\n" +
                        "Name: " + holder.nameRes.getText() + "\n" +
                        "Arrival Date: " + bookingClass.getArrivalDate() + "\n" +
                        "Departure Date: " + bookingClass.getDepartureDate() + "\n" +
                        //list guest names on separate lines if there are any
                        "Guest Names: " + guestNamesList.toString().replace("[", "").replace("]", "") + "\n" +
                        "Notes: " + holder.notes.getText() + "\n" +
                        "\n\n" +
                        "Thank you,\n" +
                        "The Camp Moab Team");
                email.setType("message/rfc822");
                try {
                    context.startActivity(Intent.createChooser(email, "Choose an Email client :"));
                } catch (Exception e) {
                    Toast.makeText(context, "Error: " + e.toString(), Toast.LENGTH_LONG).show();
                    Log.i("Error", e.toString());
                }
            }
        });

        holder.confirmationStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { confirmRes(position); }
        });

        holder.deleteRez.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteReservation(position);
            }
        });

        holder.modifyRez.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editReservation(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        //return bookingClassArrayList == null ? 0 : bookingClassArrayList.size());
        return userClassArrayList == null ? 0 : userClassArrayList.size();
    }

    //Method to change confirmation status on click
    public void confirmRes(int position) {
        BookingClass bookingClass = bookingClassArrayList.get(position);
        Query query1 = FirebaseDatabase.getInstance().getReference()
                            .child("Reservations").child(uid1.get(position)).child(uid2.get(position));
        query1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (bookingClass.getConfirmationStatus().equals("Pending")) {
                        ((DatabaseReference) query1).child("confirmationStatus").setValue("Confirmed");
                        bookingClass.setConfirmationStatus("Confirmed");
                        //set color of text to green
                    } else if (bookingClass.getConfirmationStatus().equals("Confirmed")) {
                        ((DatabaseReference) query1).child("confirmationStatus").setValue("Cancelled");
                        bookingClass.setConfirmationStatus("Cancelled");
                    } else if (bookingClass.getConfirmationStatus().equals("Cancelled")) {
                        ((DatabaseReference) query1).child("confirmationStatus").setValue("Pending");
                        bookingClass.setConfirmationStatus("Pending");
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
                .child("Reservations").child(uid1.get(position)).child(uid2.get(position));
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
        BookingClass bookingClass = bookingClassArrayList.get(position);
        UserClass userClass = userClassArrayList.get(position);
        Intent intent = new Intent(context, AdminEditRes.class);
        intent.putExtra("firstName", userClass.getFirstName());
        intent.putExtra("lastName", userClass.getLastName());
        intent.putExtra("arrivalDate", bookingClass.getArrivalDate());
        intent.putExtra("departureDate", bookingClass.getDepartureDate());
        if(bookingClass.getGuestNames() != null) {
            intent.putExtra("guestNames", bookingClass.getGuestNames());
        } else {
            intent.putExtra("guestNames", "0");
        }
        if (!bookingClass.getGuestNames().contains("No Guests")) {
            intent.putExtra("numOfGuests", bookingClass.getGuestNames().size());
        } else {
            intent.putExtra("numOfGuests", 0);
        }
        Log.i("numOfGuests", String.valueOf(bookingClass.getGuestNames().size()));

        intent.putExtra("addTxt", bookingClass.getAddTxt());
        intent.putExtra("confirmationStatus", bookingClass.getConfirmationStatus());
        intent.putExtra("dateBooked", bookingClass.getDateBooked());
        //get the uid1 and uid2 from the position
        intent.putExtra("uid1", uid1.get(position).toString());
        intent.putExtra("email", userClass.getEmail());
        //Log.v("uid1", uid1.get(position).toString());
        intent.putExtra("uid2", uid2.get(position).toString());
        Log.v("uid2", uid2.get(position).toString());

        context.startActivity(intent);
    }


    //*** Sub Class to create references of the views in Card view ***\\
        // ("reservation_card.xml") \\
        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView resDate, notes, confirmationStatus, placedResDate, nameRes, email, resId, emailStatusUpdate;
            ImageView modifyRez, deleteRez, home, account, checkmark;
            LinearLayout linearLayoutField;

            public ViewHolder(View itemView)
            {
                super(itemView);

                linearLayoutField = (LinearLayout)itemView.findViewById(R.id.guestLinearList);
                email = (TextView)itemView.findViewById(R.id.email);
                resId = (TextView)itemView.findViewById(R.id.ResID);
                resDate = (TextView)itemView.findViewById(R.id.dateReserved);
                notes = (TextView)itemView.findViewById(R.id.addInformation);
                modifyRez = (ImageView)itemView.findViewById(R.id.modifyRez);
                deleteRez = (ImageView)itemView.findViewById(R.id.deleteRez);
                home = (ImageView) itemView.findViewById(R.id.homeImage);
                account = (ImageView) itemView.findViewById(R.id.acctImage);
                confirmationStatus = (TextView) itemView.findViewById(R.id.confirmationStatus);
                checkmark = (ImageView) itemView.findViewById(R.id.checkmark);
                placedResDate = (TextView) itemView.findViewById(R.id.placedReservationDate);
                nameRes = (TextView) itemView.findViewById(R.id.reservedBy);
                emailStatusUpdate = (TextView) itemView.findViewById(R.id.emailStatusUpdate);
            }
        }

}
