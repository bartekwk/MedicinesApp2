package com.example.medicinesapp.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.medicinesapp.data.PillDB
import io.reactivex.Observable


@Dao
interface PillDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(pillDB: PillDB)

    @Query("SELECT * FROM my_pills")
    fun getPills(): Observable<List<PillDB>>

    @Query("DELETE FROM my_pills WHERE id=:id")
    suspend fun deleteOnePill(id:String)

    @Query("SELECT * FROM my_pills WHERE id IN(:ids)")
     fun getSelectedPills(ids:List<String>):Observable<List<PillDB>>

    @Query("UPDATE my_pills SET doseLeft = :doseLeft WHERE id = :id")
    suspend fun updatePillDoseLeft(id:String,doseLeft:Int)

    @Query("UPDATE my_pills SET doseLeftNow = :doseLeftNow WHERE id = :id")
    suspend fun updatePillDoseLeftNow(id:String,doseLeftNow:Int)

    @Query("UPDATE my_pills SET doseLeftNow = doseLeft")
    suspend fun restartUpdatePill():Int

}