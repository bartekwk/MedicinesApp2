package com.example.medicinesapp.social.barcode

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.medicinesapp.repository.AppRepository


class BarcodeViewModelFactory(private val repository: AppRepository, private val app: Application): ViewModelProvider.Factory
{

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        return BarcodeViewModel(
            repository,
            app
        ) as T
    }

}