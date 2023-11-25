package net.androidbootcamp.campmoab.Admin;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import net.androidbootcamp.campmoab.Admin.Adapters.AdminResAdapter;
import net.androidbootcamp.campmoab.Bookings.BookingClass;
import net.androidbootcamp.campmoab.MainActivity;
import net.androidbootcamp.campmoab.R;
import net.androidbootcamp.campmoab.UserAccountAttributes.UserAccount;
import net.androidbootcamp.campmoab.UserAccountAttributes.UserClass;

import java.util.ArrayList;

public class AdminReservations extends AppCompatActivity {
    int count = 0;
    private String firstName, lastName, email;
    private String arrivalDate, departureDate, dateBooked, confirmationStatus, notes, uid1, uid2;

    private ArrayList<String> guestNames;
    private ArrayList<String> uids1;
    private ArrayList<String> uids2;
    private ArrayList<BookingClass> bookingClassArrayList;
    private ArrayList<UserClass> userClassArrayList;

    private ImageView home, account;

    private RecyclerView mainRecycler;
    private AdminResAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_reservations);
        mainRecycler = (RecyclerView) findViewById(R.id.recyclerView);
        mainRecycler.setLayoutManager(new LinearLayoutManager(this));
        home = (ImageView) findViewById(R.id.home);
        account = (ImageView) findViewById(R.id.acct);

        //Get instance of database
        //ref = FirebaseDatabase.getInstance().getReference();
        // Connecting object of required Adapter class to
        // the Adapter class itself
        //LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        fetch();

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminReservations.this, MainActivity.class));
            }
        });

        account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminReservations.this, UserAccount.class));
            }
        });
    }

        private void fetch() {
            bookingClassArrayList = new ArrayList<BookingClass>();
            userClassArrayList = new ArrayList<UserClass>();
            uids1 = new ArrayList<String>();
            uids2 = new ArrayList<String>();

            // Create an instance of the database and get its reference
            Query query1 = FirebaseDatabase.getInstance().getReference()
                    .child("Reservations");
            Log.v("Query1", query1.toString());

            query1.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) {
                            uid1 = dataSnapshot1.getKey();
                            Log.v("UID1", uid1);
                            uids1.add(uid1);
                            for (DataSnapshot dataSnapshot2 : dataSnapshot1.getChildren()) {
                                uid2 = dataSnapshot2.getKey();
                                Log.v("UID2", uid2);
                                uids2.add(uid2);
                                BookingClass bookingClass = dataSnapshot2.getValue(BookingClass.class);
                                arrivalDate = bookingClass.getArrivalDate();
                                departureDate = bookingClass.getDepartureDate();
                                guestNames = bookingClass.getGuestNames();
                                notes = bookingClass.getAddTxt();
                                confirmationStatus = bookingClass.getConfirmationStatus();
                                dateBooked = bookingClass.getDateBooked();

                                bookingClassArrayList.add(new BookingClass(arrivalDate, departureDate, guestNames, notes, confirmationStatus, dateBooked));

                                if (uids2.size() > 1) {
                                    uids1.add(uid1);
                                }
                               // Log.v("Booking Class", uids1.get(count) + " " + arrivalDate + " " + departureDate + " " + guestNames + " " + dateBooked + " " + confirmationStatus + " " + notes);
                                count++;
                            }
                        }
                        Log.v("UID1 Count", String.valueOf(uids1.size()));
                        Log.v("UID2 Count", String.valueOf(uids2.size()));
                    } else {
                        Log.v("No Data", "No data found");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            }); //End of query1

            //Create an instance of the database and get its reference
            Query query2 = FirebaseDatabase.getInstance().getReference()
                    .child("Users");
            Log.v("Query2", query2.toString());

            //query database for user first and last name and add to userClassArrayList array if the uid is equal to the uid in the booking class
            query2.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        //for each reservation in the bookingClassArrayList array list set the first and last name to the userClassArrayList array list
                        //by matching the uid in the bookingClassArrayList array list to the uid in the userClassArrayList array list
                        for (int i = 0; i < count; i++) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                if (dataSnapshot.getKey().equals(uids1.get(i))) {
                                    Log.v("UID", dataSnapshot.getKey());

                                    UserClass userClass = dataSnapshot.getValue(UserClass.class);
                                    firstName = userClass.getFirstName();
                                    lastName = userClass.getLastName();
                                    email = userClass.getEmail();

                                    userClassArrayList.add(new UserClass(firstName, lastName, email));

                                    Log.v("Users Name", firstName + " " + lastName);
                                } else {
                                    continue;
                                }
                            }
                        }

                        adapter = new AdminResAdapter(bookingClassArrayList, userClassArrayList, uids1,  uids2,AdminReservations.this);
                        mainRecycler.setAdapter(adapter);

                    } else {
                            Log.v("No Data", "No data found");
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
