package net.androidbootcamp.campmoab;

import androidx.annotation.NonNull;

import android.content.SharedPreferences;
import android.graphics.BlurMaskFilter;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.MaskFilterSpan;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import net.androidbootcamp.campmoab.Classes.BookingClass;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Arrival extends BaseActivity {
    private ListView listView;
    private RelativeLayout relativeLayout;
    private CheckBox first, second, third, fourth, fifth;
    private String uid;
    private static final String RESERVATIONS = "ViewReservations";
    private TextView keyCode, networkID, networkPassword;
    //private ImageView home, account;
    private FirebaseUser user;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_arrival, findViewById(R.id.content_frame));
        toolbar.setTitle("On Arrival");

        listView = (ListView) findViewById(R.id.listView);
        relativeLayout = (RelativeLayout) findViewById(R.id.layoutOnComplete);
        first = (CheckBox) findViewById(R.id.checkBox1);
        second = (CheckBox) findViewById(R.id.checkBox2);
        third = (CheckBox) findViewById(R.id.checkBox3);
        fourth = (CheckBox) findViewById(R.id.checkBox4);
        fifth = (CheckBox) findViewById(R.id.checkBox5);
        //home = (ImageView) findViewById(R.id.homeImage);
        //account = (ImageView) findViewById(R.id.acctImage);
        keyCode = (TextView) findViewById(R.id.txtKeyCode);
        networkID = (TextView) findViewById(R.id.txtNetworkID);
        networkPassword = (TextView) findViewById(R.id.txtNetworkPassword);

        //TODO add geofencing that will enable key code for lockbox when in range of the house

        //create adapter that retrieves resource from values strings.menu and adapts list_item
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(this,
                R.array.extraInfoOnArrival, R.layout.activity_arrival_list_item);

        //set array adapter to listview
        listView.setAdapter(arrayAdapter);

        //Load checkbox state from Shared Preferences
        first.setChecked(load("CheckBox1"));
        second.setChecked(load("CheckBox2"));
        third.setChecked(load("CheckBox3"));
        fourth.setChecked(load("CheckBox4"));
        fifth.setChecked(load("CheckBox5"));

        visibilityValidation();
        handleWidgetsBasedOnReservations();

        // Set up the toolbar
        //setSupportActionBar(binding.topAppBar);

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

        /*home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Arrival.this, MainActivity.class));
            }
        });

        account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Arrival.this, UserAccount.class));
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

    private void handleWidgetsBasedOnReservations() {
        //if current user reservation is today, tomorrow, or 2 days from now, unblur codes
        //else blur codes
        //Format Dates
        LocalDate today = LocalDate.now();
        String formatDate = today.format(formatter);
        LocalDate twoDaysFromToday = today.plusDays(2);
        LocalDate oneDayFromToday = today.plusDays(1);
        String formatNewDate2 = twoDaysFromToday.format(formatter);
        String formatNewDate1 = oneDayFromToday.format(formatter);
        Log.d("Arrival", "Today: " + formatDate);
        Log.d("Arrival", "1 + day: " + formatNewDate1);
        Log.d("Arrival", "2 + days" + formatNewDate2);

        // get the currently logged in user.
        user = FirebaseAuth.getInstance().getCurrentUser();
        // get the UID of the currently logged in user
        uid = user.getUid();
        Log.d("Arrival", "UID: " + uid);

        // Create an instance of the database and get its reference
        Query query = FirebaseDatabase.getInstance().getReference()
                .child(RESERVATIONS).child(uid);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Check if reservation dates exist in firebase
                if (snapshot.exists()) {
                    String arrivalDateFromFB;
                    String departureDateFromFB;
                    int count = 0;
                    ArrayList<String> arrivalDateList = new ArrayList<>();
                    ArrayList<String> departureDateList = new ArrayList<>();

                    //If true, get all arrival dates and put them into an array list
                    for (DataSnapshot date: snapshot.getChildren()) {
                        BookingClass snapDate = date.getValue(BookingClass.class);

                        arrivalDateFromFB = snapDate.getArrivalDate();
                        departureDateFromFB = snapDate.getDepartureDate();
                        Log.d("arrival dates", arrivalDateFromFB);
                        Log.d("departure dates", departureDateFromFB);

                        arrivalDateFromFB = ValidateDates(CalendarDay.from(Integer.parseInt(arrivalDateFromFB.split("-")[2]),
                                Integer.parseInt(arrivalDateFromFB.split("-")[0]) - 1,
                                Integer.parseInt(arrivalDateFromFB.split("-")[1])));
                        departureDateFromFB = ValidateDates(CalendarDay.from(Integer.parseInt(departureDateFromFB.split("-")[2]),
                                Integer.parseInt(departureDateFromFB.split("-")[0]) - 1,
                                Integer.parseInt(departureDateFromFB.split("-")[1])));

                        arrivalDateList.add(arrivalDateFromFB);
                        departureDateList.add(departureDateFromFB);
                        count++;
                    }
                    //Log.d("Arrival", "Count: " + String.valueOf(count));

                    //Only allow checkboxes to be saved if is on or after arrivalDate but before departureDate
                    //Remove checkbox state if date is 1 or 2 days from today
                    if (arrivalDateList.contains(formatNewDate1) ||
                            arrivalDateList.contains(formatNewDate2)) {
                        first.setChecked(false);
                        second.setChecked(false);
                        third.setChecked(false);
                        fourth.setChecked(false);
                        fifth.setChecked(false);
                    }

                    //if dates are found in the dateList un-blur layoutCodes else blur keyCode, networkID, and networkPassword
                    if (arrivalDateList.contains(formatDate) || arrivalDateList.contains(formatNewDate1) ||
                            arrivalDateList.contains(formatNewDate2))
                    {
                        Log.d("Arrival", "blurred: false");
                        //do not blur keyCode, networkID, and networkPassword
                    } else {
                        //blur keyCode, networkID, and networkPassword with mask filter span
                        SpannableString spannableString = new SpannableString(keyCode.getText().toString());
                        spannableString.setSpan(new MaskFilterSpan(new BlurMaskFilter(25f, BlurMaskFilter.Blur.NORMAL)),
                                0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        keyCode.setText(spannableString);
                        networkID.setText(spannableString);
                        networkPassword.setText(spannableString);

                        //disable copy and paste for keyCode, networkID, and networkPassword
                        keyCode.setLongClickable(false);
                        networkID.setLongClickable(false);
                        networkPassword.setLongClickable(false);
                        Log.d("Arrival", "blurred: true");
                    }
                } else {
                    Log.d("Arrival", "No dates found");

                    //blur keyCode, networkID, and networkPassword with mask filter span
                    SpannableString spannableString = new SpannableString(keyCode.getText().toString());
                    spannableString.setSpan(new MaskFilterSpan(new BlurMaskFilter(25f, BlurMaskFilter.Blur.NORMAL)),
                            0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    keyCode.setText(spannableString);
                    networkID.setText(spannableString);
                    networkPassword.setText(spannableString);

                    //disable copy and paste for keyCode, networkID, and networkPassword
                    keyCode.setLongClickable(false);
                    networkID.setLongClickable(false);
                    networkPassword.setLongClickable(false);
                    Log.d("Arrival", "blurred: true");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

            //Fix Error in code that causes a parsing error
    private String ValidateDates(CalendarDay date) {
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
}