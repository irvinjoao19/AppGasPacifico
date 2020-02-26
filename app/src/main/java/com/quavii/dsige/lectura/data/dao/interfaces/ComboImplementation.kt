package com.quavii.dsige.lectura.data.dao.interfaces

import com.quavii.dsige.lectura.data.model.DetalleGrupo
import com.quavii.dsige.lectura.data.model.Formato
import com.quavii.dsige.lectura.data.model.Motivo
import com.quavii.dsige.lectura.data.model.TipoLectura
import io.realm.RealmResults

interface ComboImplementation {

    fun tipoLecturaAll(): RealmResults<TipoLectura>

    fun detalleGrupoAll(): RealmResults<DetalleGrupo>

    fun getDetalleGrupoById(id: Int): DetalleGrupo

    fun getDetalleGrupoByLectura(servicioId: Int): RealmResults<DetalleGrupo>

    fun getDetalleGrupoByMotivo(servicioId: Int, grupo: String): RealmResults<DetalleGrupo>

    fun getDetalleGrupoByResultado(servicioId: Int, grupo: String): RealmResults<DetalleGrupo>

    fun getMotivos(): RealmResults<Motivo>

    fun getMotivosById(id: Int?): Motivo?

    fun getFormato(tipo: Int): RealmResults<Formato>

    fun getDetalleGrupoByParentId(id: Int): RealmResults<DetalleGrupo>

    fun getCausaByServicio(id: Int): RealmResults<DetalleGrupo>

}