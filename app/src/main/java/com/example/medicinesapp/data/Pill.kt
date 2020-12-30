package com.example.medicinesapp.data



data class Pill(val name:String,
                val days:MutableList<Long>?,
                val description:String,
                val url:String,
                val patient:String,
                val doctor:String?){

    constructor() : this("",null,"","","","" )
}