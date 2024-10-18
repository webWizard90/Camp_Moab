package net.androidbootcamp.campmoab.Reservations;

import androidx.annotation.NonNull;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import net.androidbootcamp.campmoab.BaseActivities.BaseActivity;
import net.androidbootcamp.campmoab.BaseActivities.BaseMainActivity;
import net.androidbootcamp.campmoab.Classes.ReservationClass;
import net.androidbootcamp.campmoab.Classes.DateClass;
import net.androidbootcamp.campmoab.Classes.FirebaseHelperClass;
import net.androidbootcamp.campmoab.R;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BookReservation extends BaseActivity {
    private EditText additionalInfo;
    private TextView reservation;
    private Button confirmRes;
    private String arrivalDate;
    private String departureDate;
    private String resID, userId, UID;
    private LinearLayout linearLayout;
    private LocalDate localArrivalDate, localDepartureDate;
    private static final String RESERVATIONS = "Reservations";
    //private static final String USERS = "Users";
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
    private FirebaseHelperClass firebaseHelper;
    private DatabaseReference ref;
    private FirebaseUser user;
    private DateClass dateClass;
    private boolean isAdminCheck = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_reservation_booking_confirmation, findViewById(R.id.content_frame));
        toolbar.setTitle("Confirm Reservation");

        //account = (ImageView) findViewById(R.id.acctImage);
        reservation = (TextView) findViewById(R.id.resDate);
        confirmRes = (Button) findViewById(R.id.btnConfirmRes);
        linearLayout = (LinearLayout) findViewById(R.id.guestLinearList);
        additionalInfo = (EditText) findViewById(R.id.txtBox);

        firebaseHelper = new FirebaseHelperClass();
        ref = firebaseHelper.getRef();
        user = firebaseHelper.getCurrentUser();
        UID = user.getUid(); // get the UID of the currently logged in user
        //Log.d("BookReservation", "User UID: " + UID);

        SharedPreferences sharedPreferences = getSharedPreferences("TempBooking", MODE_PRIVATE);
        arrivalDate = sharedPreferences.getString("arrivalDate", String.valueOf(0));
        departureDate = sharedPreferences.getString("departureDate", String.valueOf(0));

        ArrayList<Long> groupQty = new ArrayList<>();

        // Loop through shared preferences to get groupQty for each of the 4 groups
        for (int i = 0; i < 4; i++) {
            groupQty.add(sharedPreferences.getLong("ageGroup_" + i, 0));
            //Log.d("BookReservation", "GroupQty" + i + " :" + groupQty.get(i));
        }

        Log.d("BookReservation", "ArrivalDate: " + arrivalDate);
        Log.d("BookReservation","DepartureDate: " + departureDate);

        //set reservation text with formatted date
        String stringDate = "Arrival: " + arrivalDate + "\nDeparture: " + departureDate;
        reservation.setText(stringDate);

        dateClass = new DateClass();

        //dateClass.fixInvalidMonthForNewYear(arrivalDate);
        //dateClass.fixInvalidMonthForNewYear(departureDate);

        //Parse and format date from sharedprefs
        localArrivalDate = LocalDate.parse(arrivalDate, formatter);
        Log.d("BookReservation", "Local Arrival Date: " + localArrivalDate);

        localDepartureDate = LocalDate.parse(departureDate, formatter);
        Log.d("BookReservation","Local Departure Date: " + localDepartureDate);

        List<String> ageGroups = Arrays.asList("Adults: ", "Children: ", "Infants: ", "Service Animals: ");
        //Loop through groupQty and add to linear layout with age group and qty of guests > 0
        for (int i = 0; i < groupQty.size(); i++) {
            if (groupQty.get(i) > 0) {
                TextView textView = new TextView(this);
                textView.setText(ageGroups.get(i) + groupQty.get(i));
                textView.setTextSize(20);
                textView.setTextColor(getResources().getColor(R.color.Black));
                linearLayout.setGravity(Gravity.CENTER);
                linearLayout.addView(textView);
            }
        }

        confirmRes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String status = "Pending";
                resID = ref.push().getKey(); //Value of your key

                String dateBooked = dateClass.getCurrentFormattedDateTime();

                //send email to user email and camp moab email
                //sendReservationBookingEmail(userId, arrivalDate, departureDate, guestList, additionalInfo.getText().toString(), status);

                //Create reservationClass object with new formatted date
                ReservationClass reservationClass = new
                        ReservationClass(arrivalDate, departureDate, groupQty, additionalInfo.getText().toString(), status, dateBooked);
                reservationClass.toMap();

                ref.child(RESERVATIONS).child(UID).child(resID).setValue(reservationClass).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //Toast.makeText(BookReservation.this, "Reservation Confirmed", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(BookReservation.this, ViewReservations.class));
                        Log.d("BookReservation", "Reservation Confirmed for UID: " + UID + " ResID: " + resID);
                    }
                });
            }
        });
    }

    /*private void sendReservationBookingEmail(String userId, String arrivalDate, String departureDate, ArrayList<String> guestList, String additionalInfo, String status){
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child(USERS).child(userId).addValueEventListener(new ValueEventListener() {
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
                        "Guests: " + guestQty + "\n" +
                        "Additional Information: " + additionalInfo + "\n" +
                        "Status: " + status + "\n\n" +
                        "Please note that your reservation is not confirmed until you receive a confirmation email from us. You can also log in to your account to view your reservation.\n\n" +
                        "Thank you for choosing Camp Moab!\n\n" +
                        "Sincerely,\n" +
                        "The Camp Moab Team");
                try {
                    startActivity(Intent.createChooser(sendEmail, "Choose an Email client :"));
                } catch (Exception e) {
                    Toast.makeText(BookReservation.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("BookReservation","Email Error: " + e);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }*/


    /*private void confirmRes() {
        ref = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();

        String resID = ref.push().getKey(); //Value of your key
        String userId = user.getUid();

        ref.child("Users").child(userId).child("ViewReservations").child(resID).child("Guest_Names");
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> guestList = new ArrayList<>();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String name = ds.getValue(String.class);
                    guestList.add(name);
                }

                Log.d("BookReservation", TAG + " Value is: " + guestList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.w("BookReservation", TAG + " Failed to read value. " + error.toException());
            }
        };

        ref.addListenerForSingleValueEvent(valueEventListener);
    }*/

    /*private void checkUserAccessLevel() {
        //UID = user.getUid(); // Get the UID of the currently logged-in user
        Log.d("ViewReservations", "User UID: " + UID);

        if (UID != null) {
            checkIfAdmin(UID, new FirebaseHelperClass.AdminCheckCallback() {
                @Override
                public void onAdminCheck(boolean isAdmin) {
                    Log.d("ViewReservations", "CheckUserAccess isAdmin: " + isAdmin);
                    if (isAdmin) {
                        Log.d("ViewReservations", "User is an admin");
                        isAdminCheck = true;
                    } else {
                        Log.d("ViewReservations", "User is not an admin");
                        isAdminCheck = false;
                    }
                }
            });
        } else {
            Log.e("ViewReservations", "No user is logged in.");
        }
    }

    // Method to check if the user is an admin
    private void checkIfAdmin(String uid, FirebaseHelperClass.AdminCheckCallback callback) {
        DatabaseReference adminRef = ref.child("Admins").child(uid);

        adminRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean isAdmin = snapshot.exists() && snapshot.child("isAdmin").getValue(Boolean.class);
                Log.d("ViewReservations", "Admin status: " + isAdmin);
                callback.onAdminCheck(isAdmin);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ViewReservations", "Error checking admin status: " + error.getMessage());
                callback.onAdminCheck(false); // Assume not admin if an error occurs
            }
        });
    }
     */
}
