package com.example.medicinesapp.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.medicinesapp.data.WithoutInternetPillDB
import io.reactivex.Observable

@Dao
interface NoInternetPillDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun noInsert(pillDB: WithoutInternetPillDB)

    @Query("SELECT * FROM pills_no_internet")
    fun noGetPills(): Observable<List<WithoutInternetPillDB>>

    @Query("delete from pills_no_internet where id=:id")
    suspend fun noDeleteOnePill(id:Int)

}