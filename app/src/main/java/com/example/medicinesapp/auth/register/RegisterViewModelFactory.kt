package com.example.medicinesapp.auth.register

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.medicinesapp.repository.AppRepository

class RegisterViewModelFactory(private val repository: AppRepository,private val app: Application): ViewModelProvider.Factory
{

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        return RegisterViewModel(repository,app) as T
    }

}