package com.quavii.dsige.lectura.data.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Photo : RealmObject {

    @PrimaryKey
    var iD_Foto: Int = 0
    var conformidad: Int = 0 // TODO se usara para mostrar la foto de acta de conformidad 0 = normal , 1 = acta , 2 = firma
    var iD_Suministro: Int = 0
    var rutaFoto: String = ""
    var fecha_Sincronizacion_Android: String = ""
    var tipo: Int = 0
    var estado: Int = 0
    var latitud: String = ""
    var longitud: String = ""
    var firm: Int = 0
    var tipoFirma: String = ""

    constructor() : super()

    constructor(iD_Foto: Int, conformidad: Int, iD_Suministro: Int, rutaFoto: String, fecha_Sincronizacion_Android: String, tipo: Int, estado: Int, latitud: String, longitud: String) : super() {
        this.iD_Foto = iD_Foto
        this.conformidad = conformidad
        this.iD_Suministro = iD_Suministro
        this.rutaFoto = rutaFoto
        this.fecha_Sincronizacion_Android = fecha_Sincronizacion_Android
        this.tipo = tipo
        this.estado = estado
        this.latitud = latitud
        this.longitud = longitud
    }
}