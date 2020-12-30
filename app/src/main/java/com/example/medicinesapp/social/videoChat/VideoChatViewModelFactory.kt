package com.example.medicinesapp.social.videoChat

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.medicinesapp.repository.AppRepository

class VideoChatViewModelFactory(private val repository: AppRepository, private val app: Application): ViewModelProvider.Factory
{

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        return VideoChatViewModel(repository,app) as T
    }

}