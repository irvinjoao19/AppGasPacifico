package com.quavii.dsige.lectura.data.dao.interfaces

import com.quavii.dsige.lectura.data.model.PhotoSelfi

interface PhotoSelfiImplementation {

    fun save(photo: PhotoSelfi)

    fun delete(id: Int)

    fun getPhotoIdentity(): Int

    fun photoById(id: Int): PhotoSelfi

    fun closePhoto(photo:PhotoSelfi,estado: Int)

}