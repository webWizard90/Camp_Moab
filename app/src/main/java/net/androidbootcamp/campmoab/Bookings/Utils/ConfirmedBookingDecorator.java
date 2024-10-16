package net.androidbootcamp.campmoab.Bookings.Utils;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.style.ForegroundColorSpan;
import android.text.style.StrikethroughSpan;

import androidx.appcompat.app.AppCompatActivity;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import net.androidbootcamp.campmoab.Bookings.Utils.AddTextToDates;

import java.util.ArrayList;
import java.util.List;

public class ConfirmedBookingDecorator extends AppCompatActivity implements DayViewDecorator {
    private List<CalendarDay> dates = new ArrayList<>();
    //get the status from DayViewDecorator
    private String status;

    public ConfirmedBookingDecorator() {
    }

    public ConfirmedBookingDecorator(List<CalendarDay> disableSpecificDates) {
        this.dates = new ArrayList<>(dates);
    }

    public ConfirmedBookingDecorator(List<CalendarDay> dates, String status) {
        this.dates = new ArrayList<>(dates);
        this.status = status;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return dates.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        //Adds a dot below the disabled dates
        view.addSpan(new StrikethroughSpan());
        view.addSpan(new ForegroundColorSpan(Color.RED));
        view.addSpan(new AddTextToDates(status));
        view.setDaysDisabled(true);
        view.setSelectionDrawable(new ColorDrawable(Color.TRANSPARENT));
    }
}


