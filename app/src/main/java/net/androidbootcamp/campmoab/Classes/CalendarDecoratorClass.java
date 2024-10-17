package net.androidbootcamp.campmoab.Classes;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.style.ForegroundColorSpan;
import android.text.style.StrikethroughSpan;

import androidx.appcompat.app.AppCompatActivity;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.util.ArrayList;
import java.util.List;

public class CalendarDecoratorClass extends AppCompatActivity implements DayViewDecorator {
    private List<CalendarDay> dates = new ArrayList<>();

    public CalendarDecoratorClass() {}

    public CalendarDecoratorClass(List<CalendarDay> dates) {
        this.dates = new ArrayList<>(dates);
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return dates.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
            view.addSpan(new StrikethroughSpan());
            view.addSpan(new ForegroundColorSpan(Color.GRAY));
            view.setDaysDisabled(false);
            view.setSelectionDrawable(new ColorDrawable(Color.TRANSPARENT));
    }
}
