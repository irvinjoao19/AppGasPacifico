package com.quavii.dsige.lectura.ui.services

import android.app.AlarmManager
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
import com.quavii.dsige.lectura.ui.broadcast.DataMovilReceiver
import io.realm.Realm

class SendDataMovilService : Service() {

    override fun onBind(intent: Intent): IBinder? {
        Log.i("service", "Close SendDataMovilService2")
        return null
    }

    override fun onCreate() {
        super.onCreate()
        Log.i("service", "Open SendDataMovilService")
    }

    override fun onDestroy() {
        val am = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val i = Intent(this, DataMovilReceiver::class.java).putExtra("tipo", 0)
        val pi = PendingIntent.getBroadcast(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT)
        pi.cancel()
        am.cancel(pi)
        super.onDestroy()
        Log.i("service", "Close SendDataMovilService")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val realm = Realm.getDefaultInstance()
        val parametroImp: ParametroImplementation = ParametroOver(realm)
        val loginImp: LoginImplementation = LoginOver(realm)
        val usuario = loginImp.login
        val ubicacion = parametroImp.getParametroById(3)
        if (ubicacion != null) {
            if (usuario != null) {
                val am = getSystemService(Context.ALARM_SERVICE) as AlarmManager
                val i = Intent(this, DataMovilReceiver::class.java)
                        .putExtra("usuarioId", usuario.iD_Operario)
                        .putExtra("tipo", 1)
                val pi = PendingIntent.getBroadcast(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT)
                am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), ubicacion.valor.toLong() * 1000, pi)
            }
        }
        return START_STICKY
    }
}