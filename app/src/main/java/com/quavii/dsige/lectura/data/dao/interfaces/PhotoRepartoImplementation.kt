package com.quavii.dsige.lectura.data.dao.interfaces

import com.quavii.dsige.lectura.data.model.Photo
import io.realm.RealmResults

interface PhotoRepartoImplementation {

//    fun save(photo: Photo)

    fun delete(id: Int)

    fun deleteSelfi(name: String)

    fun deleteSelfiRegistro(name: String)

    fun getPhotoIdentity(): Int

    fun photoAllById(id: Int, tipo: Int): RealmResults<Photo>

    fun getPhotoAll(estado: Int): RealmResults<Photo>?

    fun photoById(id: Int): Photo

    fun closePhoto(photo:Photo,estado: Int)

}