package com.example.medicinesapp.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PrevPill(val id:String,
                    val date:String,
                    val time:String,
                    val name:String,
                    val description:String,
                    val type:String,
                    val amount:Double,
                    val dayStart:String,
                    val dayEnd:String,
                    val doctor:String?,
                    var startHour:String?,
                    var period:String?,
                    var listHour:String?): Parcelable