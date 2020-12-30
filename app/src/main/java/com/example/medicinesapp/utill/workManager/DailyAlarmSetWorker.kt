 package com.example.medicinesapp.utill.workManager

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.medicinesapp.db.AppPreferences
import com.example.medicinesapp.repository.AppRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class DailyAlarmSetWorker(private val context: Context, params: WorkerParameters,
                          private val alarmManager: AlarmManager,
                          private val repository: AppRepository): CoroutineWorker(context,params) {

    override suspend fun doWork()= withContext(Dispatchers.IO){


        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm")
        val newCalendar = Calendar.getInstance()

        val timeNow = sdf.format(newCalendar.time)
        val splitStart = timeNow.split(' ')
        val dateStart = splitStart.first()
        val hourStart = splitStart.last()

        val startOut = "$dateStart $hourStart"

        newCalendar.add(Calendar.MINUTE,15)

        val timeEnd = sdf.format(newCalendar.time)
        val splitEnd = timeEnd.split(' ')
        val dateEnd = splitEnd.first()
        val hourEnd = splitEnd.last()

        val endOut = "$dateEnd $hourEnd"


        val pillsResult = repository.getPillBetweenDates(startOut,endOut)
        var count = AppPreferences.counter
        //var count = 0

        pillsResult.groupBy{it.time}.forEach { (_, value) ->

            val first = value.first()
            newCalendar.time = sdf.parse("${first.date} ${first.time}")!!
            val timeFromCal = newCalendar.timeInMillis

            var info:String=""
            value.forEach {
                info = "${it.name} ${it.amount}\n"
                repository.updatePillDone(it.id)
            }

            val valueOut = "$info&AND&$count"

            Log.d("1", "WITOM 2 $info $timeFromCal $count")

            val intent = Intent(context, AlarmReceiver::class.java)
            intent.action = "PUT_ALARM"
            intent.putExtra("KEY_ALARM", valueOut)
            val pendingIntent =
                PendingIntent.getBroadcast(context, count, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeFromCal, pendingIntent)

            count++
        }
        AppPreferences.counter = count


        Log.d("1", "WITOM CIE E ")



        Result.success()
    }

}
