package com.quavii.dsige.lectura.data.model

open class Mensaje {

    var codigo: Int = 0
    var mensaje: String = ""

    constructor()

    constructor(codigo: Int, mensaje: String) {
        this.codigo = codigo
        this.mensaje = mensaje
    }

}