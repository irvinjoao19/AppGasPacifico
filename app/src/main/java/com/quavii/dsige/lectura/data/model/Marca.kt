package com.quavii.dsige.lectura.data.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Marca : RealmObject() {
    @PrimaryKey
    var marcaMedidorId: Int = 0
    var nombre: String = ""
}