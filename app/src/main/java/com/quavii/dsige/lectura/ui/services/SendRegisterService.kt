package com.quavii.dsige.lectura.ui.services

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Environment
import android.os.IBinder
import android.util.Log
import com.quavii.dsige.lectura.data.apiServices.ApiServices
import com.quavii.dsige.lectura.data.dao.interfaces.*
import com.quavii.dsige.lectura.data.dao.overMethod.*
import com.quavii.dsige.lectura.data.apiServices.ConexionRetrofit
import com.quavii.dsige.lectura.data.model.*
import com.quavii.dsige.lectura.helper.Util
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.util.ArrayList
import java.util.concurrent.TimeUnit

class SendRegisterService : Service() {

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        Log.i("service", "Iniciando Envio de Registros")
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val realm = Realm.getDefaultInstance()
        val registroImp = RegistroOver(realm)
        val sendInterfaces = ConexionRetrofit.api.create(ApiServices::class.java)
        sendDataRx(this@SendRegisterService, registroImp, sendInterfaces)
        return START_STICKY
    }

    private fun sendDataRx(context: Context, registroImp: RegistroImplementation, sendInterfaces: ApiServices) {
        var mensaje = ""
        val auditorias = registroImp.getAllRegistroRx(1)
        auditorias.flatMap { observable ->
            Observable.fromIterable(observable).flatMap { a ->

                val realm = Realm.getDefaultInstance()
                val registroImpRx: RegistroImplementation = RegistroOver(realm)
                val b = MultipartBody.Builder()
                val filePaths: ArrayList<String> = ArrayList()
                var tieneFoto = 0
                var estado = "1"

                for (p: Photo in a.photos!!) {
                    if (p.rutaFoto.isNotEmpty()) {
                        val file = File(Util.getFolder(context), p.rutaFoto)
                        if (file.exists()) {
                            filePaths.add(file.toString())
                            tieneFoto++
                        } else {
                            registroImpRx.closePhotoEstado(0, p)
                            estado = "0"
                        }
                    }
                }

                for (i in 0 until filePaths.size) {
                    val file = File(filePaths[i])
                    b.addFormDataPart("fotos", file.name, RequestBody.create(MediaType.parse("multipart/form-data"), file))
                }

                val r = registroImpRx.updateRegistroTienePhoto(tieneFoto, estado, a)
                val json = Gson().toJson(realm.copyFromRealm(r))
                Log.i("TAG", json)
                b.setType(MultipartBody.FORM)
                b.addFormDataPart("model", json)

                val requestBody = b.build()
                Observable.zip(Observable.just(a), sendInterfaces.sendRegistroRx(requestBody), BiFunction<Registro, Mensaje, Mensaje> { registro, mensaje ->
                    registroImpRx.closeOneRegistro(registro, 0)
                    mensaje
                })
            }
        }.subscribeOn(Schedulers.io())
                .delay(600, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Mensaje> {

                    override fun onSubscribe(d: Disposable) {
                        Log.i("TAG", d.toString())
                    }

                    override fun onNext(t: Mensaje) {
                        mensaje = t.mensaje
                    }

                    override fun onError(e: Throwable) {
                        Log.i("TAG", e.message.toString())
                    }

                    override fun onComplete() {
                        stopSelf()
                        Log.i("TAG", mensaje)
                    }
                })
    }
}