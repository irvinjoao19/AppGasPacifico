package com.quavii.dsige.lectura.data.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class PhotoReparto: RealmObject{

    @PrimaryKey
    var iD_Foto: Int? = 0
    var id_Registro: Int? = 0
    var id_Reparto: Int? = 0
    var rutaFoto: String? = null
    var fecha_Sincronizacion_Android: String? = null
    var tipo: Int? = 0
    var estado: Int = 0

    constructor() : super()

    constructor(iD_Foto: Int?, id_Registro: Int?, id_Reparto: Int?, rutaFoto: String?, fecha_Sincronizacion_Android: String?, tipo: Int?, estado: Int) : super() {
        this.iD_Foto = iD_Foto
        this.id_Registro = id_Registro
        this.id_Reparto = id_Reparto
        this.rutaFoto = rutaFoto
        this.fecha_Sincronizacion_Android = fecha_Sincronizacion_Android
        this.tipo = tipo
        this.estado = estado
    }

}