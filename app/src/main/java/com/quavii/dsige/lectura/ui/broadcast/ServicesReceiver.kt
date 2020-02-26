package com.quavii.dsige.lectura.ui.broadcast

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.quavii.dsige.lectura.ui.services.EnableGpsService
import com.quavii.dsige.lectura.ui.services.SendDataMovilService
import com.quavii.dsige.lectura.ui.services.SendLocationService
import com.quavii.dsige.lectura.ui.services.SyncCortesReconexionesService

class ServicesReceiver : BroadcastReceiver() {

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context, intent: Intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val i2 = Intent(context, SendDataMovilService::class.java)
            i2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startForegroundService(i2)

            val i3 = Intent(context, EnableGpsService::class.java)
            i3.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startForegroundService(i3)

            val i4 = Intent(context, SendLocationService::class.java)
            i4.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startForegroundService(i4)
        } else {
            val i2 = Intent(context, SendDataMovilService::class.java)
            i2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startService(i2)

            val i3 = Intent(context, EnableGpsService::class.java)
            i3.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startService(i3)

            val i4 = Intent(context, SendLocationService::class.java)
            i4.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startService(i4)
        }
    }
}
