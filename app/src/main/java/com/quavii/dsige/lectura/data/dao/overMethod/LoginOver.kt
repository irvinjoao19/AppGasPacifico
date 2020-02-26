package com.quavii.dsige.lectura.data.dao.overMethod

import com.quavii.dsige.lectura.data.dao.interfaces.LoginImplementation
import com.quavii.dsige.lectura.data.model.Login
import com.quavii.dsige.lectura.data.model.Operario
import io.realm.Realm
import io.realm.RealmQuery
import io.realm.RealmResults

class LoginOver(private val realm: Realm) : LoginImplementation {

    override val login: Login?
        get() = realm.where(Login::class.java).findFirst()

    override fun save(login: Login) {
        realm.executeTransaction { realm ->
            realm.copyToRealmOrUpdate(login)
        }
    }

    override fun ifExistLogin(login: Login): Boolean {
        val l = realm.where(Login::class.java).equalTo("iD_Operario", login.iD_Operario).findFirst()
        return l != null
    }

    override fun delete() {
        realm.executeTransaction { realm ->
            val logins = realm.where(Login::class.java).findAll()
            logins.deleteAllFromRealm()
        }
    }

    override fun getIdUser(): RealmQuery<Login>? {
        return realm.where(Login::class.java)
    }

    override fun updateLogin(valor: Int) {
        realm.executeTransaction {
            val usuario = realm.where(Login::class.java).findFirst()
            if (usuario != null) {
                usuario.lecturaManual = valor
            }
        }
    }

    // TODO about Operarios


    override fun saveOperarios(operarios: List<Operario>) {
        realm.executeTransaction { realm ->
            realm.copyToRealmOrUpdate(operarios)
        }
    }

    override fun getAllOperarios(): RealmResults<Operario> {
        return realm.where(Operario::class.java).findAll()
    }

    override fun updateOperario(operario: Operario, value: Int) {
        realm.executeTransaction {
            operario.lecturaManual = value
        }
    }
}
