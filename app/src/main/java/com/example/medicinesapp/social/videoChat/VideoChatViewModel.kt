package com.example.medicinesapp.social.videoChat

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicinesapp.repository.AppRepository
import kotlinx.coroutines.launch

class VideoChatViewModel(private val repository: AppRepository, private val app: Application): ViewModel() {

    fun notifyUser(myTopic:String,text:String){
        viewModelScope.launch {
            repository.notifyUser(app,myTopic,text)
        }
    }

}
