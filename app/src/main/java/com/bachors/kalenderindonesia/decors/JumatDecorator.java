package com.bachors.kalenderindonesia.decors;

import android.content.Context;

import com.bachors.kalenderindonesia.spans.JumatSpan;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.util.Collection;
import java.util.HashSet;

/*
    https://github.com/bachors/Android-Kalender-Indonesia
*/

public class JumatDecorator implements DayViewDecorator {

    private final HashSet<CalendarDay> dates;
    Context con;

    public JumatDecorator(Context con, Collection<CalendarDay> dates) {
        this.dates = new HashSet<>(dates);
        this.con = con;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return dates.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new JumatSpan(con));
    }
}
