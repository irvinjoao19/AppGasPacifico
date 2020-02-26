package com.quavii.dsige.lectura.data.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Estado : RealmObject() {

    @PrimaryKey
    var estadoId: Int = 0
    var nombre: String = ""
    var abreviatura: String = ""
    var grupo: Int = 0
    var estado: Int = 0

}