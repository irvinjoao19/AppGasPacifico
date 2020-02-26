package com.quavii.dsige.lectura.data.dao.overMethod

import com.quavii.dsige.lectura.data.dao.interfaces.ComboImplementation
import com.quavii.dsige.lectura.data.model.DetalleGrupo
import com.quavii.dsige.lectura.data.model.Formato
import com.quavii.dsige.lectura.data.model.Motivo
import com.quavii.dsige.lectura.data.model.TipoLectura
import io.realm.Realm
import io.realm.RealmResults

class ComboOver(private val realm: Realm) : ComboImplementation {

    override fun detalleGrupoAll(): RealmResults<DetalleGrupo> {
        return realm.where(DetalleGrupo::class.java).findAll()
    }

    override fun tipoLecturaAll(): RealmResults<TipoLectura> {
        return realm.where(TipoLectura::class.java).findAll()
    }

    override fun getDetalleGrupoById(id: Int): DetalleGrupo {
        return realm.where(DetalleGrupo::class.java).equalTo("iD_DetalleGrupo", id).findFirst()!!
    }

    override fun getDetalleGrupoByLectura(servicioId: Int): RealmResults<DetalleGrupo> {
        return realm.where(DetalleGrupo::class.java).equalTo("id_Servicio", servicioId).findAll()
    }

    override fun getDetalleGrupoByMotivo(servicioId: Int, grupo: String): RealmResults<DetalleGrupo> {
        return realm.where(DetalleGrupo::class.java).equalTo("id_Servicio", servicioId).equalTo("grupo", grupo).findAll()
    }

    override fun getDetalleGrupoByResultado(servicioId: Int, grupo: String): RealmResults<DetalleGrupo> {
        return realm.where(DetalleGrupo::class.java).equalTo("id_Servicio", servicioId).equalTo("grupo", grupo).findAll()
    }

    override fun getMotivos(): RealmResults<Motivo> {
        return realm.where(Motivo::class.java).findAll()
    }

    override fun getMotivosById(id: Int?): Motivo? {
        return realm.where(Motivo::class.java).equalTo("codigo", id).findFirst()
    }

    override fun getFormato(tipo: Int): RealmResults<Formato> {
        return realm.where(Formato::class.java).equalTo("tipo", tipo).findAll()
    }

    override fun getDetalleGrupoByParentId(id: Int): RealmResults<DetalleGrupo> {
        return realm.where(DetalleGrupo::class.java).equalTo("parentId", id).findAll()
    }

    override fun getCausaByServicio(id: Int): RealmResults<DetalleGrupo> {
        return realm.where(DetalleGrupo::class.java).equalTo("id_Servicio",id).findAll()
    }
}