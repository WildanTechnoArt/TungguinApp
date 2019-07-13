package com.hyperdev.tungguin.fcm

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.hyperdev.tungguin.R
import com.hyperdev.tungguin.database.SharedPrefManager
import com.hyperdev.tungguin.ui.activity.HistoryActivity
import android.os.Build
import androidx.core.app.NotificationCompat
import com.hyperdev.tungguin.ui.activity.ChatActivity
import com.hyperdev.tungguin.ui.activity.DetailOrderActivity
import com.hyperdev.tungguin.ui.activity.TestimoniActivity

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {

        // Membuat Click Listener pada Notifikasi yang Muncul
        val data: MutableMap<String, String>? = remoteMessage?.data
        val type = data?.get("type").toString()
        val typeId = data?.get("type_id").toString()
        val title = remoteMessage?.notification?.title.toString()
        val message = remoteMessage?.notification?.body.toString()

        when (type) {
            "top_up_success" -> {
                val intent = Intent(this, HistoryActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                notificationProperties(title, message, intent)
            }
            "top_up_expired" -> {
                val intent = Intent(this, HistoryActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                notificationProperties(title, message, intent)
            }
            "order_finish" -> {
                val intent = Intent(this, TestimoniActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intent.putExtra("sendOrderID", typeId)
                notificationProperties(title, message, intent)
            }
            "payment_order_success" -> {
                val intent = Intent(this, DetailOrderActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intent.putExtra("sendOrderID", typeId)
                notificationProperties(title, message, intent)
            }
            "payment_order_expired" -> {
                val intent = Intent(this, DetailOrderActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intent.putExtra("sendOrderID", typeId)
                notificationProperties(title, message, intent)
            }
            "new_order_message" -> {
                val intent = Intent(this, ChatActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intent.putExtra("sendOrderID", typeId)
                notificationProperties(title, message, intent)
            }
            "designerFound" -> {
                val intent = Intent(this, DetailOrderActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intent.putExtra("sendOrderID", typeId)
                notificationProperties(title, message, intent)
            }
        }
    }

    private fun notificationProperties(title: String, message: String, intent: Intent) {

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_ONE_SHOT
        )
        val channelId = "Default"
        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setDefaults(Notification.DEFAULT_ALL)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Default channel", NotificationManager.IMPORTANCE_DEFAULT)
            manager.createNotificationChannel(channel)
        }
        manager.notify(0, builder.build())
    }

    //Method untuk mendapatkan Token Baru
    override fun onNewToken(token: String?) {
        super.onNewToken(token)
        //Mendapatkan Token dari FCM untuk klien
        Log.d(TAG, "Token Saya : " + token!!)
        storeToken(token)
    }

    companion object {
        private val TAG = MyFirebaseMessagingService::class.java.simpleName
    }

    //Mengirim dan menyimpan Token pada SharedPreference
    private fun storeToken(refreshedToken: String?) {
        SharedPrefManager.getInstance(this).storeTokenFCM(refreshedToken.toString())
    }
}