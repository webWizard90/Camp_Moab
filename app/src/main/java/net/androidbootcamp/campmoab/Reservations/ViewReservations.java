package net.androidbootcamp.campmoab.Reservations;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import net.androidbootcamp.campmoab.BaseActivities.BaseActivity;
import net.androidbootcamp.campmoab.Classes.ReservationClass;
import net.androidbootcamp.campmoab.Reservations.Adapters.ResAdapter;
import net.androidbootcamp.campmoab.Classes.DateClass;
import net.androidbootcamp.campmoab.Classes.FirebaseHelperClass;
import net.androidbootcamp.campmoab.MainActivity;
import net.androidbootcamp.campmoab.R;
import net.androidbootcamp.campmoab.Classes.UserClass;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ViewReservations extends BaseActivity {
    private String resID, firstName, lastName, email, UID;
    private ArrayList<String> resIDs;
    private RecyclerView mainRecycler;
    private ResAdapter adapter;
    private static final String RESERVATIONS = "Reservations";
    private static final String USERS = "Users";
    private FirebaseHelperClass firebaseHelper;
    private DatabaseReference ref;
    private FirebaseUser user;
    private boolean isActivityVisible = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_reservations, findViewById(R.id.content_frame));
        toolbar.setTitle("Reservations");

        //Set recycler views to layout manager
        mainRecycler = (RecyclerView) findViewById(R.id.recyclerView);
        mainRecycler.setLayoutManager(new LinearLayoutManager(this));

        firebaseHelper = new FirebaseHelperClass();
        ref = firebaseHelper.getRef();
        user = firebaseHelper.getCurrentUser();
        UID = user.getUid(); // Get the UID of the currently logged-in user

        checkUserAccessLevel();
        onSwipe();
    }

    // Query Current User Reservations Using UID
    private void loadCurrentUserReservations() {
        ArrayList<ReservationClass> userReservations = new ArrayList<>();
        DateClass date = new DateClass();
        String stToday = date.getCurrentFormattedDate();

        Log.d("ViewReservations", "User UID: " + UID);

        Query queryUserReservations = ref.child(RESERVATIONS).child(UID);
        queryUserReservations.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<ReservationClass> allConfirmedUserReservations = new ArrayList<>();
                resIDs = new ArrayList<>();

                if (snapshot.exists()) {
                    //Log.d("ViewReservations", "Snapshot: " + "Snapshot exists");
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        ReservationClass reservationClass = ds.getValue(ReservationClass.class);
                        resID = ds.getKey();

                        if (reservationClass != null && resID != null) {
                            /*Log.d("ViewReservations", "Loaded resID: " + resID
                                + ", UID: " + UID
                                + ", Notes: " + reservationClass.getNotes()
                                + ", Arrival: " + reservationClass.getArrivalDate()
                                + ", Departure: " + reservationClass.getDepartureDate()
                                + ", GroupQty: " + reservationClass.getGroupQty());*/

                            LocalDate departureDate = date.parseStringToDate(reservationClass.getDepartureDate()); // Get the departure date (LocalDate)
                            LocalDate today = LocalDate.now(); // Get the current date

                            if (departureDate.isAfter(today)) {
                                resIDs.add(resID);
                                allConfirmedUserReservations.add(reservationClass);
                            }

                        } else {
                            Log.e("ViewReservations", "ReservationClass: " + "ReservationClass is null");
                        }
                    }

                    userReservations.addAll(allConfirmedUserReservations);

                    adapter = new ResAdapter(userReservations, UID, resIDs, ViewReservations.this, false);
                    mainRecycler.setAdapter(adapter);
                } else {
                    Log.e("ViewReservations", "Snapshot: " + "Snapshot does not exist");
                    showNoReservationsDialog();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ViewReservations", "Firebase error: " + error.getMessage());
            }
        });

    }

    // ADMIN QUERY FOR All RESERVATIONS
    private void loadAllReservations() {
        ArrayList<ReservationClass> allReservations = new ArrayList<>();
        ArrayList<String> UIDs = new ArrayList<>();
        resIDs = new ArrayList<>();

        Query queryAllUsersReservations = ref.child(RESERVATIONS);
        queryAllUsersReservations.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) {
                        UID = dataSnapshot1.getKey();
                        //Log.d("ViewReservations", "All Reservations UID: " + UID);

                        List<ReservationClass> userReservations = new ArrayList<>();

                        for (DataSnapshot dataSnapshot : dataSnapshot1.getChildren()) {
                            resID = dataSnapshot.getKey();
                            ReservationClass reservationClass = dataSnapshot.getValue(ReservationClass.class);

                            if (reservationClass != null && resID != null) {
                                resIDs.add(resID);
                                UIDs.add(UID);
                                reservationClass.setReservationID(resID);

                                /*Log.d("ViewReservations", "Loaded resID: " + resID
                                        + ", UID: " + UID
                                        + ", Notes: " + reservationClass.getNotes()
                                        + ", Arrival: " + reservationClass.getArrivalDate()
                                        + ", Departure: " + reservationClass.getDepartureDate()
                                        + ", GroupQty: " + reservationClass.getGroupQty());*/

                                userReservations.add(reservationClass);
                            }
                        }
                        allReservations.addAll(userReservations);
                    }

                    // Log the final sizes of the lists
                    //Log.d("ViewReservations", "Final UIDs size: " + UIDs.size());
                    //Log.d("ViewReservations", "Final resIDs size: " + resIDs.size());
                    //Log.d("ViewReservations", "Final allReservations size: " + allReservations.size());

                    loadUserInfo(allReservations, UIDs, resIDs);

                } else {
                    Log.d("ViewReservations", "No Data found for all User Query Reservations");
                }

                //adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadUserInfo(ArrayList<ReservationClass> userReservations, ArrayList<String> UIDs, ArrayList<String> resIDs) {
        ArrayList<UserClass> userArrayList = new ArrayList<>();
        // QUERY DB FOR ALL USERS
        Query queryUser = ref.child(USERS);
        queryUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (int i = 0; i < userReservations.size(); i++) {

                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                        if (dataSnapshot.getKey().equals(UIDs.get(i))) {
                        //Log.d("ViewReservations", "UIDs Size: " + UIDs.size() + " i: " + userID);
                            UserClass userClass = dataSnapshot.getValue(UserClass.class);
                            if (userClass != null) {
                                //Log.d("ViewReservations", "userID For USER is equal to: " + dataSnapshot.getKey());
                                if (userClass != null) {
                                    //Log.d("ViewReservations", "UserClass is not null");

                                    firstName = userClass.getFirstName();
                                    lastName = userClass.getLastName();
                                    email = userClass.getEmail();

                                    ReservationClass reservationClass = new ReservationClass();
                                    reservationClass.setUser(new UserClass(firstName, lastName, email));

                                    //userArrayList.add(new UserClass(firstName, lastName, email, UIDs.get(i)));
                                    userArrayList.add(userClass);

                                    //Log.d("ViewReservations", "Users Name: " + userClass.getFirstName() + " " + userClass.getLastName() + " Email: " + userClass.getEmail() + " userID: " + UIDs.get(i));
                                } else {
                                    Log.e("ViewReservations", "UserClass is null for userID: " + dataSnapshot.getKey());
                                }
                            } else {
                                //Log.d("ViewReservations", "userID For USER is not equal to: " + dataSnapshot.getKey());
                            }
                        }
                    }

                    adapter = new ResAdapter(userReservations, userArrayList, UIDs, resIDs, ViewReservations.this, true);
                    mainRecycler.setAdapter(adapter);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showNoReservationsDialog() {
        if (isActivityVisible) { // Only show the dialog if the activity is visible
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("You have no reservations. Would you like to make one?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            startActivity(new Intent(ViewReservations.this, CalendarActivity.class));
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            startActivity(new Intent(ViewReservations.this, MainActivity.class));
                        }
                    });
            builder.create();
            builder.show();
        }
    }

    private void checkUserAccessLevel() {
        if (UID != null) {
            firebaseHelper.checkIfAdmin(UID, new FirebaseHelperClass.AdminCheckCallback() {
                @Override
                public void onAdminCheck(boolean isAdmin) {
                    //Log.d("ViewReservations", "CheckUserAccess isAdmin: " + isAdmin);
                    if (isAdmin) {
                        Log.d("ViewReservations", "User is an admin");
                        loadAllReservations();
                    } else {
                        Log.d("ViewReservations", "User is not an admin");
                        loadCurrentUserReservations();
                    }
                }
            });
        } else {
            Log.e("ViewReservations", "No user is logged in.");
        }
    }

    // Swipe to delete
    private void onSwipe() {
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

                    AlertDialog.Builder builder = new AlertDialog.Builder(ViewReservations.this);
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

    @Override
    protected void onStart() {
        super.onStart();
        isActivityVisible = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        isActivityVisible = false;
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