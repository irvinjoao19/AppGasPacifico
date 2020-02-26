package com.quavii.dsige.lectura.data.dao.overMethod

import com.quavii.dsige.lectura.data.dao.interfaces.PhotoRepartoImplementation
import com.quavii.dsige.lectura.data.model.Photo
import com.quavii.dsige.lectura.data.model.Registro
import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort

class PhotoRepartoOver(private val realm: Realm) : PhotoRepartoImplementation {

    override fun delete(id: Int) {
        realm.executeTransaction {
            val photo: Photo? = realm.where(Photo::class.java).equalTo("iD_Foto", id).findFirst()
            photo?.deleteFromRealm()
        }
    }
    override fun deleteSelfi(name: String) {
        realm.executeTransaction {
            val photo: Photo? = realm.where(Photo::class.java).equalTo("rutaFoto", name).findFirst()
            photo?.deleteFromRealm()
        }
    }
    override fun deleteSelfiRegistro(name: String) {
        realm.executeTransaction {
            val registro: Registro? = realm.where(Registro::class.java).equalTo("codigo_Resultado", name).findFirst()
            registro?.deleteFromRealm()
        }
    }

    override fun getPhotoIdentity(): Int {
        val photo = realm.where(Photo::class.java).max("iD_Foto")
        val result: Int
        result = if (photo == null) {
            1
        } else {
            photo.toInt() + 1
        }
        return result
    }

//    override fun save(photo: Photo) {
//        realm.executeTransaction { realm ->
//            val reparto: Registro? = realm.where(SendReparto::class.java).equalTo("id_Reparto", photo.id_Reparto).findFirst()
//            if (reparto != null) {
//                realm.copyToRealmOrUpdate(photo)
//                reparto.reparto_foto?.add(photo)
//            }
//        }
//    }

    override fun photoAllById(id: Int, tipo: Int): RealmResults<Photo> {
        return realm.where(Photo::class.java).equalTo("iD_Suministro", id).equalTo("tipo", tipo).findAll().sort("iD_Foto", Sort.ASCENDING)
    }

    override fun getPhotoAll(estado: Int): RealmResults<Photo>? {
        return realm.where(Photo::class.java).equalTo("estado", estado).findAll()
    }

    override fun photoById(id: Int): Photo {
        return realm.where(Photo::class.java).equalTo("iD_Foto", id).findFirst()!!
    }

    override fun closePhoto(photo: Photo, estado: Int) {
        realm.executeTransaction{
            photo.estado = estado
        }
    }
}