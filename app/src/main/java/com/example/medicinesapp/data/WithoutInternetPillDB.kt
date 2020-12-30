package com.example.medicinesapp.data

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "pills_no_internet",indices = [Index(value = ["id"], unique = true)])
@Parcelize
data class WithoutInternetPillDB(@PrimaryKey(autoGenerate = false)
                                 val id:Int,
                                 @Embedded(prefix = "pill")
                                 val pillDB: PillDB?,
                                 val type:String): Parcelable {

    constructor() : this(-1,null, "")
}