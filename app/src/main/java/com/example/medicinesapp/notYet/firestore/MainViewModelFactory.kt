package com.example.medicinesapp.notYet.firestore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.medicinesapp.repository.AppRepository

class MainViewModelFactory(private val repository: AppRepository): ViewModelProvider.Factory
{

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        return MainViewModel(repository) as T
    }

}