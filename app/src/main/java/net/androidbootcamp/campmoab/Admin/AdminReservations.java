package net.androidbootcamp.campmoab.Admin;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import net.androidbootcamp.campmoab.Admin.Adapters.AdminResAdapter;
import net.androidbootcamp.campmoab.BaseActivity;
import net.androidbootcamp.campmoab.Classes.AdminBookingClass;
import net.androidbootcamp.campmoab.R;
import net.androidbootcamp.campmoab.Classes.UserClass;

import java.util.ArrayList;

public class AdminReservations extends BaseActivity {
    int count = 0;
    private String firstName, lastName, email;
    private String arrivalDate, departureDate, confirmationStatus, dateBooked, dateEdited, editedBy, notes, UID1, UID2;
    private ArrayList<Long> groupQty;
    private ArrayList<String> UIDs1;
    private ArrayList<String> UIDs2;
    private ArrayList<AdminBookingClass> adminBookingClassArrayList;
    private ArrayList<UserClass> userClassArrayList;
    private RecyclerView mainRecycler;
    private AdminResAdapter adapter;
    private static final String RESERVATIONS = "Reservations";
    private static final String USERS = "Users";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.admin_reservations, findViewById(R.id.content_frame));
        toolbar.setTitle("Admin Reservations");

        mainRecycler = (RecyclerView) findViewById(R.id.recyclerView);
        mainRecycler.setLayoutManager(new LinearLayoutManager(this));

        fetch();
    }

    private void fetch() {
        adminBookingClassArrayList = new ArrayList<AdminBookingClass>();
        userClassArrayList = new ArrayList<UserClass>();
        UIDs1 = new ArrayList<String>();
        UIDs2 = new ArrayList<String>();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            // Create an instance of the database and get its reference
            Query query1 = FirebaseDatabase.getInstance().getReference()
                    .child(RESERVATIONS);
            Log.d("AdminReservations", "Query1 :" + query1);

            query1.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) {
                            UID1 = dataSnapshot1.getKey();
                            Log.d("AdminReservations", "UID1: " + UID1);
                            UIDs1.add(UID1);
                            for (DataSnapshot dataSnapshot2 : dataSnapshot1.getChildren()) {
                                UID2 = dataSnapshot2.getKey();
                                Log.d("AdminReservations", "UID2: " + UID2);
                                UIDs2.add(UID2);
                                AdminBookingClass adminBookingClass = dataSnapshot2.getValue(AdminBookingClass.class);
                                arrivalDate = adminBookingClass.getArrivalDate();
                                departureDate = adminBookingClass.getDepartureDate();
                                groupQty = adminBookingClass.getGroupQty();
                                Log.d("AdminReservations", "Group Qty: " + groupQty);
                                notes = adminBookingClass.getNotes();
                                confirmationStatus = adminBookingClass.getStatus();
                                dateBooked = adminBookingClass.getDateBooked();
                                dateEdited = adminBookingClass.getDateEdited();

                                //bookingClassArrayList.add(new BookingClass(arrivalDate, departureDate, guestNames, notes, confirmationStatus, dateBooked));

                                adminBookingClassArrayList.add(adminBookingClass);

                                if (UIDs2.size() > 1) {
                                    UIDs1.add(UID1);
                                }
                                // Log.v("Booking Class", UIDs1.get(count) + " " + arrivalDate + " " + departureDate + " " + guestNames + " " + dateBooked + " " + confirmationStatus + " " + notes);
                                count++;
                            }
                        }
                        Log.d("AdminReservations", "UID1 Count: " + UIDs1.size());
                        Log.d("AdminReservations", "UID2 Count: " + UIDs2.size());
                    } else {
                        Log.d("AdminReservations", "No Data: No data found for query 1");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            }); //End of query1

            //Create an instance of the database and get its reference
            Query query2 = FirebaseDatabase.getInstance().getReference()
                    .child(USERS);
            Log.d("AdminReservations", "Query2: " + query2);

            //query database for user first and last name and add to userClassArrayList array if the uid is equal to the uid in the booking class
            query2.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        //for each reservation in the adminBookingClassArrayList array list set the first and last name to the userClassArrayList array list
                        //by matching the uid in the adminBookingClassArrayList array list to the uid in the userClassArrayList array list
                        for (int i = 0; i < count; i++) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                if (dataSnapshot.getKey().equals(UIDs1.get(i))) {
                                    Log.d("AdminReservations", "UID: " + dataSnapshot.getKey());

                                    UserClass userClass = dataSnapshot.getValue(UserClass.class);
                                    if (userClass != null) {
                                    firstName = userClass.getFirstName();
                                    lastName = userClass.getLastName();
                                    email = userClass.getEmail();

                                    userClassArrayList.add(new UserClass(firstName, lastName, email));

                                    Log.d("AdminReservations", "Users Name: " + firstName + " " + lastName);
                                    } else {
                                        Log.e("AdminReservations", "UserClass is null for UID: " + dataSnapshot.getKey());
                                    }
                                } else {
                                    continue;
                                }
                            }
                        }

                        adapter = new AdminResAdapter(adminBookingClassArrayList, userClassArrayList, UIDs1, UIDs2, AdminReservations.this);
                        mainRecycler.setAdapter(adapter);

                    } else {
                        Log.d("AdminReservations", "No Data: No data found for query 2");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            }); //End of query2


            //On swipe, delete reservation from firebase
            new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                    ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                //False because there is not dragging involved
                @Override
                public boolean onMove(@NonNull RecyclerView recyclerView,
                                      @NonNull RecyclerView.ViewHolder viewHolder,
                                      @NonNull RecyclerView.ViewHolder target) {
                    return false;
                }

                @Override
                public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                    // Show delete confirmation if swipped left
                    if (direction == ItemTouchHelper.LEFT) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(AdminReservations.this);
                        builder.setMessage("Are you sure you want to delete?")
                                .setCancelable(false)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        adapter.deleteReservation(viewHolder.getAdapterPosition());
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                        mainRecycler.setAdapter(adapter);
                                    }
                                });
                        builder.create();
                        builder.show();

                    } else if (direction == ItemTouchHelper.RIGHT) {
                        mainRecycler.setAdapter(adapter);
                    }
                }

            }).attachToRecyclerView(mainRecycler);
        }
    }

}
