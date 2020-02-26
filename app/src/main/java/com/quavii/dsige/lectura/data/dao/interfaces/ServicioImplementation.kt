package com.quavii.dsige.lectura.data.dao.interfaces

import com.quavii.dsige.lectura.data.model.Mega
import com.quavii.dsige.lectura.data.model.Servicio
import io.reactivex.Observable
import io.realm.RealmResults

interface ServicioImplementation {

    val servicioAll: RealmResults<Servicio>

    val servicioAllSync: RealmResults<Servicio>

    fun getServices(): Observable<List<Servicio>>

    fun insertServicio(s: Servicio)

    fun saveBytes(bytes: Int, usuario: Int)

    fun updateBytes(bytes: Int, fecha: String)

    fun getBytesIdentity(): Int

    fun getMega(): Mega

}
