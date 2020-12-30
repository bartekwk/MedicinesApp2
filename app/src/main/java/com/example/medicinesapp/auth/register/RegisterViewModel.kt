package com.example.medicinesapp.auth.register

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicinesapp.data.PillDB
import com.example.medicinesapp.data.User
import com.example.medicinesapp.repository.AppRepository
import com.google.firebase.auth.FirebaseAuth
import io.reactivex.Observable
import kotlinx.coroutines.delay

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


@SuppressLint("CheckResult")
class RegisterViewModel (private val repository: AppRepository,private val app: Application): ViewModel(){

    private val auth = FirebaseAuth.getInstance()


    val _isRegisterOk = MutableStateFlow("null")
    val isRegisterOk: StateFlow<String> = _isRegisterOk


    fun getPills(): Observable<List<PillDB>> {
        return repository.getPills()
    }

    suspend fun insert(pillDB: PillDB){
        repository.insert(pillDB)
    }

    init{

        //days()
        //noInternet()


        viewModelScope.launch {
           //repository.deletePatientDocument()
            enqueueDownload(app)
            delay(200)
            enqueueDownload(app)
        }


        /*
        repository.getAllUserRecipesFromDoctor().subscribe{

            Log.d("1", "GOOOOOOOOT  $it")
            viewModelScope.launch {
                insert(it)
                val pill1 = PillDB(6,"Z PIECA SPADŁO", mutableListOf("wtorek","sroda","czwartek"),"po posilku","null","bartek","Adam Nowak")
                insert(pill1)
            }
        }

        repository.getIds()?.subscribe {id->
            Log.d("1", "MAAAAAM $id")

            viewModelScope.launch {
                repository.checkDeletion(id)
            }
        }
        */

    }

    fun deleteLater(){
        
    }


    private fun days(){

        /*
        val pill1 = PillDB(7,"ALEHANDRO", mutableListOf("wtorek","sroda","czwartek"),"Bajlando","null","Wiesiek","Adam Kowalczyk")

        val date = PillTimeDB(pillId =7,date = "2020-10-01",time="09:30:00")
        val date2 = PillTimeDB(pillId =7,date = "2020-10-01",time="11:15:00")
        val date3 = PillTimeDB(pillId =7,date = "2020-10-01",time="13:18:00")
        val date4 = PillTimeDB(pillId =7,date = "2020-10-01",time="15:30:00")
        val date5 = PillTimeDB(pillId =7,date = "2020-10-02",time="09:10:00")
        val date6 = PillTimeDB(pillId =7,date = "2020-10-02",time="12:40:00")
        val date7 = PillTimeDB(pillId =7,date = "2020-10-02",time="14:00:00")

        val myList = mutableListOf<PillTimeDB>()
        myList.add(date)
        myList.add(date2)
        myList.add(date3)
        myList.add(date4)
        myList.add(date5)
        myList.add(date6)
        myList.add(date7)
        myList.add(date7)


        viewModelScope.launch {

            repository.insert(pill1)

            myList.forEach {
                repository.insertDatePill(it)
            }

        }

         */

    }



    fun noInternet(){

        /*

        val pill1 = PillDB(6,"Z PIECA SPADŁO", mutableListOf("wtorek","sroda","czwartek"),"po posilku","null","bartek","Adam Nowak")

        val pillWithout1 = WithoutInternetPillDB(pill1.id,pill1,"add")
        val pillWithout2 = WithoutInternetPillDB(pill1.id,pill1,"replace")
        val pillWithout3 = WithoutInternetPillDB(pill1.id,pill1,"delete")

        viewModelScope.launch {
            repository.noInsert(pillWithout1)
            delay(2000)
            repository.noGetPills().subscribe {list->
                list.forEach {
                    when(it.type){
                        "add"->{ Log.d("1", "deleteLater: ADD $it ")}
                        "replace"->{Log.d("1", "deleteLater: REPLACE $it ")}
                        "delete"->{Log.d("1", "deleteLater: DELETE $it ")}
                    }
                }
            }
        }

         */
    }


    private fun enqueueDownload(context: Context){
        repository.enqueueDownload(context)
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