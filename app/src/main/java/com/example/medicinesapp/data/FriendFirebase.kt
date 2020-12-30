package com.example.medicinesapp.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FriendFirebase(val id:String): Parcelable{

    constructor() : this("")
}


