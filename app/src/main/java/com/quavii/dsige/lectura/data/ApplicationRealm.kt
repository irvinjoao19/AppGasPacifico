package com.quavii.dsige.lectura.data

import android.annotation.SuppressLint
import android.app.Application
import io.realm.Realm
import io.realm.RealmConfiguration

@SuppressLint("Registered")
open class ApplicationRealm : Application() {

    override fun onCreate() {
        super.onCreate()

        Realm.init(applicationContext)

        val config = RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build()
        Realm.setDefaultConfiguration(config)
    }

//    override fun attachBaseContext(base: Context) {
//        super.attachBaseContext(base)
//        MultiDex.install(this)
//    }
}
