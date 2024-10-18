package net.androidbootcamp.campmoab.Reservations;

import androidx.annotation.NonNull;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnRangeSelectedListener;

import net.androidbootcamp.campmoab.BaseActivities.BaseActivity;
import net.androidbootcamp.campmoab.Classes.ReservationClass;
import net.androidbootcamp.campmoab.Reservations.Adapters.GuestSpinnerAdapter;
import net.androidbootcamp.campmoab.Classes.CustomSpinnerClass;
import net.androidbootcamp.campmoab.Classes.DateClass;
import net.androidbootcamp.campmoab.Classes.FirebaseHelperClass;
import net.androidbootcamp.campmoab.R;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class CalendarActivity extends BaseActivity {
    private TextView checkin, checkout, txtSelectedGuests;
    //private Spinner spinnerGuests;
    private Button btnNext;
    private static final String RESERVATIONS = "Reservations";
    private List<ReservationClass> disableCD, disablePD, disableDates;
    private MaterialCalendarView calendarView;
    private DatePickerDialog.OnDateSetListener dateSetListener;
    private String arrivalDate, departureDate;
    private CalendarDay checkinDate, checkoutDate;
    private DateClass dateClass;
    private FirebaseHelperClass firebaseHelperClass;
    private DatabaseReference ref;
    private boolean isPageLoading = false;
    private ArrayList<Long> quantities = new ArrayList<>();
    private CustomSpinnerClass spinnerGuests;
    private GuestSpinnerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_reservation_calendar, findViewById(R.id.content_frame));
        toolbar.setTitle("Book Your Stay");

        calendarView = (MaterialCalendarView) findViewById(R.id.calendarView);
        checkin = (TextView) findViewById(R.id.editCheckinDate);
        checkout = (TextView) findViewById(R.id.editCheckoutDate);
        txtSelectedGuests = (TextView) findViewById(R.id.txtSelectedGuests);
        spinnerGuests = findViewById(R.id.spinnerGuests);
        btnNext = (Button) findViewById(R.id.btnNext);
        View guestLayout = findViewById(R.id.guests);

        // Initialize Firebase
        firebaseHelperClass = new FirebaseHelperClass();
        ref = firebaseHelperClass.getRef();

        final Calendar nextYear = Calendar.getInstance();
        nextYear.add(Calendar.YEAR, 5);

        // Set the date range in MaterialCalendarView
        calendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_RANGE);
        calendarView.state().edit()
                .setMinimumDate(CalendarDay.today())
                .setMaximumDate(nextYear).commit();
        //calendarView.invalidateDecorators();
        //calendarView.setSelectedDate(CalendarDay.today());
        //calendarView.setCurrentDate(CalendarDay.today());

        dateClass = new DateClass();
        quantities = new ArrayList<>();

        List<String> ageGroupTitles = Arrays.asList("Adults", "Teens", "Kids", "Service Animal");
        List<String> ageGroups = Arrays.asList("18+", "Ages 12-17", "Under 12", "");
        adapter = new GuestSpinnerAdapter(this, ageGroups, ageGroupTitles, spinnerGuests);
        spinnerGuests.setAdapter(adapter);
        spinnerGuests.setDropDownVerticalOffset(65);

        guestLayout.post(() -> {
            int parentWidth = guestLayout.getWidth();
            spinnerGuests.setDropDownWidth(parentWidth);  // Set the rounded_dropdown_background width to the parent width)
        });

        // Set the listener to update the TextView based on total guests
        adapter.setOnGuestCountChangeListener(totalGuests -> {
            txtSelectedGuests.setText(totalGuests + (totalGuests == 1 ? " Guest" : " Guests"));
        });

        // Method to highlight selected dates on range selection and set check in/out text fields
        calendarView.setOnRangeSelectedListener(new OnRangeSelectedListener() {
            @Override
            public void onRangeSelected(@NonNull MaterialCalendarView widget,
                                        @NonNull List<CalendarDay> dates) {

                // Get the selected start and end dates
                checkinDate = dates.get(0);
                checkoutDate = dates.get(dates.size() - 1);

                //Log.d("CalendarActivity", "Checkin Selected: " + checkinDate);
                //Log.d("CalendarActivity", "Checkout Selected: " + checkoutDate);

                if (isPageLoading) {
                    Log.d("CalendarActivity", "Page is loading, setting dates");

                    // Validate selection, update the TextViews
                    //checkin.setText(dateClass.formatDate(checkinDate));
                    //checkout.setText(dateClass.formatDate(checkoutDate));

                    //Log.d("CalendarActivity", "Checkin Selected: " + checkin.getText().toString());
                    //Log.d("CalendarActivity", "Checkout Selected: " + checkout.getText().toString());

                } else {
                    Log.d("CalendarActivity", "Page is not loading, validating dates");
                    // Check if there are disabled dates in the selected range
                    LocalDate start = LocalDate.of(checkinDate.getYear(), checkinDate.getMonth() + 1, checkinDate.getDay());
                    LocalDate end = LocalDate.of(checkoutDate.getYear(), checkoutDate.getMonth() + 1, checkoutDate.getDay());

                    if (dateClass.isDateRangeDisabled(start, end)) {
                        Toast.makeText(CalendarActivity.this,
                                "Selection not authorized, select new date range",
                                Toast.LENGTH_SHORT).show();
                        calendarView.clearSelection();
                        checkin.setText("");
                        checkout.setText("");
                    }
                    else {
                        // Validate selection, update the TextViews
                        checkin.setText(dateClass.formatDate(checkinDate));
                        checkout.setText(dateClass.formatDate(checkoutDate));

                        //Log.d("CalendarActivity", "Checkin Selected: " + checkin.getText().toString());
                        //Log.d("CalendarActivity", "Checkout Selected: " + checkout.getText().toString());

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

        spinnerGuests.setSpinnerEventsListener(new CustomSpinnerClass.OnSpinnerEventsListener() {
            @Override
            public void onSpinnerOpened() {
                Log.d("CalendarActivity", "Dropdown opened");
                // Handle the rounded_dropdown_background opening event here
            }

            @Override
            public void onSpinnerClosed() {
                Log.d("CalendarActivity", "Dropdown closed");
                // Handle the rounded_dropdown_background closing event here
                updateQuantities();
            }
        });

        //On button click -- confirm res and moves to reservation page
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPref = getSharedPreferences(
                        "TempBooking", MODE_PRIVATE);
                sharedPref.edit().clear().apply();
                SharedPreferences.Editor editor = sharedPref.edit();

                //ToDo Add validation for dates before moving on to next page
                //Validate date range is selected
                if (checkin.getText().length() > 0 && checkout.getText().length() > 0) {
                    //Save dates to shared preferences
                    editor.putString("arrivalDate", checkin.getText().toString());
                    editor.putString("departureDate", checkout.getText().toString());

                    quantities = adapter.getGroupQty();
                    //Log.d("CalendarActivity", "Quantity Size: " + quantities.size());

                    for (int i = 0; i < quantities.size(); i++) {
                        editor.putLong("ageGroup_" + i, quantities.get(i));
                        //Log.d("CalendarActivity", "AgeGroup_" + i + ": " + quantities.get(i));
                    }
                    editor.commit();

                    //Log.d("CalendarActivity", "SharedPrefArrivalDate: " + sharedPref.getString("arrivalDate", ""));
                    //Log.d("CalendarActivity", "SharedPrefDepartureDate: " + sharedPref.getString("departureDate", ""));

                    //Move to next activity
                    startActivity(new Intent(CalendarActivity.this, BookReservation.class));
                } else {
                    Toast.makeText(CalendarActivity.this, "Select Date Range", Toast.LENGTH_SHORT).show();
                    //throw new IllegalArgumentException("Selected date must be non-null.");
                }
            }
        });
    }

    private void queryForDisabledDatabase() {
        disableDates = new ArrayList<>();

        //Firebase query arrival and departure dates
        Query dateQuery = ref.child(RESERVATIONS);
        dateQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Log.d("CalendarActivity", "DataSnapshot Exists");

                    for (DataSnapshot ds : snapshot.getChildren()) {

                        for (DataSnapshot ds2 : ds.getChildren()) {
                            ReservationClass bookings = ds2.getValue(ReservationClass.class);
                            assert bookings != null;
                            //String status = bookings.getStatus();
                            arrivalDate = bookings.getArrivalDate();
                            departureDate = bookings.getDepartureDate();
                            //Log.d("CalendarActivity", "ArrivalDateConfirmed: " + arrivalDate);
                            //Log.d("CalendarActivity", "DepartureDateConfirmed: " + departureDate);

                            // Use the validate methods for parsing the dates
                            /*CalendarDay arrivalCalendarDay = dateClass.validateSecondDate(arrivalDate);
                            CalendarDay departureCalendarDay = dateClass.validateSecondDate(departureDate);

                            //Log.d("CalendarActivity", "ArrivalDateConfirmed2: " + arrivalDate);
                            //Log.d("CalendarActivity", "DepartureDateConfirmed2: " + departureDate);

                            //disableDates.add(new ReservationClass(arrivalDate, departureDate));
                            disableDates.add(new ReservationClass(
                                    dateClass.formatDate(arrivalCalendarDay),
                                    dateClass.formatDate(departureCalendarDay)
                            ));*/
                            //call method ValidateDates2 to fix error in code that causes a parsing error
                            arrivalDate = dateClass.formatDate(CalendarDay.from(Integer.parseInt(arrivalDate.split("-")[2]),
                                    Integer.parseInt(arrivalDate.split("-")[0]) - 1,
                                    Integer.parseInt(arrivalDate.split("-")[1])));
                            departureDate = dateClass.formatDate(CalendarDay.from(Integer.parseInt(departureDate.split("-")[2]),
                                    Integer.parseInt(departureDate.split("-")[0]) - 1,
                                    Integer.parseInt(departureDate.split("-")[1])));

                            Log.d("CalendarActivity", "ArrivalDateConfirmed2: " + arrivalDate);
                            Log.d("CalendarActivity", "DepartureDateConfirmed2: " + departureDate);

                            disableDates.add(new ReservationClass(arrivalDate, departureDate));
                        }
                    }

                    try {
                        disableDates(disableDates);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                } else {
                    Log.d("CalendarActivity", "No dates to disable found");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // Disable dates that are confirmed
    /*private void disableConfirmedDates(List<ReservationClass> disableCD) throws ParseException {
       //dateClass.disableDates(disableCD, calendarView, "N/A");
        dateClass.disableDates(disableCD, calendarView);
    }

    // Disable dates that are pending
    private void disablePendingDates(List<ReservationClass> disablePD) throws ParseException {
        //dateClass.disableDates(disablePD, calendarView, "Pending");
        dateClass.disableDates(disablePD, calendarView);
    }*/

    private void disableDates(List<ReservationClass> disableDates) throws ParseException {
        dateClass.disableDates(disableDates, calendarView);
    }

    // Disable specific dates
    private void disableDefaultDates() {
        dateClass.disableSpecificDates(calendarView);
    }

    // Highlight initial dates when page is loaded
    private void initializeCalendar() {
        //isPageLoading = true;
        //queryForDisabledDatabase();

        dateClass.setSelectedDefaultDates(calendarView, checkin, checkout, isPageLoading);
        //Log.d("CalendarActivity", "Setting default dates: " + checkin.getText().toString() + " - " + checkout.getText().toString());
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

        // Check if the textfield has the placeholder or is empty
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
                        CalendarActivity.this,
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

    @Override
    protected void onResume() {
        super.onResume();
        isPageLoading = true; // Set loading state for page load
        // Any additional logic for page load
        disableDefaultDates();
        queryForDisabledDatabase(); // Query database for disabled dates

        // Delay setting the default dates to ensure the calendar is fully ready
        new Handler().postDelayed(() -> {
            //queryForDisabledDatabase(); // Query database for disabled dates
            initializeCalendar(); // Initialize calendar after delay
            isPageLoading = false; // Set loading state to false after initialization
            Log.d("CalendarActivity", "Page Loading completed.");
        }, 100);

        Log.d("CalendarActivity", "OnResume Page Loading: " + isPageLoading);

    }

    private void updateQuantities() {
        spinnerGuests.postDelayed(() -> {
            // Retrieve quantities when the rounded_dropdown_background closes
            quantities = adapter.getGroupQty();
            Log.d("CalendarActivity", "Quantities after closing rounded_dropdown_background: " + quantities);
        }, 200);
    }
}