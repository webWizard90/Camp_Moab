package net.androidbootcamp.campmoab.Bookings;

import android.annotation.SuppressLint;
import android.content.Context;
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
import androidx.recyclerview.widget.RecyclerView;

import net.androidbootcamp.campmoab.R;
import net.androidbootcamp.campmoab.UserAccountAttributes.UserClass;

import java.util.ArrayList;

public class ResAdapter extends RecyclerView.Adapter<ResAdapter.ViewHolder>{
    Context context;
    ArrayList<BookingClass> bookingClassArrayList;
    String userID;

    public ResAdapter() {
    }

    public ResAdapter(ArrayList<BookingClass> bookingClassArrayList, String userID, Context context) {
        this.bookingClassArrayList = bookingClassArrayList;
        this.userID = userID;
        this.context = context;
    }

    @NonNull
    @Override
    public ResAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.reservation_card, parent, false);
        return new ResAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ResAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        TextView guestNames;
        BookingClass booking = bookingClassArrayList.get(position);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //Add from booking to appropriate view in Card view
        holder.resDate.setText(booking.arrivalDate + " - " + booking.departureDate);
        //if getGuestNames if not "" then add guest names to linear layout
        if(!booking.getGuestNames().contains("") || booking.getGuestNames().toString().length() > 2) {
            for (String name : booking.getGuestNames()) {
                guestNames = new TextView(holder.linearLayoutField.getContext());
                guestNames.setText(name);
                guestNames.setTextSize(20);
                guestNames.setTextColor(Color.BLACK);
                layoutParams.bottomMargin = -5;
                layoutParams.topMargin = -8;
                guestNames.setLayoutParams(layoutParams);
                holder.linearLayoutField.addView(guestNames);
            }
        } else {
            guestNames = new TextView(holder.linearLayoutField.getContext());
            guestNames.setText("No Guests");
            guestNames.setTextSize(20);
            guestNames.setTextColor(Color.BLACK);
            layoutParams.bottomMargin = -5;
            layoutParams.topMargin = -8;
            guestNames.setLayoutParams(layoutParams);
            holder.linearLayoutField.addView(guestNames);
            Log.d("Guest Name", "Guest name is null");
        }
        //Get text of reservation and add to appropriate view in Card view
        if(!booking.getAddTxt().contains("") || booking.getAddTxt().length() > 2) {
            holder.notesTitle.setVisibility(View.VISIBLE);
            holder.notes.setVisibility(View.VISIBLE);
            holder.lineView.setVisibility(View.VISIBLE);
            holder.notes.setText(booking.getAddTxt());
        } else {
            holder.notesTitle.setVisibility(View.GONE);
            holder.notes.setVisibility(View.GONE);
            holder.lineView.setVisibility(View.GONE);
        }
        holder.reservationStatus.setText(booking.confirmationStatus);
        //Use delete button to remove reservation from firebase
        holder.deleteRez.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteReservation(position);
            }
        });

        //Use modify button to modify reservation
        holder.modifyRez.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editReservation(position);
            }
        });

    }

    public void deleteReservation(int position) {
    }

    public void editReservation(int position) {
        //Get all information from the reservation position and pass it to the edit reservation activity
        BookingClass bookingClass = bookingClassArrayList.get(position);
        Intent intent = new Intent(context, Edit_Reservation.class);
        intent.putExtra("arrivalDate", bookingClass.getArrivalDate());
        intent.putExtra("departureDate", bookingClass.getDepartureDate());
       /* if(!bookingClass.getGuestNames().contains("")) {
            //add all guests to the intent
            intent.putExtra("guestNames", bookingClass.getGuestNames());
        } else {
            intent.putExtra("guestNames", 0);
        }

        */
        //if guestName is not null, add arraylist to intent
        if(bookingClass.getGuestNames() != null) {
            intent.putExtra("guestNames", bookingClass.getGuestNames().toString());
            Log.v("Guest Names", bookingClass.getGuestNames().toString());
        } else {
          //  intent.putExtra("guestNames", 0);
            Log.v("Guest Names", "No Guests");
        }

        if (bookingClass.getGuestNames().size() > 0) {
            intent.putExtra("numOfGuests", bookingClass.getGuestNames().size());
        } else {
            intent.putExtra("numOfGuests", 0);
        }
        intent.putExtra("addTxt", bookingClass.getAddTxt());
        intent.putExtra("confirmationStatus", bookingClass.getConfirmationStatus());
        intent.putExtra("resID", userID);
        Log.v("Res ID", userID);
        intent.putExtra("dateBooked", bookingClass.getDateBooked());
        Log.v("Date Booked", bookingClass.getDateBooked());
        //get the key of the reservation from firebase at the position without a snapshot
        //String key = getSnapshots().getSnapshot(position).getKey();
        //intent.putExtra("key", key);

        //get the key of the reservation from firebase at the position
        //String key = getSnapshots().getSnapshot(position).getKey();
        //pass the key to the edit reservation activity
        //intent.putExtra("key", key);

        //get uid of the user from firebase without a snapshot at the position

        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return bookingClassArrayList == null ? 0 : bookingClassArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView resDate, notes, notesTitle, reservationStatus;
        ImageView modifyRez, deleteRez, home, account;
        LinearLayout linearLayoutField;
        View lineView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            linearLayoutField = (LinearLayout)itemView.findViewById(R.id.guestLinearList);
            lineView = (View)itemView.findViewById(R.id.view2);
            resDate = (TextView)itemView.findViewById(R.id.dateReserved);
            notes = (TextView)itemView.findViewById(R.id.addInformation);
            notesTitle = (TextView)itemView.findViewById(R.id.txtTitle);
            reservationStatus = (TextView)itemView.findViewById(R.id.reservationStatus);
            modifyRez = (ImageView)itemView.findViewById(R.id.modifyRez);
            deleteRez = (ImageView)itemView.findViewById(R.id.deleteRez);
            home = (ImageView) itemView.findViewById(R.id.homeImage);
            account = (ImageView) itemView.findViewById(R.id.acctImage);
        }
    }
}
