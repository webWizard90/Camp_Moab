package net.androidbootcamp.campmoab.Classes;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnRangeSelectedListener;

import net.androidbootcamp.campmoab.R;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.List;

public class PopupCalendarClass {
    private Context context;
    private View anchorView;
    private PopupWindow popupWindow;
    private MaterialCalendarView calendarView;
    private TextView checkin;
    private TextView checkout;
    private DateTimeFormatter formatter;

    public PopupCalendarClass() {

    }

    public PopupCalendarClass(Context context, DateTimeFormatter formatter) {
        this.context = context;
        this.formatter = formatter;
    }

    public PopupCalendarClass(Context context, View anchorView, TextView checkin, TextView checkout, DateTimeFormatter formatter) {

            this.context = context;
            this.anchorView = anchorView;
            this.checkin = checkin;
            this.checkout = checkout;
            this.formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
    }

    // Method to show the calendar popup
    public void showCalendarPopup() {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.admin_reservations_calendar_popup, null);

        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;
        boolean focusable = true;

        popupWindow = new PopupWindow(popupView, width, height, focusable);
        popupWindow.showAtLocation(anchorView, Gravity.CENTER, 0, 0);

        calendarView = popupView.findViewById(R.id.calendarView);
        initializeCalendar();
        setupPopupActions(popupView);
    }

    // Initialize the calendar view with the appropriate settings
    private void initializeCalendar() {
        Calendar nextYear = Calendar.getInstance();
        nextYear.add(Calendar.YEAR, 5);

        calendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_RANGE);
        calendarView.state().edit()
                .setMinimumDate(CalendarDay.today())
                .setMaximumDate(nextYear)
                .commit();

        setupInitialDateRange();
    }

    // Set up initial range if check-in and check-out dates exist
    private void setupInitialDateRange() {
        if (!checkin.getText().toString().isEmpty() && !checkout.getText().toString().isEmpty()) {
            LocalDate today = LocalDate.now();
            LocalDate localCheckinDate = LocalDate.parse(checkin.getText().toString(), formatter);

            if (localCheckinDate.isBefore(today)) {
                setCalendarRange(today.toString(), checkout.getText().toString());
            } else {
                setCalendarRange(checkin.getText().toString(), checkout.getText().toString());
            }
        }
    }

    // Helper method to set the date range on the calendar
    private void setCalendarRange(String checkinDateStr, String checkoutDateStr) {
        String[] checkinDate = checkinDateStr.split("-");
        String[] checkoutDate = checkoutDateStr.split("-");

        CalendarDay firstDate = CalendarDay.from(
                Integer.parseInt(checkinDate[0]),
                Integer.parseInt(checkinDate[1]) - 1,
                Integer.parseInt(checkinDate[2])
        );

        CalendarDay lastDate = CalendarDay.from(
                Integer.parseInt(checkoutDate[0]),
                Integer.parseInt(checkoutDate[1]) - 1,
                Integer.parseInt(checkoutDate[2])
        );

        calendarView.selectRange(firstDate, lastDate);
    }

    // Set up popup actions like handling the calendar range selection and closing the popup
    private void setupPopupActions(View popupView) {
        // Set listener for range selection
        calendarView.setOnRangeSelectedListener((widget, dates) -> {
            if (!dates.isEmpty()) {
                checkin.setText(ValidateDates(dates.get(0)));
                checkout.setText(ValidateDates(dates.get(dates.size() - 1)));
            }
        });

        // Confirm button to dismiss the popup
        Button confirmButton = popupView.findViewById(R.id.btnConfirm);
        confirmButton.setOnClickListener(v -> popupWindow.dismiss());

        // Cancel button to dismiss the popup
        Button cancelButton = popupView.findViewById(R.id.btnCancel);
        cancelButton.setOnClickListener(v -> popupWindow.dismiss());
    }

    // Example of how you might validate the dates
    private String ValidateDates(CalendarDay date) {
        LocalDate localDate = LocalDate.of(date.getYear(), date.getMonth() + 1, date.getDay());
        return localDate.format(formatter);  // Adjust as per your format
    }
}
