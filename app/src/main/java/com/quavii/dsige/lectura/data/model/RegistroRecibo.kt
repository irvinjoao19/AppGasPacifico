package com.quavii.dsige.lectura.data.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class RegistroRecibo : RealmObject() {

    @PrimaryKey
    var reciboId: Int = 0
    var repartoId: Int = 0
    var operarioId: Int = 0
    var tipo: Int = 0
    var ciclo: String = ""
    var year: Int = 0
    var piso: Int = 0
    var formatoVivienda: Int = 0
    var nombreformatoVivienda: String = ""
    var otrosVivienda: String = ""
    var formatoCargoColor: Int = 0
    var nombreformatoCargoColor: String = ""
    var otrosCargoColor: String = ""
    var formatoCargoPuerta: Int = 0
    var nombreformatoCargoPuerta: String = ""
    var otrosCargoPuerta: String = ""
    var formatoCargoColorPuerta: Int = 0
    var nombreformatoCargoColorPuerta: String = ""
    var otrosCargoColorPuerta: String = ""
    var formatoCargoRecibo: Int = 0
    var nombreformatoCargoRecibo: String = ""
    var dniCargoRecibo: String = ""
    var parentesco: String = ""
    var formatoCargoDevuelto: Int = 0
    var nombreformatoCargoDevuelto: String = ""
    var fechaMax: String = ""
    var fechaEntrega: String = ""
    var observacionCargo: String = ""
    var firmaCliente: String = ""
}