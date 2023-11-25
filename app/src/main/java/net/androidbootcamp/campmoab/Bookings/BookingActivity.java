package net.androidbootcamp.campmoab.Bookings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import net.androidbootcamp.campmoab.MainActivity;
import net.androidbootcamp.campmoab.R;
import net.androidbootcamp.campmoab.UserAccountAttributes.UserAccount;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class BookingActivity extends AppCompatActivity {
    //variables called to be used throughout the BookingActivity class
    private TextView reservation;
    private ImageView home, account;
    private Button btnNext;

    private TextView checkin, checkout;

    private Date today;

    private static final String RESERVATIONS = "Reservations";

    private ArrayList<Integer> list;
    private static ArrayList<LocalDate> disableDates;
    private List<LocalDate> selectedDates;
    private ArrayList<String> stringDates;
    private ArrayList<String> datesInBetween = new ArrayList<>();
    private List<CalendarDay> disableConfirmedDates;
    private List<BookingClass> disableCD;
    private List<CalendarDay> disableDefaultDates;
    private List<CalendarDay> disablePendingDates;
    private List<BookingClass> disablePD;
    private ArrayList<CalendarDay> datesToDisable;

    private MaterialCalendarView calendarView;

    //private DatePickerDialog.OnDateSetListener dateSetListener1;
    //private DatePickerDialog.OnDateSetListener dateSetListener2;
    private DatePickerDialog.OnDateSetListener dateSetListener;

    private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
    private String arrivalDate, departureDate;

    private DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_booking);
        //instantiate controls
        home = (ImageView) findViewById(R.id.homeImage);
        account = (ImageView) findViewById(R.id.acctImage);
        reservation = (TextView) findViewById(R.id.txtReservation);
        calendarView = (MaterialCalendarView) findViewById(R.id.calendarView);
        btnNext = (Button) findViewById(R.id.btnNext);
        checkin = (TextView) findViewById(R.id.editCheckinDate);
        checkout = (TextView) findViewById(R.id.editCheckoutDate);

        //btnNext.setEnabled(false);
        //btnNext.setBackgroundColor(Color.GRAY);

        //get reference of the db
        ref = FirebaseDatabase.getInstance().getReference();

        today = Calendar.getInstance().getTime();

        final Calendar nextYear = Calendar.getInstance();
        nextYear.add(Calendar.YEAR, 5);

        //create and initalize date Collection
        disableConfirmedDates = new ArrayList<>();
        disablePendingDates = new ArrayList<>();
        disableDefaultDates = new ArrayList<>();

        //Call method to disable pre-defined dates
        DisableDefaultDates();
        //Call method to retrieve reservations from firebase database
        QueryDatabase();

        //calendarView.state().edit().setMinimumDate(min).commit();
        //calendarView.state().edit().setMaximumDate(max).commit();
        calendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_RANGE);
        calendarView.state().edit()
                .setMinimumDate(CalendarDay.today())
                .setMaximumDate(nextYear).commit();
        calendarView.setCurrentDate(today);
        //calendarView.setDateSelected(today, true);
        calendarView.invalidateDecorators();

        //CalendarDay todayDate = CalendarDay.today();

        //set default date (today) after validating date
        //checkin.setText(ValidateDates(todayDate));


        ////On range selection set check in/out text fields and highlight selected dates\\\\
        calendarView.setOnRangeSelectedListener(new OnRangeSelectedListener() {
            @Override
            public void onRangeSelected(@NonNull MaterialCalendarView widget,
                                        @NonNull List<CalendarDay> dates) {
                for (int i = 0; i < dates.size(); i++) {
                    if (disableConfirmedDates.contains(dates.get(i)) && disablePendingDates.contains(dates.get(i)) &&
                            disableDefaultDates.contains(dates.get(i))) {
                        Toast.makeText(BookingActivity.this,
                                "Selection not authorized, select new date range",
                                Toast.LENGTH_SHORT).show();
                        calendarView.clearSelection();
                        checkin.setText("");
                        checkout.setText("");
                        break;
                    } else {
                        checkin.setText(ValidateDates(dates.get(0)));
                        //Log.i("selected date 1:", checkin.getText().toString());

                        checkout.setText(ValidateDates(dates.get(dates.size() - 1)));
                        //Log.i("selected date 2:", checkout.getText().toString());
                    }
                }
            }
        });

        ////change textview date on single date selection\\\\
        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget,
                                       @NonNull CalendarDay date, boolean selected) {
                //if (selected) {
                //date.getMonth() + 1;
                    checkin.setText(ValidateDates(widget.getSelectedDate()));
                    calendarView.setSelectedDate(widget.getSelectedDate());
                    checkout.setText("");
                //}
            }
        });

        ////Manually setting check-in date with datepickerdialog\\\\
        checkin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialogBox(checkin);
            }
        });

        //checkin date spinner validates selected date and
        // updates range on calendar and textfield\\
        SetDateListener(checkin);

        ////Manually setting check-out date with datepickerdialog\\\\
        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialogBox(checkout);
            }
        });

        //checkout date spinner validates selected date and
        // updates range on calendar and textfield\\
        SetDateListener(checkout);

        //On button click -- confirm res and moves to reservation page
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPref = getSharedPreferences(
                        "sharedPref", MODE_PRIVATE);
                sharedPref.edit().clear().apply();
                SharedPreferences.Editor editor = sharedPref.edit();

                //ToDo Add validation for dates before moving on to next page
                //Validate date range is selected
                if (checkin.getText().length() > 0 && checkout.getText().length() > 0) {
                        //Save dates to shared preferences
                        editor.putString("arrivalDate", checkin.getText().toString());
                        editor.putString("departureDate", checkout.getText().toString());
                        editor.commit();

                        //Log.v("sharedPrefArrivalDate", sharedPref.getString("arrivalDate", ""));
                        //Log.v("sharedPrefDepartureDate", sharedPref.getString("departureDate", ""));

                        //Move to next activity
                        startActivity(new Intent(BookingActivity.this, AddGuestsToBooking.class));
                } else {
                    Toast.makeText(BookingActivity.this, "Select Date Range", Toast.LENGTH_SHORT).show();
                    //throw new IllegalArgumentException("Selected date must be non-null.");
                }
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BookingActivity.this, MainActivity.class));
            }
        });

        account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BookingActivity.this, UserAccount.class));
            }
        });

    }

    private void QueryDatabase() {
        disableCD = new ArrayList<>();
        disablePD = new ArrayList<>();
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
                            String status = bookings.getConfirmationStatus();
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
                            if (status.equals("Confirmed")) {
                                //Log.v("status", status);
                                disableCD.add(new BookingClass(arrivalDate, departureDate));
                            } else if (status.equals("Pending")) {
                                //Log.v("status", status);
                                disablePD.add(new BookingClass(arrivalDate, departureDate));
                            }
                        }
                    }
                    //Disable dates by calling methods within a method
                    DisableConfirmedDates(disableCD);
                    DisablePendingDates(disablePD);
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

    ///Disable dates in range\\\
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
                LocalDate currentDate = LocalDate.now();
                //if arrival date is before current date, set arrival date to current date
                if (arrivalFormat.isBefore(currentDate) && departureFormat.isAfter(currentDate) || departureDate.equals(currentDate)) {
                    arrivalFormat = currentDate;
                }

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

    private DatePickerDialog.OnDateSetListener SetDateListener(TextView spinner) {
        dateSetListener =
                new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                CalendarDay spinnerDate = ValidateFirstDate(year, month, day);
                spinner.setText(ValidateDates(spinnerDate));

                calendarView.clearSelection();
                calendarView.setSelectedDate(ValidateSecondDate(spinner.getText().toString()));

                if (!checkout.getText().equals(null)) {
                    calendarView.selectRange(ValidateSecondDate(spinner.getText().toString()),
                            ValidateFirstDate(year, month, day));
                }
                calendarView.invalidateDecorators();
            }
        };
        return dateSetListener;
    }

    private CalendarDay ValidateFirstDate(int year, int month, int day) {
        String formattedMonth = "" + month;
        String formattedDay = "" + day;

        //format dates to add a 0 infront of numbers < 0
        if (month < 10) {
            formattedMonth = "0" + month;
        }
        if (day < 10) {
            formattedDay = "0" + day;
        }

        //String departureDate = formattedMonth + "-" + formattedDay + "-" + year;

        //Log.d("Validate firstDate: ", formattedMonth + "-" + formattedDay + "-" + year);

        return CalendarDay.from(year, Integer.parseInt(formattedMonth),
                Integer.parseInt(formattedDay));
    }


    private CalendarDay ValidateSecondDate(String text) {
        String[] dateParts = new String[3];
        dateParts = text.split("-");
        int month = Integer.parseInt(dateParts[0]);
        int day = Integer.parseInt(dateParts[1]);
        int year = Integer.parseInt(dateParts[2]);

        month -= 1;

        //Log.i("secondDate", month + "-" + day + "-" + year);

        return CalendarDay.from(year, month, day);
    }


    private TextView DatePickerDialogBox(TextView textfield) {
        String[] dateParts = new String[3];

        if (textfield.length() > 0) {
            dateParts = textfield.getText().toString().split("-");
        } else if (textfield.length() == 0) {
            String formattedDateTxt = ValidateDates(CalendarDay.today());
            //String formattedDateTxt = LocalDate.parse(today.toString(), formatter).toString();
            dateParts = formattedDateTxt.split("-");
        }

        int month = Integer.parseInt(dateParts[0]);
        int day = Integer.parseInt(dateParts[1]);
        int year = Integer.parseInt(dateParts[2]);

        DatePickerDialog dpd = new DatePickerDialog(
                BookingActivity.this,
                android.R.style.Theme_Holo_Dialog_MinWidth,
                dateSetListener,
                year, month - 1, day);
        dpd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dpd.show();

        return textfield;
    }


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

        //Log.i("newlyFormatedString", newMonthFormat + "-" + newDayFormat + "-" + year);

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
}