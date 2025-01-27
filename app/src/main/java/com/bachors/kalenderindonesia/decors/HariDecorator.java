package com.bachors.kalenderindonesia.decors;

import android.content.Context;

import com.bachors.kalenderindonesia.spans.HariSpan;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

/*
    https://github.com/bachors/Android-Kalender-Indonesia
*/

public class HariDecorator implements DayViewDecorator {

    private final CalendarDay date;
    private final String hij;
    Context con;

    public HariDecorator(Context con, CalendarDay day, String h) {
        this.date = day;
        this.hij = h;
        this.con = con;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return day.equals(date);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new HariSpan(con, hij));
    }

}
