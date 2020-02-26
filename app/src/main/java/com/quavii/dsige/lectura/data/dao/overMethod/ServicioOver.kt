package com.quavii.dsige.lectura.data.dao.overMethod

import com.quavii.dsige.lectura.data.dao.interfaces.ServicioImplementation
import com.quavii.dsige.lectura.data.model.*
import com.quavii.dsige.lectura.helper.Util
import io.reactivex.Observable
import io.realm.Realm
import io.realm.RealmResults

class ServicioOver(private val realm: Realm) : ServicioImplementation {

    override val servicioAll: RealmResults<Servicio>
        get() = realm.where(Servicio::class.java).findAll()

    override val servicioAllSync: RealmResults<Servicio>
        get() = realm.where(Servicio::class.java).findAllAsync()

    override fun getServices(): Observable<List<Servicio>> {
        return Observable.create { e ->
            Realm.getDefaultInstance().use { realm ->
                val list = ArrayList<Servicio>()
                val services = realm.where(Servicio::class.java).findAll()
                val activo = 1
                val lectura = 1
                val relectura = 2
                for (s: Servicio in services) {

                    when (s.nombre_servicio) {
                        "Lectura" -> {
                            val a = realm.where(SuministroLectura::class.java).distinct("iD_Suministro").equalTo("activo", activo).equalTo("estado", lectura).findAll()
                            s.size = a.size
                            list.add(s)
                        }
                        "Relectura" -> {
                            val a = realm.where(SuministroLectura::class.java).distinct("iD_Suministro").equalTo("activo", activo).equalTo("estado", relectura).findAll()
                            s.size = a.size
                            list.add(s)
                        }
                        "Cortes" -> {
                            val a = realm.where(SuministroCortes::class.java).equalTo("activo", activo).findAll()
                            s.size = a.size
                            list.add(s)
                        }
                        "Reconexiones" -> {
                            val a = realm.where(SuministroReconexion::class.java).equalTo("activo", activo).findAll()
                            s.size = a.size
                            list.add(s)
                        }
                        "Distribucion" -> {
                            val a = realm.where(SuministroReparto::class.java).equalTo("activo", activo).findAll()
                            s.size = a.size
                            list.add(s)
                        }
                    }
                }
                e.onNext(list)
                e.onComplete()
            }

        }
    }

    override fun insertServicio(s: Servicio) {
        realm.executeTransaction { r ->
            r.copyToRealmOrUpdate(s)
        }
    }

    override fun saveBytes(bytes: Int, usuario: Int) {
        realm.executeTransaction { realm ->
            val b = realm.where(Mega::class.java).findFirst()
            if (b == null) {
                val b2 = Mega(getBytesIdentity(), bytes, usuario, Util.getFechaActual())
                realm.copyToRealmOrUpdate(b2)
            }
        }
    }

    override fun updateBytes(bytes: Int, fecha: String) {
        realm.executeTransaction { realm ->
            val b = realm.where(Mega::class.java).findFirst()
            if (b != null) {
                b.bytes = b.bytes!! + bytes
                b.fecha = fecha
            }
        }
    }

    override fun getBytesIdentity(): Int {
        val max = realm.where(Mega::class.java).max("megaId")
        return if (max == null) 1 else max.toInt() + 1
    }

    override fun getMega(): Mega {
        return realm.where(Mega::class.java).findFirst()!!
    }
}