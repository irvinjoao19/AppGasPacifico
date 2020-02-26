package com.quavii.dsige.lectura.data.dao.interfaces

import androidx.lifecycle.LiveData
import com.quavii.dsige.lectura.data.model.Photo
import io.reactivex.Completable
import io.realm.RealmResults

interface PhotoImplementation {

    fun save(photo: Photo)

    fun delete(id: Int, tipo: Int)

    fun deleteByName(name: String)

    fun getPhotoIdentity(): Int

    fun photoAllBySuministro(id: Int, tipo: Int, conformidad: Int): RealmResults<Photo>

    fun getPhotoAll(estado: Int): RealmResults<Photo>

    fun getPhotoAllLiveData(estado: Int): LiveData<RealmResults<Photo>>

    fun getPhotoFirmLiveData(id: Int, firm: Int): LiveData<RealmResults<Photo>>

    fun getPhotoFirm(id: Int, firm: Int): RealmResults<Photo>

    fun photoById(id: Int): Photo

    fun closePhoto(photo: Photo, estado: Int)

    fun saveEjemplo(photo: Photo)

    fun getPhotoEjemplo(name: String): RealmResults<Photo>?

    // TODO NUEVO

    fun insertOrUpdatePhoto(p: Photo): Completable

    fun deletePhoto(p: Int): Completable

}