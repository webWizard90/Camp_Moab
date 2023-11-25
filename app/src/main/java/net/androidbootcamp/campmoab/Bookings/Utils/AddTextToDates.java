package net.androidbootcamp.campmoab.Bookings.Utils;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.style.LineBackgroundSpan;

import androidx.annotation.ColorInt;

public class AddTextToDates implements LineBackgroundSpan {
    private String status;

    public AddTextToDates(String text) {
        this.status = text;
    }

    @Override
    public void drawBackground(
            Canvas canvas,
            Paint paint,
            int left,
            int right,
            int top,
            int baseline,
            int bottom,
            CharSequence text,
            int start,
            int end,
            int lnum
    ) {
        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setTextSize(40);
        canvas.drawText(status, ((left + right) / 4), (top + bottom) * 2, paint);
    }
}
