package net.androidbootcamp.campmoab.Classes;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.style.ForegroundColorSpan;
import android.text.style.StrikethroughSpan;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import net.androidbootcamp.campmoab.Bookings.Utils.AddTextToDates;

import java.util.ArrayList;
import java.util.List;

public class CalendarDecoratorClass extends AppCompatActivity implements DayViewDecorator {
    private List<CalendarDay> dates = new ArrayList<>();
    private String status;

    public CalendarDecoratorClass() {
    }

    public CalendarDecoratorClass(List<CalendarDay> dates) {
        this.dates = new ArrayList<>(dates);
    }

    public CalendarDecoratorClass(List<CalendarDay> dates, String status) {
        this.dates = new ArrayList<>(dates);
        this.status = status;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return dates.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        /*if ("Pending".equals(status.trim())) {
            //view.addSpan(new DotSpan(5, Color.YELLOW));
            view.addSpan(new AddTextToDates(status));
            view.addSpan(new ForegroundColorSpan(Color.DKGRAY));
            view.setDaysDisabled(true);
            view.setSelectionDrawable(new ColorDrawable(Color.TRANSPARENT));
        } else if ("N/A".equals(status.trim())) {
            //Adds a dot below the disabled dates
            view.addSpan(new StrikethroughSpan());
            view.addSpan(new ForegroundColorSpan(Color.RED));
            view.addSpan(new AddTextToDates(status));
            view.setDaysDisabled(true);
            view.setSelectionDrawable(new ColorDrawable(Color.TRANSPARENT));
        } else if ("default".equals(status.trim())) {*/
            view.addSpan(new StrikethroughSpan());
            view.addSpan(new ForegroundColorSpan(Color.GRAY));
            view.setDaysDisabled(false);
            view.setSelectionDrawable(new ColorDrawable(Color.TRANSPARENT));
        //} else if ("highlight".equals(status.trim())) {
            //view.addSpan(new ForegroundColorSpan(Color.RED));
            //view.setDaysDisabled(false);
            //view.setSelectionDrawable(new ColorDrawable(Color.TRANSPARENT));
        /*} else {
            Log.d("CalendarDecoratorClass", "No status found");
        }*/
    }
}
