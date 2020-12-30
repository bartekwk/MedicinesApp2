package com.example.medicinesapp.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "pills_organizer",indices = [Index(value = ["id"], unique = true)],
    foreignKeys = [ForeignKey(entity=PillDB::class,parentColumns = ["id"],childColumns = ["pillId"],
        onDelete = ForeignKey.CASCADE,onUpdate = ForeignKey.CASCADE)])
@Parcelize
data class PillOrganizerDB(@PrimaryKey(autoGenerate = true)
                           val id:Int?=null,
                           val pillId:String,
                           val picture:String?,
                           val price:Double?,
                           val amount:Double?,
                           val left:Int?,
                           var leftNow:Int?,
                           val inside:String?,
                           var bought:Boolean,
                           val name:String?):Parcelable {
}