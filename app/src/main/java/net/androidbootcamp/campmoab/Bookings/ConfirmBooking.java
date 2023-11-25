package net.androidbootcamp.campmoab.Bookings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import net.androidbootcamp.campmoab.UserAccountAttributes.LoginActivity;
import net.androidbootcamp.campmoab.UserAccountAttributes.UserAccount;
import net.androidbootcamp.campmoab.UserAccountAttributes.UserClass;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class ConfirmBooking extends AppCompatActivity {
    private EditText addTxt;
    private TextView reservation;
    private ImageView home, account;
    private Button confirmRes;

    private String arrivalDate;
    private String departureDate;
    private String date1, date2;
    private String pushUID, userId;
    private int guestCount;

    private LinearLayout linearLayout;

    private LocalDate localArrivalDate, localDepartureDate;

    private ArrayList<String> guestList;

    //private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    //private SimpleDateFormat txtDateFormat = new SimpleDateFormat("MM-dd-yyyy");
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");

    private DatabaseReference ref;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_booking);

        //instantiate controls
        home = (ImageView) findViewById(R.id.homeImage);
        account = (ImageView) findViewById(R.id.acctImage);
        reservation = (TextView) findViewById(R.id.resDate);
        confirmRes = (Button) findViewById(R.id.btnConfirmRes);

        linearLayout = (LinearLayout) findViewById(R.id.guestLinearList);
        addTxt = (EditText) findViewById(R.id.txtBox);

        SharedPreferences sharedPreferences = getSharedPreferences("sharedPref", MODE_PRIVATE);
        arrivalDate = sharedPreferences.getString("arrivalDate", String.valueOf(0));
        departureDate = sharedPreferences.getString("departureDate", String.valueOf(0));

        Log.v("arrivalDate", arrivalDate);
        Log.v("departureDate", departureDate);

        SharedPreferences sharedPref =  PreferenceManager.getDefaultSharedPreferences(ConfirmBooking.this);
        guestCount = sharedPref.getInt("guestCount", 0);

        TextView guestNames[] = new TextView[guestCount];
        LinearLayout.LayoutParams layoutParams = new LinearLayout
                .LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);


        for(int i = 0; i < guestCount; i++)
        {
            guestNames[i] = new TextView(this);
            guestNames[i].setTextSize(20);
            guestNames[i].setLayoutParams(layoutParams);
            linearLayout.setGravity(Gravity.CENTER);
            linearLayout.addView(guestNames[i]);

        }

        //set reservation text with formatted date
        String stringDate = "Arrival: " + arrivalDate +
                "\nDeparture: " + departureDate;
        reservation.setText(stringDate);

        //Get guest names and add to list
        for(int i = 0; i < guestCount; i++)
        {
            guestNames[i].setText(sharedPref.getString(String.valueOf(i), String.valueOf(0)));
        }

        //Parse and format date from sharedprefs
        localArrivalDate = LocalDate.parse(arrivalDate, formatter);
        Log.i("mapToFirbase1", String.valueOf(localArrivalDate));

        localDepartureDate = LocalDate.parse(departureDate, formatter);
        Log.i("mapToFirbase2", String.valueOf(localDepartureDate));


        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ConfirmBooking.this, MainActivity.class));
            }
        });

        account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ConfirmBooking.this, UserAccount.class));
            }
        });

        confirmRes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String status = "Pending";
                ref = FirebaseDatabase.getInstance().getReference();
                user = FirebaseAuth.getInstance().getCurrentUser();

                pushUID = ref.push().getKey(); //Value of your key
                userId = user.getUid();
                guestList = new ArrayList<>();
                SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
                String dateBooked = dateFormat.format(System.currentTimeMillis());

                //Get guest names and add to list
                //if guestNames are empty, add " " to list
                if (guestCount == 0) {
                    guestList.add("");
                } else {
                    for (int i = 0; i < guestCount; i++) {
                        guestList.add(guestNames[i].getText().toString());
                    }
                }

                //send email to user email and camp moab email
                sendReservationBookingEmail(userId, arrivalDate, departureDate, guestList, addTxt.getText().toString(), status);

                //Create bookingClass object with new formated date
                BookingClass bookingClass = new
                        BookingClass(arrivalDate, departureDate, guestList, addTxt.getText().toString(), status, dateBooked);
                bookingClass.toMap();

                ref.child("Reservations").child(userId).child(pushUID).setValue(bookingClass).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //Toast.makeText(ConfirmBooking.this, "Reservation Confirmed", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(ConfirmBooking.this, Reservations.class));
                    }
                });

                /*if(guestList != null) {
                    //confirmRes();
                    startActivity(new Intent(ConfirmBooking.this, Reservations.class));
                }*/
            }
        });

    }

    private void sendReservationBookingEmail(String userId, String arrivalDate, String departureDate, ArrayList<String> guestList, String addTxt, String status){
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child("Users").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserClass userClass = snapshot.getValue(UserClass.class);
                String email = userClass.getEmail();
                String firstName = userClass.getFirstName();
                String lastName = userClass.getLastName();

                Intent sendEmail = new Intent(Intent.ACTION_SEND);
                sendEmail.putExtra(Intent.EXTRA_EMAIL, new String[]{String.valueOf(email), "campmoabteam@gamil.com"});
                sendEmail.putExtra(Intent.EXTRA_SUBJECT, "Camp Moab Account Created");
                sendEmail.putExtra(Intent.EXTRA_TEXT, "Hello " + firstName + " " + lastName + ",\n\n" +
                        "Your reservation was received and is currently being reviewed.\n\n" +
                        "If you have any questions, please contact us at campmoabteam@gmail.com." + "\n\n" +
                        "Please see the details below:\n\n" +
                        "Arrival Date: " + arrivalDate + "\n" +
                        "Departure Date: " + departureDate + "\n" +
                        "Guest Names: " + guestList + "\n" +
                        "Additional Information: " + addTxt + "\n" +
                        "Status: " + status + "\n\n" +
                        "Please note that your reservation is not confirmed until you receive a confirmation email from us. You can also log in to your account to view your reservation.\n\n" +
                        "Thank you for choosing Camp Moab!\n\n" +
                        "Sincerely,\n" +
                        "The Camp Moab Team");
                try {
                    startActivity(Intent.createChooser(sendEmail, "Choose an Email client :"));
                } catch (Exception e) {
                    Toast.makeText(ConfirmBooking.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.i("Error", e.toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    /*private void confirmRes() {
        ref = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();

        String pushUID = ref.push().getKey(); //Value of your key
        String userId = user.getUid();

        ref.child("Users").child(userId).child("Reservations").child(pushUID).child("Guest_Names");
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> guestList = new ArrayList<>();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String name = ds.getValue(String.class);
                    guestList.add(name);
                }

                Log.d(TAG, "Value is: " + guestList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        };

        ref.addListenerForSingleValueEvent(valueEventListener);
    }*/
}
