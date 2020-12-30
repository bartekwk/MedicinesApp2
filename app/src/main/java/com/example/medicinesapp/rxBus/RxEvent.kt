package com.example.medicinesapp.rxBus

import com.example.medicinesapp.data.FromInternet
import com.example.medicinesapp.data.FromScan
import com.example.medicinesapp.data.PrevPill

class RxEvent {

    data class EventDate(val daysList:List<String>)

    data class EventQuery(val query:String)

    data class EventHourOne(val hour:String)

    data class EventHourTwoOne(val hour:String)

    data class EventHourTwoTwo(val period:String)

    data class EventHourThree(val eventList:List<String>)

    object EventClick

    data class EventDose(val dose:String)

    data class EventInfo(val info:String)

    data class EventChoice(val choice:String)

    data class EventInternetList(val list:List<FromInternet>)

    data class BottomSheetEvent(val type:Int)

    data class TypeChosenEvent(val type:Int)

    data class PrevPillChosen(val pill: PrevPill)

    object EventHourClean


}