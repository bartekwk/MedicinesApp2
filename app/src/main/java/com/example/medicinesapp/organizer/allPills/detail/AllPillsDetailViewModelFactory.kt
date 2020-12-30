package com.example.medicinesapp.organizer.allPills.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.medicinesapp.repository.AppRepository

class AllPillsDetailViewModelFactory(private val repository: AppRepository): ViewModelProvider.Factory
{

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        return AllPillsDetailViewModel(
            repository
        ) as T
    }

}