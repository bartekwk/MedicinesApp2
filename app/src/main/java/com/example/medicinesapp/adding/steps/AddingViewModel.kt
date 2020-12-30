package com.example.medicinesapp.adding.steps

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.medicinesapp.data.*
import com.example.medicinesapp.repository.AppRepository
import io.reactivex.Observable
import kotlinx.coroutines.launch

class AddingViewModel (private val repository: AppRepository, private val app: Application): ViewModel(){

    val currentUser = repository.readCurrentUser().asLiveData()


    fun insertPill(pillDB: PillDB){
        viewModelScope.launch {
            repository.insert(pillDB)
        }
    }

    fun insertPillDate(pillTimeDB: PillTimeDB){

        viewModelScope.launch {
            repository.insertDatePill(pillTimeDB)

            Log.d("1", "QAWS")
        }
    }

    fun insertPillFirebase(pillFirebase: PillFirebase,user:String): Observable<String> {
        return repository.addPrescriptionToUser(pillFirebase,user)
    }


    fun updatePillDoseLeft(id:String,doseLeft:Int){
        viewModelScope.launch {
            repository.updatePillDoseLeft(id,doseLeft)
        }
    }

    fun insertPillOrganizer(pillOrganizerDB: PillOrganizerDB){
        viewModelScope.launch {
            repository.insertPillOrganizer(pillOrganizerDB)
        }
    }


}
