package com.quavii.dsige.lectura.ui.broadcast

import android.annotation.TargetApi
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.location.LocationManager
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.core.app.NotificationCompat
import com.quavii.dsige.lectura.R
import com.quavii.dsige.lectura.data.apiServices.ApiServices
import com.quavii.dsige.lectura.data.apiServices.ConexionRetrofit
import com.quavii.dsige.lectura.data.model.EstadoOperario
import com.quavii.dsige.lectura.data.model.Mensaje
import com.quavii.dsige.lectura.helper.Gps
import com.quavii.dsige.lectura.helper.Util
import com.google.gson.Gson
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UbicationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val tipo = intent.getIntExtra("tipo", 0)
        if (tipo == 1) {
            val gps = Gps(context)
            if (gps.isLocationEnabled()) {
                if (gps.latitude.toString() != "0.0" || gps.longitude.toString() != "0.0") {
                    val latitud = gps.latitude.toString()
                    val longitud = gps.longitude.toString()
                    val usuarioId = intent.getIntExtra("usuarioId", 0)
                    SyncData().execute(usuarioId.toString(), latitud, longitud)
//                    val servicesInterface = ConexionRetrofit.api.create(ApiServices::class.java)
//                    sendGps(servicesInterface, usuarioId, latitud, longitud)
                }
            } else {
                notificationGps(context)
            }
        }
    }

    private fun sendGps(servicesInterface: ApiServices, operarioId: Int, latitud: String, longitud: String) {
        val estadoOperario = EstadoOperario(operarioId, latitud, longitud, "", Util.getFechaActual())
        val sendRegistro = Gson().toJson(estadoOperario)
        val requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), sendRegistro)
        val mensajeCall = servicesInterface.saveOperarioGps(requestBody)
        mensajeCall.enqueue(object : Callback<Mensaje> {
            override fun onFailure(call: Call<Mensaje>?, t: Throwable?) {
            }

            override fun onResponse(call: Call<Mensaje>?, response: Response<Mensaje>?) {
                Log.i("TAG", "Gps Enviado")
            }
        })
    }

    private class SyncData : AsyncTask<String, Void, String>() {
        override fun doInBackground(vararg strings: String): String? {
            val operarioId = strings[0]
            val latitud = strings[1]
            val longitud = strings[2]

            val servicesInterface = ConexionRetrofit.api.create(ApiServices::class.java)
            val estadoOperario = EstadoOperario(operarioId.toInt(), latitud, longitud, "", Util.getFechaActual())
            val send = Gson().toJson(estadoOperario)
            val requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), send)
            val mensajeCall = servicesInterface.saveOperarioGps(requestBody)
            mensajeCall.enqueue(object : Callback<Mensaje> {
                override fun onFailure(call: Call<Mensaje>?, t: Throwable?) {
                }

                override fun onResponse(call: Call<Mensaje>?, response: Response<Mensaje>?) {
                    Log.i("TAG", "GPS ENVIADO")
                }
            })

            publishProgress()
            return "Ok"
        }
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

    private fun notificationGps(context: Context) {
        val manager: LocationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT)
            val nBuilder = getBasicNotificationBuilder(context, CHANNEL_ID_TIMER, false)
            nBuilder.setContentTitle("Mensaje Gps")
                    .setContentText("Gps desactivado, necesitas activarlo para poder continuar \n con el servicio")
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
            val mMediaPlayer = MediaPlayer.create(context, R.raw.ic_error)
            mMediaPlayer.start()

            val nManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nManager.createNotificationChannel(CHANNEL_ID_TIMER, CHANNEL_NAME_TIMER, true)
            nManager.notify(TIMER_ID, nBuilder.build())
        }
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