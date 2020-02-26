package com.quavii.dsige.lectura.data.dao.overMethod

import com.quavii.dsige.lectura.data.dao.interfaces.SincronizarImplementation
import com.quavii.dsige.lectura.data.model.Sincronizar
import com.quavii.dsige.lectura.data.model.SuministroCortes
import com.quavii.dsige.lectura.data.model.SuministroLectura
import com.quavii.dsige.lectura.data.model.SuministroReconexion
import io.realm.Realm

class SincronizarOver(private val realm: Realm) : SincronizarImplementation {

    override fun save(sincronizar: Sincronizar, activo: Int) {
        realm.executeTransaction { realm ->
            val c = sincronizar.suministrosCortes
            if (c != null) {
                for (suministroCortes: SuministroCortes in c) {
                    val cortes: SuministroCortes? = realm.where(SuministroCortes::class.java).equalTo("iD_Suministro", suministroCortes.iD_Suministro).equalTo("activo", activo).findFirst()
                    if (cortes == null) {
                        realm.copyToRealmOrUpdate(suministroCortes)
                    }
                }
            }

            val r = sincronizar.suministroReconexion
            if (r != null) {
                for (suministroReconexion: SuministroReconexion in r) {
                    val reconexion: SuministroReconexion? = realm.where(SuministroReconexion::class.java).equalTo("iD_Suministro", suministroReconexion.iD_Suministro).equalTo("activo", activo).findFirst()
                    if (reconexion == null) {//
                        realm.copyToRealmOrUpdate(suministroReconexion)
                    }
                }
            }
        }
    }

    override fun saveLecturasEncontradas(suministroLecturas: List<SuministroLectura>, activo: Int) {
        realm.executeTransaction { realm ->
            for (lectura: SuministroLectura in suministroLecturas) {
                val lecturas: SuministroLectura? = realm.where(SuministroLectura::class.java).equalTo("iD_Suministro", lectura.iD_Suministro).equalTo("activo", activo).findFirst()
                if (lecturas == null) {
                    realm.copyToRealmOrUpdate(lectura)
                }
            }
        }
    }
}