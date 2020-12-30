package com.example.medicinesapp.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PillFirebase(
                  val id:String,
                  val name:String,
                  val description:String?,
                  val type:String,
                  val doseLeft:Int?,
                  val dayStart:String,
                  val dayEnd:String,
                  val patient:String,
                  val doctor:String?,
                  var amount:Double?,
                  var listDays:List<String>?): Parcelable {

    constructor():this("","","","",null,"","","",null,0.0,null)

}