package com.example.medicinesapp.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AllPillsFragmentData( val id:String,
                                 val name:String,
                                 val description:String?,
                                 val type:String,
                                 var doseLeft:Int?,
                                 var doseLeftNow:Int?,
                                 val dayStart:String,
                                 val dayEnd:String,
                                 val patient:String,
                                 val amount:Double,
                                 val doctor:String?,
                                 var leftOrganizer:Double?,
                                 var leftNowOrganizer:Double?): Parcelable {
}