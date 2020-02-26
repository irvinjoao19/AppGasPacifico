package com.quavii.dsige.lectura.data.dao.overMethod

import com.quavii.dsige.lectura.data.dao.interfaces.MigrationImplementation
import com.quavii.dsige.lectura.data.model.*
import io.realm.Realm
import io.realm.RealmResults

open class MigrationOver(private val realm: Realm) : MigrationImplementation {

    override fun save(migration: Migration) {
        realm.executeTransaction { realm ->
            realm.copyToRealmOrUpdate(migration)
        }
    }

    override fun deleteAll() {
        realm.executeTransaction { realm ->
            val detalleGrupo: RealmResults<DetalleGrupo>? = realm.where(DetalleGrupo::class.java).findAll()
            detalleGrupo?.deleteAllFromRealm()
            val registros: RealmResults<Registro>? = realm.where(Registro::class.java).findAll()
            registros?.deleteAllFromRealm()
            val photos: RealmResults<Photo>? = realm.where(Photo::class.java).findAll()
            photos?.deleteAllFromRealm()
            val suministroCortes: RealmResults<SuministroCortes>? = realm.where(SuministroCortes::class.java).findAll()
            suministroCortes?.deleteAllFromRealm()
            val suministroLectura: RealmResults<SuministroLectura>? = realm.where(SuministroLectura::class.java).findAll()
            suministroLectura?.deleteAllFromRealm()
            val suministroReparto: RealmResults<SuministroReparto>? = realm.where(SuministroReparto::class.java).findAll()
            suministroReparto?.deleteAllFromRealm()
            val sendReparto: RealmResults<SendReparto>? = realm.where(SendReparto::class.java).findAll()
            sendReparto?.deleteAllFromRealm()
            val servicio: RealmResults<Servicio>? = realm.where(Servicio::class.java).findAll()
            servicio?.deleteAllFromRealm()
            val suministroReconexion: RealmResults<SuministroReconexion>? = realm.where(SuministroReconexion::class.java).findAll()
            suministroReconexion?.deleteAllFromRealm()
            val registroRecibo: RealmResults<RegistroRecibo>? = realm.where(RegistroRecibo::class.java).findAll()
            registroRecibo?.deleteAllFromRealm()
            val c: RealmResults<GrandesClientes>? = realm.where(GrandesClientes::class.java).findAll()
            c?.deleteAllFromRealm()
        }
    }

    override fun deleteAllReparto() {
        realm.executeTransaction {
            val sendReparto: RealmResults<SendReparto>? = realm.where(SendReparto::class.java).findAll()
            val photoReparto: RealmResults<PhotoReparto>? = realm.where(PhotoReparto::class.java).findAll()
            sendReparto?.deleteAllFromRealm()
            photoReparto?.deleteAllFromRealm()
        }
    }
}
