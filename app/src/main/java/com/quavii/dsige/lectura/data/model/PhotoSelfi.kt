package com.quavii.dsige.lectura.data.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class PhotoSelfi : RealmObject {
    @PrimaryKey
    var id: Int? = 0
    var idUser: Int? = 0
    var namePhoto: String? = null
    var dateStart: String? = null
    var dateEnd: String? = null
    var state: Int? = 0

    constructor(): super()

    constructor(id: Int?, idUser: Int?, namePhoto: String?, dateStart: String?, dateEnd: String?, state: Int?) : super() {
        this.id = id
        this.idUser = idUser
        this.namePhoto = namePhoto
        this.dateStart = dateStart
        this.dateEnd = dateEnd
        this.state = state
    }
}