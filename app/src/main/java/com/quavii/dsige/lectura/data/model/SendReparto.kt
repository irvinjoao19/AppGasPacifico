package com.quavii.dsige.lectura.data.model

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class SendReparto : RealmObject {

    @PrimaryKey
    var id_Reparto: Int? = 0
    var id_Operario_Reparto: Int? = 0
    var id_observacion: Int? = 0
    var registro_fecha_sqlite: String? = null
    var registro_latitud: String? = null
    var registro_longitud: String? = null
    var estado: Int? = 0
    var reparto_foto: RealmList<PhotoReparto>? = null

    constructor() : super()

    constructor(id_Reparto: Int?, id_Operario_Reparto: Int?, id_observacion: Int?, registro_fecha_sqlite: String?, registro_latitud: String?, registro_longitud: String?, estado: Int?) : super() {
        this.id_Reparto = id_Reparto
        this.id_Operario_Reparto = id_Operario_Reparto
        this.id_observacion = id_observacion
        this.registro_fecha_sqlite = registro_fecha_sqlite
        this.registro_latitud = registro_latitud
        this.registro_longitud = registro_longitud
        this.estado = estado
    }


}