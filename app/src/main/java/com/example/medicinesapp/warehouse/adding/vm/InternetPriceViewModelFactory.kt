package com.example.medicinesapp.warehouse.adding.vm

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.medicinesapp.repository.AppRepository

class InternetPriceViewModelFactory(private val repository: AppRepository, private val app: Application): ViewModelProvider.Factory
{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        return InternetPriceViewModel(repository, app) as T
    }

}