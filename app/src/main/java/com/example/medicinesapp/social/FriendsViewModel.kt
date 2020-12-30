package com.example.medicinesapp.social

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicinesapp.data.FriendIDFirebase
import com.example.medicinesapp.data.PillFirebase
import com.example.medicinesapp.data.User
import com.example.medicinesapp.data.UserBothChecked
import com.example.medicinesapp.repository.AppRepository
import io.reactivex.Observable
import kotlinx.coroutines.launch

class FriendsViewModel(private val repository: AppRepository, private val app: Application): ViewModel() {


    private val _userPair = MutableLiveData<UserBothChecked>()
    val userPair: LiveData<UserBothChecked>
        get()=_userPair


    fun getFriendIDS(): Observable<FriendIDFirebase?> {
        return repository.getFriendsIDS()
    }

    fun updateFriendSwitcher(friendID:String,switchOn:Boolean){
        viewModelScope.launch {
            repository.updateFriendSwitcher(friendID,switchOn)
        }
    }

    fun getFriendsWithAllowInfo(id:String){
        viewModelScope.launch {

            val friendPar = repository.getFriendWithAllowInfo(id)
            friendPar?.let {
                Log.d("1", "OLABOGA $it ")
                _userPair.value = it
            }
        }
    }

    fun getUserPrescription(user:String): Observable<PillFirebase?> {
        return repository.getUserPrescription(user)
    }

}