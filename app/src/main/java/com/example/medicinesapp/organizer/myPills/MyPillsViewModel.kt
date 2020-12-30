package com.example.medicinesapp.organizer.myPills

import androidx.lifecycle.ViewModel
import com.example.medicinesapp.data.DailyPill
import com.example.medicinesapp.data.PillFirebase
import com.example.medicinesapp.repository.AppRepository
import io.reactivex.Observable

class MyPillsViewModel(private val repository: AppRepository): ViewModel() {


    fun getDailyPill(date:String): Observable<List<DailyPill>> {
        return repository.getDailyPill(date)
    }



}