package com.example.medicinesapp.adding.dialogs.vm

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicinesapp.data.PrevPill
import com.example.medicinesapp.repository.AppRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class PrevPillsViewModel (private val repository: AppRepository, private val app: Application): ViewModel(){

    private val _pills = MutableLiveData<List<PrevPill>>()
    val pills: LiveData<List<PrevPill>>
        get()=_pills


    fun getPreviousPills(){
       viewModelScope.launch {
           repository.getPreviousPills().collect {
               _pills.value = it
           }
       }
    }



}