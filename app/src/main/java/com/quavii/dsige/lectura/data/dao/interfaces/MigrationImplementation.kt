package com.quavii.dsige.lectura.data.dao.interfaces

import com.quavii.dsige.lectura.data.model.Migration

interface MigrationImplementation {

    fun save(migration: Migration)

    fun deleteAll()

    fun deleteAllReparto()

}