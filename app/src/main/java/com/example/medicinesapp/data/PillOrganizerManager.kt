package com.example.medicinesapp.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PillOrganizerManager(val id:String,val listPill:List<PillOrganizerDB>): Parcelable