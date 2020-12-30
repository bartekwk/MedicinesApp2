package com.example.medicinesapp.utill.workManager

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.medicinesapp.repository.AppRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FirebaseWorker(private val context: Context, params: WorkerParameters,
                     private val repository: AppRepository): CoroutineWorker(context,params) {


    override suspend fun doWork(): Result = withContext(Dispatchers.IO){


        val input = inputData.getInt("PUT",-1)

        Log.d("1", "doWork: $input")

        /*
        val recipe =repository.getOneRecipe(input)
        val id = repository.auth.uid!!
        repository.db.getReference("/users-recipes/$id").push().setValue(recipe).await()

         */

        Result.success()
    }

}