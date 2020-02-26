package com.quavii.dsige.lectura.data.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class GrandesClientes : RealmObject() {

    @PrimaryKey
    var clienteId: Int = 0
    var fechaImportacion: String = ""
    var archivoImportacion: String = ""
    var codigoEMR: String = ""
    var nombreCliente: String = ""
    var direccion: String = ""
    var distrito: String = ""
    var fechaAsignacion: String = ""
    var fechaEnvioCelular: String = ""
    var operarioId: Int = 0
    var ordenLectura: Int = 0
    var fechaRegistroInicio: String = ""
    var clientePermiteAcceso: String = ""
    var fotoConstanciaPermiteAcceso: String = ""
    var porMezclaExplosiva: String = ""
    var vManoPresionEntrada: String = ""
    var fotovManoPresionEntrada: String = ""
    var marcaCorrectorId: Int = 0
    var fotoMarcaCorrector: String = ""
    var vVolumenSCorreUC: String = ""
    var confirmarVolumenSCorreUC: String = ""
    var fotovVolumenSCorreUC: String = ""
    var vVolumenSCorreMedidor: String = ""
    var fotovVolumenSCorreMedidor: String = ""
    var vVolumenRegUC: String = ""
    var fotovVolumenRegUC: String = ""
    var vPresionMedicionUC: String = ""
    var fotovPresionMedicionUC: String = ""
    var vTemperaturaMedicionUC: String = ""
    var fotovTemperaturaMedicionUC: String = ""
    var tiempoVidaBateria: String = ""
    var fotoTiempoVidaBateria: String = ""
    var fotoPanomarica: String = ""
    var tieneGabinete: String = ""
    var foroSitieneGabinete: String = ""
    var presenteCliente: String = ""
    var contactoCliente: String = ""
    var latitud: String = ""
    var longitud: String = ""
    var estado: Int = 0
    var comentario: String = ""
}