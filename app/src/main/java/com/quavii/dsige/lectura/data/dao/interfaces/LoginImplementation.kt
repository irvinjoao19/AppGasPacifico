package com.quavii.dsige.lectura.data.dao.interfaces

import com.quavii.dsige.lectura.data.model.Login
import com.quavii.dsige.lectura.data.model.Operario
import io.realm.RealmQuery
import io.realm.RealmResults

interface LoginImplementation {

    val login: Login?

    fun save(login: Login)

    fun ifExistLogin(login: Login): Boolean

    fun delete()

    fun getIdUser(): RealmQuery<Login>?

    fun updateLogin(valor : Int)

    // TODO about Operarios

    fun saveOperarios(operarios: List<Operario>)

    fun getAllOperarios(): RealmResults<Operario>

    fun updateOperario(operario: Operario,value : Int)



}
