package com.quavii.dsige.lectura.data.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class SuministroLectura : RealmObject() {

    @PrimaryKey
    var iD_Suministro: Int = 0
    var suministro_Numero: String = ""
    var suministro_Medidor: String = ""
    var suministro_Cliente: String = ""
    var suministro_Direccion: String = ""
    var suministro_UnidadLectura: String = ""
    var suministro_TipoProceso: String = ""
    var suministro_LecturaMinima: String = ""
    var suministro_LecturaMaxima: String = ""
    var suministro_Fecha_Reg_Movil: String = ""
    var suministro_UltimoMes: String = ""
    var consumoPromedio: Double = 0.toDouble()
    var lecturaAnterior: String = ""
    var suministro_Instalacion: String = ""
    var valida1: Int = 0
    var valida2: Int = 0
    var valida3: Int = 0
    var valida4: Int = 0
    var valida5: Int = 0
    var valida6: Int = 0
    var tipoCliente: Int = 0
    var estado: Int = 0
    var suministroOperario_Orden: Int = 0
    var orden: Int = 0
    var activo: Int = 0
    var flagObservada: Int = 0
    var latitud: String = ""
    var longitud: String = ""
    var telefono: String = ""
    var nota: String = ""
    var fechaAsignacion: String = ""
}
