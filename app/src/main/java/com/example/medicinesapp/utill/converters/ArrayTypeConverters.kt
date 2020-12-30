package com.example.medicinesapp.utill.converters

import androidx.room.TypeConverter

object ArrayTypeConverters {
    @TypeConverter
    @JvmStatic
    fun toListOfStrings(flatStringList: String): List<String> {
        return flatStringList.split(",")
    }
    @TypeConverter
    @JvmStatic
    fun fromListOfStrings(listOfString: List<String>): String {
        return listOfString.joinToString(",")
    }
}
