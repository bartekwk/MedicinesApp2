package com.example.medicinesapp.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PillWorkManager(val id:Int,
                           val name:String,
                           val description:String,
                           val doctor:String?,
                           val date:String,
                           val time:String,
                           val amount:Double): Parcelable