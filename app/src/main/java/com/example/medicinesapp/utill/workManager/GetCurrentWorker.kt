package com.example.medicinesapp.utill.workManager

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.medicinesapp.repository.AppRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GetCurrentWorker(private val context: Context, params: WorkerParameters,
                     private val repository: AppRepository
): CoroutineWorker(context,params) {


    override suspend fun doWork(): Result = withContext(Dispatchers.IO){

        repository.checkCurrentUser()
        Result.success()
    }

}