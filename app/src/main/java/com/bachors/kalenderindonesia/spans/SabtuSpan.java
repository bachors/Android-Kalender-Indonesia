package com.bachors.kalenderindonesia.spans;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.style.LineBackgroundSpan;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.bachors.kalenderindonesia.R;

/*
    https://github.com/bachors/Android-Kalender-Indonesia
*/

public class SabtuSpan implements LineBackgroundSpan {

    Context con;

    public SabtuSpan(Context con) {
        this.con = con;
    }

    @Override
    public void drawBackground(
            @NonNull Canvas canvas, Paint paint,
            int left, int right, int top, int baseline, int bottom,
            @NonNull CharSequence charSequence,
            int start, int end, int lineNum
    ) {

        paint.setColor(ContextCompat.getColor(con, R.color.colorSabtu));
    }
}