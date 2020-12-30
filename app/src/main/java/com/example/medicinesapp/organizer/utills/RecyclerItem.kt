package com.example.medicinesapp.organizer.utills

import com.example.medicinesapp.data.DailyPill

sealed class RecyclerItem {

    data class Pill(val name:String):RecyclerItem()
    data class Section(val title:String):RecyclerItem()
}

sealed class RecyclerItem2 {

    data class Pill(val pill: DailyPill):RecyclerItem2()
    data class Section(val title:String,var isFirst:Boolean):RecyclerItem2()
}