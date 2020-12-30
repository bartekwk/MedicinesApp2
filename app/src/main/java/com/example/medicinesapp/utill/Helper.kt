package com.example.medicinesapp.utill

import android.content.Context
import com.example.medicinesapp.db.*
import com.example.medicinesapp.repository.AppRepository

object Helper{

   fun provideRepository(context: Context):AppRepository{

       val dao:PillDao = PillsDatabase(context.applicationContext).pillDao()
       val dao2:NoInternetPillDao = PillsDatabase(context.applicationContext).noInternetPillDao()
       val dao3:PillTimeDao = PillsDatabase(context.applicationContext).pillTimeDao()
       val dao4:PillOrganizerDao = PillsDatabase(context.applicationContext).pillOrganizerDao()
       return AppRepository(dao,dao2,dao3,dao4,context)
   }


}