package com.example.medicinesapp.social.videoChat

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicinesapp.data.FriendAllFirebase
import com.example.medicinesapp.data.PillChart
import com.example.medicinesapp.repository.AppRepository
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch

class VideoChatViewModel(private val repository: AppRepository, private val app: Application): ViewModel() {


    private val _user = MutableLiveData<FriendAllFirebase>()
    val user: LiveData<FriendAllFirebase>
        get() = _user

    fun notifyUser(myTopic:String,text:String){
        viewModelScope.launch {
            repository.notifyUser(app,myTopic,text)
        }
    }

     fun getUserName(id:String){
        viewModelScope.launch {
            val userRepository = repository.getUserName(id)
            userRepository?.let {
               _user.value = it
           }
        }
    }

}
