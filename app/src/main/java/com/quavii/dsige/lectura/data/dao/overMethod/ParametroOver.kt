package com.quavii.dsige.lectura.data.dao.overMethod

import com.quavii.dsige.lectura.data.dao.interfaces.ParametroImplementation
import com.quavii.dsige.lectura.data.model.Parametro
import io.realm.Realm
import io.realm.RealmResults

class ParametroOver(private val realm: Realm) : ParametroImplementation {

    override val getAllParametro: RealmResults<Parametro>?
        get() = realm.where(Parametro::class.java).findAll()

    override fun getParametroById(id: Int): Parametro? {
        return realm.where(Parametro::class.java).equalTo("id_Configuracion", id).findFirst()
    }
}