package com.example.medicinesapp.ui

import android.app.Application
import androidx.lifecycle.*
import com.example.medicinesapp.data.PillWorkManager
import com.example.medicinesapp.data.User
import com.example.medicinesapp.repository.AppRepository
import kotlinx.coroutines.launch

class DashboardViewModel(private val repository: AppRepository,private val app:Application):ViewModel() {


    private val _currentUser = MutableLiveData<User?>()
    val currentUser: LiveData<User?>
        get()=_currentUser

    private val _currentPill = MutableLiveData<PillWorkManager>()
    val currentPill: LiveData<PillWorkManager>
        get()=_currentPill

    init{
        getCurrentUser()
    }

     fun getClosestPill(date:String){
         viewModelScope.launch {
             val pill = repository.getClosestPill(date)
             _currentPill.value = pill
         }
     }

    fun markAsOnline() {
        viewModelScope.launch {
            repository.markAsOnline()
        }
    }

    fun subscribeToTopic(){
        repository.subscribeToTopic()
    }

    private fun getCurrentUser(){
        viewModelScope.launch {
            _currentUser.value = repository.getCurrentUser()
        }
    }



}