package com.quavii.dsige.lectura.data.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Operario : RealmObject {

    @PrimaryKey
    var operarioId: Int = 0
    var operarioNombre: String = ""
    var lecturaManual: Int = 0

    constructor() : super()

    constructor(operarioId: Int, operarioNombre: String, lecturaManual: Int) : super() {
        this.operarioId = operarioId
        this.operarioNombre = operarioNombre
        this.lecturaManual = lecturaManual
    }


}