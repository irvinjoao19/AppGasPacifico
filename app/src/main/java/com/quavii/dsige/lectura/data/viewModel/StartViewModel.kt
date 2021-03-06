package com.quavii.dsige.lectura.data.viewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException
import com.quavii.dsige.lectura.data.apiServices.ApiServices
import com.quavii.dsige.lectura.data.apiServices.ConexionRetrofit
import com.quavii.dsige.lectura.data.dao.interfaces.*
import com.quavii.dsige.lectura.data.dao.overMethod.*
import com.quavii.dsige.lectura.data.model.*
import com.quavii.dsige.lectura.helper.MessageError
import com.quavii.dsige.lectura.helper.Util
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import io.realm.RealmResults
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Converter
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

class StartViewModel : ViewModel() {

    val mensajeError: MutableLiveData<String> = MutableLiveData()
    val mensajeSuccess: MutableLiveData<String> = MutableLiveData()

    lateinit var realm: Realm
    lateinit var photoImp: PhotoImplementation
    lateinit var registroImp: RegistroImplementation
    lateinit var suministroImp: SuministroImplementation
    lateinit var loginImp: LoginImplementation
    lateinit var servicioImp: ServicioImplementation
    lateinit var apiServices: ApiServices

    fun initialRealm() {
        realm = Realm.getDefaultInstance()
        photoImp = PhotoOver(realm)
        registroImp = RegistroOver(realm)
        suministroImp = SuministroOver(realm)
        loginImp = LoginOver(realm)
        servicioImp = ServicioOver(realm)
        apiServices = ConexionRetrofit.api.create(ApiServices::class.java)
    }

    fun setError(s: String) {
        mensajeError.value = s
    }

    val error: LiveData<String>
        get() = mensajeError

    val success: LiveData<String>
        get() = mensajeSuccess

    fun getLogin(): Login {
        return loginImp.login!!
    }

    fun getServicio(): RealmResults<Servicio> {
        return servicioImp.servicioAll
    }

    fun getGoTrabajo(usuarioId: Int, fecha: String, state: Int, name: String): Registro? {
        return registroImp.getGoTrabajo(usuarioId, fecha, state, name)
    }

    fun getLecturaOnCount(activo: Int, type: Int): Long? {
        return suministroImp.getLecturaOnCount(activo, type)
    }

    fun getLecturaReclamoOnCount(activo: Int, type: String): Long? {
        return suministroImp.getLecturaReclamoOnCount(activo, type)
    }

    fun getResultRegistro(): RealmResults<Registro> {
        return registroImp.getAllRegistro(1)
    }

    fun getResultRegistroLiveData(): LiveData<RealmResults<Registro>> {
        return registroImp.getAllRegistroLiveData(1)
    }

    fun getResultPhoto(): RealmResults<Photo> {
        return photoImp.getPhotoAll(1)
    }

    fun getPhotoAllLiveData(): LiveData<RealmResults<Photo>> {
        return photoImp.getPhotoAllLiveData(1)
    }

    fun getSuministroReparto(): RealmResults<SuministroReparto> {
        return suministroImp.getSuministroReparto(1)
    }


    fun sendSelfie(context: Context, state: Int, name: String) {
        var mensaje = ""
        val auditorias = registroImp.getSelfie(state, name)
        auditorias.flatMap { a ->
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
            Observable.zip(Observable.just(r), apiServices.sendRegistroRx(requestBody), { registro, mensaje ->
                registroImpRx.closeOneRegistro(registro, 0)
                mensaje
            })
        }.subscribeOn(Schedulers.computation())
                .delay(600, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Mensaje> {

                    override fun onSubscribe(d: Disposable) {
                        Log.i("TAG", d.toString())
                    }

                    override fun onNext(t: Mensaje) {
                        mensaje = t.mensaje
                    }

                    override fun onError(t: Throwable) {
                        if (t is HttpException) {
                            val body = t.response().errorBody()
                            val errorConverter: Converter<ResponseBody, MessageError> = ConexionRetrofit.api.responseBodyConverter(MessageError::class.java, arrayOfNulls<Annotation>(0))
                            try {
                                val error = errorConverter.convert(body!!)
                                mensajeError.postValue(error!!.Message)
                            } catch (e: IOException) {
                                e.printStackTrace()
                                mensajeError.postValue(e.toString())
                            }
                        } else {
                            mensajeError.postValue(t.toString())
                        }
                    }

                    override fun onComplete() {
                        mensajeSuccess.postValue(mensaje)
                    }
                })
    }

    override fun onCleared() {
        super.onCleared()
        realm.close()
    }


    fun getPhotoIdentity(): Int {
        return photoImp.getPhotoIdentity()
    }

    fun getRegistroIdentity(): Int {
        return registroImp.getRegistroIdentity()
    }

    fun saveZonaPeligrosa(registro: Registro) {
        registroImp.saveZonaPeligrosa(registro)
    }

}