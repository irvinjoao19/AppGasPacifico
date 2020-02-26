package com.quavii.dsige.lectura.ui.services

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.util.Log
import com.quavii.dsige.lectura.data.apiServices.ApiServices
import com.quavii.dsige.lectura.data.dao.interfaces.LoginImplementation
import com.quavii.dsige.lectura.data.dao.interfaces.ParametroImplementation
import com.quavii.dsige.lectura.data.dao.interfaces.SincronizarImplementation
import com.quavii.dsige.lectura.data.dao.overMethod.LoginOver
import com.quavii.dsige.lectura.data.dao.overMethod.ParametroOver
import com.quavii.dsige.lectura.data.dao.overMethod.SincronizarOver
import com.quavii.dsige.lectura.data.apiServices.ConexionRetrofit
import com.quavii.dsige.lectura.data.model.Sincronizar
import com.quavii.dsige.lectura.ui.broadcast.SyncReceiver
import io.realm.Realm
import retrofit2.Call
import retrofit2.Response

class SyncCortesReconexionesService : Service() {

    override fun onBind(p0: Intent?): IBinder? {
        Log.i("service", "Close SyncCortesReconexionesService2")
        return null
    }

    override fun onCreate() {
        Log.i("service", "Open SyncCortesReconexionesService")
        super.onCreate()
    }

    override fun onDestroy() {
        super.onDestroy()
        val am = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val i = Intent(this, SyncReceiver::class.java).putExtra("tipo", 0)
        val pi = PendingIntent.getBroadcast(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT)
        pi.cancel()
        am.cancel(pi)
        Log.i("service", "Close SyncCortesReconexionesService")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val realm = Realm.getDefaultInstance()
        val loginImp: LoginImplementation = LoginOver(realm)
        val usuario = loginImp.login
        if (usuario != null) {
            val am = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val i = Intent(this, SyncReceiver::class.java)
                    .putExtra("usuarioId", usuario.iD_Operario)
                    .putExtra("tipo", 1)
            val pi = PendingIntent.getBroadcast(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT)
            am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 60000, pi)
        }

        return START_STICKY
    }
}
