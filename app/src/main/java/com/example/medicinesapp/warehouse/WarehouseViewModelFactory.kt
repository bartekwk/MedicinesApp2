package com.example.medicinesapp.warehouse

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.medicinesapp.repository.AppRepository

class WarehouseViewModelFactory(private val repository: AppRepository, private val app: Application): ViewModelProvider.Factory
{

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        return WarehouseViewModel(repository,app) as T
    }

}