package com.quavii.dsige.lectura.data.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Motivo : RealmObject {

    @PrimaryKey
    var motivoId: Int = 0
    var grupo: String = ""
    var codigo: Int = 0
    var descripcion: String = ""

    constructor() : super()
}