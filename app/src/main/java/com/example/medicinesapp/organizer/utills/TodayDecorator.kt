package com.example.medicinesapp.organizer.utills

import android.content.Context
import android.graphics.Typeface
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import androidx.core.content.ContextCompat
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade


class TodayDecorator(private var context: Context,private var color: Int):DayViewDecorator {

    override fun shouldDecorate(day: CalendarDay?): Boolean {
        return day == CalendarDay.today()
    }

    override fun decorate(view: DayViewFacade?) {
        view!!.addSpan(StyleSpan(Typeface.BOLD))
        view.addSpan(RelativeSizeSpan(1.1f))
        view.addSpan(ForegroundColorSpan(ContextCompat.getColor(context, color)))
    }
}