package com.quavii.dsige.lectura.data.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Login : RealmObject {

    @PrimaryKey
    var iD_Operario: Int = 0
    var operario_Login: String = ""
    var operario_Nombre: String = ""
    var operario_EnvioEn_Linea: Int = 0
    var tipoUsuario: String = ""
    var estado: String = ""
    var lecturaManual: Int = 0
    var mensaje: String = ""

    constructor()

    constructor(iD_Operario: Int, operario_Login: String, operario_Nombre: String, operario_EnvioEn_Linea: Int, tipoUsuario: String, estado: String, lecturaManual: Int, mensaje: String) : super() {
        this.iD_Operario = iD_Operario
        this.operario_Login = operario_Login
        this.operario_Nombre = operario_Nombre
        this.operario_EnvioEn_Linea = operario_EnvioEn_Linea
        this.tipoUsuario = tipoUsuario
        this.estado = estado
        this.lecturaManual = lecturaManual
        this.mensaje = mensaje
    }
}
