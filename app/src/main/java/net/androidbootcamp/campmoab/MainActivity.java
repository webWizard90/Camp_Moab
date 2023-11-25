package net.androidbootcamp.campmoab;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import net.androidbootcamp.campmoab.Admin.AdminMainActivity;
import net.androidbootcamp.campmoab.Amenities.Amenities;
import net.androidbootcamp.campmoab.Bookings.BookingClass;
import net.androidbootcamp.campmoab.Bookings.BookingActivity;
import net.androidbootcamp.campmoab.UserAccountAttributes.LogoutPage;
import net.androidbootcamp.campmoab.Bookings.Reservations;
import net.androidbootcamp.campmoab.UserAccountAttributes.UserAccount;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    //variables called
    private CardView hiking, amenities, booking, reservations, arrival, departure, account, logout;
    private TextView linkToAdmin;

    private String uid;
    private static final String RESERVATIONS = "Reservations";

    private FirebaseUser user;
    private DatabaseReference ref;

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);

        hiking = findViewById(R.id.btnHiking);
        amenities = findViewById(R.id.btnAmenities);
        booking = findViewById(R.id.btnBook);
        reservations = findViewById(R.id.btnReservation);
        arrival = findViewById(R.id.btnUponArrival);
        departure = findViewById(R.id.btnUponDeparture);
        account = findViewById(R.id.btnAccount);
        logout = findViewById(R.id.btnLogout);
        linkToAdmin = findViewById(R.id.linkToAdmin);

        ref = FirebaseDatabase.getInstance().getReference();
        // get the currently logged in user.
        user = FirebaseAuth.getInstance().getCurrentUser();
        // get the UID of the currently logged in user
        uid = user.getUid();

        QueryDBForAdminBtn();
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
                startActivity(new Intent(MainActivity.this, BookingActivity.class));
            }
        });

        reservations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Reservations.class));
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

        account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, UserAccount.class));
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, LogoutPage.class));
            }
        });

        linkToAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AdminMainActivity.class));
            }
        });
    }

    public void QueryDBForAdminBtn() {
        Query query = ref.child("Admins").child(uid).child("isAdmin");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String isAdmin = snapshot.getValue().toString();
                    Log.i("isAdmin", isAdmin);
                    if (isAdmin.equals("true")) {
                        linkToAdmin.setVisibility(View.VISIBLE);
                        Log.i("isAdminTrue", isAdmin);
                    } else {
                        linkToAdmin.setVisibility(View.GONE);
                        Log.i("isAdminFalse", isAdmin);
                    }
                } else {
                    Log.i("isAdminNull", "null");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

   /* public void EnableDisableButtons() {
        //initially set background color to light gray and disable click
        arrival.setCardBackgroundColor(Color.LTGRAY);
        arrival.setEnabled(false);
        departure.setCardBackgroundColor(Color.LTGRAY);
        departure.setEnabled(false);

        //Format Dates
        LocalDate today = LocalDate.now();
        String formatDate = today.format(formatter);
        LocalDate twoDaysFromToday = today.plusDays(2);
        LocalDate oneDayFromToday = today.plusDays(1);
        String formatNewDate2 = twoDaysFromToday.format(formatter);
        String formatNewDate1 = oneDayFromToday.format(formatter);
        Log.v("formatted day", formatDate);
        Log.v("2 + days", formatNewDate1);
        Log.v("2 + days", formatNewDate2);

        // Create an instance of the database and get its reference
        Query query = FirebaseDatabase.getInstance().getReference()
                .child("Users").child(uid).child(RESERVATIONS);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Check if reservation dates exist in firebase
                if (snapshot.exists()) {
                    String arrivalDateFromFB;
                    String departureDateFromFB;
                    int positionDate = 0;
                    int count = 0;
                    ArrayList<String> arrivalDateList = new ArrayList<>();
                    ArrayList<String> departureDateList = new ArrayList<>();

                    //If true, get all arrival dates and put them into an array list
                    for (DataSnapshot date : snapshot.getChildren()) {
                        BookingClass snapDate = date.getValue(BookingClass.class);

                        arrivalDateFromFB = snapDate.getArrivalDate();
                        departureDateFromFB = snapDate.getDepartureDate();

                        arrivalDateFromFB = ValidateDates2(CalendarDay.from(Integer.parseInt(arrivalDateFromFB.split("-")[2]),
                                Integer.parseInt(arrivalDateFromFB.split("-")[0]) - 1,
                                Integer.parseInt(arrivalDateFromFB.split("-")[1])));
                        departureDateFromFB = ValidateDates2(CalendarDay.from(Integer.parseInt(departureDateFromFB.split("-")[2]),
                                Integer.parseInt(departureDateFromFB.split("-")[0]) - 1,
                                Integer.parseInt(departureDateFromFB.split("-")[1])));

                        arrivalDateList.add(arrivalDateFromFB);
                        Log.d("arrival dates", arrivalDateFromFB);
                        departureDateList.add(departureDateFromFB);
                        Log.d("departure dates", departureDateFromFB);
                        count++;
                    }
                    Log.d("count", String.valueOf(count));


                    //if dates are found in the dateList enable arrival and departure buttons
                    if (arrivalDateList.contains(formatDate) || arrivalDateList.contains(formatNewDate1) ||
                            arrivalDateList.contains(formatNewDate2)) {
                        arrival.setEnabled(true);
                        arrival.setCardBackgroundColor(Color.WHITE);
                        arrival.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(MainActivity.this, Arrival.class));
                            }
                        });
                        departure.setEnabled(true);
                        departure.setCardBackgroundColor(Color.WHITE);
                        departure.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(MainActivity.this, Departure.class));
                            }
                        });
                    } else {
                        //if reservations do not exist,
                        //set background color to light gray and disable click
                        arrival.setCardBackgroundColor(Color.LTGRAY);
                        arrival.setEnabled(false);
                        departure.setCardBackgroundColor(Color.LTGRAY);
                        departure.setEnabled(false);
                    }

                    //get position of arrivaldate in list
                    for (int i = 0; i < count; i++) {

                        if (arrivalDateList.contains(formatDate)) {
                            positionDate = i;

                            Log.v("position", String.valueOf(positionDate));
                            break;
                        }
                    }

                    //get dates in between arrival and departure dates
                    LocalDate arrivalFormat = LocalDate.parse(arrivalDateList.get(positionDate), formatter);
                    LocalDate departureFormat = LocalDate.parse(departureDateList.get(positionDate), formatter);

                    ArrayList<String> datesInBetween = new ArrayList<>();
                    int daysBetween = (int) ChronoUnit.DAYS.between(arrivalFormat, departureFormat);
                    Log.v("days between", String.valueOf(daysBetween));

                    for (int i = 1; i < daysBetween; i++) {
                        String moreDays = arrivalFormat.plusDays(i).toString();
                        String formatDays = LocalDate.parse(moreDays).format(formatter);
                        datesInBetween.add(formatDays);
                        Log.v("more days", datesInBetween.toString());

                    }
                    datesInBetween.add(LocalDate.parse(departureFormat.toString()).format(formatter));
                    Log.v("dates in between", datesInBetween.toString());

                    //if today's date is in between arrival and departure dates
                    for (String dates: datesInBetween) {
                        if (dates.equals(formatDate)) {
                            arrival.setEnabled(true);
                            arrival.setCardBackgroundColor(Color.WHITE);
                            arrival.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    startActivity(new Intent(MainActivity.this, Arrival.class));
                                }
                            });
                            departure.setEnabled(true);
                            departure.setCardBackgroundColor(Color.WHITE);
                            departure.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    startActivity(new Intent(MainActivity.this, Departure.class));
                                }
                            });
                        }
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //Fix Error in code that causes a parsing error
    private String ValidateDates2(CalendarDay date) {
        //if the date is less than 10 add a 0 in front of the number
        String month = String.valueOf(date.getMonth() + 1);
        String day = String.valueOf(date.getDay());
        if (date.getMonth() + 1 < 10) {
            month = "0" + month;
        }
        if (date.getDay() < 10) {
            day = "0" + day;
        }
        return month + "-" + day + "-" + date.getYear();
    }

    */
}