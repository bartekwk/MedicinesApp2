package com.example.medicinesapp

import android.app.AlarmManager
import android.content.Context
import androidx.multidex.MultiDexApplication
import androidx.work.Configuration
import androidx.work.DelegatingWorkerFactory
import com.example.medicinesapp.db.AppPreferences
import com.example.medicinesapp.utill.Helper
import com.example.medicinesapp.utill.workManager.FirebaseWorkerFactory

class KtApplication: MultiDexApplication(), Configuration.Provider{


    override fun getWorkManagerConfiguration(): Configuration {

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val myWorkerFactory = DelegatingWorkerFactory()
        myWorkerFactory.addFactory(FirebaseWorkerFactory(Helper.provideRepository(this),alarmManager))


        return Configuration.Builder().setMinimumLoggingLevel(android.util.Log.DEBUG)
            .setWorkerFactory(myWorkerFactory).build()
    }



}