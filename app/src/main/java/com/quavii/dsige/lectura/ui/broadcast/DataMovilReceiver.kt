package com.quavii.dsige.lectura.ui.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.BatteryManager
import android.provider.Settings
import android.util.Log
import com.quavii.dsige.lectura.data.apiServices.ApiServices
import com.quavii.dsige.lectura.data.apiServices.ConexionRetrofit
import com.quavii.dsige.lectura.data.model.EstadoMovil
import com.quavii.dsige.lectura.data.model.Mensaje
import com.quavii.dsige.lectura.helper.Gps
import com.quavii.dsige.lectura.helper.Util
import com.google.gson.Gson
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DataMovilReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val tipo = intent.getIntExtra("tipo", 0)
        if (tipo == 1) {
            val usuarioId = intent.getIntExtra("usuarioId", 0)
            val servicesInterface = ConexionRetrofit.api.create(ApiServices::class.java)
            movil(context, servicesInterface, usuarioId)
        }
    }

    private fun movil(context: Context, servicesInterface: ApiServices, operarioId: Int) {
        val ifilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val batteryStatus: Intent = context.registerReceiver(null, ifilter)!!
        val level: Int = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1)

        val batteryPct: Int = level

        val gps = Gps(context)
        val gpsActivo = if (gps.isLocationEnabled()) 1 else 0
        val modoAvion = if (Settings.System.getInt(context.contentResolver, Settings.Global.AIRPLANE_MODE_ON, 0) == 0) 0 else 1

        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo

        val planDatos = if (activeNetwork != null) {
            if (activeNetwork.type == ConnectivityManager.TYPE_MOBILE || activeNetwork.isConnected) 1 else 0
        } else {
            0
        }

        val operario = EstadoMovil(operarioId, gpsActivo, batteryPct, Util.getFechaActual(), modoAvion, planDatos)
        val sendRegistro = Gson().toJson(operario)
        val requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), sendRegistro)
        val mensajeCall = servicesInterface.saveEstadoMovil(requestBody)
        mensajeCall.enqueue(object : Callback<Mensaje> {
            override fun onFailure(call: Call<Mensaje>?, t: Throwable?) {
                Log.e("TAG", "NO SINCRONIZO")
            }

            override fun onResponse(call: Call<Mensaje>?, response: Response<Mensaje>?) {
                Log.i("TAG", "Enviado")
            }
        })
    }
}