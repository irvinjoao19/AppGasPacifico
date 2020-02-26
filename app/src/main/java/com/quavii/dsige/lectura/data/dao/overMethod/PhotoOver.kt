package com.quavii.dsige.lectura.data.dao.overMethod

import androidx.lifecycle.LiveData
import com.quavii.dsige.lectura.data.RealmLiveData
import com.quavii.dsige.lectura.data.dao.interfaces.PhotoImplementation
import com.quavii.dsige.lectura.data.model.Photo
import com.quavii.dsige.lectura.data.model.Registro
import io.reactivex.Completable
import io.realm.Case
import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort

class PhotoOver(private val realm: Realm) : PhotoImplementation {

    override fun delete(id: Int, tipo: Int) {
        realm.executeTransaction {
            val photo: Photo? = realm.where(Photo::class.java).equalTo("iD_Foto", id).findFirst()

            if (tipo == 10) {
                if (photo != null) {
                    photo.estado = 2
                }
            } else {
                photo?.deleteFromRealm()
            }
        }
    }

    override fun deleteByName(name: String) {
        realm.executeTransaction {
            val photo: Photo? = realm.where(Photo::class.java).equalTo("rutaFoto", name).findFirst()
            photo?.deleteFromRealm()
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

    override fun save(photo: Photo) {
        realm.executeTransaction { realm ->
            val registro: Registro? = realm.where(Registro::class.java).equalTo("iD_Suministro", photo.iD_Suministro).findFirst()
            if (registro != null) {
                realm.copyToRealmOrUpdate(photo)
                registro.photos?.add(photo)
            }
        }
    }

    override fun photoAllBySuministro(id: Int, tipo: Int, conformidad: Int): RealmResults<Photo> {
        val estado = 2
        return realm.where(Photo::class.java)
                .notEqualTo("estado", estado)
                .equalTo("iD_Suministro", id)
                .equalTo("tipo", tipo)
                .equalTo("conformidad", conformidad)
                .findAll().sort("iD_Foto", Sort.ASCENDING)
    }

    override fun getPhotoAll(estado: Int): RealmResults<Photo> {
        return realm.where(Photo::class.java).equalTo("estado", estado).findAll()
    }

    override fun getPhotoAllLiveData(estado: Int): LiveData<RealmResults<Photo>> {
        return RealmLiveData(realm.where(Photo::class.java).equalTo("estado", estado).findAllAsync())
    }

    override fun getPhotoFirmLiveData(id: Int, firm: Int): LiveData<RealmResults<Photo>> {
        return RealmLiveData(realm.where(Photo::class.java).equalTo("iD_Suministro", id).equalTo("firm", firm).findAllAsync())
    }

    override fun getPhotoFirm(id: Int, firm: Int): RealmResults<Photo> {
        return realm.where(Photo::class.java).equalTo("iD_Suministro", id).equalTo("firm", firm).findAll()
    }

    override fun photoById(id: Int): Photo {
        return realm.where(Photo::class.java).equalTo("iD_Foto", id).findFirst()!!
    }

    override fun closePhoto(photo: Photo, estado: Int) {
        realm.executeTransaction {
            photo.estado = estado
        }
    }

    override fun saveEjemplo(photo: Photo) {
        realm.executeTransaction { realm ->
            realm.copyToRealmOrUpdate(photo)
        }
    }

    override fun getPhotoEjemplo(name: String): RealmResults<Photo>? {
        return realm.where(Photo::class.java).beginGroup().contains("rutaFoto", name, Case.INSENSITIVE).endGroup().findAll()
    }

    override fun insertOrUpdatePhoto(p: Photo): Completable {
        return Completable.fromAction {
            Realm.getDefaultInstance().use { realm ->
                realm.executeTransaction { r ->
                    val registro: Registro? = r.where(Registro::class.java).equalTo("iD_Suministro", p.iD_Suministro).findFirst()
                    if (registro != null) {
                        r.copyToRealmOrUpdate(p)
                        registro.photos?.add(p)
                    }
                }
            }
        }
    }

    override fun deletePhoto(p : Int): Completable {
        return Completable.fromAction {
            Realm.getDefaultInstance().use { realm ->
                realm.executeTransaction { r ->
                    val photo = r.where(Photo::class.java).equalTo("iD_Foto", p).findFirst()
                    photo?.deleteFromRealm()
                }
            }
        }
    }
}