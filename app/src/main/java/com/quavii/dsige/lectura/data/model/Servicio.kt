package com.quavii.dsige.lectura.data.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Servicio : RealmObject() {

    @PrimaryKey
    var id_servicio: Int = 0
    var nombre_servicio: String = ""
    var estado: Int = 0
    var size: Int = 0
}
