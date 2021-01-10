package com.example.medicinesapp.mainActivity

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicinesapp.data.*
import com.example.medicinesapp.repository.AppRepository
import io.reactivex.Single
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivityViewModel(private val repository: AppRepository, private val app: Application): ViewModel() {



    private val _list = MutableLiveData<List<PillOrganizerDB>>()
    val list: LiveData<List<PillOrganizerDB>>
        get() = _list

    private val _updateDoneOne = MutableLiveData<Int>()
    val updateDoneOne: LiveData<Int>
        get() = _updateDoneOne

    private val _updateDoneTwo = MutableLiveData<Int>()
    val updateDoneTwo: LiveData<Int>
        get() = _updateDoneTwo

    init{
        restartOrganizer()
        restartUpdatePill()
    }



    fun updatePillDoseLeftNow(id:String,doseLeftNow:Int){
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.updatePillDoseLeftNow(id, doseLeftNow)
            }
        }
    }

    fun updateOrganizerPillDoseLeftNow(id:String,doseLeftNow:Int,doseAll:Int){
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.updateOrganizerPillDoseLeftNow(id, doseLeftNow,doseAll)
            }
        }
    }

    fun getAllLeftDoseByIDS(date:String,time:String): Single<List<DoseLeftData>> {
        return repository.getAllLeftDoseByIDS(date,time)
    }


    fun checkIfNegativeDoseLeftNow(){
        viewModelScope.launch {
            repository.checkIfNegativeDoseLeftNow()
                .collect{
                    _list.value = it
                }
        }
    }

    fun markAsUsed(id:Int){

        viewModelScope.launch {
            repository.markAsUsed(id)
        }
    }


    fun updateOrganizerPillDoseLeftNowNegativeInOther(idPill:String,difference:Int){
        viewModelScope.launch {
            repository.updateOrganizerPillDoseLeftNowNegativeInOther(idPill,difference)
        }
    }


    private fun restartUpdatePill(){
        viewModelScope.launch {
            _updateDoneOne.value = repository.restartUpdatePill()
        }
    }

    private fun restartOrganizer(){
        viewModelScope.launch {
           _updateDoneTwo.value = repository.restartOrganizer()
        }
    }

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