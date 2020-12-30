package com.example.medicinesapp.warehouse.detail

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicinesapp.data.PillChart
import com.example.medicinesapp.repository.AppRepository
import kotlinx.coroutines.launch

class WarehouseDetailViewModel(private val repository: AppRepository, private val app: Application): ViewModel() {


    private val _listChart = MutableLiveData<List<PillChart>>()
    val listChart: LiveData<List<PillChart>>
        get() = _listChart



    fun  getPillsToChart(pillId:String,date:String,time:String){
        viewModelScope.launch {
            _listChart.value = repository.getPillsToChart(pillId,date,time)
        }
    }

    fun markAsBoughtOrNot(id:Int,bought:Boolean){
        viewModelScope.launch {
            repository.markAsBoughtOrNot(id,bought)
        }
    }


}