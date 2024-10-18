package net.androidbootcamp.campmoab;

import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import net.androidbootcamp.campmoab.Amenities.Amenities;
import net.androidbootcamp.campmoab.BaseActivities.BaseActivity;
import net.androidbootcamp.campmoab.BaseActivities.BaseMainActivity;
import net.androidbootcamp.campmoab.ArrivalsDepartures.Arrival;
import net.androidbootcamp.campmoab.ArrivalsDepartures.Departure;
import net.androidbootcamp.campmoab.Reservations.CalendarActivity;
import net.androidbootcamp.campmoab.Hiking_Trails.Hiking_Trails;
import net.androidbootcamp.campmoab.Reservations.ViewReservations;

import java.time.format.DateTimeFormatter;

public class MainActivity extends BaseMainActivity {
    //variables called
    private CardView hiking, amenities, booking, reservations, arrival, departure;
    //private ImageView menuButton;
    private TextView linkToAdmin;
    private String uid;
    private static final String RESERVATIONS = "Reservations";
    private FirebaseUser user;
    private DatabaseReference ref;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_main, findViewById(R.id.content_frame));
        //toolbar.setTitle("Home");

        hiking = (CardView) findViewById(R.id.btnHiking);
        amenities = (CardView) findViewById(R.id.btnAmenities);
        booking = (CardView) findViewById(R.id.btnBook);
        reservations = (CardView) findViewById(R.id.btnReservation);
        arrival = (CardView) findViewById(R.id.btnUponArrival);
        departure = (CardView) findViewById(R.id.btnUponDeparture);
        //menuButton = (ImageView) findViewById(R.id.menuButton);
        //linkToAdmin = (TextView) findViewById(R.id.linkToAdmin);

        ref = FirebaseDatabase.getInstance().getReference();
        // get the currently logged in user.
        user = FirebaseAuth.getInstance().getCurrentUser();
        // get the UID of the currently logged in user
        uid = user.getUid();

        //QueryDBForAdminBtn();
        //EnableDisableButtons();

        hiking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Hiking_Trails.class));
            }
        });

        amenities.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Amenities.class));
            }
        });

        booking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, CalendarActivity.class));
            }
        });

        reservations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ViewReservations.class));
            }
        });

        arrival.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Arrival.class));
            }
        });

        departure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Departure.class));
            }
        });

        /* account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, UserAccount.class));
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, LogoutActivity.class));
            }
        });

        linkToAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AdminMainActivity.class));
            }
        }); */
    }

    /*public void QueryDBForAdminBtn() {
        Query query = ref.child("Admins").child(uid).child("isAdmin");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String isAdmin = snapshot.getValue().toString();
                    Log.d("MainActivity", "isAdmin: " + isAdmin);
                    if (isAdmin.equals("true")) {
                        linkToAdmin.setVisibility(View.VISIBLE);
                        Log.d("MainActivity", "Admin Visible");
                    } else {
                        linkToAdmin.setVisibility(View.GONE);
                        Log.d("MainActivity", "Admin Not Visible");
                    }
                } else {
                    Log.d("MainActivity", "Not Admin");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }*/
}