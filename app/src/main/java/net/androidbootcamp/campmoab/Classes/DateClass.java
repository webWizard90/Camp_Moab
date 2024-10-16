package net.androidbootcamp.campmoab.Classes;

import android.util.Log;
import android.util.Pair;
import android.widget.TextView;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DateClass {
    private DateTimeFormatter formatter;
    private ArrayList<CalendarDay> defaultDatesToDisable;
    private ArrayList<CalendarDay> dbDatesToDisable;
    private ArrayList<CalendarDay> allDisabledDates;

    public DateClass() {
        this.formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
        this.defaultDatesToDisable = new ArrayList<>();
        this.dbDatesToDisable = new ArrayList<>();
        this.allDisabledDates = new ArrayList<>();
    }

    // Method to add decorators to MaterialCalendarView
    public void applyDecorators(ArrayList<CalendarDay> dates, MaterialCalendarView calendarView) {
        //CalendarDecoratorClass calendarDecoratorClass = new CalendarDecoratorClass(dates, status);
        //calendarView.addDecorator(new CalendarDecoratorClass(dates, status));
        //CalendarDecoratorClass calendarDecoratorClass = new CalendarDecoratorClass(dates);
        calendarView.addDecorator(new CalendarDecoratorClass(dates));
        calendarView.invalidateDecorators();
    }

    // Method to format a date string
    public String formatDate(CalendarDay date) {
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }
        return String.format("%02d-%02d-%04d", date.getMonth() + 1, date.getDay(), date.getYear());
    }

    public String validateDates(String checkinString, String checkoutString) throws ParseException {
        LocalDate checkinDate = LocalDate.parse(checkinString, formatter);
        LocalDate checkoutDate = LocalDate.parse(checkoutString, formatter);

        // Check if either date is null
        if (checkinDate == null || checkoutDate == null) {
            return "Please select both check-in and check-out dates.";
        }

        // Check if check-in date is after the checkout date
        if (checkinDate.isAfter(checkoutDate)) {
            return "Check-in date cannot be after check-out date.";
        }
        return null; // Dates are valid
    }

    // Method to calculate the number of days between two dates
    public long daysBetweenDates(LocalDate startDate, LocalDate endDate) {
        return ChronoUnit.DAYS.between(startDate, endDate);
    }

    // Method to add days to a date
    public LocalDate addDaysToDate(LocalDate date, long daysToAdd) {
        return date.plusDays(daysToAdd);
    }

    // Method to add years to a date
    public LocalDate addYearsToDate(LocalDate date, long yearsToAdd) {
        return date.plusYears(yearsToAdd);
    }

    // Convert LocalDate to CalendarDay
    public CalendarDay toCalendarDay(LocalDate date) {
        return CalendarDay.from(date.getYear(), date.getMonthValue() - 1, date.getDayOfMonth());
    }

    // Method to parse a date string and return LocalDate
    public LocalDate parseStringToDate(String dateString) {
        return LocalDate.parse(dateString, formatter); // Parse the string into LocalDate
    }

    // Method to return a formatted date
    public String getFormattedDate(LocalDate date) {
        return date.format(formatter);
    }

    // Method to get and format the current date
    public String getCurrentFormattedDate() {
        CalendarDay currentDate = CalendarDay.today();
        return formatDate(currentDate);
    }

    // Method to get and format the current date and time
    public String getCurrentFormattedDateTime() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm:ss");
        LocalDateTime currentDateTime = LocalDateTime.now();
        return currentDateTime.format(dateTimeFormatter);
    }

    // Method to validate and format the first date (adds 0 in front of month and day if < 10)
    public CalendarDay validateFirstDate(int year, int month, int day) {
        // Format month and day to add a leading 0 if needed
        String formattedMonth = month < 10 ? "0" + month : String.valueOf(month);
        String formattedDay = day < 10 ? "0" + day : String.valueOf(day);

        // Return the CalendarDay object after parsing and formatting the values
        return CalendarDay.from(year, Integer.parseInt(formattedMonth), Integer.parseInt(formattedDay));
    }

    // Method to validate and format a second date from a string (dd-MM-yyyy format)
    public CalendarDay validateSecondDate(String dateText) {
        if (dateText == null || dateText.isEmpty()) {
            throw new IllegalArgumentException("Date string cannot be null or empty");
        }

        // Split the string into day, month, year
        String[] dateParts = dateText.split("-");
        if (dateParts.length != 3) {
            throw new IllegalArgumentException("Date string is not in the correct format");
        }

        int month = Integer.parseInt(dateParts[0]) - 1; // Adjust month since CalendarDay uses 0-indexed months
        int day = Integer.parseInt(dateParts[1]);
        int year = Integer.parseInt(dateParts[2]);

        // Return the CalendarDay object
        return CalendarDay.from(year, month, day);
    }

    // Method to check if any dates in the range is disabled
    public boolean isDateRangeDisabled(LocalDate startDate, LocalDate endDate) {
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            if (isDateDisabled(date, allDisabledDates)) {
                //Log.d("DateClass", "Disabled Date Found in Range: " + startDate + " to " + endDate);
                return true; // A disabled date is found in the range
            }
        }
        return false; // No disabled dates in the range
    }

    // Method to check if a single date is disabled
    public boolean isSingleDateDisabled(CalendarDay selectedDate) {
        LocalDate localDate = convertToLocalDate(selectedDate.getDate());
        return isDateDisabled(localDate, allDisabledDates);
    }

    // Method to check if a specific date is disabled
    /*private boolean isDateDisabled(LocalDate date, ArrayList<CalendarDay> disabledDates) {
        return disabledDates.contains(toCalendarDay(date));
    }*/

    public boolean isDateDisabled(LocalDate date, List<CalendarDay> disabledDates) {
        boolean isDisabled = disabledDates.contains(toCalendarDay(date));
        //Log.d("DatesClass", "Checking if date is disabled: " + date + " -> " + isDisabled);
        return isDisabled;
    }

    // Method to find the next available check-in and check-out dates that are not disabled
    public Pair<CalendarDay, CalendarDay> findNextAvailableDates(CalendarDay currentCheckIn, CalendarDay currentCheckOut) {
        LocalDate checkInDate = convertToLocalDate(currentCheckIn.getDate());
        LocalDate checkOutDate = convertToLocalDate(currentCheckOut.getDate());

        // Add logging to track the iterations
        //Log.d("DateClass", "Starting search for next available dates after: " + checkInDate + " to " + checkOutDate);

        // Loop until we find a range with no disabled dates
        while (true) {
            // Log current range check
            //Log.d("DateClass", "Checking range: " + checkInDate + " to " + checkOutDate);

            // Check if any dates in the range are disabled
            if (!isDateRangeDisabled(checkInDate, checkOutDate)) {
                //Log.d("DateClass", "Available dates found: " + checkInDate + " to " + checkOutDate);
                break; // Found available dates, exit the loop
            }

            // If dates are disabled, move both the check-in and check-out dates forward
            checkInDate = checkInDate.plusDays(1); // Move to the next day
            checkOutDate = checkInDate.plusDays(2); // Ensure check-out is 2 days after check-in
        }

        // Return the next available check-in and check-out dates
        //Log.d("DateClass", "Returning next available dates: " + checkInDate + " to " + checkOutDate);
        return new Pair<>(toCalendarDay(checkInDate), toCalendarDay(checkOutDate));
    }

    // Disable dates in range
    public void disableDates(List<BookingClass> disableDates, MaterialCalendarView calendarView) throws ParseException {
        dbDatesToDisable.clear(); // Clear previous dates

        for (BookingClass booking : disableDates) {
            // Parse arrival and departure dates
            LocalDate arrivalFormat = LocalDate.parse(booking.getArrivalDate(), formatter);
            LocalDate departureFormat = LocalDate.parse(booking.getDepartureDate(), formatter);

            // Calculate the number of days between arrival and departure
            int daysBetween = (int) daysBetweenDates(arrivalFormat, departureFormat);

            // Add the arrival date
            dbDatesToDisable.add(toCalendarDay(arrivalFormat));
            //Log.d("Dates", "StartDate: " + arrivalFormat);

            // Loop through the days in between and add them
            for (int i = 1; i <= daysBetween; i++) {
                LocalDate nextDate = arrivalFormat.plusDays(i);
                //Log.d("Dates", "Added dates: " + nextDate);
                dbDatesToDisable.add(toCalendarDay(nextDate));
            }
        }

        // Get today's date
        LocalDate today = LocalDate.now();

        // Add today's date to disabled dates if it's not already present
        if (!allDisabledDates.contains(today)) {
            allDisabledDates.add(toCalendarDay(today));
            //Log.d("DateClass", "Today's date added to disabled dates: " + today);
        }

        //Log.d("Dates", "Disabled Dates: " + dbDatesToDisable);
        addAllDisabledDates(dbDatesToDisable);
        //applyDecorators(dbDatesToDisable, calendarView, status);
        applyDecorators(allDisabledDates, calendarView);
    }

    // Method to set default check-in and check-out dates on page load
    public void setSelectedDefaultDates(MaterialCalendarView calendarView, TextView checkin, TextView checkout, boolean isPageLoading) {
        // Initialize today's date and set check-in date to tomorrow
        LocalDate today = LocalDate.now();
        LocalDate checkInLocalDate = today.plusDays(1); // Check-in is 1 day after today
        CalendarDay checkInDate = toCalendarDay(checkInLocalDate); // Convert to CalendarDay
        String checkInDateString = formatDate(checkInDate); // Format as string

        // Set Check-out Date to 2 days after the check-in date
        LocalDate checkOutLocalDate = checkInLocalDate.plusDays(2); // Check-out is 2 days after check-in
        CalendarDay checkOutDate = toCalendarDay(checkOutLocalDate); // Convert to CalendarDay
        String checkOutDateString = formatDate(checkOutDate); // Format as string

        //Log.d("DateClass", "Default Check-in Date: " + checkInLocalDate);
        //Log.d("DateClass", "Default Check-out Date: " + checkOutLocalDate);


        // If any date in the range (from check-in to check-out) is disabled, adjust the default dates
        if (isDateRangeDisabled(checkInLocalDate, checkOutLocalDate)) {
            // Find the next available check-in and check-out dates where the next day is also available
            Pair<CalendarDay, CalendarDay> availableDates = findNextAvailableDates(checkInDate, checkOutDate);
            checkInDate = availableDates.first;
            checkOutDate = availableDates.second;

            // Update the check-in and check-out TextViews
            checkin.setText(formatDate(checkInDate));
            checkout.setText(formatDate(checkOutDate));

            // Set the current date in the calendar view to the adjusted the date month view
            calendarView.setCurrentDate(checkInDate);

            //Log.d("DateClass", "Adjusted Check-in Date: " + checkInDate.getDate());
            //Log.d("DateClass", "Adjusted Check-out Date: " + checkOutDate.getDate());
        } else {
            // If no disabled dates are found, set the default dates as is
            checkin.setText(checkInDateString);
            checkout.setText(checkOutDateString);

            //Log.d("DateClass", "Check-in Date: " + checkInDateString);
            //Log.d("DateClass", "Check-out Date: " + checkOutDateString);
        }

        // Set the range in MaterialCalendarView
        //calendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_RANGE);
        calendarView.selectRange(checkInDate, checkOutDate);
        calendarView.invalidateDecorators();

        // Reset isPageLoading to false after page load is complete
        isPageLoading = false;
    }

    // Method to disable default dates
    public void disableSpecificDates(MaterialCalendarView calendarView) {
        // All dates in calendar view are 0 based, so January is 0, February is 1, etc.
        String AprilStart = "04-01-2024";
        String AprilEnd = "04-30-2024";
        String OctoberStart = "10-01-2024";
        String OctoberEnd = "10-31-2024";

        LocalDate AprilFormatStart = LocalDate.parse(AprilStart, formatter);
        LocalDate AprilFormatEnd = LocalDate.parse(AprilEnd, formatter);
        LocalDate OctoberFormatStart = LocalDate.parse(OctoberStart, formatter);
        LocalDate OctoberFormatEnd = LocalDate.parse(OctoberEnd, formatter);

        long daysInBetweenApril = daysBetweenDates(AprilFormatStart, AprilFormatEnd);
        long daysInBetweenOctober = daysBetweenDates(OctoberFormatStart, OctoberFormatEnd);

        //Disable April
        defaultDatesToDisable.add(CalendarDay.from(2024, 4, 1));
        for (int i = 0; i <= 4; i++) {
            for (int n = 0; n <= daysInBetweenApril; n++) {
                LocalDate date = addYearsToDate(AprilFormatStart, i).plusDays(n);
                defaultDatesToDisable.add(toCalendarDay(date));
            }
        }
        defaultDatesToDisable.add(CalendarDay.from(2024, 4, 30));

        defaultDatesToDisable.add(CalendarDay.from(2024, 10, 1));
        for (int i = 0; i <= 4; i++) {
            for (int n = 0; n <= daysInBetweenOctober; n++) {
                LocalDate date = addYearsToDate(OctoberFormatStart, i).plusDays(n);
                defaultDatesToDisable.add(toCalendarDay(date));
            }
        }
        defaultDatesToDisable.add(CalendarDay.from(2024, 10, 31));
        addAllDisabledDates(defaultDatesToDisable);

        //applyDecorators(defaultDatesToDisable, calendarView, "default");
        applyDecorators(defaultDatesToDisable, calendarView);
    }

    // Method to disable date changes when the reservation has already started
    public void disableDateChange(TextView checkin, TextView checkout, MaterialCalendarView calendarView) {
        // Disable the check-in and check-out TextViews
        checkin.setEnabled(false);
        checkout.setEnabled(false);

        // Disable interaction with the calendar
        calendarView.setAllowClickDaysOutsideCurrentMonth(false);
        calendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_NONE);
    }

    private LocalDate convertToLocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    // Method to add all the disabled dates to one list
    public void addAllDisabledDates(List<CalendarDay> datesToDisable) {
        allDisabledDates.addAll(datesToDisable);
    }
}
