package com.quavii.dsige.lectura.ui.services

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.*
import android.content.Intent
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.net.Uri
import android.os.*
import android.util.Log
import androidx.core.app.NotificationCompat
import com.quavii.dsige.lectura.R
import com.quavii.dsige.lectura.helper.Util
import com.quavii.dsige.lectura.ui.activities.RepartoReciboFormActivity
import com.quavii.dsige.lectura.ui.activities.SuministroFormRepartoActivity
import java.lang.RuntimeException
import java.util.*

class AlertRepartoSleepService : Service() {

    private val timer = Timer()

    override fun onBind(intent: Intent): IBinder? {
        Log.i("service", "Close AlertRepartoSleepService2")
        return null
    }

    override fun onCreate() {
        super.onCreate()
        stopService(Intent(this, DistanceService::class.java))
        Log.i("service", "Open AlertRepartoSleepService")
    }

    override fun onDestroy() {
        timer.cancel()
        super.onDestroy()
        Log.i("service", "Close AlertRepartoSleepService")
    }

    private val toastHandler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            Util.clearNotification(applicationContext)
            val bundle = msg.data
            val ordenReparto = bundle.getString("Cod_Orden_Reparto")!!
            val repartoId = bundle.getInt("id_cab_Reparto")
            val direction = bundle.getString("direction")!!
            val suministroNumeroReparto = bundle.getString("suministroNumeroReparto")!!
            val foto = bundle.getInt("foto")
            val operarioId = bundle.getInt("operarioId")
            val cliente = bundle.getString("cliente")!!
            val registroId = bundle.getInt("registroId")
            notificationReparto(this@AlertRepartoSleepService, ordenReparto, repartoId, direction, suministroNumeroReparto, foto, operarioId, cliente, registroId)
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val bundle = intent.extras
        if (bundle != null) {
            timer.scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    try {
                        val msg: Message? = toastHandler.obtainMessage()
                        val b = Bundle()
                        b.putString("Cod_Orden_Reparto", bundle.getString("Cod_Orden_Reparto"))
                        b.putInt("id_cab_Reparto", bundle.getInt("id_cab_Reparto"))
                        b.putString("direction", bundle.getString("direction")!!)
                        b.putString("suministroNumeroReparto", bundle.getString("suministroNumeroReparto"))
                        b.putInt("foto", bundle.getInt("foto"))
                        b.putInt("operarioId", bundle.getInt("operarioId"))
                        b.putString("cliente", bundle.getString("cliente"))
                        b.putInt("registroId", bundle.getInt("registroId"))
                        msg?.data = b
                        toastHandler.sendMessage(msg)
                    } catch (e: RuntimeException) {
                        Log.e("TAG", e.toString())
                        stopSelf()
                    }
                }
            }, 0, 3000)
        }
        return START_STICKY
    }

    private fun notificationReparto(context: Context,
                                    ordenReparto: String,
                                    repartoId: Int,
                                    direction: String,
                                    suministroNumeroReparto: String,
                                    foto: Int,
                                    operarioId: Int,
                                    cliente: String,
                                    registroId: Int) {

        val intent: Intent = if (foto == 2) {
            Intent(context, SuministroFormRepartoActivity::class.java)
                    .putExtra("Cod_Orden_Reparto", ordenReparto)
                    .putExtra("id_cab_Reparto", repartoId)
                    .putExtra("suministroNumeroReparto", suministroNumeroReparto)
        } else {
            Intent(context, RepartoReciboFormActivity::class.java)
                    .putExtra("repartoId", registroId)
                    .putExtra("recibo", suministroNumeroReparto)
                    .putExtra("operarioId", operarioId)
                    .putExtra("cliente", cliente)
                    .putExtra("validation", foto)
        }

        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val nBuilder = getBasicNotificationBuilder(context, CHANNEL_ID_TIMER, true)
        nBuilder.setContentTitle(String.format("Cuenta Contrato : %s", suministroNumeroReparto))
                .setContentText(direction)
                .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
        //   val mMediaPlayer = MediaPlayer.create(context, R.raw.ic_error)
        //   mMediaPlayer.start()
        val nManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nManager.createNotificationChannel(CHANNEL_ID_TIMER, CHANNEL_NAME_TIMER, true)
        nManager.notify(1, nBuilder.build())
//        (Date().time / 1000L % Integer.MAX_VALUE).toInt()
    }

    private fun getBasicNotificationBuilder(context: Context, channelId: String, playSound: Boolean)
            : NotificationCompat.Builder {
        val notificationSound: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val nBuilder = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher))
                .setAutoCancel(true)
                .setDefaults(0)
        if (playSound) nBuilder.setSound(notificationSound)
        return nBuilder
    }

    @TargetApi(26)
    private fun NotificationManager.createNotificationChannel(
            channelID: String, channelName: String, playSound: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelImportance = if (playSound) NotificationManager.IMPORTANCE_DEFAULT
            else NotificationManager.IMPORTANCE_LOW
            val nChannel = NotificationChannel(channelID, channelName, channelImportance)
            nChannel.enableLights(true)
            nChannel.lightColor = Color.BLUE
            this.createNotificationChannel(nChannel)
        }
    }

    companion object {
        private const val CHANNEL_ID_TIMER = "reparto"
        private const val CHANNEL_NAME_TIMER = "Dsige_Reparto"
    }
}