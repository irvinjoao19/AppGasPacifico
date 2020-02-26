package com.quavii.dsige.lectura.data.model

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Registro : RealmObject {

    @PrimaryKey
    var id: Int = 0
    var iD_Registro: Int = 0
    var iD_Operario: Int = 0
    var iD_Suministro: Int = 0  // TODO --> suministro_medidor en tipo 6 // EN REPARTO ID_OOBSERVACION
    var suministro_Numero: Int = 0
    var iD_TipoLectura: Int = 0
    var registro_Fecha_SQLITE: String = ""
    var registro_Latitud: String = ""
    var registro_Longitud: String = ""
    var registro_Lectura: String = ""
    var registro_Confirmar_Lectura: String = ""
    var registro_Observacion: String = ""
    var grupo_Incidencia_Codigo: String = ""
    var registro_TieneFoto: String = ""
    var registro_TipoProceso: String = ""
    var fecha_Sincronizacion_Android: String = ""
    var registro_Constancia: String = ""
    var registro_Desplaza: String = ""
    var codigo_Resultado: String = ""
    var tipo: Int = 0
    var orden: Int = 0
    var estado: Int = 0
    var horaActa: String = ""
    var suministroCliente: String = ""
    var suministroDireccion: String = ""
    var lecturaManual: Int = 0
    var motivoId: Int = 0
    var parentId: Int = 0
    var responsable: String = ""
    var parentesco: String = ""
    var precinto: String = ""
    var photos: RealmList<Photo>? = null
    var recibo: RegistroRecibo? = null

    constructor() : super()

    constructor(id: Int, iD_Registro: Int, iD_Operario: Int, iD_Suministro: Int, suministro_Numero: Int, iD_TipoLectura: Int, registro_Fecha_SQLITE: String, registro_Latitud: String, registro_Longitud: String, registro_Lectura: String, registro_Confirmar_Lectura: String, registro_Observacion: String, grupo_Incidencia_Codigo: String, registro_TieneFoto: String, registro_TipoProceso: String, fecha_Sincronizacion_Android: String, registro_Constancia: String, registro_Desplaza: String, codigo_Resultado: String, tipo: Int, orden: Int, estado: Int, lecturaManual: Int, motivoId: Int, parentId: Int, responsable: String, parentesco: String,precinto:String) : super() {
        this.id = id
        this.iD_Registro = iD_Registro
        this.iD_Operario = iD_Operario
        this.iD_Suministro = iD_Suministro
        this.suministro_Numero = suministro_Numero
        this.iD_TipoLectura = iD_TipoLectura
        this.registro_Fecha_SQLITE = registro_Fecha_SQLITE
        this.registro_Latitud = registro_Latitud
        this.registro_Longitud = registro_Longitud
        this.registro_Lectura = registro_Lectura
        this.registro_Confirmar_Lectura = registro_Confirmar_Lectura
        this.registro_Observacion = registro_Observacion
        this.grupo_Incidencia_Codigo = grupo_Incidencia_Codigo
        this.registro_TieneFoto = registro_TieneFoto
        this.registro_TipoProceso = registro_TipoProceso
        this.fecha_Sincronizacion_Android = fecha_Sincronizacion_Android
        this.registro_Constancia = registro_Constancia
        this.registro_Desplaza = registro_Desplaza
        this.codigo_Resultado = codigo_Resultado
        this.tipo = tipo
        this.orden = orden
        this.estado = estado
        this.lecturaManual = lecturaManual
        this.motivoId = motivoId
        this.parentId = parentId
        this.responsable = responsable
        this.parentesco = parentesco
        this.precinto = precinto
    }

    // TODO SOBRE SUMINISTRO ENCONTRADO
    constructor(id: Int, iD_Operario: Int, iD_Suministro: Int, registro_Fecha_SQLITE: String, registro_Observacion: String, tipo: Int, estado: Int, suministroCliente: String, suministroDireccion: String, registro_Constancia: String) : super() {
        this.id = id
        this.iD_Operario = iD_Operario
        this.iD_Suministro = iD_Suministro
        this.registro_Fecha_SQLITE = registro_Fecha_SQLITE
        this.registro_Observacion = registro_Observacion
        this.tipo = tipo
        this.estado = estado
        this.suministroCliente = suministroCliente
        this.suministroDireccion = suministroDireccion
        this.registro_Constancia = registro_Constancia
    }

    // TODO SOBRE ZONA PELIGROSA
    constructor(id: Int, iD_Operario: Int, iD_Suministro: Int, registro_Fecha_SQLITE: String, registro_Latitud: String, registro_Longitud: String, tipo: Int, estado: Int, registro_Observacion: String, photos: RealmList<Photo>) : super() {
        this.id = id
        this.iD_Operario = iD_Operario
        this.iD_Suministro = iD_Suministro
        this.registro_Fecha_SQLITE = registro_Fecha_SQLITE
        this.registro_Latitud = registro_Latitud
        this.registro_Longitud = registro_Longitud
        this.tipo = tipo
        this.estado = estado
        this.registro_Observacion = registro_Observacion
        this.photos = photos
    }

    // TODO SOBRE REPARTO
    constructor(id: Int, iD_Suministro: Int, iD_Operario: Int, registro_Fecha_SQLITE: String, registro_Latitud: String, registro_Longitud: String, registro_Observacion: String, tipo: Int, estado: Int) : super() {
        this.id = id
        this.iD_Suministro = iD_Suministro
        this.iD_Operario = iD_Operario
        this.registro_Fecha_SQLITE = registro_Fecha_SQLITE
        this.registro_Latitud = registro_Latitud
        this.registro_Longitud = registro_Longitud
        this.registro_Observacion = registro_Observacion
        this.tipo = tipo
        this.estado = estado
    }

    // TODO SOBRE SELFI
    constructor(id: Int, iD_Operario: Int, iD_Suministro: Int, registro_Fecha_SQLITE: String, registro_Latitud: String, registro_Longitud: String, tipo: Int, estado: Int, registro_Observacion: String, codigo_Resultado: String, photos: RealmList<Photo>) : super() {
        this.id = id
        this.iD_Operario = iD_Operario
        this.iD_Suministro = iD_Suministro
        this.registro_Fecha_SQLITE = registro_Fecha_SQLITE
        this.registro_Latitud = registro_Latitud
        this.registro_Longitud = registro_Longitud
        this.tipo = tipo
        this.registro_TipoProceso = tipo.toString()
        this.estado = estado
        this.registro_Observacion = registro_Observacion
        this.codigo_Resultado = codigo_Resultado
        this.photos = photos
    }
}