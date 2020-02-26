package com.quavii.dsige.lectura.data.dao.interfaces

import com.quavii.dsige.lectura.data.model.Parametro
import io.realm.RealmResults

interface ParametroImplementation {

    val getAllParametro: RealmResults<Parametro>?

    fun getParametroById(id: Int): Parametro?
}