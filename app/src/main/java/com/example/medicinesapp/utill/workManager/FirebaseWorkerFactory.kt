package com.example.medicinesapp.utill.workManager

import android.app.AlarmManager
import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.example.medicinesapp.repository.AppRepository

class FirebaseWorkerFactory(private val repository: AppRepository,private val alarmManager: AlarmManager): WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return when(workerClassName){
            FirebaseWorker::class.java.name ->  FirebaseWorker(appContext,workerParameters,repository)
            GetCurrentWorker::class.java.name -> GetCurrentWorker(appContext,workerParameters,repository)
            DailyAlarmSetWorker::class.java.name -> DailyAlarmSetWorker(appContext,workerParameters,alarmManager,repository)
            else->null
        }
    }
}