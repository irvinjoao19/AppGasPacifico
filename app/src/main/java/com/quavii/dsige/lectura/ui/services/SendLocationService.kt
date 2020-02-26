package com.quavii.dsige.lectura.ui.services

import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.quavii.dsige.lectura.data.dao.interfaces.LoginImplementation
import com.quavii.dsige.lectura.data.dao.interfaces.ParametroImplementation
import com.quavii.dsige.lectura.data.dao.overMethod.LoginOver
import com.quavii.dsige.lectura.data.dao.overMethod.ParametroOver
import io.realm.Realm
import android.app.AlarmManager
import com.quavii.dsige.lectura.ui.broadcast.UbicationReceiver

class SendLocationService : Service() {

    override fun onBind(p0: Intent?): IBinder? {
        Log.i("service", "Close SendLocationService2")
        return null
    }

    override fun onCreate() {
        Log.i("service", "Open SendLocationService")
        super.onCreate()
    }

    override fun onDestroy() {
        super.onDestroy()
        val am = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val i = Intent(this, UbicationReceiver::class.java).putExtra("tipo", 0)
        val pi = PendingIntent.getBroadcast(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT)
        pi.cancel()
        am.cancel(pi)
        Log.i("service", "Close SendLocationService")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val realm = Realm.getDefaultInstance()
        val parametroImp: ParametroImplementation = ParametroOver(realm)
        val loginImp: LoginImplementation = LoginOver(realm)
        val usuario = loginImp.login
        val ubicacion = parametroImp.getParametroById(1)

        if (ubicacion != null) {
            if (usuario != null) {
                val am = getSystemService(Context.ALARM_SERVICE) as AlarmManager
                val i = Intent(this, UbicationReceiver::class.java)
                        .putExtra("usuarioId", usuario.iD_Operario)
                        .putExtra("tipo", 1)
                val pi = PendingIntent.getBroadcast(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT)
                am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), ubicacion.valor.toLong() * 1000, pi)
            }
        }
        return START_STICKY
    }
}