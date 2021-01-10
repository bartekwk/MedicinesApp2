package com.example.medicinesapp.warehouse

import android.app.Application
import androidx.lifecycle.ViewModel
import com.example.medicinesapp.data.PillOrganizerDB
import com.example.medicinesapp.repository.AppRepository
import io.reactivex.Observable


class WarehouseViewModel(private val repository: AppRepository, private val app: Application): ViewModel() {


    fun getPillsOrganizer(): Observable<List<PillOrganizerDB>> {
        return repository.getPillsOrganizer()
    }

}