package com.example.medicinesapp.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize


@Entity(tableName = "my_pills",indices = [Index(value = ["id"], unique = true)])
@Parcelize
data class PillDB(@PrimaryKey(autoGenerate = false)
                  val id:String,
                  val name:String,
                  val description:String?,
                  val type:String,
                  val doseLeft:Int?,
                  var doseLeftNow:Int?,
                  val dayStart:String,
                  val dayEnd:String,
                  val patient:String,
                  val amount:Double,
                  val doctor:String?): Parcelable {

    constructor():this("","","","",null,null,"","","",0.0,null)

}