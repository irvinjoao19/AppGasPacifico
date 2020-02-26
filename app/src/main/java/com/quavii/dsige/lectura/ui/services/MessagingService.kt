package com.quavii.dsige.lectura.ui.services

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.core.app.NotificationCompat
import android.util.Log
import com.quavii.dsige.lectura.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import android.media.MediaPlayer
import com.quavii.dsige.lectura.ui.activities.MainActivity
import java.util.*

class MessagingService : FirebaseMessagingService() {

    var GROUP_KEY_WORK_EMAIL = "com.android.example.WORK_EMAIL"

    lateinit var mediaPlayer: MediaPlayer

    override fun onNewToken(s: String) {
        super.onNewToken(s)
        Log.i("token", s)
        getSharedPreferences("TOKEN", MODE_PRIVATE).edit().putString("token", s).apply()
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        if (remoteMessage.notification != null) {
            mostrarNotificacion(remoteMessage.notification!!.title, remoteMessage.notification!!.body)
        }
    }

    private fun mostrarNotificacion(title: String?, body: String?) {

        val intent = Intent(this@MessagingService, MainActivity::class.java)
        intent.putExtra("update", "si")
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val notificationBuilder = NotificationCompat.Builder(this, "CHANNEL_ID")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setGroup(GROUP_KEY_WORK_EMAIL)
                .setContentIntent(pendingIntent)

        mediaPlayer = MediaPlayer.create(this, R.raw.notificacion)
        mediaPlayer.start()
        mediaPlayer.setOnCompletionListener { mp -> mp.release() }

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(Random().nextInt(), notificationBuilder.build())
    }
}