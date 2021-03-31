package com.quavii.dsige.lectura.ui.services

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.util.Log
import com.google.gson.Gson
import com.quavii.dsige.lectura.data.apiServices.ApiServices
import com.quavii.dsige.lectura.data.apiServices.ConexionRetrofit
import com.quavii.dsige.lectura.data.dao.interfaces.RegistroImplementation
import com.quavii.dsige.lectura.data.dao.overMethod.RegistroOver
import com.quavii.dsige.lectura.data.model.Mensaje
import com.quavii.dsige.lectura.data.model.Photo
import com.quavii.dsige.lectura.data.model.Registro
import com.quavii.dsige.lectura.helper.Util
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
import java.util.*
import java.util.concurrent.TimeUnit

class SendRepartoServices : Service() {

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
        timer.scheduleAtFixedRate(mainTask(), 0,   10 * 60 * 1000L /* 10 minutos*/)
    }

    private inner class mainTask : TimerTask() {
        override fun run() {
            toastHandler.sendEmptyMessage(0)
        }
    }

    private val toastHandler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            val realm = Realm.getDefaultInstance()
            val registroImp = RegistroOver(realm)
            val sendInterfaces = ConexionRetrofit.api.create(ApiServices::class.java)
            sendDataRx(this@SendRepartoServices,registroImp, sendInterfaces)
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

    private fun sendDataRx(context:Context,registroImp: RegistroImplementation, sendInterfaces: ApiServices) {
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
                        Log.i("TAG",  "ENVIO REPARTO")
                    }

                    override fun onError(e: Throwable) {
                        Log.i("TAG", e.message.toString())
                    }

                    override fun onComplete() {

                    }
                })
    }
}