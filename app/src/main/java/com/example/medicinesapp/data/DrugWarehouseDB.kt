package com.example.medicinesapp.data

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

/*
@Entity
data class DrugWarehouseDB(@PrimaryKey(autoGenerate = true)
                           val id:Int?=null,
                           val pillId:Int,
                           val bought:Boolean,
                           val amount:Int,
                           @Embedded(prefix = "_drug")
                           val drug:FromInternet)

 */

data class DrugWarehouseDB(val id:Int?=null,
                           val pillId:Int,
                           val bought:Boolean,
                           val amount:Int,
                           val drug:FromInternet)