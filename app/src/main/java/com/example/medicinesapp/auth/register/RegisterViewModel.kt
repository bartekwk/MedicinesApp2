package com.example.medicinesapp.auth.register

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicinesapp.data.PillDB
import com.example.medicinesapp.data.User
import com.example.medicinesapp.repository.AppRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


@SuppressLint("CheckResult")
class RegisterViewModel (private val repository: AppRepository,private val app: Application): ViewModel(){

    private val auth = FirebaseAuth.getInstance()


    val _isRegisterOk = MutableStateFlow("null")
    val isRegisterOk: StateFlow<String> = _isRegisterOk


    suspend fun insert(pillDB: PillDB){
        repository.insert(pillDB)
    }


    private fun startGetCurrentUserWorker(){
        repository.startGetCurrentUserWorker(app)
    }


    fun register( username:String , password:String){
        viewModelScope.launch {
            auth.createUserWithEmailAndPassword(username,password).addOnSuccessListener {
                _isRegisterOk.value="true"
                startGetCurrentUserWorker()
                Log.d("1", "register: SUCCESS")
            }.addOnFailureListener {
                _isRegisterOk.value="false"
                Log.d("1", "register: FAILURE") }
        }
    }

    fun addUserToDatabase(user: User) = viewModelScope.launch {
        repository.addUserToDatabase(user)
    }

}