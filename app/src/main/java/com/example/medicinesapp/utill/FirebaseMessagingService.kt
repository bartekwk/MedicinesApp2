package com.example.medicinesapp.utill




import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.medicinesapp.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FirebaseMessagingService: FirebaseMessagingService() {

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
    }

    override fun onMessageReceived(p0: RemoteMessage) {

        Log.d("1", "MAM RECEIVE 1 ${p0.data}")

        p0.data.isNotEmpty().let{

            val message =p0.data["message"]!!

            if(message.contains("&AND&")){
                Log.d("1", "MAM RECEIVE 2 $message")
                startDeepLink(message)
            }

            else {
                newMessageComing(message)
                Log.d("1", "onMessageReceived: $it ")
            }
        }
    }

    private fun startDeepLink(message:String){

        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("medicineApp://startChat.com/$message"))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        }


    private fun newMessageComing(message:String) {

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel =
                NotificationChannel("CHANNEL", "ABCD", NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.BLUE
            notificationChannel.enableVibration(true)
            notificationChannel.description = "New message from cheese chat"
            notificationManager.createNotificationChannel(notificationChannel)
        }

        val notificationBuilder = NotificationCompat.Builder(this,"CHANNEL")
            .setContentTitle("New message").setContentText(message).setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)

        notificationManager.notify(0,notificationBuilder.build())
    }

}
