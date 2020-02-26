package com.quavii.dsige.lectura.data.dao.interfaces

import androidx.lifecycle.LiveData
import com.quavii.dsige.lectura.data.model.*
import io.realm.RealmResults


interface SuministroImplementation {

    fun updateRepartoEnvio(id: Int)

    fun suministroRepartoUpdate(barCode: String, activo: Int)

    fun suministroRepartoEnvio(codigo: Int, estado: Int)

    fun getRegistroReparto(codigo: Int): Int

    val allLectura: RealmResults<SuministroLectura>

    val allCortes: RealmResults<SuministroCortes>

    fun getSuministroLectura(estado: Int, activo: Int, observadas: Int): RealmResults<SuministroLectura>

    fun getSuministroReclamos(tipo: String, activo: Int): RealmResults<SuministroLectura>

    fun getSuministroCortes(estado: Int, activo: Int): RealmResults<SuministroCortes>

    fun CortesSearch(estado: Int, activo: Int, cliente: String): RealmResults<SuministroCortes>

    fun CortesNext(orden: Int, activo: Int): RealmResults<SuministroCortes>

    fun suministroLecturaById(id: Int): SuministroLectura

    fun suministroCortesById(id: Int): SuministroCortes

    fun updateActivoSuministroLectura(id: Int, activo: Int)

    fun updateActivoSuministroCortes(id: Int, activo: Int)

    fun suministroLecturaByOrden(orden: Int): SuministroLectura

    fun suministroCortesByOrden(orden: Int): SuministroCortes

    fun countCorte(activo: Int)

    fun searchSuministro(estado: Int, activo: Int, tipo: String, cliente: String): RealmResults<SuministroCortes>

    val allSuministroReparto: RealmResults<SuministroReparto>

    fun suministroRepartoById(id: Int): SuministroReparto

    fun getSuministroReparto(activo: Int): RealmResults<SuministroReparto>

    fun getSuministroRepartoLiveData(activo: Int): LiveData<RealmResults<SuministroReparto>>

    fun suministroRepartoByOrden(orden: Int): SuministroReparto

    fun suministroRepartoUpdate(barCode: String)

    fun getCodigoBarra(codigo: String, activo: Int): SuministroReparto?

    fun suministroRepartoDatos(codigo: String): SuministroReparto

    fun repartoSaved(id: Int, iD_Suministro: Int, iD_Operario: Int, registro_Fecha_SQLITE: String, registro_Latitud: String, registro_Longitud: String, registro_Observacion: String, estado: Int)

    fun repartoSaveService(id: Int, iD_Suministro: Int, iD_Operario: Int, registro_Fecha_SQLITE: String, registro_Latitud: String, registro_Longitud: String, registro_Observacion: String, estado: Int)

    fun getRepartoAll(estado: Int): RealmResults<SendReparto>

    fun closeAllReparto(sendReparto: RealmResults<SendReparto>, estado: Int)

    val allReconexion: RealmResults<SuministroReconexion>

    fun getSuministroReconexion(estado: Int, activo: Int): RealmResults<SuministroReconexion>

    fun suministroReconexionById(id: Int): SuministroReconexion

    fun updateActivoSuministroReconexion(id: Int, activo: Int)

    fun suministroReconexionByOrden(orden: Int): SuministroReconexion

    fun ReconexionesSearch(estado: String, activo: Int, cliente: String): RealmResults<SuministroReconexion>

    fun ReconexionesNext(orden: Int, activo: Int): RealmResults<SuministroReconexion>

    fun LecturaNext(orden: Int, activo: Int): RealmResults<SuministroLectura>?

    fun buscarLecturaByOrden(orden: Int, activo: Int): SuministroLectura

    fun buscarCortesByOrden(orden: Int, activo: Int): SuministroCortes

    fun buscarReconexionesByOrden(orden: Int, activo: Int): SuministroReconexion


    // TODO NUEVO

    fun getLecturaOnCount(activo: Int, type: Int): Long?

    fun getLecturaReclamoOnCount(activo: Int, type: String): Long?


    // TODO Grandes Clientes

    fun getGrandesClientes(): RealmResults<GrandesClientes>

    fun getClienteById(id: Int): GrandesClientes

    fun getMarca(): RealmResults<Marca>

    fun getMarcaById(id: Int): Marca
}
