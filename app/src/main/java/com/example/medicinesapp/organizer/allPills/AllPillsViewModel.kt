package com.example.medicinesapp.organizer.allPills

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicinesapp.data.*
import com.example.medicinesapp.repository.AppRepository
import io.reactivex.Observable
import io.reactivex.Single
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AllPillsViewModel(private val repository: AppRepository):ViewModel() {

    private val _list = MutableLiveData<List<PillOrganizerDB>>()
    val list: LiveData<List<PillOrganizerDB>>
        get() = _list



    suspend fun insert(pillDB: PillDB){
        repository.insert(pillDB)
    }

    fun getFirebasePills(user:String): Observable<PillFirebase?> {
        return repository.getUserPrescription(null)
    }


    fun getPillsToAllPillsFragment():Observable<List<AllPillsFragmentData>>{
        return repository.getPillsToAllPillsFragment()
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


    fun updateOrganizerPillDoseLeftNowNegativeInOther(idPill:String,difference:Int){
        viewModelScope.launch {
            repository.updateOrganizerPillDoseLeftNowNegativeInOther(idPill,difference)
        }
    }




}