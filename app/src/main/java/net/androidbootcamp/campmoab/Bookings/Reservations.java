package net.androidbootcamp.campmoab.Bookings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import net.androidbootcamp.campmoab.MainActivity;
import net.androidbootcamp.campmoab.R;
import net.androidbootcamp.campmoab.UserAccountAttributes.UserAccount;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class Reservations extends AppCompatActivity {
    private ImageView home, account;
    private String arrival, departure, uid, status, addTxt, resUID, dateBooked;
    private ArrayList<String> guests;
    private ArrayList<BookingClass> reservations;

    private RecyclerView mainRecycler;
    private ResAdapter adapter;

    private static final String RESERVATIONS = "Reservations";

    private DatabaseReference ref;
    private FirebaseUser user;

    AlertDialog.Builder builder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservations);
        home = (ImageView) findViewById(R.id.homeImage);
        account = (ImageView) findViewById(R.id.acctImage);
        //Set recycler views to layout manager
        mainRecycler = (RecyclerView) findViewById(R.id.recyclerView);
        mainRecycler.setLayoutManager(new LinearLayoutManager(this));

        builder = new AlertDialog.Builder(Reservations.this);

        fetch();

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Reservations.this, MainActivity.class));
            }
        });

        account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Reservations.this, UserAccount.class));
            }
        });
    }

    private void fetch() {
        //Get instance of database
        ref = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser(); // get the currently logged in user.
        uid = user.getUid(); // get the UID of the currently logged in user
        Log.i("User UID", uid);
        //get current date
        String stToday = new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault()).format(new Date());
        //String resUID = ref.push().getKey(); //Value of your key

        //QUERY DB FOR RESERVATIONS THAT HAVE RANDOM KEYS AND CHILD NODE OF UID
        Query query = ref.child(RESERVATIONS).child(uid);

        // Query database to fetch appropriate data using FirebaseUI
        /*FirebaseRecyclerOptions<BookingClass> options =
                new FirebaseRecyclerOptions.Builder<BookingClass>()
                        //.setIndexedQuery(keyQuery, ref, BookingClass.class)
                        .setQuery(query, BookingClass.class)
                        .build();

         */
        // Check if user has any reservations
        // If not, ask if they would like to make one
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                reservations = new ArrayList<>();
                if (!snapshot.exists()) {
                    builder.setMessage("You have no reservations. Would you like to make one?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    startActivity(new Intent(Reservations.this, BookingActivity.class));
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    startActivity(new Intent(Reservations.this, MainActivity.class));
                                }
                            });
                    builder.create();
                    builder.show();
                } else {
                    Log.i("Snapshot", "Snapshot exists");
                   for (DataSnapshot ds : snapshot.getChildren()) {
                       resUID = ds.getKey();
                       Log.i("User ref", resUID);

                       arrival = ds.child("arrivalDate").getValue(String.class);
                       departure = ds.child("departureDate").getValue(String.class);
                       guests = (ArrayList<String>) ds.child("guestNames").getValue();
                       addTxt = ds.child("addTxt").getValue(String.class);
                       status = ds.child("confirmationStatus").getValue(String.class);
                       dateBooked = ds.child("dateBooked").getValue(String.class);
                       //bookingClass = new BookingClass(arrival, departure, guests, addTxt, status, dateBooked);

                       //for (DataSnapshot ds2 : ds.getChildren()) {
                           //BookingClass bookingClass = new BookingClass(ds2.getValue(BookingClass.class));
                           /*BookingClass bookingClass = ds2.getValue(BookingClass.class);
                           arrival = bookingClass.getArrivalDate();
                           departure = bookingClass.getDepartureDate();
                           guests = bookingClass.getGuestNames();
                           addTxt = bookingClass.getAddTxt();
                           status = bookingClass.getConfirmationStatus();
                           resUID = ds2.getKey();
                           dateBooked = bookingClass.getDateBooked();*/

                        Log.i("Departure", departure);

                       //if booking Departure date is after stToday, don't add to list
                        if (departure.compareTo(stToday) > 0) {
                            reservations.add(new BookingClass(arrival, departure, guests, addTxt, status, dateBooked));
                            //reservations.add(bookingClass);
                            //Log.d("User ref", ds.getRef().toString());
                            //Log.d("User val", ds.getValue().toString());
                            Log.v("Reservation", arrival + " " + departure + " " + guests + " " + addTxt + " " + resUID + " " + status);
                        }
                       //}
                   }
                    // Connecting object of required Adapter class to
                    // the Adapter class itself
                    adapter = new ResAdapter(reservations, resUID,Reservations.this);
                    // Connecting Adapter class with the Recycler view
                    mainRecycler.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


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

                    AlertDialog.Builder builder = new AlertDialog.Builder(Reservations.this);
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
    // Function to tell the app to start getting
    // data from database on starting of the activity
 /*  @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    // Function to tell the app to stop getting
    // data from database on stopping of the activity
    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

  */
}