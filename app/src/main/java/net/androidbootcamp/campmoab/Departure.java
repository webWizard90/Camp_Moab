package net.androidbootcamp.campmoab;

import androidx.annotation.NonNull;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import net.androidbootcamp.campmoab.Classes.BookingClass;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Departure extends BaseActivity {
    private CheckBox first, second, third, fourth, fifth, sixth, seventh, eighth, ninth;
    private RelativeLayout relativeLayout;
    private static final String RESERVATIONS = "ViewReservations";
    private String uid, arrivalDateFromFB;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_departure, findViewById(R.id.content_frame));
        toolbar.setTitle("On Departure");

        relativeLayout = (RelativeLayout) findViewById(R.id.layoutOnComplete);
        first = (CheckBox) findViewById(R.id.checkBox1);
        second = (CheckBox) findViewById(R.id.checkBox2);
        third = (CheckBox) findViewById(R.id.checkBox3);
        fourth = (CheckBox) findViewById(R.id.checkBox4);
        fifth = (CheckBox) findViewById(R.id.checkBox5);
        sixth = (CheckBox) findViewById(R.id.checkBox6);
        seventh = (CheckBox) findViewById(R.id.checkBox7);
        eighth = (CheckBox) findViewById(R.id.checkBox8);
        ninth = (CheckBox) findViewById(R.id.checkBox9);
        //account = (ImageView) findViewById(R.id.acctImage);


        //GET DATE 2 DAYS
        // get the currently logged in user.
        user = FirebaseAuth.getInstance().getCurrentUser();
        // get the UID of the currently logged in user
        uid = user.getUid();

        // Create an instance of the database and get its reference
        Query query = FirebaseDatabase.getInstance().getReference()
                .child("Users").child(uid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Format Dates
                LocalDate today = LocalDate.now();
                String formatDate = today.format(formatter);
                LocalDate oneDayFromToday = today.plusDays(1);
                String formatNewDate1 = oneDayFromToday.format(formatter);
                LocalDate twoDaysFromToday = today.plusDays(2);
                String formatNewDate2 = twoDaysFromToday.format(formatter);
                Log.d("Departure", "Formatted day: " + formatDate);
                Log.d("Departure", "2 + days: " + formatNewDate2);

                ArrayList<String> arrivalDateList = new ArrayList<>();

                if (snapshot.exists()) {
                    for (DataSnapshot date : snapshot.child(RESERVATIONS).getChildren()) {
                        BookingClass snapDate = date.getValue(BookingClass.class);

                        arrivalDateFromFB = snapDate.getArrivalDate();
                        arrivalDateList.add(arrivalDateFromFB);
                    }
                    Log.d("Departure", "Arrival date: " + arrivalDateList);

                    //Only allow checkboxes to be saved if is on or after arrivalDate but before departureDate
                    //Remove checkbox state if date is today, or 1 or 2 days from today
                    if (arrivalDateList.contains(formatNewDate1) ||
                            arrivalDateList.contains(formatNewDate2)) {
                        first.setChecked(false);
                        second.setChecked(false);
                        third.setChecked(false);
                        fourth.setChecked(false);
                        fifth.setChecked(false);
                        sixth.setChecked(false);
                        seventh.setChecked(false);
                        eighth.setChecked(false);
                        ninth.setChecked(false);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Load checkbox state from Shared Preferences
        first.setChecked(load("CheckBox1"));
        second.setChecked(load("CheckBox2"));
        third.setChecked(load("CheckBox3"));
        fourth.setChecked(load("CheckBox4"));
        fifth.setChecked(load("CheckBox5"));
        sixth.setChecked(load("CheckBox6"));
        seventh.setChecked(load("CheckBox7"));
        eighth.setChecked(load("CheckBox8"));
        ninth.setChecked(load("CheckBox9"));

        visibilityValidation();

        first.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                save("CheckBox1", isChecked);
                visibilityValidation();
            }
        });

        second.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                save("CheckBox2", isChecked);
                visibilityValidation();
            }
        });

        third.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                save("CheckBox3", isChecked);
                visibilityValidation();
            }
        });

        fourth.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                save("CheckBox4", isChecked);
                visibilityValidation();
            }
        });

        fifth.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                save("CheckBox5", isChecked);
                visibilityValidation();
            }
        });

        sixth.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                save("CheckBox6", isChecked);
                visibilityValidation();
            }
        });

        seventh.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                save("CheckBox7", isChecked);
                visibilityValidation();
            }
        });

        eighth.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                save("CheckBox8", isChecked);
                visibilityValidation();
            }
        });

        ninth.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                save("CheckBox9", isChecked);
                visibilityValidation();
            }
        });

        /*home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Departure.this, MainActivity.class));
            }
        });

        account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Departure.this, UserAccount.class));
            }
        });*/

    }

    private void save(String key, boolean isChecked) {
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, isChecked);
        editor.commit();
    }

    private boolean load(String key) {
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        return sharedPreferences.getBoolean(key, false);
    }

    private void visibilityValidation() {
        if (first.isChecked() && second.isChecked() && third.isChecked()
                && fourth.isChecked() && fifth.isChecked())
        {
            relativeLayout.setVisibility(View.VISIBLE);
        } else {
            relativeLayout.setVisibility(View.GONE);
        }
    }
}