package com.quavii.dsige.lectura.data.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey


open class Parametro : RealmObject {

    @PrimaryKey
    var id_Configuracion: Int = 0
    var nombre_parametro: String? = null
    var valor: Int = 0

    constructor()

    constructor(id_Configuracion: Int, nombre_parametro: String, valor: Int) {
        this.id_Configuracion = id_Configuracion
        this.nombre_parametro = nombre_parametro
        this.valor = valor
    }
}
