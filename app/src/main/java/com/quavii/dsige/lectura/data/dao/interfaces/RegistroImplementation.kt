package com.quavii.dsige.lectura.data.dao.interfaces

import androidx.lifecycle.LiveData
import com.quavii.dsige.lectura.data.model.*
import io.reactivex.Completable
import io.reactivex.Observable
import io.realm.RealmResults

interface RegistroImplementation {

    fun getRegistroIdentity(): Int

    fun save(registro: Registro)

    fun saveZonaPeligrosa(registro: Registro)

    fun updateRegistroDesplaza(id: Int, tipo: Int, estado: Int)

    fun getRegistroBySuministro(id: Int): Registro

    fun getRegistroByOrden(orden: Int, tipo: Int): Registro?

    fun getRegistro(orden: Int, tipo: Int, recupero: Int): Registro?

    fun confirmRegistro(orden: Int, tipo: Int): Boolean

    fun getAllRegistro(estado: Int): RealmResults<Registro>

    fun getAllRegistroLiveData(estado: Int): LiveData<RealmResults<Registro>>

    fun closeAllRegistro(registros: RealmResults<Registro>, estado: Int)

    fun closeOneRegistro(registro: Registro, estado: Int)

    fun getRepartoIdentity(): Int?

    fun getAllRegistroReparto(estado: Int): RealmResults<SendReparto>?

    fun closeAllRegistroReparto(registros: RealmResults<SendReparto>, estado: Int)

    fun getSuministro(iD_Suministro: Int): Registro?

    // TODO SOBRE ACTA DE CONFORMIDAD

    fun saveActaConformidad(iD_Suministro: Int, horaActa: String, estado: Int)

    // TODO SOBRE SUMINISTOR ENCONTRADO

    fun saveSuministroEncontrado(registro: Registro)

    fun confirmSuministroEncontrado(iD_Suministro: Int): Boolean

    // TODO RX JAVA

    fun getAllRegistroRx(estado: Int): Observable<RealmResults<Registro>>

    fun getSelfie(state: Int, name: String): Observable<Registro>

    fun updateRegistroTienePhoto(cantidad: Int, estado: String, registro: Registro): Registro

    fun closePhotoEstado(estado: Int, p: Photo)

    fun getRegistroByOrdenRx(orden: Int, tipo: Int): Observable<Registro>

    // TODO REPARTO

    fun getRegistroReciboIdentity(): Int

    fun getRegistroByFk(id: Int): RegistroRecibo?

    fun getRegistroRecibidoAll(repartoId: Int): RealmResults<RegistroRecibo>

    fun getRegistroRecibidoAllLiveData(repartoId: Int): LiveData<RealmResults<RegistroRecibo>>

    fun getUpdateRegistro(repartoId: Int, firm: String)

    fun getInicioFinTrabajo(usuario: Int, fecha: String, observacion: String): Registro?

    fun insertOrUpdateRegistroRecibo(r: RegistroRecibo): Completable


    // TODO SELFIE SERVICES
    fun getClienteById(clienteId: Int): Observable<GrandesClientes>


    fun getGoTrabajo(usuarioId: Int, fecha: String, tipo: Int, observacion: String): Registro?

    // TODO GRANDES CLIENTE

    fun updateClientes(g: GrandesClientes): Completable

    fun closeFileClienteById(id: Int): Completable
}