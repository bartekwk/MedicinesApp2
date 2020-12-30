package com.example.medicinesapp.warehouse

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicinesapp.data.DoseLeftData
import com.example.medicinesapp.data.PillChart
import com.example.medicinesapp.data.PillOrganizerDB
import com.example.medicinesapp.repository.AppRepository
import io.reactivex.Observable
import io.reactivex.Single
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.collect

class WarehouseViewModel(private val repository: AppRepository, private val app: Application): ViewModel() {


    private val _list = MutableLiveData<List<PillOrganizerDB>>()
    val list: LiveData<List<PillOrganizerDB>>
        get() = _list



    fun getPillsOrganizer(): Observable<List<PillOrganizerDB>> {
        return repository.getPillsOrganizer()
    }

    fun getAllLeftDoseByIDS(date:String,time:String): Single<List<DoseLeftData>> {
        return repository.getAllLeftDoseByIDS(date,time)
    }

    fun updateOrganizerPillDoseLeftNow(id:String,doseLeftNow:Int,doseAll:Int){
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.updateOrganizerPillDoseLeftNow(id, doseLeftNow,doseAll)
            }
        }
    }

    fun checkIfNegativeDoseLeftNow(){
        viewModelScope.launch {
            repository.checkIfNegativeDoseLeftNow().collect{
                _list.value = it
            }
        }
    }

    fun markAsUsed(id:Int){

        viewModelScope.launch {
            repository.markAsUsed(id)
        }
    }

    fun updatePillDoseLeftNow(id:String,doseLeftNow:Int){
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.updatePillDoseLeftNow(id, doseLeftNow)
            }
        }
    }

    fun updateOrganizerPillDoseLeftNowNegativeInOther(idPill:String,difference:Int){
        viewModelScope.launch {
            repository.updateOrganizerPillDoseLeftNowNegativeInOther(idPill,difference)
        }
    }





}