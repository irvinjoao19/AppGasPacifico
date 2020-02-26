package com.quavii.dsige.lectura.data.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class SuministroReparto : RealmObject() {

    @PrimaryKey
    var id_Reparto: Int = 0
    var id_Operario_Reparto: Int = 0
    var foto_Reparto: Int = 0
    var id_observacion: Int = 0
    var Suministro_Medidor_reparto: String = ""
    var Suministro_Numero_reparto: String = ""
    var Direccion_Reparto: String = ""
    var Cod_Orden_Reparto: String = ""
    var Cod_Actividad_Reparto: String = ""
    var Cliente_Reparto: String = ""
    var CodigoBarra: String = ""
    var estado: Int = 0
    var activo: Int = 0
    var latitud: String = ""
    var longitud: String = ""
    var telefono: String = ""
    var nota: String = ""
}