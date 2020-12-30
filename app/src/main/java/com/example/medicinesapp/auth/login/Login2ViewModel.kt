package com.example.medicinesapp.auth.login

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.medicinesapp.repository.AppRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class Login2ViewModel (private val repository: AppRepository, private val app: Application): ViewModel(){

    private val auth = FirebaseAuth.getInstance()
    val userLogin = repository.readUserLogin().asLiveData()


    suspend fun login(login:String,password:String,checked:Boolean): Boolean = suspendCoroutine { cont->

        auth.signInWithEmailAndPassword(login,password)
            .addOnSuccessListener {
                cont.resume(true)
                startGetCurrentUserWorker()
            }.addOnFailureListener {
                cont.resume(false)
            }
    }
    private fun startGetCurrentUserWorker(){
        repository.startGetCurrentUserWorker(app)
    }

    fun updateUser(login:String,password:String,checked:Boolean){

        viewModelScope.launch {
            repository.updateUserLogin(login,password,checked)
        }
    }

}