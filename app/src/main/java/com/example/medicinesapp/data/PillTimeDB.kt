package com.example.medicinesapp.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize


@Entity(tableName = "pills_time",indices = [Index(value = ["id"], unique = true)],
    foreignKeys = [ForeignKey(entity=PillDB::class,parentColumns = ["id"],childColumns = ["pillId"],
        onDelete =ForeignKey.CASCADE,onUpdate = ForeignKey.CASCADE)])
@Parcelize
data class PillTimeDB(@PrimaryKey(autoGenerate = true)
                      val id:Int?=null,
                      val pillId:String,
                      val date:String,
                      val time:String,
                      var done:Boolean?):Parcelable