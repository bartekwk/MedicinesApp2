package com.example.medicinesapp.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PillChart(val count:Double,
                     val sum:Int,
                     val date:String,
                     val name:String,
                     val amount:Double,
                     val pillId:String,
                     val doseLeftNow:Int?): Parcelable {
}