package com.quavii.dsige.lectura.data.viewModel

import android.os.Environment
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.quavii.dsige.lectura.data.apiServices.ApiServices
import com.quavii.dsige.lectura.data.apiServices.ConexionRetrofit
import com.quavii.dsige.lectura.data.dao.interfaces.PhotoImplementation
import com.quavii.dsige.lectura.data.dao.interfaces.RegistroImplementation
import com.quavii.dsige.lectura.data.dao.interfaces.SuministroImplementation
import com.quavii.dsige.lectura.data.dao.overMethod.PhotoOver
import com.quavii.dsige.lectura.data.dao.overMethod.RegistroOver
import com.quavii.dsige.lectura.data.dao.overMethod.SuministroOver
import com.quavii.dsige.lectura.helper.MessageError
import com.quavii.dsige.lectura.helper.Util
import com.google.gson.Gson
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException
import com.quavii.dsige.lectura.data.model.*
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
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

class EnvioViewModel : ViewModel() {

    val mensajeError: MutableLiveData<String> = MutableLiveData()
    val mensajeSuccess: MutableLiveData<String> = MutableLiveData()

    lateinit var realm: Realm
    lateinit var photoImp: PhotoImplementation
    lateinit var registroImp: RegistroImplementation
    lateinit var suministroImp: SuministroImplementation
    lateinit var apiServices: ApiServices

    fun initialRealm() {
        realm = Realm.getDefaultInstance()
        photoImp = PhotoOver(realm)
        registroImp = RegistroOver(realm)
        suministroImp = SuministroOver(realm)
        apiServices = ConexionRetrofit.api.create(ApiServices::class.java)
    }

    fun setError(s: String) {
        mensajeError.value = s
    }

    val error: LiveData<String>
        get() = mensajeError

    val success: LiveData<String>
        get() = mensajeSuccess

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

    fun sendData() {
        val auditorias = registroImp.getAllRegistroRx(1)
        auditorias.flatMap { observable ->
            Observable.fromIterable(observable).flatMap { a ->
                val realm = Realm.getDefaultInstance()
                val registroImpRx: RegistroImplementation = RegistroOver(realm)
                val b = MultipartBody.Builder()
                val filePaths: ArrayList<String> = ArrayList()
                var tieneFoto = 0
                var estado = "1"

                val recibo: RegistroRecibo? = a.recibo
                if (recibo != null) {
                    if (recibo.firmaCliente.isNotEmpty()) {
                        val file = File(Environment.getExternalStorageDirectory().toString() + "/" + Util.FolderImg + "/" + recibo.firmaCliente)
                        if (file.exists()) {
                            filePaths.add(file.toString())
                        }
                    }
                }

                for (p: Photo in a.photos!!) {
                    if (p.rutaFoto.isNotEmpty()) {
                        val file = File(Environment.getExternalStorageDirectory().toString() + "/" + Util.FolderImg + "/" + p.rutaFoto)
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
                Observable.zip(Observable.just(r), apiServices.sendRegistroRx(requestBody), BiFunction<Registro, Mensaje, Mensaje> { registro, mensaje ->
                    registroImpRx.closeOneRegistro(registro, 0)
                    mensaje
                })
            }
        }.subscribeOn(Schedulers.computation())
                .delay(600, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Mensaje> {

                    override fun onSubscribe(d: Disposable) {
                        Log.i("TAG", d.toString())
                    }

                    override fun onNext(t: Mensaje) {
                        Log.i("TAG", t.mensaje)
                    }

                    override fun onError(t: Throwable) {
                        if (t is HttpException) {
                            val body = t.response().errorBody()
                            val errorConverter: Converter<ResponseBody, MessageError> = ConexionRetrofit.api.responseBodyConverter(MessageError::class.java, arrayOfNulls<Annotation>(0))
                            try {
                                val error = errorConverter.convert(body!!)
                                mensajeError.postValue(error.Message)
                            } catch (e: IOException) {
                                mensajeError.postValue(e.toString())
                            }
                        } else {
                            mensajeError.postValue(t.toString())
                        }
                    }

                    override fun onComplete() {
                        mensajeSuccess.postValue("Registros Enviados")
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