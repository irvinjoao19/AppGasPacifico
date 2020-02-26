package com.quavii.dsige.lectura.data.dao.overMethod

import androidx.lifecycle.LiveData
import com.quavii.dsige.lectura.data.RealmLiveData
import com.quavii.dsige.lectura.data.dao.interfaces.SuministroImplementation
import com.quavii.dsige.lectura.data.model.*
import io.realm.Case
import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort

class SuministroOver(private val realm: Realm) : SuministroImplementation {

    override fun updateRepartoEnvio(id: Int) {
        realm.executeTransaction {
            val reparto: Registro = realm.where(Registro::class.java).equalTo("id", id).findFirst()!!
            reparto.estado = 1
            val suministroReparto = realm.where(SuministroReparto::class.java).equalTo("id_Reparto", reparto.iD_Suministro).findFirst()
            suministroReparto?.activo = 0
        }
    }

    override fun suministroRepartoUpdate(barCode: String, activo: Int) {
        realm.executeTransaction {
            val suministroReparto: SuministroReparto? = realm.where(SuministroReparto::class.java)
                    .equalTo("Suministro_Numero_reparto", barCode)
                    .findFirst()
            suministroReparto?.activo = activo
        }
    }

    override fun suministroRepartoEnvio(codigo: Int, estado: Int) {
        realm.executeTransaction {
            val reparto: Registro = realm.where(Registro::class.java).equalTo("iD_Suministro", codigo).findFirst()!!
            reparto.estado = estado
        }
    }

    override fun getRegistroReparto(codigo: Int): Int {
        val reparto: Registro = realm.where(Registro::class.java).equalTo("iD_Suministro", codigo).findFirst()!!
        return reparto.id
    }

    override val allLectura: RealmResults<SuministroLectura>
        get() = realm.where(SuministroLectura::class.java).findAll()

    override val allCortes: RealmResults<SuministroCortes>
        get() = realm.where(SuministroCortes::class.java).findAll()

    override fun getSuministroLectura(estado: Int, activo: Int, observadas: Int): RealmResults<SuministroLectura> {
        return realm.where(SuministroLectura::class.java).equalTo("suministro_TipoProceso", estado.toString()).equalTo("estado", estado).equalTo("activo", activo).equalTo("flagObservada", observadas).findAll().sort("orden", Sort.ASCENDING)
    }

    override fun getSuministroReclamos(tipo: String, activo: Int): RealmResults<SuministroLectura> {
        return realm.where(SuministroLectura::class.java).equalTo("suministro_TipoProceso", tipo).equalTo("activo", activo).findAll().sort("orden", Sort.ASCENDING)
    }

    override fun getSuministroCortes(estado: Int, activo: Int): RealmResults<SuministroCortes> {
        return realm.where(SuministroCortes::class.java).equalTo("estado", estado).equalTo("activo", activo).findAll().sort("orden", Sort.ASCENDING)
    }

    override fun CortesSearch(estado: Int, activo: Int, cliente: String): RealmResults<SuministroCortes> {
        return realm.where(SuministroCortes::class.java)//
                .beginGroup().equalTo("activo", activo)
                .contains("suministro_Cliente", cliente, Case.INSENSITIVE)
                .endGroup()
                .findAll().sort("orden", Sort.ASCENDING)
    }

    override fun suministroLecturaById(id: Int): SuministroLectura {
        return realm.where(SuministroLectura::class.java).equalTo("iD_Suministro", id).findFirst()!!
    }

    override fun suministroCortesById(id: Int): SuministroCortes {
        return realm.where(SuministroCortes::class.java).equalTo("iD_Suministro", id).findFirst()!!
    }

    override fun updateActivoSuministroLectura(id: Int, activo: Int) {
        realm.executeTransaction { realm ->
            val lectura: SuministroLectura = realm.where(SuministroLectura::class.java).equalTo("iD_Suministro", id).findFirst()!!
            lectura.activo = activo
        }
    }

    override fun updateActivoSuministroCortes(id: Int, activo: Int) {
        realm.executeTransaction { realm ->
            val cortes: SuministroCortes = realm.where(SuministroCortes::class.java).equalTo("iD_Suministro", id).findFirst()!!
            cortes.activo = activo
        }
    }

    override fun suministroLecturaByOrden(orden: Int): SuministroLectura {
        return realm.where(SuministroLectura::class.java).equalTo("suministroOperario_Orden", orden).findFirst()!!
    }

    override fun suministroCortesByOrden(orden: Int): SuministroCortes {
        return realm.where(SuministroCortes::class.java).equalTo("suministroOperario_Orden", orden).findFirst()!!
    }

    override fun countCorte(activo: Int) {
        realm.executeTransaction { realm ->
            val cortes: Long = realm.where(SuministroCortes::class.java).equalTo("activo", activo).count()
            val coun: SuministroCortes? = null
            coun?.countCorte = cortes.toInt()
        }
    }

    override fun searchSuministro(estado: Int, activo: Int, tipo: String, cliente: String): RealmResults<SuministroCortes> {
        return realm.where(SuministroCortes::class.java).equalTo("estado", estado).equalTo("activo", activo).equalTo("suministro_TipoProceso", tipo).equalTo("suministro_Medidor", cliente).findAll()
    }

    override val allSuministroReparto: RealmResults<SuministroReparto>
        get() = realm.where(SuministroReparto::class.java).findAll() //To change initializer of created properties use File | Settings | File Templates.

    override fun getSuministroReparto(activo: Int): RealmResults<SuministroReparto> {
        return realm.where(SuministroReparto::class.java).equalTo("activo", activo).findAll()
    }

    override fun getSuministroRepartoLiveData(activo: Int): LiveData<RealmResults<SuministroReparto>> {
        return RealmLiveData(realm.where(SuministroReparto::class.java).equalTo("activo", activo).findAllAsync())
    }

    override fun suministroRepartoById(id: Int): SuministroReparto {
        return realm.where(SuministroReparto::class.java).equalTo("id_Reparto", id).findFirst()!!
    }

    override fun suministroRepartoByOrden(orden: Int): SuministroReparto {
        return realm.where(SuministroReparto::class.java).equalTo("estado", orden).findFirst()!!
    }

    override fun suministroRepartoUpdate(barCode: String) {
        realm.executeTransaction { realm ->
            val suministroReparto: SuministroReparto = realm.where(SuministroReparto::class.java).equalTo("CodigoBarra", barCode).findFirst()!!
            suministroReparto.activo = 0
        }
    }

    override fun getCodigoBarra(codigo: String, activo: Int): SuministroReparto? {
        return realm.where(SuministroReparto::class.java)
                .equalTo("Suministro_Numero_reparto", codigo)
                .equalTo("activo", activo).findFirst()
    }

    override fun suministroRepartoDatos(codigo: String): SuministroReparto {
        return realm.where(SuministroReparto::class.java).equalTo("Suministro_Numero_reparto", codigo).findFirst()!!
    }

    override fun repartoSaved(id: Int, iD_Suministro: Int, iD_Operario: Int, registro_Fecha_SQLITE: String, registro_Latitud: String, registro_Longitud: String, registro_Observacion: String, estado: Int) {
        realm.executeTransaction { realm ->
            val a = Registro(id, iD_Suministro, iD_Operario, registro_Fecha_SQLITE, registro_Latitud, registro_Longitud, registro_Observacion, 5, estado)
            realm.copyToRealmOrUpdate(a)
        }
    }

    override fun repartoSaveService(id: Int, iD_Suministro: Int, iD_Operario: Int, registro_Fecha_SQLITE: String, registro_Latitud: String, registro_Longitud: String, registro_Observacion: String, estado: Int) {
        realm.executeTransaction { realm ->
            val r: Registro? = realm.where(Registro::class.java).equalTo("iD_Suministro", iD_Suministro).findFirst()
            if (r == null) {
                val a = Registro(id, iD_Suministro, iD_Operario, registro_Fecha_SQLITE, registro_Latitud, registro_Longitud, registro_Observacion, 5, estado)
                realm.copyToRealmOrUpdate(a)
                val reparto: SuministroReparto? = realm.where(SuministroReparto::class.java).equalTo("id_Reparto", iD_Suministro).findFirst()
                reparto?.activo = 0
            }
        }
    }

    override fun getRepartoAll(estado: Int): RealmResults<SendReparto> {
        return realm.where(SendReparto::class.java).equalTo("estado", estado).findAll()
    }

    override fun closeAllReparto(sendReparto: RealmResults<SendReparto>, estado: Int) {
        realm.executeTransaction {
            val r = sendReparto.createSnapshot()
            if (r != null) {
                for (i in r.indices) {
                    r[i]?.estado = estado
                }
            }
        }
    }

    /**
     * Reconexion
     */
    override val allReconexion: RealmResults<SuministroReconexion>
        get() = realm.where(SuministroReconexion::class.java).findAll()

    override fun getSuministroReconexion(estado: Int, activo: Int): RealmResults<SuministroReconexion> {
        return realm.where(SuministroReconexion::class.java).equalTo("estado", estado).equalTo("activo", activo).findAll().sort("orden", Sort.ASCENDING)
    }

    override fun suministroReconexionById(id: Int): SuministroReconexion {
        return realm.where(SuministroReconexion::class.java).equalTo("iD_Suministro", id).findFirst()!!
    }

    override fun updateActivoSuministroReconexion(id: Int, activo: Int) {
        realm.executeTransaction { realm ->
            val cortes: SuministroReconexion = realm.where(SuministroReconexion::class.java).equalTo("iD_Suministro", id).findFirst()!!
            cortes.activo = activo
        }
    }

    override fun suministroReconexionByOrden(orden: Int): SuministroReconexion {
        return realm.where(SuministroReconexion::class.java).equalTo("suministroOperario_Orden", orden).findFirst()!!
    }

    override fun ReconexionesSearch(estado: String, activo: Int, cliente: String): RealmResults<SuministroReconexion> {
        return realm.where(SuministroReconexion::class.java)//
                .beginGroup().equalTo("activo", activo)
                .contains("suministro_Cliente", cliente, Case.INSENSITIVE)
                .endGroup()
                .findAll().sort("orden", Sort.ASCENDING)
    }

    override fun CortesNext(orden: Int, activo: Int): RealmResults<SuministroCortes> {
        return realm.where(SuministroCortes::class.java).equalTo("orden", orden).equalTo("activo", activo).findAll()
    }

    override fun ReconexionesNext(orden: Int, activo: Int): RealmResults<SuministroReconexion> {
        return realm.where(SuministroReconexion::class.java).equalTo("orden", orden).equalTo("activo", activo).findAll()!!
    }

    override fun LecturaNext(orden: Int, activo: Int): RealmResults<SuministroLectura>? {
        return realm.where(SuministroLectura::class.java).equalTo("orden", orden).equalTo("activo", activo).findAll()
    }

    // TODO -> nuevo

    override fun buscarLecturaByOrden(orden: Int, activo: Int): SuministroLectura {
        return realm.where(SuministroLectura::class.java).equalTo("orden", orden).equalTo("activo", activo).findFirst()!!
    }

    override fun buscarCortesByOrden(orden: Int, activo: Int): SuministroCortes {
        return realm.where(SuministroCortes::class.java).equalTo("orden", orden).equalTo("activo", activo).findFirst()!!
    }

    override fun buscarReconexionesByOrden(orden: Int, activo: Int): SuministroReconexion {
        return realm.where(SuministroReconexion::class.java).equalTo("orden", orden).equalTo("activo", activo).findFirst()!!
    }

    override fun getLecturaOnCount(activo: Int, type: Int): Long? {
        return realm.where(SuministroLectura::class.java)?.distinct("iD_Suministro")?.equalTo("activo", activo)?.equalTo("estado", activo)?.equalTo("flagObservada", type)?.equalTo("suministro_TipoProceso", activo.toString())?.count()
    }

    override fun getLecturaReclamoOnCount(activo: Int, type: String): Long? {
        return realm.where(SuministroLectura::class.java)?.distinct("iD_Suministro")?.equalTo("activo", activo)?.equalTo("estado", activo)?.equalTo("suministro_TipoProceso", type)?.count()
    }

    override fun getGrandesClientes(): RealmResults<GrandesClientes> {
        val estado = 7
        return realm.where(GrandesClientes::class.java).notEqualTo("estado", estado).findAll()
    }

    override fun getClienteById(id: Int): GrandesClientes {
        return realm.where(GrandesClientes::class.java).equalTo("clienteId", id).findFirst()!!
    }

    override fun getMarca(): RealmResults<Marca> {
        return realm.where(Marca::class.java).findAll()
    }

    override fun getMarcaById(id: Int): Marca {
        return realm.where(Marca::class.java).equalTo("marcaMedidorId", id).findFirst()!!
    }
}