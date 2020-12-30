package com.example.medicinesapp.social.barcode

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.medicinesapp.data.FriendFirebase
import com.example.medicinesapp.data.UserBothChecked
import com.example.medicinesapp.repository.AppRepository
import io.reactivex.Observable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject

class BarcodeViewModel(private val repository: AppRepository, private val app: Application): ViewModel() {

    val currentUser = repository.readCurrentUser().asLiveData()


     fun addUserToFriendList(friend:FriendFirebase){
        viewModelScope.launch {
            repository.addUserToFriendList(friend)
        }
    }

    fun getFriendsSearch(query:String): Observable<UserBothChecked?> {
        return repository.getFriendsSearch(query)
    }


}