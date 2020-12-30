package com.example.medicinesapp.organizer.myPills

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.medicinesapp.repository.AppRepository

class MyPillsViewModelFactory(private val repository: AppRepository): ViewModelProvider.Factory
{

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        return MyPillsViewModel(
            repository
        ) as T
    }

}