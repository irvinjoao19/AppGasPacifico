package com.quavii.dsige.lectura.data.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.quavii.dsige.lectura.data.apiServices.ApiServices
import com.quavii.dsige.lectura.data.apiServices.ConexionRetrofit
import com.quavii.dsige.lectura.data.dao.interfaces.ComboImplementation
import com.quavii.dsige.lectura.data.dao.interfaces.PhotoImplementation
import com.quavii.dsige.lectura.data.dao.interfaces.RegistroImplementation
import com.quavii.dsige.lectura.data.dao.interfaces.SuministroImplementation
import com.quavii.dsige.lectura.data.dao.overMethod.ComboOver
import com.quavii.dsige.lectura.data.dao.overMethod.PhotoOver
import com.quavii.dsige.lectura.data.dao.overMethod.RegistroOver
import com.quavii.dsige.lectura.data.dao.overMethod.SuministroOver
import com.quavii.dsige.lectura.data.model.Formato
import com.quavii.dsige.lectura.data.model.RegistroRecibo
import io.reactivex.Completable
import io.reactivex.CompletableObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import io.realm.RealmResults

class RepartoViewModel : ViewModel() {

    val mensajeError: MutableLiveData<String> = MutableLiveData()
    val mensajeSuccess: MutableLiveData<String> = MutableLiveData()

    lateinit var realm: Realm
    lateinit var photoImp: PhotoImplementation
    lateinit var sendInterfaces: ApiServices
    lateinit var registroImp: RegistroImplementation
    lateinit var suministroImp: SuministroImplementation
    lateinit var comboImp: ComboImplementation

    fun initialRealm() {
        realm = Realm.getDefaultInstance()
        comboImp = ComboOver(realm)
        photoImp = PhotoOver(realm)
        registroImp = RegistroOver(realm)
        suministroImp = SuministroOver(realm)
        sendInterfaces = ConexionRetrofit.api.create(ApiServices::class.java)
    }

    fun setError(s: String) {
        mensajeError.value = s
    }

    fun getFormato(tipo: Int): RealmResults<Formato> {
        return comboImp.getFormato(tipo)
    }

    fun getRegistroByFk(id: Int): RegistroRecibo? {
        return registroImp.getRegistroByFk(id)
    }

    fun getRegistroReciboIdentity(): Int {
        return registroImp.getRegistroReciboIdentity()
    }

    fun getUpdateRegistro(repartoId: Int, firm: String) {
        registroImp.getUpdateRegistro(repartoId, firm)
        mensajeSuccess.value = "Datos Generales Guardados"
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(object : CompletableObserver {
//                    override fun onComplete() {
//                        mensajeSuccess.value = "Datos Generales Guardados"
//                    }
//
//                    override fun onSubscribe(d: Disposable) {
//
//                    }
//
//                    override fun onError(e: Throwable) {
//                        mensajeError.value = e.toString()
//                    }
//                })
    }

    fun getRegistroRecibidoAll(repartoId: Int): RealmResults<RegistroRecibo> {
        return registroImp.getRegistroRecibidoAll(repartoId)
    }

    fun getRegistroRecibidoAllLiveData(repartoId: Int): LiveData<RealmResults<RegistroRecibo>> {
        return registroImp.getRegistroRecibidoAllLiveData(repartoId)
    }


    fun validateRegistroRecibo(r: RegistroRecibo, validation: Int): Boolean {

        if (r.piso == 0) {
            mensajeError.value = "Ingrese Nro Piso"
            return false
        }

        if (r.formatoVivienda == 0) {
            mensajeError.value = "Ingrese Vivienda"
            return false
        }

        if (r.formatoVivienda == 11) {
            if (r.otrosVivienda.isEmpty()) {
                mensajeError.value = "Ingrese Otros Vivienda"
                return false
            }
        }

        if (r.formatoCargoColor == 0) {
            mensajeError.value = "Ingrese Color/Fachada"
            return false
        }

        if (r.formatoCargoColor == 16) {
            if (r.otrosCargoColor.isEmpty()) {
                mensajeError.value = "Ingrese Otros Color Fachada"
                return false
            }
        }

        if (r.formatoCargoPuerta == 0) {
            mensajeError.value = "Ingrese Puerta"
            return false
        }
        if (r.formatoCargoPuerta == 20) {
            if (r.otrosCargoPuerta.isEmpty()) {
                mensajeError.value = "Ingrese Otros Puerta"
                return false
            }
        }

        if (r.formatoCargoColorPuerta == 0) {
            mensajeError.value = "Ingrese Color Puerta"
            return false
        }

        if (r.formatoCargoColorPuerta == 25) {
            if (r.otrosCargoColorPuerta.isEmpty()) {
                mensajeError.value = "Ingrese Otros Color Puerta"
                return false
            }
        }

        if (r.formatoCargoRecibo == 0) {
            mensajeError.value = "Ingrese Recibido por."
            return false
        }

        insertOrUpdateRegistroRecibo(r, validation)
        return true
    }

    private fun insertOrUpdateRegistroRecibo(r: RegistroRecibo, validation: Int) {
        registroImp.insertOrUpdateRegistroRecibo(r)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : CompletableObserver {
                    override fun onComplete() {
                        if (validation == 2) {
                            mensajeSuccess.value = "Favor de firmar para completar formulario"
                        } else {
                            mensajeSuccess.value = "Recibo Guardado"
                        }
                    }

                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onError(e: Throwable) {
                        mensajeError.value = e.toString()
                    }
                })
    }


    fun updateRepartoEnvio(id: Int) {
        suministroImp.updateRepartoEnvio(id)
    }
}