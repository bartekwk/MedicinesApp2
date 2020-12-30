package com.example.medicinesapp.organizer.allPills

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.medicinesapp.repository.AppRepository

class AllPillsViewModelFactory(private val repository: AppRepository): ViewModelProvider.Factory
{

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        return AllPillsViewModel(
            repository
        ) as T
    }

}