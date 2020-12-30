package com.example.medicinesapp.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class VideoChat(var idFirebase:String,val idChat:String): Parcelable {

    constructor() : this("","")
}