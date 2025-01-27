package com.bachors.kalenderindonesia.spans;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.style.LineBackgroundSpan;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.bachors.kalenderindonesia.R;

/*
    https://github.com/bachors/Android-Kalender-Indonesia
*/

public class HariSpan implements LineBackgroundSpan {

    private String hij;
    Context con;

    public HariSpan(Context con, String hij) {
        this.hij = hij;
        this.con = con;
    }

    @Override
    public void drawBackground(
            @NonNull Canvas canvas, @NonNull Paint paint,
            int left, int right, int top, int baseline, int bottom,
            @NonNull CharSequence charSequence,
            int start, int end, int lineNum
    ) {

        if(hij.length() == 1){
            hij = "  " + hij;
        }
        Paint paints = new Paint();
        paints.setColor(ContextCompat.getColor(con, R.color.colorHari));
        paints.setTextSize((float) (32));
        paints.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD));
        canvas.drawText(hij, (float) (90/2.5), (float) (8/2.5), paints);
    }
}