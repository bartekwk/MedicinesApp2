package com.example.medicinesapp.social.videoChat.ringing

import android.app.Application
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicinesapp.data.User
import com.example.medicinesapp.repository.AppRepository
import kotlinx.coroutines.launch

class RingingViewModel(private val repository: AppRepository, private val app: Application): ViewModel() {

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?>
        get()=_user

    private val _userPhoto = MutableLiveData<Uri?>()
    val userPhoto: LiveData<Uri?>
        get()=_userPhoto


    fun getUserById(id:String){
        viewModelScope.launch {
            _user.value = repository.getUserByID(id)
        }
    }

    fun getUserPhotoById(id:String){
        viewModelScope.launch {
            _userPhoto.value = repository.getUserPhotoByID3(id)
        }
    }


}

