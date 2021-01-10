package com.example.medicinesapp.organizer.allPills


import androidx.lifecycle.ViewModel
import com.example.medicinesapp.data.*
import com.example.medicinesapp.repository.AppRepository
import io.reactivex.Observable

class AllPillsViewModel(private val repository: AppRepository):ViewModel() {


    suspend fun insert(pillDB: PillDB){
        repository.insert(pillDB)
    }

    fun getFirebasePills(user:String): Observable<PillFirebase?> {
        return repository.getUserPrescription(user)
    }


    fun getPillsToAllPillsFragment():Observable<List<AllPillsFragmentData>>{
        return repository.getPillsToAllPillsFragment()
    }

}