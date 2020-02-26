package com.quavii.dsige.lectura.ui.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.quavii.dsige.lectura.data.apiServices.ApiServices
import com.quavii.dsige.lectura.data.apiServices.ConexionRetrofit
import com.quavii.dsige.lectura.data.dao.interfaces.SincronizarImplementation
import com.quavii.dsige.lectura.data.dao.overMethod.SincronizarOver
import com.quavii.dsige.lectura.data.model.Sincronizar
import io.realm.Realm
import retrofit2.Call
import retrofit2.Response

class SyncReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val tipo = intent.getIntExtra("tipo", 0)
        if (tipo == 1) {
            val realm = Realm.getDefaultInstance()
            val servicesInterface = ConexionRetrofit.api.create(ApiServices::class.java)
            val sincronizarImp: SincronizarImplementation = SincronizarOver(realm)
            val usuario = intent.getIntExtra("usuarioId", 0)
            sync(servicesInterface, usuario, sincronizarImp)
        }
    }

    private fun sync(servicesInterfaces: ApiServices, operarioId: Int, sincronizarImp: SincronizarImplementation) {
        val sincronizarCall = servicesInterfaces.syncCorteReconexion(operarioId)
        sincronizarCall.enqueue(object : retrofit2.Callback<Sincronizar> {
            override fun onFailure(call: Call<Sincronizar>?, t: Throwable?) {
                Log.e("TAG", "NO SINCRONIZO")
            }

            override fun onResponse(call: Call<Sincronizar>?, response: Response<Sincronizar>?) {
                val sincronizar = response?.body()
                if (sincronizar != null) {
                    sincronizarImp.save(sincronizar, 0)
                    Log.e("TAG", "SINCRONIZO")
                }
            }
        })
    }
}