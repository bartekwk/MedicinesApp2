package com.example.medicinesapp.utill.workManager

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.medicinesapp.R

class AlarmReceiver:BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {

        val result = p1?.getStringExtra("KEY_ALARM")

        Log.d("1", "OTO ON3 $result")

        if (p1?.action == "PUT_ALARM") {

            if (result != null && p0 != null) {
                val split = result.split("&AND&")
                val message = split.first()
                val id = split.last().toInt()
                newMessageComing(message, p0,id)
            }
        }
    }

    companion object {
         fun newMessageComing(message: String, context: Context,id:Int) {

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val notificationChannel =
                    NotificationChannel("CHANNEL", "ABCD", NotificationManager.IMPORTANCE_HIGH)
                notificationChannel.enableLights(true)
                notificationChannel.lightColor = Color.BLUE
                notificationChannel.enableVibration(true)
                notificationChannel.description = "Powiadomienie o wzięciu leków z twojej apteczki"
                notificationManager.createNotificationChannel(notificationChannel)
            }

            val notificationBuilder = NotificationCompat.Builder(context, "CHANNEL")
                .setContentTitle("Weź lekarstwo").setContentText(message)
                .setSmallIcon(R.drawable.ic_pill)

            notificationManager.notify(id, notificationBuilder.build())
        }
    }
}