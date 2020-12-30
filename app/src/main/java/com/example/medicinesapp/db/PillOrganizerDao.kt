package com.example.medicinesapp.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.medicinesapp.data.AllPillsFragmentData
import com.example.medicinesapp.data.PillOrganizerDB
import io.reactivex.Observable
import kotlinx.coroutines.flow.Flow

@Dao
interface PillOrganizerDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPillOrganizer(pillOrganizerDB: PillOrganizerDB)

    @Query("SELECT * FROM pills_organizer")
    fun getPillsOrganizer(): Observable<List<PillOrganizerDB>>

    @Query("delete from pills_organizer where id=:id")
    suspend fun deleteOnePillOrganizer(id:Int)

    @Query("SELECT * FROM pills_organizer WHERE pillId IN(:ids)")
    fun getPillsOrganizerByIDS(ids:List<String>):Observable<List<PillOrganizerDB>>

    @Query("SELECT mp.id,mp.name,mp.description,mp.type,mp.doseLeft,mp.doseLeftNow,mp.dayStart,mp.dayEnd,mp.patient,mp.amount,mp.doctor,SUM(po.`left`) AS 'leftOrganizer',SUM(po.leftNow) AS 'leftNowOrganizer'" +
            "FROM my_pills mp LEFT JOIN pills_organizer po ON mp.id = po.pillId " +
            "WHERE ((po.bought  IS NULL) OR (NOT  po.bought = 0)) AND (mp.id NOT LIKe 'null') " +
            "GROUP BY mp.id " +
            "UNION " +
            "SELECT mp.id,mp.name,mp.description,mp.type,mp.doseLeft,mp.doseLeftNow,mp.dayStart,mp.dayEnd,mp.patient,mp.amount,mp.doctor,0 AS 'leftOrganizer',0 AS 'leftNowOrganizer' " +
            "FROM my_pills mp LEFT JOIN pills_organizer po ON mp.id = po.pillId " +
            "WHERE (po.bought=0 ) AND (pillId NOT IN (SELECT pillId FROM pills_organizer WHERE bought = 1)) AND (mp.id NOT LIKe 'null')  " +
            "GROUP BY mp.id ")
    fun getPillsToAllPillsFragment():Observable<List<AllPillsFragmentData>>

    @Query("UPDATE pills_organizer SET leftNow = `left`-(:doseAll-:doseLeftNow) WHERE id =(SELECT id FROM pills_organizer WHERE (pillId = :id) AND (bought=1) AND ((leftNow IS NULL) OR (leftNow>=0) ) ORDER BY id LIMIT 1 ) ")
    suspend fun updateOrganizerPillDoseLeftNow(id:String,doseLeftNow:Int,doseAll:Int)

    @Query("SELECT * FROM pills_organizer WHERE leftNow<0 AND inside IS NOT 'used' ")
    fun checkIfNegativeDoseLeftNow(): Flow<List<PillOrganizerDB>>

    @Query("UPDATE pills_organizer SET leftNow = `left`-:difference WHERE id =(SELECT id FROM pills_organizer WHERE (pillId = :idPill) AND (bought=1) AND ((leftNow IS NULL) OR (leftNow>=0) ) LIMIT 1 ) ")
    suspend fun updateOrganizerPillDoseLeftNowNegativeInOther(idPill:String,difference:Int)

    @Query("UPDATE pills_organizer SET inside = 'used' WHERE id = :id ")
    suspend fun markAsUsed(id:Int)

    @Query("UPDATE pills_organizer SET bought =:bought WHERE id = :id")
    suspend fun markAsBoughtOrNot(id:Int,bought:Boolean)


}