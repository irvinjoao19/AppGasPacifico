package com.quavii.dsige.lectura.data.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class DetalleGrupo : RealmObject() {

    @PrimaryKey
    var id: Int = 0
    var iD_DetalleGrupo: Int = 0
    var grupo: String = ""
    var codigo: String = ""
    var descripcion: String = ""
    var abreviatura: String = ""
    var estado: String = ""
    var descripcionGrupo: String = ""
    var pideFoto: String = ""
    var noPideFoto: String = ""
    var pideLectura: String = ""
    var id_Servicio: Int = 0
    var parentId: Int = 0
    var ubicaMedidor: Int = 0
}