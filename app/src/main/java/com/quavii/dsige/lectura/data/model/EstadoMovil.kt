package com.quavii.dsige.lectura.data.model


open class EstadoMovil {

    private var operarioId: Int? = 0
    private var gpsActivo: Int? = 0
    private var estadoBateria: Int? = 0
    private var fecha: String? = ""
    private var modoAvion: Int? = 0
    private var planDatos: Int? = 0

    constructor(operarioId: Int?, gpsActivo: Int?, estadoBateria: Int?, fecha: String?, modoAvion: Int?, planDatos: Int?) {
        this.operarioId = operarioId
        this.gpsActivo = gpsActivo
        this.estadoBateria = estadoBateria
        this.fecha = fecha
        this.modoAvion = modoAvion
        this.planDatos = planDatos
    }

    constructor()
}