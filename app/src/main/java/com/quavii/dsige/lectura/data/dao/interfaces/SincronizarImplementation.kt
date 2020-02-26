package com.quavii.dsige.lectura.data.dao.interfaces

import com.quavii.dsige.lectura.data.model.Sincronizar
import com.quavii.dsige.lectura.data.model.SuministroLectura

interface SincronizarImplementation {

    fun save(sincronizar: Sincronizar, activo: Int)

    fun saveLecturasEncontradas(suministroLecturas: List<SuministroLectura>, activo: Int)
}
