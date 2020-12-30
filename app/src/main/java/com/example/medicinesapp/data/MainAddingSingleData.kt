package com.example.medicinesapp.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class MainAddingSingleData(val id:String,
                           var name:String,
                           var dayStart:String,
                           var lastDay:String,
                           var days:List<String>,
                           var patient:String,
                           var doctor:String?,
                           var dose:Double,
                           var info:String?,
                           var type:String,
                           var period:String?,
                           var hourStart:String?,
                           var hours:MutableList<String>):Parcelable {
}