package net.androidbootcamp.campmoab.Bookings;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnRangeSelectedListener;

import net.androidbootcamp.campmoab.BaseActivity;
import net.androidbootcamp.campmoab.Bookings.Adapters.GuestSpinnerAdapter;
import net.androidbootcamp.campmoab.Classes.BookingClass;
import net.androidbootcamp.campmoab.Classes.CustomSpinnerClass;
import net.androidbootcamp.campmoab.Classes.DateClass;
import net.androidbootcamp.campmoab.Classes.FirebaseHelperClass;
import net.androidbootcamp.campmoab.Classes.PopupCalendarClass;
import net.androidbootcamp.campmoab.R;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditReservation extends BaseActivity {
    private TextView checkin, checkout, txtSelectedGuests, txtName, txtEmail, txtResID, txtDisclaimer, txtNotes;
    private EditText editNotes;
    private ImageView modifyNotes, saveNotes;
    private CustomSpinnerClass spinnerGuests;
    private GuestSpinnerAdapter adapter;
    private Button btnUpdate;
    private LinearLayout idLayout;
    private String arrival, departure, notes, status, resID, name, email, dateBooked, UID;
    private static final String RESERVATIONS = "Reservations";
    private static final String USERS = "Users";
    private FirebaseHelperClass firebaseHelper;
    private DatabaseReference ref;
    private FirebaseUser user;
    private ArrayList<Long> groupQty;
    private ArrayList<Long> quantities;
    private LinearLayout idBodyLayout;
    private boolean isAdmin = false;
    private List<BookingClass> disableDates;
    private MaterialCalendarView calendarView;
    private PopupCalendarClass popupCalendarClass;
    private DateClass dateClass;
    private DatePickerDialog.OnDateSetListener dateSetListener;
    private CalendarDay checkinDate, checkoutDate;
    private boolean isPageLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_reservations_edit, findViewById(R.id.content_frame));
        toolbar.setTitle("Edit Reservation");

        checkin = (TextView) findViewById(R.id.editCheckinDate);
        checkout = (TextView) findViewById(R.id.editCheckoutDate);
        calendarView = (MaterialCalendarView) findViewById(R.id.calendarView);
        txtNotes = (TextView) findViewById(R.id.txtNotes);
        editNotes = (EditText) findViewById(R.id.editNotes);
        modifyNotes = (ImageView) findViewById(R.id.modifyNotes);
        saveNotes = (ImageView) findViewById(R.id.saveNotes);
        txtSelectedGuests = (TextView) findViewById(R.id.txtSelectedGuests);
        spinnerGuests = findViewById(R.id.spinnerGuests);
        btnUpdate = (Button) findViewById(R.id.updateBtn);
        idBodyLayout = (LinearLayout) findViewById(R.id.idBodyLayout);
        txtName = (TextView) findViewById(R.id.name);
        txtEmail = (TextView) findViewById(R.id.email);
        txtResID = (TextView) findViewById(R.id.resID);
        idLayout = (LinearLayout) findViewById(R.id.userInfoLayout);
        txtDisclaimer = (TextView) findViewById(R.id.txtDisclaimer);
        View guestLayout = findViewById(R.id.guests);

        dateClass = new DateClass();

        firebaseHelper = new FirebaseHelperClass();
        ref = firebaseHelper.getRef();
        user = firebaseHelper.getCurrentUser();

        //get the snapshot of the reservation from the intent
        Bundle extras = getIntent().getExtras();
        resID = extras.getString("resID");
        isAdmin = extras.getBoolean("isAdmin");

        if (isAdmin) {
            UID = extras.getString("UID");
        } else {
            UID = user.getUid();
        }

        Log.d("EditReservation", "UID: " + UID);
        Log.d("EditReservation", "resID: " + resID);
        Log.d("EditReservation", "isAdmin: " + isAdmin);

        txtResID.setText(resID);

        final Calendar nextYear = Calendar.getInstance();
        nextYear.add(Calendar.YEAR, 5);

        // Set the date range in MaterialCalendarView
        calendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_RANGE);
        calendarView.state().edit()
                .setMinimumDate(CalendarDay.today())
                .setMaximumDate(nextYear).commit();

        List<String> ageGroupTitles = Arrays.asList("Adults", "Teens/Kids", "Toddlers", "Service Animal");
        List<String> ageGroups = Arrays.asList("18+", "Ages 6-17", "Under 6", "");
        adapter = new GuestSpinnerAdapter(this, ageGroups, ageGroupTitles, spinnerGuests);
        spinnerGuests.setAdapter(adapter);

        guestLayout.post(() -> {
            int parentWidth = guestLayout.getWidth();
            spinnerGuests.setDropDownWidth(parentWidth);  // Set the rounded_dropdown_background width to the parent width)
        });

        // Set the listener to btnUpdate the TextView based on total guests
        adapter.setOnGuestCountChangeListener(totalGuests -> {
            txtSelectedGuests.setText(totalGuests + (totalGuests == 1 ? " Guest" : " Guests"));
        });

        updateViewIfAdmin();

        spinnerGuests.setSpinnerEventsListener(new CustomSpinnerClass.OnSpinnerEventsListener() {
            @Override
            public void onSpinnerOpened() {
                Log.d("EditReservation", "Dropdown opened");
                // Handle the rounded_dropdown_background opening event here
            }

            @Override
            public void onSpinnerClosed() {
                Log.d("EditReservation", "Dropdown closed");
                // Handle the rounded_dropdown_background closing event here
                updateQuantities();
            }
        });

        // Method to change textview on range selection
        calendarView.setOnRangeSelectedListener(new OnRangeSelectedListener() {
            @Override
            public void onRangeSelected(@NonNull MaterialCalendarView widget, @NonNull List<CalendarDay> dates) {
                if (isPageLoading) {

                } else {
                    // Get the selected start and end dates
                    checkinDate = dates.get(0);
                    checkoutDate = dates.get(dates.size() - 1);

                    Log.d("CalendarActivity", "Checkin Selected: " + checkinDate);
                    Log.d("CalendarActivity", "Checkout Selected: " + checkoutDate);

                    LocalDate start = LocalDate.of(checkinDate.getYear(), checkinDate.getMonth() + 1, checkinDate.getDay());
                    LocalDate end = LocalDate.of(checkoutDate.getYear(), checkoutDate.getMonth() + 1, checkoutDate.getDay());

                    if (dateClass.isDateRangeDisabled(start, end)) {
                        Toast.makeText(EditReservation.this,
                                "Selection not authorized, select new date range",
                                Toast.LENGTH_SHORT).show();
                        calendarView.clearSelection();
                        checkin.setText("");
                        checkout.setText("");
                    }
                    else {
                        // Validate selection, btnUpdate the TextViews
                        checkin.setText(dateClass.formatDate(checkinDate));
                        checkout.setText(dateClass.formatDate(checkoutDate));

                        Log.d("CalendarActivity", "Checkin Selected: " + checkin.getText().toString());
                        Log.d("CalendarActivity", "Checkout Selected: " + checkout.getText().toString());

                        // Validate the selected dates
                        try {
                            dateClass.validateDates(checkin.getText().toString(), checkout.getText().toString());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        // Method to change textview date on single date selection
        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget,
                                       @NonNull CalendarDay date, boolean selected) {
                if (selected) {
                    if (dateClass.isSingleDateDisabled(date)) {
                        widget.setDateSelected(date, false);
                    } else {
                        checkin.setText(dateClass.formatDate(widget.getSelectedDate()));
                        calendarView.setSelectedDate(widget.getSelectedDate());
                        checkinDate = widget.getSelectedDate();
                        checkout.setText("");
                    }
                }
            }
        });

        checkin.setOnClickListener(v -> datePickerDialogBox(checkin, true));
        checkout.setOnClickListener(v -> datePickerDialogBox(checkout, false));

        // Handle the click on the pencil icon to enable editing
        modifyNotes.setOnClickListener(v -> {
            // Set the text from TextView to EditText and enable editing
            editNotes.setText(txtNotes.getText().toString());
            txtNotes.setVisibility(View.GONE);  // Hide TextView
            editNotes.setVisibility(View.VISIBLE);  // Show EditText
            editNotes.requestFocus();  // Focus on EditText for typing

            modifyNotes.setVisibility(View.GONE);  // Hide pencil icon
            saveNotes.setVisibility(View.VISIBLE);  // Show checkmark icon
        });

        // Handle the click on the checkmark icon to save the changes
        saveNotes.setOnClickListener(v -> {
            // Set the text from EditText to TextView and disable editing
            txtNotes.setText(editNotes.getText().toString());
            txtNotes.setVisibility(View.VISIBLE);  // Show TextView
            editNotes.setVisibility(View.GONE);  // Hide EditText

            saveNotes.setVisibility(View.GONE);  // Hide checkmark icon
            modifyNotes.setVisibility(View.VISIBLE);  // Show pencil icon
        });

        //Update the database with the new information
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.d("EditReservation", "Uid when updating: " + UID);
                if (UID == null) {
                    Toast.makeText(EditReservation.this, "User not authenticated.", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    // Update the reservation
                    updateReservation();

                    // Return to the ViewReservations activity
                    startActivity(new Intent(EditReservation.this, ViewReservations.class));
                }
            }
        });
    }

    // Method to btnUpdate views based on user access level
    private void updateViewIfAdmin() {
        if (isAdmin) {
            idLayout.setVisibility(View.VISIBLE);
            txtDisclaimer.setVisibility(View.GONE);
        } else {
            idLayout.setVisibility(View.GONE);
            txtDisclaimer.setVisibility(View.VISIBLE);
        }
    }

    private void disableDates(List<BookingClass> disableDates) throws ParseException {
        dateClass.disableDates(disableDates, calendarView);
    }

    // Disable specific dates
    private void disableDefaultDates() {
        dateClass.disableSpecificDates(calendarView);
    }

    // Highlight initial dates when page is loaded
    private void initializeCalendar() {
        if (isAdmin) {
            loadUserInfo();
        }

        queryDBForReservation();
        //queryDatesInDB();
    }


    private DatePickerDialog.OnDateSetListener setDateListener(TextView spinner, boolean isCheckIn) {
        dateSetListener =
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        CalendarDay selectedDate = dateClass.validateFirstDate(year, month, day);
                        spinner.setText(dateClass.formatDate(selectedDate));

                        if (isCheckIn) {
                            CalendarDay currentCheckoutDate = dateClass.validateSecondDate(checkout.getText().toString());
                            if (!checkout.getText().toString().isEmpty()) {
                                calendarView.clearSelection();
                                calendarView.selectRange(selectedDate, currentCheckoutDate);
                            } else {
                                calendarView.setSelectedDate(selectedDate);
                            }
                        } else {
                            CalendarDay currentCheckinDate = dateClass.validateSecondDate(checkin.getText().toString());
                            if (!checkin.getText().toString().isEmpty()) {
                                calendarView.clearSelection();
                                calendarView.selectRange(currentCheckinDate, selectedDate);
                            } else {
                                calendarView.setSelectedDate(selectedDate);
                            }
                        }

                        calendarView.invalidateDecorators();
                    }
                };
        return dateSetListener;
    }

    // DatePickerDialogBox implementation
    private void datePickerDialogBox(TextView textfield, boolean isCheckIn) {
        String[] dateParts;

        // Check if the text field has the placeholder or is empty
        String dateText = textfield.getText().toString();
        if (dateText.isEmpty() || dateText.equals("MM/DD/YYYY")) {
            // Use today's date if the field is empty or has placeholder text
            dateParts = dateClass.formatDate(CalendarDay.today()).split("-");
        } else {
            // Split the existing date text
            dateParts = dateText.split("-");
        }

        // Make sure the dateParts has the expected length
        if (dateParts.length == 3) {
            try {
                int month = Integer.parseInt(dateParts[0]);
                int day = Integer.parseInt(dateParts[1]);
                int year = Integer.parseInt(dateParts[2]);

                DatePickerDialog dpd = new DatePickerDialog(
                        EditReservation.this,
                        android.R.style.Theme_Holo_Dialog_MinWidth,
                        //dateSetListener,
                        setDateListener(textfield, isCheckIn),
                        year,
                        month - 1,
                        day
                );
                dpd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dpd.show();
            } catch (NumberFormatException e) {
                e.printStackTrace();
                // Handle the error if parsing fails
            }
        } else {
            // Handle case where dateParts doesn't have 3 elements
            // Maybe show an error message or set default date
        }
    }

    private void queryDatesInDB() {
        disableDates = new ArrayList<>();

        //Firebase query arrival and departure dates
        Query dateQuery = ref.child(RESERVATIONS);
        dateQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    //Loop 1 to go through all the child nodes of users
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        for (DataSnapshot ds2 : ds.getChildren()) {
                            BookingClass bookings = ds2.getValue(BookingClass.class);
                            assert bookings != null;
                            arrival = bookings.getArrivalDate();
                            departure = bookings.getDepartureDate();
                            //notes = bookings.getNotes();
                            status = bookings.getStatus();

                            //Log.d("CalendarActivity", "ArrivalDateConfirmed: " + arrival);
                            //Log.d("CalendarActivity", "DepartureDateConfirmed: " + departureDate);

                            //call method ValidateDates2 to fix error in code that causes a parsing error
                            arrival = dateClass.formatDate(CalendarDay.from(Integer.parseInt(arrival.split("-")[2]),
                                    Integer.parseInt(arrival.split("-")[0]) - 1,
                                    Integer.parseInt(arrival.split("-")[1])));
                            departure = dateClass.formatDate(CalendarDay.from(Integer.parseInt(departure.split("-")[2]),
                                    Integer.parseInt(departure.split("-")[0]) - 1,
                                    Integer.parseInt(departure.split("-")[1])));

                            // Add the dates to the list of disabled dates if they are not the current reservation
                            if (!arrival.equals(checkin.getText().toString()) && !departure.equals(checkout.getText().toString())) {
                                disableDates.add(new BookingClass(arrival, departure));
                            } else {
                                continue;
                            }
                        }
                    }

                    try {
                        disableDates(disableDates);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void queryDBForReservation() {
        //Log.d("EditReservation", "QueryDatabaseForReservation: " + resID);
        //Log.d("EditReservation", "QueryDatabaseForReservation: " + UID);

        Query reservationQuery = ref.child(RESERVATIONS).child(UID).child(resID);
        reservationQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    BookingClass booking = snapshot.getValue(BookingClass.class);
                    if (booking != null) {
                        arrival = booking.getArrivalDate();
                        departure = booking.getDepartureDate();
                        quantities = new ArrayList<>(booking.getGroupQty());  // Hold original values
                        groupQty = new ArrayList<>(quantities);  // Hold updated values
                        notes = booking.getNotes();
                        status = booking.getStatus();
                        dateBooked = booking.getDateBooked();
                        resID = booking.getReservationID();

                        //Log.d("EditReservation", "ArrivalDate: " + arrivalDate);
                        //Log.d("EditReservation", "DepartureDate: " + departureDate);
                        //Log.d("EditReservation", "GroupQty: " + txtSelectedGuests);

                        //call method ValidateDates2 to fix error in code that causes a parsing error
                        arrival = dateClass.formatDate(CalendarDay.from(Integer.parseInt(arrival.split("-")[2]),
                                Integer.parseInt(arrival.split("-")[0]) - 1,
                                Integer.parseInt(arrival.split("-")[1])));
                        departure = dateClass.formatDate(CalendarDay.from(Integer.parseInt(departure.split("-")[2]),
                                Integer.parseInt(departure.split("-")[0]) - 1,
                                Integer.parseInt(departure.split("-")[1])));

                        //Log.d("EditReservation", "ArrivalDate2: " + arrivalDate);
                        //Log.d("EditReservation", "DepartureDate2: " + departureDate);

                        // Parse and highlight the dates on the calendar
                        checkinDate = CalendarDay.from(
                                Integer.parseInt(arrival.split("-")[2]),  // Year
                                Integer.parseInt(arrival.split("-")[0]) - 1,  // Month (zero-indexed)
                                Integer.parseInt(arrival.split("-")[1])  // Day
                        );

                        checkoutDate = CalendarDay.from(
                                Integer.parseInt(departure.split("-")[2]),  // Year
                                Integer.parseInt(departure.split("-")[0]) - 1,  // Month (zero-indexed)
                                Integer.parseInt(departure.split("-")[1])  // Day
                        );

                        // Set the textfields with the arrival and departure dates
                        checkin.setText(arrival);
                        checkout.setText(departure);

                        if (notes != null && !notes.isEmpty()) {
                            txtNotes.setText(notes);
                        }

                        txtResID.setText(resID);
                        //txtStatus.setText(status);

                        // Set groupQty to the adapter
                        adapter.setGroupQty(groupQty);  // Pass the groupQty to the adapter

                        // Update total guests TextView
                        txtSelectedGuests.setText(adapter.getTotalGuests() == 1 ? "1 Guest" : adapter.getTotalGuests() + " Guests");

                        // Highlight the selected range on the calendar
                        calendarView.selectRange(checkinDate, checkoutDate);

                        // Move the calendar to the month of the arrival date
                        calendarView.setCurrentDate(checkinDate);

                        // Query the database for all booked dates
                        queryDatesInDB();
                    }

                } else {
                    Log.d("EditReservation", "No data found for reservation query");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadUserInfo() {
        Query queryUser = ref.child(USERS).child(UID);
        queryUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Directly retrieve the values since they should be under the UID node
                    String firstName = snapshot.child("firstName").getValue(String.class);
                    String lastName = snapshot.child("lastName").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);

                    if (firstName != null && lastName != null && email != null) {
                        name = firstName + " " + lastName;
                        txtName.setText(name);
                        txtEmail.setText(email);
                        Log.d("ViewReservations", "User Info: Name: " + name + ", Email: " + email);
                }
            }
            else {
                    Log.d("ViewReservations", "No data found for user query");
            }
        }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void updateReservation() {
        DateClass dateClass = new DateClass();
        user = firebaseHelper.getCurrentUser();
        groupQty = adapter.getGroupQty();
        resID = txtResID.getText().toString();

        String mArrivalDate = checkin.getText().toString();
        String mDepartureDate = checkout.getText().toString();
        String mNotes = txtNotes.getText().toString();
        String dateEdited = dateClass.getCurrentFormattedDateTime();

        // Create a map for storing updates
        Map<String, Object> updates = new HashMap<>();

        // If values have changed, add them to the update map
        if (!arrival.equals(mArrivalDate)) {
            updates.put("arrivalDate", mArrivalDate);
        }

        if (!departure.equals(mDepartureDate)) {
            updates.put("departureDate", mDepartureDate);
        }

        if (!notes.equals(mNotes)) {
            updates.put("notes", mNotes);
        }

        // Check and update group quantities
        for (int i = 0; i < 4; i++) {
            if (!groupQty.get(i).equals(quantities.get(i))) {
                updates.put("groupQty", groupQty);
            }
        }

        // If any field was updated, also add the dateEdited and editedBy fields
        if (!updates.isEmpty()) {
            updates.put("dateEdited", dateEdited);

            if (isAdmin) {
                updates.put("editedBy", user.getUid());
            } else {
                updates.put("editedBy", UID);
                updates.put("status", "Pending");  // Set status to Pending if the user edits the reservation
            }

            // Perform batch update in Firebase
            ref.child(RESERVATIONS).child(UID).child(resID).updateChildren(updates)
                    .addOnSuccessListener(aVoid -> Log.d("UpdateReservation", "Reservation updated successfully"))
                    .addOnFailureListener(e -> Log.d("UpdateReservation", "Failed to update reservation: " + e.getMessage()));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        isPageLoading = true; // Set loading state for page load
        // Any additional logic for page load
        disableDefaultDates();

        // Delay setting the default dates to ensure the calendar is fully ready
        new Handler().postDelayed(() -> {
            initializeCalendar(); // Initialize calendar after delay
            isPageLoading = false; // Set loading state to false after initialization
            Log.d("CalendarActivity", "Page Loading completed.");
        }, 100);

        Log.d("CalendarActivity", "OnResume Page Loading: " + isPageLoading);

    }

    private void updateQuantities() {
        spinnerGuests.postDelayed(() -> {
            // Retrieve groupQty when the rounded_dropdown_background closes
            groupQty = adapter.getGroupQty();
        }, 200);
    }
}