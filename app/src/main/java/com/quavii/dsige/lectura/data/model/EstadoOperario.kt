package com.quavii.dsige.lectura.data.model

open class EstadoOperario {

    private var operarioId: Int? = 0
    private var latitud: String? = ""
    private var longitud: String? = ""
    private var fechaGPD: String? = ""
    private var fecha: String? = ""

    constructor()

    constructor(operarioId: Int?, latitud: String?, longitud: String?, fechaGPD: String?, fecha: String?) {
        this.operarioId = operarioId
        this.latitud = latitud
        this.longitud = longitud
        this.fechaGPD = fechaGPD
        this.fecha = fecha
    }

}