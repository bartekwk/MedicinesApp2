package com.example.medicinesapp.organizer.allPills.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicinesapp.data.DailyPill
import com.example.medicinesapp.data.PillFirebase
import com.example.medicinesapp.repository.AppRepository
import io.reactivex.Observable
import kotlinx.coroutines.launch

class AllPillsDetailViewModel(private val repository: AppRepository): ViewModel() {

    private val _pill = MutableLiveData<PillFirebase>()
    val pill: LiveData<PillFirebase>
        get() = _pill


    fun getAllPillsByID(id:String): Observable<List<DailyPill>> {
        return repository.getAllPillsByID(id)
    }

    fun getOneUserPrescription(user:String,idOfPill:String){
        viewModelScope.launch {
            val result = repository.getOneUserPrescription(user,idOfPill)
            result?.let {
                _pill.value  = it
            }
        }
    }

    fun deleteOnePill(pillDBID: String){
        viewModelScope.launch {
            repository.deleteOnePill(pillDBID)
        }
    }

    fun deletePillFirebase(pillDBID: String){
        viewModelScope.launch {
            repository.deleteMyDocument(pillDBID)
        }
    }
}