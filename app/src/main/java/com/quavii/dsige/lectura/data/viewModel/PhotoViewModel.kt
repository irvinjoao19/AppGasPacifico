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
import com.quavii.dsige.lectura.data.model.*
import com.quavii.dsige.lectura.helper.Util
import com.google.gson.Gson
import io.reactivex.CompletableObserver
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import io.realm.RealmResults
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.util.concurrent.TimeUnit

class PhotoViewModel : ViewModel() {

    val mensajeError: MutableLiveData<String> = MutableLiveData()
    val mensajeSuccess: MutableLiveData<String> = MutableLiveData()
    val mensajeDialog: MutableLiveData<String> = MutableLiveData()

    lateinit var realm: Realm
    lateinit var photoImp: PhotoImplementation
    lateinit var sendInterfaces: ApiServices
    lateinit var registroImp: RegistroImplementation
    lateinit var suministroImp: SuministroImplementation

    fun initialRealm() {
        realm = Realm.getDefaultInstance()
        photoImp = PhotoOver(realm)
        registroImp = RegistroOver(realm)
        suministroImp = SuministroOver(realm)
        sendInterfaces = ConexionRetrofit.api.create(ApiServices::class.java)
    }

    fun setError(s: String) {
        mensajeError.value = s
    }

    val error: LiveData<String>
        get() = mensajeError

    val success: LiveData<String>
        get() = mensajeSuccess

    val errorDialog: LiveData<String>
        get() = mensajeDialog

    fun getRegistro(orden: Int, tipo: Int): Registro {
        return registroImp.getRegistro(orden, tipo, 0)!!
    }

    fun getPhotoFirmLiveData(suministroId: Int): LiveData<RealmResults<Photo>> {
        return photoImp.getPhotoFirmLiveData(suministroId, 1)
    }

    fun getPhotoFirm(suministroId: Int): RealmResults<Photo> {
        return photoImp.getPhotoFirm(suministroId, 1)
    }


    fun validatePhoto(p: Photo): Boolean {
        insertOrUpdatePhoto(p)
        return true
    }

    fun getPhotoIdentity(): Int {
        return photoImp.getPhotoIdentity()
    }

    private fun insertOrUpdatePhoto(p: Photo) {
        photoImp.insertOrUpdatePhoto(p)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : CompletableObserver {
                    override fun onComplete() {
                        mensajeSuccess.value = "Firma Guardado"
                    }

                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onError(e: Throwable) {
                        mensajeError.value = e.toString()
                    }
                })
    }

    fun deletePhoto(p: Int) {
        photoImp.deletePhoto(p)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : CompletableObserver {
                    override fun onComplete() {
                        mensajeSuccess.value = "Firma Eliminada"
                    }

                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onError(e: Throwable) {
                        mensajeError.value = e.toString()
                    }
                })
    }

    // TODO : SIGUIENTE
    fun updateRegistroDesplaza(receive: Int, tipo: Int) {
        registroImp.updateRegistroDesplaza(receive, tipo, 1)
    }

    fun updateActivoSuministroReconexion(receive: Int) {
        suministroImp.updateActivoSuministroReconexion(receive, 0)
    }

    fun getSuministroReconexion(tipo: Int, activo: Int): RealmResults<SuministroReconexion> {
        return suministroImp.getSuministroReconexion(tipo, activo)
    }

    fun buscarReconexionesByOrden(returnOrden: Int, activo: Int): SuministroReconexion {
        return suministroImp.buscarReconexionesByOrden(returnOrden, activo)
    }

    fun sendData(suministro: String, orden: Int, tipo: Int) {
        val auditorias = registroImp.getRegistroByOrdenRx(orden, tipo)
        auditorias.flatMap { a ->
            val realm = Realm.getDefaultInstance()
            val registroImpRx: RegistroImplementation = RegistroOver(realm)
            val b = MultipartBody.Builder()
            val filePaths: ArrayList<String> = ArrayList()
            var tieneFoto = 0
            var estado = "1"

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
            b.addFormDataPart("suministro", suministro)

            val requestBody = b.build()
            Observable.zip(Observable.just(a), sendInterfaces.sendRegistroCorteRx(requestBody), BiFunction<Registro, Mensaje, Mensaje> { registro, mensaje ->
                registroImpRx.closeOneRegistro(registro, 0)
                mensaje
            })
        }.subscribeOn(Schedulers.io())
                .delay(600, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : io.reactivex.Observer<Mensaje> {

                    override fun onSubscribe(d: Disposable) {
                        Log.i("TAG", d.toString())
                    }

                    override fun onNext(t: Mensaje) {
                        mensajeSuccess.value = t.mensaje
                    }

                    override fun onError(e: Throwable) {
                        mensajeDialog.value = Util.MessageInternet

                    }

                    override fun onComplete() {
                    }
                })
    }

    override fun onCleared() {
        super.onCleared()
        realm.close()
    }
}