package com.example.medicinesapp.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.medicinesapp.data.*
import io.reactivex.Observable
import io.reactivex.Single
import kotlinx.coroutines.flow.Flow

@Dao
interface PillTimeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(pillTimeDB: PillTimeDB)

    @Query("SELECT * FROM pills_time")
    fun getPills(): Observable<List<PillTimeDB>>

    @Query("delete from pills_time where id=:id")
    suspend fun deleteOnePill(id:Int)

    @Query("SELECT name,description,doctor,date,time,amount,type FROM pills_time p1 INNER JOIN my_pills p2 ON p1.pillId=p2.id WHERE date = DATE(:date) ORDER BY time")
    fun getDailyPill(date:String):Observable<List<DailyPill>>

    @Query("SELECT name,description,doctor,date,time,amount,type FROM pills_time p1 INNER JOIN my_pills p2 ON p1.pillId=p2.id WHERE pillId =:id ORDER BY time")
    fun getAllPillsByID(id:String):Observable<List<DailyPill>>

    @Query("SELECT mp.id,mp.doseLeft,COUNT(*) as 'amount',mp.amount as 'dose' FROM my_pills mp JOIN pills_time pt ON pt.pillId = mp.id WHERE (date>:date) OR ((date = :date) AND  time> :time) GROUP BY mp.id UNION SELECT mp.id,mp.doseLeft,0 as 'amount',mp.amount as 'dose' FROM my_pills mp WHERE mp.id NOT IN (SELECT mp.id FROM my_pills mp JOIN pills_time pt ON pt.pillId = mp.id WHERE (date> :date) OR ((date = :date) AND  time> :time) GROUP BY mp.id)")
    fun getAllLeftDoseByIDS(date:String,time:String): Single<List<DoseLeftData>>

    @Query("SELECT pt.id AS'id',name,description,doctor,date,time,amount FROM pills_time pt JOIN my_pills mp ON pt.pillId = mp.id WHERE (DATETIME(date || ' '|| time) BETWEEN :startOut AND :endOut) AND (done IS NULL OR done = 0) ORDER BY date,time")
    suspend fun getPillBetweenDates(startOut:String,endOut:String):List<PillWorkManager>

    @Query("UPDATE pills_time SET done = 1 WHERE id = :id ")
    suspend fun updatePillDone(id:Int)

    @Query("SELECT p2.id,date,time,name,description,type,amount,dayStart,dayEnd,doctor,NULL AS startHour,NULL AS period,NULL AS listHour FROM pills_time p1 INNER JOIN my_pills p2 ON p1.pillId = p2.id WHERE (p1.date = p2.dayStart) OR (p1.date = date(p2.dayStart,'+1 day'))")
    fun getPreviousPills(): Flow<List<PrevPill>>

    @Query("SELECT COUNT(*) AS 'count',SUM(amount) AS 'sum',date,name,amount,pillId,doseLeft AS 'doseLeftNow' FROM pills_time pt JOIN my_pills mp ON pt.pillId = mp.id WHERE (pillId = :pillId) AND  ((date> :date) OR ((date = :date) AND  time> :time )) GROUP BY date,pillId")
    suspend fun getPillsToChart(pillId:String,date:String,time:String):List<PillChart>

    @Query("SELECT pt.id AS'id',name,description,doctor,date,time,amount FROM my_pills mp JOIN pills_time pt ON mp.id = pt.pillId WHERE (DATETIME(date || ' '|| time) > :date ) ORDER BY date,time LIMIT 1 ")
    suspend fun getClosestPill(date:String):PillWorkManager



}