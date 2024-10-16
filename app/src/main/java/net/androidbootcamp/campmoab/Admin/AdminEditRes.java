package net.androidbootcamp.campmoab.Admin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import net.androidbootcamp.campmoab.BaseActivity;
import net.androidbootcamp.campmoab.Bookings.Adapters.GuestSpinnerAdapter;
import net.androidbootcamp.campmoab.Classes.BookingClass;
import net.androidbootcamp.campmoab.Bookings.Utils.ConfirmedBookingDecorator;
import net.androidbootcamp.campmoab.Bookings.Utils.PendingBookingDecorator;
import net.androidbootcamp.campmoab.Classes.CustomSpinnerClass;
import net.androidbootcamp.campmoab.R;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AdminEditRes extends BaseActivity {
    private TextView userArrivalDate, userDepartureDate, cancel, name, email, resID, status, txtSelectedGuests;
    private EditText additionalInfo;
    private CustomSpinnerClass spinnerGuests;
    private Button update;
    private LinearLayout layout;
    private String uid, dateBooked;
    private String arrival, departure, info, confirmationStatus, resId, userEmail, id, firstName, lastName;
    private static final String RESERVATIONS = "Reservations";
    private static final String USERS = "Users";
    private DatabaseReference ref;
    private FirebaseUser user;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
    private String arrivalDate, departureDate;
    private List<CalendarDay> disableConfirmedDates;
    private List<BookingClass> disableCD;
    private List<CalendarDay> disableDefaultDates;
    private List<CalendarDay> disablePendingDates;
    private List<BookingClass> disablePD;
    private ArrayList<CalendarDay> datesToDisable;
    private ArrayList<String> currentBooking;
    private ArrayList<Long> groupQty = new ArrayList<>();
    private ArrayList<Long> quantities;
    private GuestSpinnerAdapter adapter;

    MaterialCalendarView calendarView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.admin_res_edit, findViewById(R.id.content_frame));
        toolbar.setTitle("User Reservations");

        //TODO: Add admin view to Edit Reservation to use only one activity
        
        userArrivalDate = (TextView) findViewById(R.id.editCheckinDate);
        userDepartureDate = (TextView) findViewById(R.id.editCheckoutDate);
        txtSelectedGuests = (TextView) findViewById(R.id.txtSelectedGuests);
        additionalInfo = (EditText) findViewById(R.id.txtNotes);
        cancel = (TextView) findViewById(R.id.txtCancel);
        spinnerGuests = findViewById(R.id.spinnerGuests);
        update = (Button) findViewById(R.id.updateBtn);
        //layout = (LinearLayout) findViewById(R.id.guestLinearList);
        status = (TextView) findViewById(R.id.resStatus);
        name = (TextView) findViewById(R.id.txtName);
        email = (TextView) findViewById(R.id.email);
        resID = (TextView) findViewById(R.id.ResID);

        /*List<String> ageGroups = Arrays.asList("Age 13+", "Ages 2-12", "Under 2", "Service Animal");
        adapter = new GuestSpinnerAdapter(this, ageGroups);*/
        List<String> ageGroupTitles = Arrays.asList("Adults", "Teens/Kids", "Toddlers", "Service Animal");
        List<String> ageGroups = Arrays.asList("18+", "Ages 6-17", "Under 6", "");
        adapter = new GuestSpinnerAdapter(this, ageGroups, ageGroupTitles, spinnerGuests);
        spinnerGuests.setAdapter(adapter);

        //get the snapshot of the reservation from the intent
        Bundle extras = getIntent().getExtras();
        firstName = extras.getString("firstName");
        lastName = extras.getString("lastName");
        arrival = extras.getString("arrivalDate");
        departure = extras.getString("departureDate");
        info = extras.getString("additionalInfo");
        //key = extras.getString("key");
        // Loop through shared preferences to get groupQty for each of the 4 groups
        for (int i = 0; i < 4; i++) {
            groupQty.add(extras.getLong("groupQty" + i));
            Log.d("EditReservation", "GroupQty" + i + ":" + groupQty.get(i));
        }
        id = extras.getString("uid1");
        //Log.v("id", id);
        resId = extras.getString("uid2");
        //Log.v("resId", resId);
        userEmail = extras.getString("email");
        //Log.v("userId", userId);
        confirmationStatus = extras.getString("confirmationStatus");
        dateBooked = extras.getString("dateBooked");
        //Log.v("confirmationStatus", confirmationStatus);

        //user = FirebaseAuth.getInstance().getCurrentUser();
        //uid = user.getUid(); // get the UID of the currently logged in user

        //set the text of the TextViews
        userArrivalDate.setText(arrival);
        //userArrivalDate.setEnabled(false);
        userDepartureDate.setText(departure);
        additionalInfo.setText(info);
        status.setText(confirmationStatus);
        name.setText(firstName + " " + lastName);
        resID.setText(resId);
        email.setText(userEmail);

        // Set the quantities to the groupQty
        quantities = new ArrayList<>();
        quantities.clear();
        quantities.addAll(groupQty);

        //QueryForAuth();

        //create and initalize date Collection
        disableConfirmedDates = new ArrayList<>();
        disablePendingDates = new ArrayList<>();
        disableDefaultDates = new ArrayList<>();
        //guestNames = new ArrayList<String>();

        //Get the information from the database
        ref = FirebaseDatabase.getInstance().getReference();

        // Set the quantities to the adapter on start
        adapter.setGroupQty(quantities);
        txtSelectedGuests.setText(adapter.getTotalGuests() == 1 ? "1 Guest" : adapter.getTotalGuests() + " Guests");

        // Set the listener to update the TextView based on total guests
        adapter.setOnGuestCountChangeListener(totalGuests -> {
            txtSelectedGuests.setText(totalGuests + (totalGuests == 1 ? " Guest" : " Guests"));
        });

        //initialize quantities with 0's
        //quantities = new ArrayList<>();
        //quantities.set(0, 1); // Set the first group to 1, others remain 0

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

        userArrivalDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Method opens a date picker dialog box with the userArrivalDate
                // CreateMaterialCalanderView(userArrivalDate);
                CreateMaterialCalanderView(userArrivalDate, userDepartureDate);
            }
        });

        userDepartureDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Method opens a date picker dialog box with the userDepartureDate
                //CreateMaterialCalanderView(userDepartureDate);
                CreateMaterialCalanderView(userArrivalDate, userDepartureDate);
            }
        });

        //If the user wants to cancel the reservation
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String confirmationStatusHolder = confirmationStatus;
                //if confirmation status != confirmationStatusHolder, then change the confirmation status
                if (!status.getText().equals(confirmationStatusHolder)) {
                    //Query query = ref.child(RESERVATIONS).child(userEmail).child(resId);
                    Query query = ref.child(RESERVATIONS).child(id).child(resId);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            //update the database with the new confirmation status
                            ((DatabaseReference) query).child("confirmationStatus").setValue(confirmationStatusHolder);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }

                //Cancel editing the reservation and return to the reservations activity
                Intent intent = new Intent(AdminEditRes.this, AdminReservations.class);
                startActivity(intent);
            }
        });

        //Update the database with the new information
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //If the user wants to add guests, add the guests to the database
                /*if (addGuests.isChecked()) {
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
                    if (userArrivalDate.getText().toString().isEmpty() ||
                            userDepartureDate.getText().toString().isEmpty() || layout.getChildCount() > 0 &&
                            guest.getText().toString().isEmpty()) {
                        Toast.makeText(AdminEditRes.this,
                                "Please enter all guest names", Toast.LENGTH_SHORT).show();
                    } else {
                        //get the information from the edittext views
                        //String mArrivalDate = arrival;
                        String mArrivalDate = userArrivalDate.getText().toString();
                        String mDepartureDate = userDepartureDate.getText().toString();
                        String mAdditionalInfo = additionalInfo.getText().toString();

                        //Create bookingClass object with new formated date
                        BookingClass bookingClass = new
                                //BookingClass(mArrivalDate, mDepartureDate, guestNames, mAdditionalInfo, confirmationStatus, dateBooked);
                        bookingClass.toMap();

                        ref.child("ViewReservations").child(id).child(resId).setValue(bookingClass);
                        Toast.makeText(AdminEditRes.this,
                                "Reservation Updated", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AdminEditRes.this, AdminReservations.class);
                        startActivity(intent);
                    }

                } else {
                    //get the information from the edittext views if the user does not want to add guests
                    guestNames.clear();
                    String mArrivalDate = userArrivalDate.getText().toString();
                    String mDepartureDate = userDepartureDate.getText().toString();
                    String mAdditionalInfo = additionalInfo.getText().toString();
                    guestNames.add("");
                    Log.v("guestNames", guestNames.toString());

                    //Create bookingClass object with new formated date
                    BookingClass bookingClass = new
                            BookingClass(mArrivalDate, mDepartureDate, guestNames, mAdditionalInfo, confirmationStatus, dateBooked);
                    bookingClass.toMap();

                    ref.child("ViewReservations").child(id).child(resId).setValue(bookingClass);
                    Toast.makeText(AdminEditRes.this,
                            "Reservation Updated", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AdminEditRes.this, AdminReservations.class);
                    startActivity(intent);
                }*/

            }
        });

        status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Query query = ref.child("Reservations").child(id).child(resId);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            BookingClass bookingClass = snapshot.getValue(BookingClass.class);
                            //set the textview to the confirmation status and update database
                            if (bookingClass.getStatus().equals("Pending")) {
                                bookingClass.setStatus("Confirmed");
                                status.setText(bookingClass.getStatus());
                                ((DatabaseReference) query).child("confirmationStatus").setValue("Confirmed");
                            } else if (bookingClass.getStatus().equals("Confirmed")) {
                                bookingClass.setStatus("Cancelled");
                                status.setText(bookingClass.getStatus());
                                ((DatabaseReference) query).child("confirmationStatus").setValue("Cancelled");
                            } else if (bookingClass.getStatus().equals("Cancelled")) {
                                bookingClass.setStatus("Pending");
                                status.setText(bookingClass.getStatus());
                                ((DatabaseReference) query).child("confirmationStatus").setValue("Pending");
                            }
                            //status.setText(bookingClass.getStatus());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }

    //TODO: CREATE A MATERIALCALENDAR CLASS TO HANDLE THE CALENDAR POPUP
    //Use a method to create MaterialCalendarView calendarView range selector in a pop up window
    // and set the textfield to the date selected
    private void CreateMaterialCalanderView(TextView checkin, TextView checkout) {
        //create a pop up window
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.admin_reservations_calendar_popup, null);
        /*int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;
        boolean focusable = true; //lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
        popupWindow.showAtLocation(checkin, Gravity.CENTER, 0, 0);
        final Calendar nextYear = Calendar.getInstance();
        nextYear.add(Calendar.YEAR, 5);

        QueryDatabase(checkin.getText().toString(), checkout.getText().toString());
        //select date in popup window and set the textfield to the date selected
        calendarView = popupView.findViewById(R.id.calendarView);
        calendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_RANGE);
        calendarView.state().edit()
                .setMinimumDate(CalendarDay.today())
                .setMaximumDate(nextYear).commit();

        Log.v("checkin", checkin.getText().toString());
        LocalDate today = LocalDate.now();
        Log.v("today", today.toString());
        //parse checkin date to a local date
        LocalDate localCheckinDate = LocalDate.parse(checkin.getText().toString(), formatter);
        Log.v("localCheckinDate", localCheckinDate.toString());

        //set the calendarView selected dates with the dates from the textfields
        if (!checkin.getText().toString().isEmpty() && !checkout.getText().toString().isEmpty()) {
            if (localCheckinDate.isBefore(today)) {
                String[] checkinDate = today.toString().split("-");
                String[] checkoutDate = checkout.getText().toString().split("-");
                Log.v("checkinDate", checkinDate[0] + checkinDate[1] + checkinDate[2]);
                Log.v("checkoutDate", checkoutDate[0] + checkoutDate[1] + checkoutDate[2]);
                //set the first calendarview highlighted date to todays date
                CalendarDay firstDate = (CalendarDay.from(Integer.parseInt(checkinDate[2]),
                        Integer.parseInt(checkinDate[0]) - 1, Integer.parseInt(checkinDate[1])));
                CalendarDay lastDate = (CalendarDay.from(Integer.parseInt(checkoutDate[2]),
                        Integer.parseInt(checkoutDate[0]) - 1, Integer.parseInt(checkoutDate[1])));
                calendarView.selectRange(firstDate, lastDate);
            } else {
                //get the dates from the textfields
                String[] checkinDate = checkin.getText().toString().split("-");
                String[] checkoutDate = checkout.getText().toString().split("-");
                Log.v("checkinDate", checkinDate[0] + checkinDate[1] + checkinDate[2]);
                Log.v("checkoutDate", checkoutDate[0] + checkoutDate[1] + checkoutDate[2]);
                //set the dates in the calendarView
                CalendarDay firstDate = (CalendarDay.from(Integer.parseInt(checkinDate[2]),
                        Integer.parseInt(checkinDate[0]) - 1, Integer.parseInt(checkinDate[1])));
                CalendarDay lastDate = (CalendarDay.from(Integer.parseInt(checkoutDate[2]),
                        Integer.parseInt(checkoutDate[0]) - 1, Integer.parseInt(checkoutDate[1])));
                calendarView.selectRange(firstDate, lastDate);
            }
        }

        //TODO: UPDATE ALL CALENDER SELECTED DATES USING THE Date Class
        //set the textfield to the date selected
        calendarView.setOnRangeSelectedListener(new OnRangeSelectedListener() {
            @Override
            public void onRangeSelected(@NonNull MaterialCalendarView widget,
                                        @NonNull List<CalendarDay> dates) {
                //set the textfield to the date selected, month + 1 because months start at 0
                for (int i = 0; i < dates.size(); i++) {
                    //if (disableConfirmedDates.contains(dates.get(i)) && disablePendingDates.contains(dates.get(i)) &&
                            //disableDefaultDates.contains(dates.get(i))) {
                    /*if (!dates.get(i).isInRange(calendarView.getSelectedDate(),
                            calendarView.getSelectedDate())) {
                        Toast.makeText(AdminEditRes.this,
                                "Selection not authorized, select new date range",
                                Toast.LENGTH_SHORT).show();
                        calendarView.clearSelection();
                        //clear text fields
                        checkin.setText("");
                        checkout.setText("");
                        break
                    } else {;*/
                        /*LocalDate arrivalD = LocalDate.parse(arrival, formatter);
                        LocalDate departureD = LocalDate.parse(departure, formatter);
                        CalendarDay deptDate = CalendarDay.from(departureD.getYear(),
                                departureD.getMonthValue() - 1, departureD.getDayOfMonth());
                        if (today.isAfter(arrivalD) && today.isBefore(departureD)) {
                            checkin.setText(arrival);
                            calendarView.setSelectedDate(widget.getSelectedDate());
                        } else {
                            checkin.setText(ValidateDates(dates.get(0)));
                            checkout.setText(ValidateDates(dates.get(dates.size() - 1)));
                        }
                    //}
                }

                Button close = popupView.findViewById(R.id.btnConfirm);
                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                    }
                });
                //popupWindow.dismiss();
            }
        });

        Button cancel = popupView.findViewById(R.id.btnCancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        ////change textview date on single date selection\\\\
        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget,
                                       @NonNull CalendarDay date, boolean selected) {
                //if (selected) {
                //date.getMonth() + 1;
                //}
                LocalDate arrivalD = LocalDate.parse(arrival, formatter);
                LocalDate departureD = LocalDate.parse(departure, formatter);
                CalendarDay deptDate = CalendarDay.from(departureD.getYear(),
                        departureD.getMonthValue() - 1, departureD.getDayOfMonth());
                if (today.isAfter(arrivalD) && today.isBefore(departureD)) {
                    //automatically set checkin date to arrival date
                    checkin.setText(arrival);
                    //calendarView.setSelectedDate(widget.getSelectedDate());

                    //allow user to add dates to original departure date but not remove
                    if (widget.getSelectedDate().isAfter(deptDate) || widget.getSelectedDate().equals(deptDate)) {
                        //checkin.setText((arrival));
                        checkout.setText(ValidateDates(widget.getSelectedDate()));
                    } else {
                        //set calendarView selected date to departureD date
                        //calendarView.setSelectedDate(deptDate);
                        checkout.setText(departure);
                    }

                    if (widget.getSelectedDate() == null) {
                        //set checkout date to last selected date
                        //set the calendarView selected date with departureD date
                        calendarView.setSelectedDate(widget.getSelectedDate());
                        checkout.setText(departure);
                    } else {
                        //checkout.setText(ValidateDates(widget.getSelectedDate()));
                    }

                 } else {
                    checkin.setText(ValidateDates(widget.getSelectedDate()));
                    calendarView.setSelectedDate(widget.getSelectedDate());
                    checkout.setText("");
                }
            }
        });*/
    }

    //Method to disable dates in the calendar view
    private void QueryDatabase(String checkin, String checkout) {
        disableCD = new ArrayList<>();
        disablePD = new ArrayList<>();
        currentBooking = new ArrayList<>();
        //Log.i("userArrivalDateQuery", checkin);
        //Log.i("userDepartureDateQuery", checkout);
        //get reference of the db
        ref = FirebaseDatabase.getInstance().getReference();
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
                            String status = bookings.getStatus();
                            //if reservation dates == arrival and departure dates, then don't add them to the list
                            if (bookings.getArrivalDate().equals(arrival) && bookings.getDepartureDate().equals(departure)) {
                                Log.i("Current Dates Not to Be Disabled ", bookings.getArrivalDate() + " " + bookings.getDepartureDate());
                                continue;
                            } else {
                                if (status.equals("Confirmed")) {
                                    //Log.v("status", status);
                                    arrivalDate = bookings.getArrivalDate();
                                    departureDate = bookings.getDepartureDate();

                                    //Log.i("arrivalDateConfirmed", arrivalDate);
                                    //Log.i("departureDateConfirmed", departureDate);

                                    //call method ValidateDates2 to fix error in code that causes a parsing error
                                    arrivalDate = ValidateDates2(CalendarDay.from(Integer.parseInt(arrivalDate.split("-")[2]),
                                            Integer.parseInt(arrivalDate.split("-")[0]) - 1,
                                            Integer.parseInt(arrivalDate.split("-")[1])));
                                    departureDate = ValidateDates2(CalendarDay.from(Integer.parseInt(departureDate.split("-")[2]),
                                            Integer.parseInt(departureDate.split("-")[0]) - 1,
                                            Integer.parseInt(departureDate.split("-")[1])));

                                    // Log.i("arrivalDateConfirmed2", arrivalDate);
                                    //Log.i("departureDateConfirmed2", departureDate);

                                    disableCD.add(new BookingClass(arrivalDate, departureDate));
                                } else if (status.equals("Pending")) {
                                    //Log.v("status", status);
                                    arrivalDate = bookings.getArrivalDate();
                                    departureDate = bookings.getDepartureDate();

                                    //Log.i("arrivalDatePending", arrivalDate);
                                    //Log.i("departureDatePending", departureDate);

                                    arrivalDate = ValidateDates2(CalendarDay.from(Integer.parseInt(arrivalDate.split("-")[2]),
                                            Integer.parseInt(arrivalDate.split("-")[0]) - 1,
                                            Integer.parseInt(arrivalDate.split("-")[1])));
                                    departureDate = ValidateDates2(CalendarDay.from(Integer.parseInt(departureDate.split("-")[2]),
                                            Integer.parseInt(departureDate.split("-")[0]) - 1,
                                            Integer.parseInt(departureDate.split("-")[1])));

                                    disablePD.add(new BookingClass(arrivalDate, departureDate));
                                }
                            }
                        }
                    }
                    if (disableCD.size() > 0) {
                        DisableConfirmedDates(disableCD);
                        //Log.v("disableDatesPairs", "No dates to disable");
                    } else if (disablePD.size() > 0){
                        DisablePendingDates(disablePD);
                        //Log.v("disableDatesPairs", "Dates to disable");
                    } else {
                        //Log.v("disableDatesPairs", "No dates to disable");
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    ///Disable confirmed days in range\\\
    private void DisableConfirmedDates(List<BookingClass> disableCD) {
        //Log.v("disableDatesPairsCD", String.valueOf(disableCD.size()));
        //Call decorator class to disable dates in Array
        ConfirmedBookingDecorator confirmedBookingDecorator = new ConfirmedBookingDecorator(DisableDates(disableCD), "  N/A");
        calendarView.addDecorators(confirmedBookingDecorator);
        calendarView.invalidateDecorators();
    }

    ///Disable pending days in range\\\
    private void DisablePendingDates(List<BookingClass> disablePD) {
        //Log.v("disableDatesPairsPD", String.valueOf(disablePD.size()));
        //Call decorator class to disable dates in Array
        PendingBookingDecorator pendingBookingDecorator = new PendingBookingDecorator(DisableDates(disablePD), "Pending ");
        calendarView.addDecorators(pendingBookingDecorator);
        calendarView.invalidateDecorators();
    }

    ///Add disabled days in range\\\
    private ArrayList<CalendarDay> DisableDates(List<BookingClass> disableDates) {
        datesToDisable = new ArrayList<>();

        //Log.v("disabled Pairs size", String.valueOf(disableDates.size()));
        if (disableDates.size() != 0) {
            //for each disabled date set in array, parse dates
            for (int i = 0; i < disableDates.size(); i++) {
                //get arrival and departure dates from array
                String arrivalDate = disableDates.get(i).getArrivalDate();
                String departureDate = disableDates.get(i).getDepartureDate();
                LocalDate arrivalFormat = LocalDate.parse(arrivalDate, formatter);
                LocalDate departureFormat = LocalDate.parse(departureDate, formatter);
                LocalDate placeHolder1 = arrivalFormat;
                //Log.v("arrivalFormat", String.valueOf(arrivalFormat));
                //Log.v("departureFormat", String.valueOf(departureFormat));

                //Get number of days between arrival and departure dates
                int daysBetween = (int) ChronoUnit.DAYS.between(arrivalFormat, departureFormat);
                //Log.v("days between", String.valueOf(daysBetween));

                //get current date
                /**LocalDate currentDate = LocalDate.now();
                //if arrival date is before current date, set arrival date to current date
                if (arrivalFormat.isBefore(currentDate)) {
                    arrivalFormat = currentDate;
                }*/

                //Add arrival date to disabled dates
                datesToDisable.add(CalendarDay.from(arrivalFormat.getYear(),
                        arrivalFormat.minusMonths(1).getMonthValue(), arrivalFormat.getDayOfMonth()));

                //Add dates between arrival and departure to disabled dates
                for (int n = 0; n < daysBetween; n++) {
                    if (arrivalFormat.isBefore(departureFormat) && arrivalFormat.getMonthValue() != 01
                            || arrivalFormat.isBefore(departureFormat) && arrivalFormat.getMonthValue() == 01
                            && arrivalFormat.getYear() == departureFormat.getYear()) {
                        //Add arrival date to disabled dates
                        datesToDisable.add(CalendarDay.from(arrivalFormat.getYear(),
                                arrivalFormat.getMonthValue() - 1, arrivalFormat.getDayOfMonth()));

                        //Log.v("dates between", arrivalFormat.getYear() + "-" +
                        //arrivalFormat.getMonthValue() + "-" + arrivalFormat.getDayOfMonth());

                        arrivalFormat = arrivalFormat.plusDays(1);
                    }

                    if (arrivalFormat.isBefore(departureFormat) && arrivalFormat.getMonthValue() == 01 &&
                            placeHolder1.getYear() != departureFormat.getYear()) {
                        int newMonth = 00;
                        //Add arrival date to disabled dates
                        datesToDisable.add(CalendarDay.from(arrivalFormat.getYear(),
                                newMonth, arrivalFormat.getDayOfMonth()));
                        Log.v("dates after the year", CalendarDay.from(arrivalFormat.getYear(),
                                arrivalFormat.getMonthValue(), arrivalFormat.getDayOfMonth()).toString());

                        arrivalFormat = arrivalFormat.plusDays(1);
                    }
                }
                //Add departure date to disabled dates
                datesToDisable.add(CalendarDay.from(departureFormat.getYear(),
                        departureFormat.getMonthValue() - 1, departureFormat.getDayOfMonth()));

            }
        }
        return datesToDisable;
    }

    private void DisableDefaultDates() {
        //Variables for default disabled dates
        String AprilStart = "03-01-2023";
        String AprilEnd = "03-30-2023";
        String OctoberStart = "09-01-2023";
        String OctoberEnd = "09-31-2023";

        //Format dates
        LocalDate AprilFormatStart = LocalDate.parse(AprilStart, formatter);
        LocalDate AprilFormatEnd = LocalDate.parse(AprilEnd, formatter);
        LocalDate OctoberFormatStart = LocalDate.parse(OctoberStart, formatter);
        LocalDate OctoberFormatEnd = LocalDate.parse(OctoberEnd, formatter);

        //Get days in between dates
        int daysInBetweenApril = (int) ChronoUnit.DAYS.between(AprilFormatStart, AprilFormatEnd);
        int daysInBetweenOctober = (int) ChronoUnit.DAYS.between(OctoberFormatStart, OctoberFormatEnd);

        //Disable April
        disableDefaultDates.add(CalendarDay.from(2023, 3, 1));
        //Loop to disable April for the next 5 years
        for (int i = 0; i <= 4; i++) {
            //Disable April
            for (int n = 0; n <= daysInBetweenApril; n++) {
                LocalDate date = AprilFormatStart.plusYears(i).plusDays(n);
                // String addzerotoMonth = String.format("%02d", date.getMonthValue());
                //String addzerotoDay = String.format("%02d", date.getDayOfMonth());
                disableDefaultDates.add(CalendarDay.from(date.getYear(),
                        date.getMonthValue(), date.getDayOfMonth()));
                //disableDates.add(AprilFormatStart.plusYears(i).plusDays(n));
            }
        }
        disableDefaultDates.add(CalendarDay.from(2023, 3, 30));

        disableDefaultDates.add(CalendarDay.from(2023, 9, 1));
        //Loop to disable October for the next 5 years
        for (int i = 0; i <= 4; i++) {
            //Disable October
            for (int n = 0; n <= daysInBetweenOctober; n++) {
                LocalDate date = OctoberFormatStart.plusYears(i).plusDays(n);
                //String addzerotoMonth = String.format("%02d", date.getMonthValue());
                //String addzerotoDay = String.format("%02d", date.getDayOfMonth());
                disableDefaultDates.add(CalendarDay.from(date.getYear(),
                        date.getMonthValue(), date.getDayOfMonth()));
            }
        }
        disableDefaultDates.add(CalendarDay.from(2023, 9, 31));
        //Log.v("disabled dates", disableDefaultDates.toString());

        //Call decorator class to disable dates in Array
        ConfirmedBookingDecorator confirmedBookingDecorator = new ConfirmedBookingDecorator(disableDefaultDates, "");
        calendarView.addDecorators(confirmedBookingDecorator);
        calendarView.invalidateDecorators();
    }

    //Method to validate dates
    private String ValidateDates(CalendarDay date) {
        String[] dateParts = new String[3];

        //check to see if the month and day is < 10
        // and remove everything but the date
        if (date.toString().length() == 21) {
            dateParts = date.toString().substring(12, 20).split("-");
        }

        if (date.toString().length() == 22) {
            dateParts = date.toString().substring(12, 21).split("-");
        }

        if (date.toString().length() == 23) {
            dateParts = date.toString().substring(12, 22).split("-");
        }


        int year = Integer.parseInt(dateParts[0]);
        int month = Integer.parseInt(dateParts[1]);
        int day = Integer.parseInt(dateParts[2]);

        String newMonthFormat = "" + month;
        String newDayFormat = "" + day;

        if (month < 10) {
            month = month + 1;
            newMonthFormat = "0" + month ;
        }

        if (month == 10 || month == 11) {
            month = month + 1;
            newMonthFormat = "" + month;
        }

        if (month == 00) {
            month = 1;
            newMonthFormat = "0" + month;
        }

        if (day < 10) {
            newDayFormat = "0" + day;
            //day1 = Integer.parseInt(newDayFormat1);
        } else {
            newDayFormat = String.valueOf(day);
        }

        Log.i("newlyFormatedString", newMonthFormat + "-" + newDayFormat + "-" + year);

        return newMonthFormat + "-" + newDayFormat + "-" + year;
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

    private void QueryForAuth() {
        //query the db for users isAdmin value and if true, set the resStatusTxt and resStatus to visible
        Query query1 = ref.child(USERS).orderByKey().equalTo(uid);
        query1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String isAdmin = dataSnapshot.child("isAdmin").getValue(String.class);

                        if (isAdmin.equals("true") & isAdmin != null) {
                            Query query2 = ref.child(RESERVATIONS).orderByKey().equalTo(uid);
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

    private void updateQuantities() {
        spinnerGuests.postDelayed(() -> {
            // Retrieve quantities when the rounded_dropdown_background closes
            quantities = adapter.getGroupQty();
            Log.d("EditReservation", "Quantities after closing rounded_dropdown_background: " + quantities);
        }, 200);
    }
}