package com.example.medicinesapp.mainActivity

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicinesapp.data.PillDB
import com.example.medicinesapp.repository.AppRepository
import kotlinx.coroutines.launch

class MainActivityViewModel(private val repository: AppRepository, private val app: Application): ViewModel() {



     fun logOut(){
        viewModelScope.launch {
            repository.markAsOffline()
        }
    }

    fun unsubscribeFromTopic(){
        repository.unsubscribeFromTopic()
    }


    fun setDailyAlarmSetterWorker(){

       repository.startServerWork(app)
    }

    fun insertInitialPill(pillDB: PillDB){

        viewModelScope.launch {
            repository.insert(pillDB)
        }
    }


}