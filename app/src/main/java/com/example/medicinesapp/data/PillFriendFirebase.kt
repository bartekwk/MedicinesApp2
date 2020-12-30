package com.example.medicinesapp.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PillFriendFirebase(
    val id:String,
    val name:String,
    val description:String?,
    val type:String,
    val doseLeft:Int?,
    val dayStart:String,
    val dayEnd:String,
    val patient:String,
    val doctor:String?,
    var temp:String?,
    var day:String?,
    var hour:String?): Parcelable {

    constructor() : this("", "", "", "", null, "", "", "", null, null, null,null)
}