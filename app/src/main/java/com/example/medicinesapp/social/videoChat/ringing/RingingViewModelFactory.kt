package com.example.medicinesapp.social.videoChat.ringing

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.medicinesapp.repository.AppRepository

class RingingViewModelFactory(private val repository: AppRepository, private val app: Application): ViewModelProvider.Factory
{

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        return RingingViewModel(repository,app) as T
    }

}