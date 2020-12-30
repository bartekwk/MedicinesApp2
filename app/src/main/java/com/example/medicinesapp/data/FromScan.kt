package com.example.medicinesapp.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FromScan(var allString:String,
                    var nameString:String,
                    var organizerString:String,
                    var doseString:String,
                    var amount:Double,
                    var howMuch:Double,
                    var dose:Double,
                    var type:String,
                    var isClicked:Boolean): Parcelable