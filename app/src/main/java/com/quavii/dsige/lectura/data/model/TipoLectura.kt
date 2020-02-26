package com.quavii.dsige.lectura.data.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class TipoLectura : RealmObject {

    @PrimaryKey
    var iD_TipoLectura: Int = 0
    var tipoLectura_Descripcion: String? = null
    var tipoLectura_Abreviatura: String? = null
    var tipoLectura_Estado: String? = null

    constructor() : super()

    constructor(iD_TipoLectura: Int, tipoLectura_Descripcion: String?, tipoLectura_Abreviatura: String?, tipoLectura_Estado: String?) : super() {
        this.iD_TipoLectura = iD_TipoLectura
        this.tipoLectura_Descripcion = tipoLectura_Descripcion
        this.tipoLectura_Abreviatura = tipoLectura_Abreviatura
        this.tipoLectura_Estado = tipoLectura_Estado
    }
}