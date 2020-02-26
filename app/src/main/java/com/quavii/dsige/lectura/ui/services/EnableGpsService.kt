package com.quavii.dsige.lectura.ui.services

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.quavii.dsige.lectura.ui.broadcast.OtherReceiver

class EnableGpsService : Service() {

    override fun onBind(intent: Intent): IBinder? {
        Log.i("service", "Close EnableGpsService2")
        return null
    }

    override fun onCreate() {
        super.onCreate()
        Log.i("service", "Open EnableGpsService")
    }

    override fun onDestroy() {
        val am = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val i = Intent(this, OtherReceiver::class.java).putExtra("tipo", 0)
        val pi = PendingIntent.getBroadcast(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT)
        pi.cancel()
        am.cancel(pi)
        super.onDestroy()
        Log.i("service", "Close EnableGpsService")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val am = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val i = Intent(this, OtherReceiver::class.java).putExtra("tipo", 1)
        val pi = PendingIntent.getBroadcast(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT)
        am.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 10000, pi)
        return START_STICKY
    }
}