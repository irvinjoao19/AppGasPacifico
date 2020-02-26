package com.quavii.dsige.lectura.data.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Mega : RealmObject {

    @PrimaryKey
    var megaId: Int? = 0
    var bytes: Int? = 0
    var usuario: Int? = 0
    var fecha: String? = ""

    constructor() : super()

    constructor(megaId: Int?, bytes: Int?, usuario: Int?, fecha: String?) : super() {
        this.megaId = megaId
        this.bytes = bytes
        this.usuario = usuario
        this.fecha = fecha
    }


}