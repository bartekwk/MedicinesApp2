package com.example.medicinesapp.warehouse.adding.vm

import android.app.Application
import androidx.lifecycle.ViewModel
import com.example.medicinesapp.data.FromInternet
import com.example.medicinesapp.data.PillDB
import com.example.medicinesapp.repository.AppRepository
import io.reactivex.Observable

class InternetDialogViewModel (private val repository: AppRepository, private val app: Application): ViewModel(){


    fun getMedicine(name:String,id:String): Observable<FromInternet>? {

        return repository.getMedicine(name,id)
    }

    fun getSelectedPills(ids:List<String>): Observable<List<PillDB>> {
        return repository.getSelectedPills(ids)
    }


}
