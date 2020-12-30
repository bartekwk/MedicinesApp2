package com.example.medicinesapp.organizer.utills

import android.content.Context
import androidx.core.content.ContextCompat
import com.example.medicinesapp.R
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade

class SelectorDecorator(var context: Context):DayViewDecorator {

    var drawable = ContextCompat.getDrawable(context, R.drawable.calendar_selector)

    override fun shouldDecorate(day: CalendarDay?): Boolean {
        return true
    }

    override fun decorate(view: DayViewFacade?) {
        view?.setSelectionDrawable(drawable!!)
    }
}