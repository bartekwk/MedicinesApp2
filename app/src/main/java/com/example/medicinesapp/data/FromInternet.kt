package com.example.medicinesapp.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FromInternet(var fromPrev:Boolean,
                        val picture:String?,
                        val body:String?,
                        var price:String?,
                        var position:Int?,
                        var amount:Double = 1.0,
                        var count:Int,
                        var connectedToPill:Boolean,
                        var nameOfPill:String,
                        var idPill:String,
                        var clicked:Boolean,
                        var firstTime:Boolean): Parcelable {
}