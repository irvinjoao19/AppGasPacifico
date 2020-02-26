package com.quavii.dsige.lectura.data.model

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Sincronizar : RealmObject {

    @PrimaryKey
    var sincronizarId: Int = 0
    var suministrosCortes: RealmList<SuministroCortes>? = null
    var suministroReconexion: RealmList<SuministroReconexion>? = null

    constructor() : super()

    constructor(sincronizarId: Int, suministrosCortes: RealmList<SuministroCortes>?, suministroReconexion: RealmList<SuministroReconexion>?) : super() {
        this.sincronizarId = sincronizarId
        this.suministrosCortes = suministrosCortes
        this.suministroReconexion = suministroReconexion
    }
}
