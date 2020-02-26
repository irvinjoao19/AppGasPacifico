package com.quavii.dsige.lectura.data.dao.overMethod

import androidx.lifecycle.LiveData
import com.quavii.dsige.lectura.data.RealmLiveData
import com.quavii.dsige.lectura.data.dao.interfaces.RegistroImplementation
import com.quavii.dsige.lectura.data.model.*
import io.reactivex.Completable
import io.reactivex.Observable
import io.realm.Realm
import io.realm.RealmResults


class RegistroOver(private val realm: Realm) : RegistroImplementation {

    override fun getRegistroIdentity(): Int {
        val registro = realm.where(Registro::class.java).max("id")
        val result: Int
        result = if (registro == null) {
            1
        } else {
            registro.toInt() + 1
        }
        return result
    }

    override fun save(registro: Registro) {
        realm.executeTransaction { realm ->
            if (!confirmRegistro(registro.orden, registro.tipo)) {
                realm.copyToRealmOrUpdate(registro)
            } else {
                val updateRegistro: Registro? = getRegistroByOrden(registro.orden, registro.tipo)
                updateRegistro?.registro_Fecha_SQLITE = registro.registro_Fecha_SQLITE
                updateRegistro?.suministro_Numero = registro.suministro_Numero
                updateRegistro?.iD_TipoLectura = registro.iD_TipoLectura
                updateRegistro?.registro_Lectura = registro.registro_Lectura
                updateRegistro?.registro_Confirmar_Lectura = registro.registro_Confirmar_Lectura
                updateRegistro?.registro_Observacion = registro.registro_Observacion
                updateRegistro?.grupo_Incidencia_Codigo = registro.grupo_Incidencia_Codigo
                updateRegistro?.registro_TieneFoto = registro.registro_TieneFoto
                updateRegistro?.registro_TipoProceso = registro.registro_TipoProceso
                updateRegistro?.registro_Constancia = registro.registro_Constancia
                updateRegistro?.registro_Desplaza = registro.registro_Desplaza
                updateRegistro?.codigo_Resultado = registro.codigo_Resultado
                updateRegistro?.tipo = registro.tipo
                updateRegistro?.orden = registro.orden
                updateRegistro?.estado = registro.estado
                updateRegistro?.horaActa = registro.horaActa
                updateRegistro?.suministroCliente = registro.suministroCliente
                updateRegistro?.suministroDireccion = registro.suministroDireccion
                updateRegistro?.lecturaManual = registro.lecturaManual
                updateRegistro?.motivoId = registro.motivoId
                updateRegistro?.parentId = registro.parentId
                updateRegistro?.responsable = registro.responsable
                updateRegistro?.parentesco = registro.parentesco
                updateRegistro?.precinto = registro.precinto
            }
        }
    }

    override fun saveZonaPeligrosa(registro: Registro) {
        realm.executeTransaction { realm ->
            realm.copyToRealmOrUpdate(registro)
        }
    }

    override fun updateRegistroDesplaza(id: Int, tipo: Int, estado: Int) {
        realm.executeTransaction { realm ->
            val registro: Registro = realm.where(Registro::class.java).equalTo("iD_Suministro", id).equalTo("tipo", tipo).findFirst()!!
            //  registro.registro_Desplaza = value
            registro.estado = estado
        }
    }

    override fun getRegistroBySuministro(id: Int): Registro {
        return realm.where(Registro::class.java).equalTo("iD_Suministro", id).findFirst()!!
    }

    override fun getRegistroByOrden(orden: Int, tipo: Int): Registro? {
        return realm.where(Registro::class.java).equalTo("orden", orden).equalTo("tipo", tipo).findFirst()
    }

    override fun getRegistro(orden: Int, tipo: Int, recupero: Int): Registro? {
        return if (recupero == 10) {
            realm.where(Registro::class.java).equalTo("orden", orden).equalTo("tipo", recupero).findFirst()
        } else {
            realm.where(Registro::class.java).equalTo("orden", orden).equalTo("tipo", tipo).findFirst()
        }
    }


    override fun confirmRegistro(orden: Int, tipo: Int): Boolean {
        var result = false
        val registro: Registro? = realm.where(Registro::class.java).equalTo("orden", orden).equalTo("tipo", tipo).findFirst()
        if (registro != null) {
            result = true
        }
        return result
    }

    override fun getAllRegistro(estado: Int): RealmResults<Registro> {
        return realm.where(Registro::class.java).equalTo("estado", estado).findAll()
    }

    override fun getAllRegistroLiveData(estado: Int): LiveData<RealmResults<Registro>> {
        return RealmLiveData(realm.where(Registro::class.java).equalTo("estado", estado).findAllAsync())
    }

    override fun closeAllRegistro(registros: RealmResults<Registro>, estado: Int) {
        realm.executeTransaction {
            val r = registros.createSnapshot()
            if (r != null) {
                for (i in r.indices) {
                    r[i]?.estado = estado
                }
            }
        }
    }

    override fun closeOneRegistro(registro: Registro, estado: Int) {
        realm.executeTransaction {
            if (registro.tipo == 1 || registro.tipo == 10) {
                if (registro.grupo_Incidencia_Codigo == "2" || registro.grupo_Incidencia_Codigo == "21") {
                    val suministro = realm.where(SuministroLectura::class.java).equalTo("suministro_Numero", registro.suministro_Numero.toString()).findFirst()
                    if (suministro != null) {
                        suministro.activo = 1
                        suministro.estado = 10
                        registro.tipo = 10
                    }
                }
            }

            registro.estado = estado
            registro.registro_Desplaza = "1"

            if (registro.photos != null) {
                val photos = registro.photos?.createSnapshot()

                for (p in photos!!) {
                    if (p.estado != 2) {
                        p.estado = 0
                    }
                    if (registro.grupo_Incidencia_Codigo == "2" || registro.grupo_Incidencia_Codigo == "21") {
                        p.tipo = 10
                    }
                }
            }
        }
    }


    override fun getRepartoIdentity(): Int? {
        val reparto = realm.where(SendReparto::class.java).max("id_Reparto")
        val result: Int
        result = if (reparto == null) {
            1
        } else {
            reparto.toInt() + 1
        }
        return result
    }

    override fun getAllRegistroReparto(estado: Int): RealmResults<SendReparto>? {
        return realm.where(SendReparto::class.java).equalTo("estado", estado).findAll()
    }

    override fun closeAllRegistroReparto(registros: RealmResults<SendReparto>, estado: Int) {
        realm.executeTransaction {
            val r = registros.createSnapshot()
            if (r != null) {
                for (i in r.indices) {
                    r[i]?.estado = estado
                }
            }
        }
    }

    override fun saveActaConformidad(iD_Suministro: Int, horaActa: String, estado: Int) {
        realm.executeTransaction {
            val updateSuministro: Registro? = getSuministro(iD_Suministro)
            updateSuministro?.horaActa = horaActa
            updateSuministro?.estado = estado
        }
    }

    override fun saveSuministroEncontrado(registro: Registro) {
        realm.executeTransaction { realm ->
            if (!confirmSuministroEncontrado(registro.iD_Suministro)) {
                realm.copyToRealmOrUpdate(registro)
            } else {
                val updateSuministro: Registro? = getSuministro(registro.iD_Suministro)
                updateSuministro?.registro_Constancia = registro.registro_Constancia
                updateSuministro?.suministroCliente = registro.suministroCliente
                updateSuministro?.suministroDireccion = registro.suministroDireccion
                updateSuministro?.registro_Observacion = registro.registro_Observacion
                updateSuministro?.estado = registro.estado
            }
        }
    }

    override fun confirmSuministroEncontrado(iD_Suministro: Int): Boolean {
        var result = false
        val suministroEncontrado: Registro? = realm.where(Registro::class.java).equalTo("iD_Suministro", iD_Suministro).findFirst()
        if (suministroEncontrado != null) {
            result = true
        }
        return result
    }

    override fun getSuministro(iD_Suministro: Int): Registro? {
        return realm.where(Registro::class.java).equalTo("iD_Suministro", iD_Suministro).findFirst()
    }

    override fun getAllRegistroRx(estado: Int): Observable<RealmResults<Registro>> {
        return Observable.create { emitter ->
            val r = Realm.getDefaultInstance()
            try {
                val a = r.where(Registro::class.java).equalTo("estado", estado).findAll()
                emitter.onNext(a)
                emitter.onComplete()
            } catch (e: Throwable) {
                emitter.onError(e)
            } finally {
                r.close()
            }
        }
    }

    override fun getSelfie(state: Int, name: String): Observable<Registro> {
        return Observable.create { emitter ->
            val r = Realm.getDefaultInstance()
            try {
                val a = r.where(Registro::class.java).equalTo("tipo", state).equalTo("registro_Observacion", name).findFirst()!!
                emitter.onNext(a)
                emitter.onComplete()
            } catch (e: Throwable) {
                emitter.onError(e)
            } finally {
                r.close()
            }
        }
    }

    /**
     * @estado == 0
     * La foto fue eliminado del celular y se cambiara de estado a 0
     * para que solo se envie el registro y que el sistema verifique que solo se guardo el registro
     */
    override fun updateRegistroTienePhoto(cantidad: Int, estado: String, registro: Registro): Registro {
        if (cantidad == 0) {
            if (estado == "0") {
                realm.executeTransaction {
                    registro.registro_TieneFoto = estado
                }
            }
        }
        return registro
    }

    /**
     * @estado == 0
     * Cuando la foto es eliminada se cambia de estado a 0
     * para que en el servidor no se pueda guardar el registro de la foto
     */
    override fun closePhotoEstado(estado: Int, p: Photo) {
        realm.executeTransaction {
            p.estado = estado
        }
    }

    override fun getRegistroByOrdenRx(orden: Int, tipo: Int): Observable<Registro> {
        return Observable.create { emitter ->
            try {
                Realm.getDefaultInstance().use { realm ->
                    val a = realm.where(Registro::class.java).equalTo("orden", orden).equalTo("tipo", tipo).findFirst()!!
                    emitter.onNext(a)
                    emitter.onComplete()
                }
            } catch (e: Throwable) {
                emitter.onError(e)
            }
        }
    }

    override fun getRegistroReciboIdentity(): Int {
        val r = realm.where(RegistroRecibo::class.java).max("reciboId")
        return if (r == null) 1 else r.toInt() + 1

    }

    override fun getRegistroByFk(id: Int): RegistroRecibo? {
        return realm.where(RegistroRecibo::class.java).equalTo("repartoId", id).findFirst()
    }


    override fun getRegistroRecibidoAll(repartoId: Int): RealmResults<RegistroRecibo> {
        return realm.where(RegistroRecibo::class.java).equalTo("repartoId", repartoId).findAll()
    }

    override fun getRegistroRecibidoAllLiveData(repartoId: Int): LiveData<RealmResults<RegistroRecibo>> {
        return RealmLiveData(realm.where(RegistroRecibo::class.java).equalTo("repartoId", repartoId).findAllAsync())
    }

    override fun getUpdateRegistro(repartoId: Int, firm: String) {
        realm.executeTransaction { rr ->
            val r = rr.where(RegistroRecibo::class.java).equalTo("repartoId", repartoId).findFirst()
            if (r != null) {
                r.firmaCliente = firm
            }
        }

//        return Completable.fromAction {
//            Realm.getDefaultInstance().use { realm ->
//                realm.executeTransaction { rr ->
//                    val r = rr.where(RegistroRecibo::class.java).equalTo("repartoId", repartoId).findFirst()
//                    if (r != null) {
//                        r.firmaCliente = firm
//                    }
//                }
//            }
//        }
    }

    override fun getInicioFinTrabajo(usuario: Int, fecha: String, observacion: String): Registro? {
        return realm.where(Registro::class.java)
                .equalTo("iD_Suministro", usuario)
                .equalTo("iD_Operario", usuario)
                .equalTo("registro_Observacion", observacion)
                .beginGroup()
                .contains("registro_Fecha_SQLITE", fecha)
                .endGroup()
                .findFirst()
    }

    override fun insertOrUpdateRegistroRecibo(r: RegistroRecibo): Completable {
        return Completable.fromAction {
            Realm.getDefaultInstance().use { realm ->
                realm.executeTransaction { rr ->
                    val registro: Registro? = rr.where(Registro::class.java).equalTo("id", r.repartoId).findFirst()
                    if (registro != null) {
                        val recibo: RegistroRecibo? = rr.where(RegistroRecibo::class.java).equalTo("reciboId", r.reciboId).findFirst()
                        if (recibo == null) {
                            registro.recibo = realm.copyToRealm(r)
                            rr.copyToRealmOrUpdate(r)
                        } else {
                            recibo.tipo = r.tipo
                            recibo.ciclo = r.ciclo
                            recibo.year = r.year
                            recibo.piso = r.piso
                            recibo.formatoVivienda = r.formatoVivienda
                            recibo.otrosVivienda = r.otrosVivienda
                            recibo.formatoCargoColor = r.formatoCargoColor
                            recibo.otrosCargoColor = r.otrosCargoColor
                            recibo.formatoCargoPuerta = r.formatoCargoPuerta
                            recibo.otrosCargoPuerta = r.otrosCargoPuerta
                            recibo.formatoCargoColorPuerta = r.formatoCargoColorPuerta
                            recibo.otrosCargoColorPuerta = r.otrosCargoColorPuerta
                            recibo.formatoCargoRecibo = r.formatoCargoRecibo
                            recibo.dniCargoRecibo = r.dniCargoRecibo
                            recibo.parentesco = r.parentesco
                            recibo.formatoCargoDevuelto = r.formatoCargoDevuelto
                            recibo.fechaMax = r.fechaMax
                            recibo.fechaEntrega = r.fechaEntrega
                            recibo.observacionCargo = r.observacionCargo
                            recibo.firmaCliente = r.firmaCliente
                            recibo.nombreformatoCargoRecibo = r.nombreformatoCargoRecibo
                            recibo.nombreformatoVivienda = r.nombreformatoVivienda
                            recibo.nombreformatoCargoColor = r.nombreformatoCargoColor
                            recibo.nombreformatoCargoPuerta = r.nombreformatoCargoPuerta
                            recibo.nombreformatoCargoColorPuerta = r.nombreformatoCargoColorPuerta
                            recibo.nombreformatoCargoDevuelto = r.nombreformatoCargoDevuelto
                        }
                    }
                }
            }
        }
    }

    override fun getClienteById(clienteId: Int): Observable<GrandesClientes> {
        return Observable.create { emitter ->
            try {
                Realm.getDefaultInstance().use { realm ->
                    val a = realm.where(GrandesClientes::class.java).equalTo("clienteId", clienteId).findFirst()!!
                    emitter.onNext(a)
                    emitter.onComplete()
                }
            } catch (e: Throwable) {
                emitter.onError(e)
            }
        }
    }

    override fun getGoTrabajo(usuarioId: Int, fecha: String, tipo: Int, observacion: String): Registro? {
        return realm.where(Registro::class.java)
                .equalTo("iD_Suministro", usuarioId)
                .equalTo("iD_Operario", usuarioId)
                .equalTo("registro_Observacion", observacion)
                .equalTo("tipo", tipo)
                .beginGroup()
                .contains("registro_Fecha_SQLITE", fecha)
                .endGroup()
                .findFirst()
    }

    override fun updateClientes(g: GrandesClientes): Completable {
        return Completable.fromAction {
            Realm.getDefaultInstance().use { realm ->
                realm.executeTransaction { rr ->
                    val c: GrandesClientes? = rr.where(GrandesClientes::class.java).equalTo("clienteId", g.clienteId).findFirst()
                    if (c != null) {
                        c.codigoEMR = g.codigoEMR
                        c.fechaRegistroInicio = g.fechaRegistroInicio
                        c.clientePermiteAcceso = g.clientePermiteAcceso
                        c.fotoConstanciaPermiteAcceso = g.fotoConstanciaPermiteAcceso
                        c.porMezclaExplosiva = g.porMezclaExplosiva
                        c.vManoPresionEntrada = g.vManoPresionEntrada
                        c.fotovManoPresionEntrada = g.fotovManoPresionEntrada
                        c.marcaCorrectorId = g.marcaCorrectorId
                        c.fotoMarcaCorrector = g.fotoMarcaCorrector
                        c.vVolumenSCorreUC = g.vVolumenSCorreUC
                        c.fotovVolumenSCorreUC = g.fotovVolumenSCorreUC
                        c.vVolumenSCorreMedidor = g.vVolumenSCorreMedidor
                        c.fotovVolumenSCorreMedidor = g.fotovVolumenSCorreMedidor
                        c.vVolumenRegUC = g.vVolumenRegUC
                        c.fotovVolumenRegUC = g.fotovVolumenRegUC
                        c.vPresionMedicionUC = g.vPresionMedicionUC
                        c.fotovPresionMedicionUC = g.fotovPresionMedicionUC
                        c.vTemperaturaMedicionUC = g.vTemperaturaMedicionUC
                        c.fotovTemperaturaMedicionUC = g.fotovTemperaturaMedicionUC
                        c.tiempoVidaBateria = g.tiempoVidaBateria
                        c.fotoTiempoVidaBateria = g.fotoTiempoVidaBateria
                        c.fotoPanomarica = g.fotoPanomarica
                        c.tieneGabinete = g.tieneGabinete
                        c.foroSitieneGabinete = g.foroSitieneGabinete
                        c.presenteCliente = g.presenteCliente
                        c.contactoCliente = g.contactoCliente
                        c.latitud = g.latitud
                        c.longitud = g.longitud
                        c.estado = g.estado
                        c.comentario = g.comentario
                        c.confirmarVolumenSCorreUC = g.confirmarVolumenSCorreUC
                    }
                }
            }
        }
    }

    override fun closeFileClienteById(id: Int): Completable {
        return Completable.fromAction {
            Realm.getDefaultInstance().use { realm ->
                realm.executeTransaction { rr ->
                    val c: GrandesClientes? = rr.where(GrandesClientes::class.java).equalTo("clienteId", id).findFirst()
                    if (c != null) {
                        c.estado = 7
                    }
                }
            }
        }
    }
}