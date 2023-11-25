package net.androidbootcamp.campmoab.Bookings.Utils;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.style.ForegroundColorSpan;

import androidx.appcompat.app.AppCompatActivity;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import net.androidbootcamp.campmoab.Bookings.Utils.AddTextToDates;

import java.util.ArrayList;
import java.util.List;

public class PendingBookingDecorator extends AppCompatActivity implements DayViewDecorator {
    private List<CalendarDay> dates = new ArrayList<>();
    private String status;

    public PendingBookingDecorator() {
    }

    public PendingBookingDecorator(List<CalendarDay> dates) {
        this.dates = new ArrayList<>(dates);
    }

    public PendingBookingDecorator(List<CalendarDay> dates, String status) {
        this.dates = new ArrayList<>(dates);
        this.status = status;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return dates.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        //view.addSpan(new DotSpan(5, Color.YELLOW));
        view.addSpan(new AddTextToDates(status));
        view.addSpan(new ForegroundColorSpan(Color.DKGRAY));
        view.setDaysDisabled(true);
        view.setSelectionDrawable(new ColorDrawable(Color.TRANSPARENT));
    }
}
