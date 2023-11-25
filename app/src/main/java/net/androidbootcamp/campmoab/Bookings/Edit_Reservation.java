package net.androidbootcamp.campmoab.Bookings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
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
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnRangeSelectedListener;

import net.androidbootcamp.campmoab.Bookings.Utils.ConfirmedBookingDecorator;
import net.androidbootcamp.campmoab.Bookings.Utils.PendingBookingDecorator;
import net.androidbootcamp.campmoab.R;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Edit_Reservation extends AppCompatActivity {
    private TextView checkin, checkout, guestsText, cancel;
    private EditText additionalInfo, guest;
    private CheckBox addGuests;
    private Spinner spinnerGuests;
    private Button update;
    private String arrival, departure, info, confirmationStatus,  resId, dateBooked, uid, originalCheckout, originalCheckin;
    private static final String RESERVATIONS = "Reservations";

    private String[] guestName2, guestCount;
    private DatabaseReference ref;
    private FirebaseUser user;

    private long numValue;

    private ArrayList<String> guestNames;

    private LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_reservation);
        checkin = (TextView) findViewById(R.id.txtArrival);
        checkout = (TextView) findViewById(R.id.txtDeparture);
        additionalInfo = (EditText) findViewById(R.id.txtAdditionalInfo);
        guestsText = (TextView) findViewById(R.id.txtNumberOfGuests);
        cancel = (TextView) findViewById(R.id.txtCancel);
        addGuests = (CheckBox) findViewById(R.id.chkBoxGuests);
        spinnerGuests = (Spinner) findViewById(R.id.spinnerGuests);
        update = (Button) findViewById(R.id.updateBtn);
        layout = (LinearLayout) findViewById(R.id.guestLinearList);

        //add string-array numGuests to the spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.numGuests, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGuests.setAdapter(adapter);

        //get the snapshot of the reservation from the intent
        Bundle extras = getIntent().getExtras();
        arrival = extras.getString("arrivalDate");
        departure = extras.getString("departureDate");
        info = extras.getString("addTxt");
        //key = extras.getString("key");
        resId = extras.getString("resID");
        Log.v("resId", resId);
        confirmationStatus = extras.getString("confirmationStatus");
        dateBooked = extras.getString("dateBooked");
        Log.v("dateBooked", dateBooked);
        //Log.v("confirmationStatus", confirmationStatus);

                //get reference of the db
        ref = FirebaseDatabase.getInstance().getReference();
        //user = FirebaseAuth.getInstance().getCurrentUser();
        //uid = user.getUid(); // get the UID of the currently logged in user

        //get the number of guests from the intent
        numValue = extras.getInt("numOfGuests");
        Log.v("numGuests", String.valueOf(numValue));
        //remove [ ] and spaces and separate the guestNames between , and add to arraylist
        guestCount = extras.getString("guestNames").split("[\\[\\]]");
        //Log.v("guestCount", guestCount[0]);

        //set the text of the TextViews
        checkin.setText(arrival);
        checkout.setText(departure);
        additionalInfo.setText(info);

        //QueryForAuth();

        //when check box is not clicked, the spinner is not visible
        addGuests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (addGuests.isChecked()) {
                    spinnerGuests.setVisibility(View.VISIBLE);
                    guestsText.setVisibility(View.VISIBLE);
                    spinnerGuests.setSelection(0);

                    //add edittext views for each guest after add guests is checked
                    guest = new EditText(Edit_Reservation.this);
                    guest.setGravity(Gravity.LEFT | Gravity.TOP);
                    guest.setTextSize(20);
                    guest.setPadding(20, 20, 20, 20);
                    guest.setBackgroundColor(getResources().getColor(R.color.white));
                    guest.setBackground(getResources().getDrawable(R.drawable.border));
                    guest.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.delete, 0);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.setMargins(0, 0, 0, 20);
                    guest.setLayoutParams(params);
                    layout = (LinearLayout) findViewById(R.id.guestLinearList);
                    layout.addView(guest);
                    guestNames.add(guest.getText().toString());

                    //when the delete icon is clicked, remove the edittext view
                    guest.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            if (guest.getCompoundDrawables()[2] != null) {
                                boolean tappedX = event.getX() > (guest.getWidth() - guest.getPaddingRight() - guest.getCompoundDrawables()[2].getIntrinsicWidth());
                                if (tappedX) {
                                    spinnerGuests.setVisibility(View.INVISIBLE);
                                    guestsText.setVisibility(View.INVISIBLE);
                                    addGuests.setChecked(false);
                                    layout.removeView(guest);
                                    return true;
                                }
                            }
                            return false;
                        }
                    });

                } else {
                    spinnerGuests.setVisibility(View.INVISIBLE);
                    guestsText.setVisibility(View.INVISIBLE);
                    layout.removeAllViews();
                }
            }
        });

        guestNames = new ArrayList<String>();

        if (guestCount.length > 0) {
            addGuests.setChecked(true);
            spinnerGuests.setVisibility(View.VISIBLE);
            guestsText.setVisibility(View.VISIBLE);
            spinnerGuests.setSelection((int) numValue - 1);

            //get the guest names from the intent
            for (int i = 0; i < numValue; i++) {
                guestName2 = guestCount[1].split(", ");
                guestNames.add(guestName2[i]);
                Log.v("guestNames", guestNames.get(i));
            }

            //add the edittext views for each guest
            for (int i = 0; i < numValue; i++) {
                guest = new EditText(this);
                guest.setText(guestNames.get(i));
                guest.setGravity(Gravity.LEFT | Gravity.TOP);
                guest.setTextSize(24);
                guest.setPadding(15, 15, 15, 15);
                guest.setBackgroundColor(getResources().getColor(R.color.white));
                guest.setBackground(getResources().getDrawable(R.drawable.border));
                guest.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.delete, 0);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 0, 0, 20);
                guest.setLayoutParams(params);
                layout = (LinearLayout) findViewById(R.id.guestLinearList);
                layout.addView(guest);
                guestNames.add(guest.getText().toString());
            }
        } else {
            addGuests.setChecked(false);
            spinnerGuests.setVisibility(View.INVISIBLE);
            guestsText.setVisibility(View.INVISIBLE);
        }

        //when the user selects a different number of guests, update the edittext views
        spinnerGuests.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //if the user selects lower number of guests than the current number of guests,
                //remove the edittext views at the end of the list
                //and remove the guest from the arraylist and update the spinner
                //if the user selects higher number of guests than the current number of guests,
                //add edittext views at the end of the list
                //and add the guest to the arraylist and update the spinner
                int numGuests = Integer.parseInt(spinnerGuests.getSelectedItem().toString());
                int currentNumGuests = layout.getChildCount();
                Log.v("numGuests", String.valueOf(numGuests));
                Log.v("currentNumGuests", String.valueOf(currentNumGuests));

                if (numGuests < currentNumGuests) {
                    for (int i = currentNumGuests; i > numGuests; i--) {
                        layout.removeViewAt(i - 1);
                        guestNames.remove(i - 1);
                    }
                } else if (numGuests > currentNumGuests && addGuests.isChecked()) {
                    for (int i = currentNumGuests; i < numGuests; i++) {
                        guest = new EditText(Edit_Reservation.this);
                        guest.setGravity(Gravity.LEFT | Gravity.TOP);
                        guest.setTextSize(24);
                        guest.setPadding(15, 15, 15, 15);
                        guest.setBackgroundColor(getResources().getColor(R.color.white));
                        guest.setBackground(getResources().getDrawable(R.drawable.border));
                        guest.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.delete, 0);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        params.setMargins(0, 0, 0, 20);
                        guest.setLayoutParams(params);
                        layout = (LinearLayout) findViewById(R.id.guestLinearList);
                        layout.addView(guest);
                        guestNames.add(guest.getText().toString());
                    }
                }

                //clicking the delete icon removes the edittext view at that position and
                //remove the guest from the arraylist and update the spinner
                for (int i = 0; i < layout.getChildCount(); i++) {
                    View v = layout.getChildAt(i);
                    if (v instanceof EditText) {
                        final int index = i;
                        final EditText editText = (EditText) v;
                        editText.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                if (event.getAction() == MotionEvent.ACTION_UP) {
                                    if (event.getRawX() >= (editText.getRight() - editText.getCompoundDrawables()[2].getBounds().width())) {
                                        layout.removeViewAt(index);
                                        if (!guestNames.isEmpty()) {
                                            guestNames.remove(index);
                                        }
                                        spinnerGuests.setSelection(spinnerGuests.getSelectedItemPosition() - 1);

                                        if (layout.getChildCount() == 0) {
                                            addGuests.setChecked(false);
                                            spinnerGuests.setVisibility(View.INVISIBLE);
                                            guestsText.setVisibility(View.INVISIBLE);
                                        }
                                        return true;
                                    }
                                }

                                return false;
                            }
                        });
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        //If the user wants to cancel the reservation
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Cancel editing the reservation and return to the reservations activity
                Intent intent = new Intent(Edit_Reservation.this, Reservations.class);
                startActivity(intent);
            }
        });

        //Update the database with the new information
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Get the information from the database
                //ref = FirebaseDatabase.getInstance().getReference();
                user = FirebaseAuth.getInstance().getCurrentUser(); // get the currently logged in user.

                FirebaseDatabase db = FirebaseDatabase.getInstance();
                DatabaseReference ref = db.getReference();
                uid = user.getUid(); // get the UID of the currently logged in user



                //Update the database
                //If the user wants to add guests, add the guests to the database
                if (addGuests.isChecked()) {
                    //get the number of guests from the spinner
                    int numGuests = Integer.parseInt(spinnerGuests.getSelectedItem().toString());
                    //get the guest names from the edittext views
                    guestNames.clear();
                    for (int i = 0; i < numGuests; i++) {
                        View v2 = layout.getChildAt(i);
                        if (v2 instanceof EditText) {
                            EditText editText = (EditText) v2;
                            guestNames.add(editText.getText().toString());
                        }
                    }

                    //check if the user has entered all the information
                    if (checkin.getText().toString().isEmpty() ||
                            checkout.getText().toString().isEmpty() || layout.getChildCount() > 0 &&
                            guest.getText().toString().isEmpty()) {
                        Toast.makeText(Edit_Reservation.this,
                                "Please enter all guest names", Toast.LENGTH_SHORT).show();
                    } else {
                        //get the information from the edittext views
                        String mArrivalDate = checkin.getText().toString();
                        String mDepartureDate = checkout.getText().toString();
                        String mAdditionalInfo = additionalInfo.getText().toString();

                        //Create bookingClass object with new formated date
                        BookingClass bookingClass = new
                                BookingClass(mArrivalDate, mDepartureDate, guestNames, mAdditionalInfo, confirmationStatus, dateBooked);
                        bookingClass.toMap();

                        ref.child("Reservations").child(uid).child(resId).setValue(bookingClass);
                        Toast.makeText(Edit_Reservation.this,
                                "Reservation Updated", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Edit_Reservation.this, Reservations.class);
                        startActivity(intent);
                    }

                } else {
                    //get the information from the edittext views if the user does not want to add guests
                    guestNames.clear();
                    String mArrivalDate = checkin.getText().toString();
                    String mDepartureDate = checkout.getText().toString();
                    String mAdditionalInfo = additionalInfo.getText().toString();
                    guestNames.add("");
                    Log.v("guestNames", guestNames.toString());

                    //Create bookingClass object with new formated date
                    BookingClass bookingClass = new
                            BookingClass(mArrivalDate, mDepartureDate, guestNames, mAdditionalInfo, confirmationStatus, dateBooked);
                    bookingClass.toMap();

                    ref.child("Reservations").child(uid).child(resId).setValue(bookingClass);
                    Toast.makeText(Edit_Reservation.this,
                            "Reservation Updated", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Edit_Reservation.this, Reservations.class);
                    startActivity(intent);
                }

            }
        });
    }

    /*private void QueryForAuth() {
        //query the db for users isAdmin value and if true, set the resStatusTxt and resStatus to visible
        Query query1 = ref.child("Users").orderByKey().equalTo(uid);
        query1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String isAdmin = dataSnapshot.child("isAdmin").getValue(String.class);

                        if (isAdmin.equals("true") & isAdmin != null) {
                            Query query2 = ref.child("Reservations").orderByKey().equalTo(uid);
                            query2.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        String status = snapshot.child("confirmationStatus").getValue(String.class);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

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

     */
}