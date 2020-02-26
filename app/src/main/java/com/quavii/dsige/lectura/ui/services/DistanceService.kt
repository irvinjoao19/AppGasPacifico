package com.quavii.dsige.lectura.ui.services

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.location.Location
import android.media.RingtoneManager
import android.net.Uri
import android.os.*
import android.util.Log
import android.view.Gravity
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.quavii.dsige.lectura.R
import com.quavii.dsige.lectura.data.dao.interfaces.RegistroImplementation
import com.quavii.dsige.lectura.data.dao.interfaces.SuministroImplementation
import com.quavii.dsige.lectura.data.dao.overMethod.RegistroOver
import com.quavii.dsige.lectura.data.dao.overMethod.SuministroOver
import com.quavii.dsige.lectura.data.model.SuministroReparto
import com.quavii.dsige.lectura.helper.Gps
import com.quavii.dsige.lectura.helper.Util
import com.quavii.dsige.lectura.ui.activities.MainActivity
import io.reactivex.Completable
import io.reactivex.CompletableObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import io.realm.RealmResults
import java.lang.Exception
import java.text.DecimalFormat
import java.util.*
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class DistanceService : Service() {

    private val timer = Timer()

    override fun onBind(intent: Intent): IBinder? {
        Log.i("service", "Close DistanceService2")
        return null
    }

    override fun onCreate() {
        Log.i("service", "Iniciando DistanceService")
        super.onCreate()
    }

    private fun startService() {
        stopService(Intent(this, AlertRepartoSleepService::class.java))
        timer.scheduleAtFixedRate(mainTask(), 0, 2000)
    }

    private inner class mainTask : TimerTask() {
        override fun run() {
            toastHandler.sendEmptyMessage(0)
        }
    }

    private val toastHandler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            val gps = Gps(this@DistanceService)
            if (gps.isLocationEnabled()) {
                if (gps.latitude.toString() != "0.0" || gps.longitude.toString() != "0.0") {
                    distance(this@DistanceService, gps.latitude.toString(), gps.longitude.toString())
                    val toast = Toast.makeText(applicationContext, "Rango SuministroReparto", Toast.LENGTH_SHORT)
                    toast.setGravity(Gravity.TOP, Gravity.CENTER, 0)
                    toast.show()
                } else {
                    Util.toastMensaje(applicationContext, "Gps Sin Cobertura")
                }
            }
        }
    }

    override fun onDestroy() {
        timer.cancel()
        Log.i("service", "Close DistanceService")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startService()
        return START_STICKY
    }

    private fun distance(context: Context, latitud: String, longitud: String) {
        Completable.fromAction {
            Realm.getDefaultInstance().use { realm ->
                val suministroImp: SuministroImplementation = SuministroOver(realm)
                val registroImp: RegistroImplementation = RegistroOver(realm)

                val result: RealmResults<SuministroReparto>? = suministroImp.getSuministroReparto(1)
                if (result != null) {
                    if (result.size != 0) {
                        for (r: SuministroReparto in result) {
                            if (r.latitud.isNotEmpty() && r.longitud.isNotEmpty()) {
                                val l1 = Location("location 1")
                                l1.latitude = r.latitud.toDouble()
                                l1.longitude = r.longitud.toDouble()
                                val l2 = Location("location 2")
                                l2.latitude = latitud.toDouble()
                                l2.longitude = longitud.toDouble()

                                try {
                                    val distancia = calculationByDistance(l1, l2)
                                    if (distancia <= 30) {
                                        if (r.foto_Reparto != 0) {
                                            val repartoId = registroImp.getRegistroIdentity()
                                            if (r.foto_Reparto != 2) {
                                                suministroImp.repartoSaveService(registroImp.getRegistroIdentity(), r.id_Reparto, r.id_Operario_Reparto, Util.getFechaActual(), latitud, longitud, r.id_observacion.toString(), 0)
                                            }
                                            context.startService(Intent(context, AlertRepartoSleepService::class.java)
                                                    .putExtra("Cod_Orden_Reparto", r.Cod_Orden_Reparto)
                                                    .putExtra("id_cab_Reparto", r.id_Reparto)
                                                    .putExtra("direction", r.Direccion_Reparto)
                                                    .putExtra("suministroNumeroReparto", r.Suministro_Numero_reparto)
                                                    .putExtra("foto", r.foto_Reparto)
                                                    .putExtra("operarioId", r.id_Operario_Reparto)
                                                    .putExtra("cliente", r.Cliente_Reparto)
                                                    .putExtra("registroId", repartoId))
                                            stopSelf()
                                            break
                                        } else {
                                            suministroImp.repartoSaveService(registroImp.getRegistroIdentity(), r.id_Reparto, r.id_Operario_Reparto, Util.getFechaActual(), latitud, longitud, r.id_observacion.toString(), 1)
                                        }
                                    }
                                } catch (e: Exception) {
                                    Util.toastMensaje(applicationContext, e.toString())
                                }
                            }
                        }
                    } else {
                        notification(this@DistanceService)
                    }
                }
            }
        }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : CompletableObserver {
                    override fun onComplete() {
                        Log.i("TAG", "REPARTO")
                    }

                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onError(e: Throwable) {
                        Log.i("TAG", e.toString())
                    }
                })
    }

    private fun calculationByDistance(StartP: Location, EndP: Location): Double {
        val radius = 6371 * 1000  // radius of earth in Km * meters
        val lat1 = StartP.latitude
        val lat2 = EndP.latitude
        val lon1 = StartP.longitude
        val lon2 = EndP.longitude
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) + (cos(Math.toRadians(lat1))
                * cos(Math.toRadians(lat2)) * sin(dLon / 2)
                * sin(dLon / 2))
        val c = 2 * asin(sqrt(a))
        val valueResult = radius * c
        val km = valueResult / 1
        val newFormat = DecimalFormat("####")
        val kmInDec = Integer.valueOf(newFormat.format(km))
        return kmInDec.toDouble()
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

    private fun notification(context: Context) {
        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val nBuilder = getBasicNotificationBuilder(context, CHANNEL_ID_TIMER, true)
        nBuilder.setContentTitle(String.format("Reparto"))
                .setContentText("Acabas de culminar tus repartos . Tomar selfie FIN DE TRABAJO")
                .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
        val nManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nManager.createNotificationChannel(CHANNEL_ID_TIMER, CHANNEL_NAME_TIMER, true)
        nManager.notify(1, nBuilder.build())
    }

    @TargetApi(26)
    private fun NotificationManager.createNotificationChannel(channelID: String,
                                                              channelName: String,
                                                              playSound: Boolean) {
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
        private const val CHANNEL_ID_TIMER = "enable_gps"
        private const val CHANNEL_NAME_TIMER = "Dsige_Enable_Gps"
        private const val TIMER_ID = 0
    }
}