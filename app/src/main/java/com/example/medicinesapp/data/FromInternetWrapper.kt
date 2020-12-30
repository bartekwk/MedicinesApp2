package com.example.medicinesapp.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FromInternetWrapper(val id:Int,val fromInternets:List<FromInternet>,val doseLeft:Int):Parcelable