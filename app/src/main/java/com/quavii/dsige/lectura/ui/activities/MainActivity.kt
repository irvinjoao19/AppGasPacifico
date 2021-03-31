package com.quavii.dsige.lectura.ui.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider.getUriForFile
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson
import com.quavii.dsige.lectura.BuildConfig
import com.quavii.dsige.lectura.R
import com.quavii.dsige.lectura.data.apiServices.ApiServices
import com.quavii.dsige.lectura.data.apiServices.ConexionRetrofit
import com.quavii.dsige.lectura.data.dao.interfaces.*
import com.quavii.dsige.lectura.data.dao.overMethod.*
import com.quavii.dsige.lectura.data.model.Login
import com.quavii.dsige.lectura.data.model.Migration
import com.quavii.dsige.lectura.data.model.Registro
import com.quavii.dsige.lectura.data.model.Servicio
import com.quavii.dsige.lectura.helper.MessageError
import com.quavii.dsige.lectura.helper.Permission
import com.quavii.dsige.lectura.helper.Util
import com.quavii.dsige.lectura.ui.fragments.SendFragment
import com.quavii.dsige.lectura.ui.fragments.StartFragment
import com.quavii.dsige.lectura.ui.services.*
import io.reactivex.Completable
import io.reactivex.CompletableObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import io.realm.RealmResults

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.nav_header_main.view.*
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    lateinit var migrationInterface: ApiServices
    lateinit var realm: Realm
    lateinit var loginImp: LoginImplementation
    lateinit var servicioImp: ServicioImplementation
    lateinit var migrationImp: MigrationImplementation
    lateinit var photoImp: PhotoImplementation
    lateinit var registroImp: RegistroImplementation

    lateinit var registros: RealmResults<Registro>
    lateinit var servicios: RealmResults<Servicio>

    lateinit var builder: AlertDialog.Builder
    lateinit var dialog: AlertDialog

    var usuarioId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        realm = Realm.getDefaultInstance()
        loginImp = LoginOver(realm)
        servicioImp = ServicioOver(realm)
        migrationImp = MigrationOver(realm)
        photoImp = PhotoOver(realm)
        registroImp = RegistroOver(realm)
        migrationInterface = ConexionRetrofit.api.create(ApiServices::class.java)
        existsUser(loginImp.login)
    }

    private fun existsUser(login: Login?) {
        if (login == null) {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        } else {
//            val instance = FirebaseRemoteConfig.getInstance()
//            instance.setConfigSettingsAsync(FirebaseRemoteConfigSettings.Builder().setMinimumFetchIntervalInSeconds(0).build())
//            val cacheExpiration = instance.info.configSettings.minimumFetchIntervalInSeconds
//            instance.fetch(cacheExpiration)
//                    .addOnCompleteListener(this) { t ->
//                        if (t.isSuccessful) {
//                            instance.activate()
//                            val isUpdate = instance.getBoolean(Util.KEY_UPDATE_ENABLE)
//                            if (isUpdate) {
//                                val version = instance.getString(Util.KEY_UPDATE_VERSION)
//                                val appVersion = Util.getVersion(this)
//                                val url = instance.getString(Util.KEY_UPDATE_URL)
//                                val name = instance.getString(Util.KEY_UPDATE_NAME)
//
//                                if (version != appVersion) {
//                                    updateAndroid(url, name, version)
//                                }
//                            }
//                        }
//                    }
            bindUI(login)
            servicios = servicioImp.servicioAll
            registros = registroImp.getAllRegistro(1)
        }
    }

    private fun bindUI(u: Login) {
        setSupportActionBar(toolbar)
        val toggle = ActionBarDrawerToggle(
                this@MainActivity,
                drawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navigationView.setNavigationItemSelectedListener(this@MainActivity)
        navigationView.menu.getItem(0).isVisible = true
        fragmentByDefault()
        getUser(u)
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.sync ->
                if (registros.size == 0) {
                    confirmMigration(usuarioId)
                } else {
                    Util.dialogMensaje(this, "Mensaje", "Tienes registros pendientes por enviar")
                }
            R.id.inicio -> changeFragment(StartFragment.newInstance("", ""), item.title.toString())
            R.id.envio -> changeFragment(SendFragment.newInstance("", ""), item.title.toString())
            R.id.logout -> {
                if (registros.size == 0) {
                    confirmLogOut()
                } else {
                    Util.dialogMensaje(this@MainActivity, "Mensaje", "Tienes registros pendientes por enviar")
                }
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun changeFragment(fragment: Fragment, title: String) {
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit()
        supportActionBar!!.title = title
    }

    private fun fragmentByDefault() {
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.content_frame, StartFragment.newInstance("", ""))
                .commit()
        supportActionBar!!.title = "Inicio de Actividades"
        navigationView.menu.getItem(0).isChecked = true
    }

    private fun getUser(u: Login) {
        val header = navigationView.getHeaderView(0)
        header.textViewName.text = u.operario_Nombre
        header.textViewEmail.text = String.format("Operario : %s", u.iD_Operario)
        header.textViewVersion.text = String.format("Versión : %s", Util.getVersion(this))
        usuarioId = u.iD_Operario
    }

    private fun load() {
        builder = AlertDialog.Builder(ContextThemeWrapper(this@MainActivity, R.style.AppTheme))
        @SuppressLint("InflateParams") val view =
                LayoutInflater.from(this@MainActivity).inflate(R.layout.dialog_login, null)
        builder.setView(view)
        val textViewTitle: TextView = view.findViewById(R.id.textViewTitle)
        textViewTitle.text = String.format("%s", "Cerrando Sesión")
        dialog = builder.create()
        dialog.setCanceledOnTouchOutside(false)
        dialog.setCancelable(false)
        dialog.show()
    }

    private fun confirmLogOut() {
        val materialDialog = MaterialAlertDialogBuilder(this)
                .setTitle("Advertencia")
                .setMessage(String.format("%s", "Al cerrar sesión estaras eliminando todo tus avances\nEn caso que no lo tengas clic en aceptar."))
                .setPositiveButton("Aceptar") { dialog, _ ->
                    stopServices()
                    load()
                    logout()
                    dialog.dismiss()
                }
                .setNegativeButton("Cancelar") { dialog, _ ->
                    dialog.cancel()
                }
        materialDialog.show()
    }

    private fun stopServices() {
        stopService(Intent(this, SendRegisterService::class.java))
        stopService(Intent(this, SyncCortesReconexionesService::class.java))
        stopService(Intent(this, EnableGpsService::class.java))
        stopService(Intent(this, SendLocationService::class.java))
        stopService(Intent(this, SendDataMovilService::class.java))
        stopService(Intent(this, DistanceService::class.java))
    }

    private fun logout() {
        val a = Completable.fromAction {
            Realm.getDefaultInstance().use { realm ->
                val loginImp: LoginImplementation = LoginOver(realm)
                val migrationImp: MigrationImplementation = MigrationOver(realm)
                migrationImp.deleteAll()
                loginImp.delete()
            }
        }
        a.subscribeOn(Schedulers.io())
                .delay(2000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : CompletableObserver {
                    override fun onComplete() {
                        if (dialog.isShowing) {
                            dialog.dismiss()
                        }
                        logOut()
                        exitProcess(0)
                    }

                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onError(e: Throwable) {
                        Log.i("TAG", e.toString())
                    }

                })
    }

    private fun logOut() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun confirmMigration(operarioId: Int?) {
        val materialDialog = MaterialAlertDialogBuilder(this)
                .setTitle("Mensaje")
                .setMessage(String.format("%s", "Si cuentas con datos se borraran !.\nDeseas Sincronizar ?."))
                .setPositiveButton("Sincronizar") { dialog, _ ->
                    SyncData().execute(operarioId)
                    dialog.dismiss()
                }
                .setNegativeButton("Cancelar") { dialog, _ ->
                    dialog.cancel()
                }
        materialDialog.show()
    }

    @SuppressLint("StaticFieldLeak")
    private inner class SyncData : AsyncTask<Int, Void, String>() {
        private lateinit var builder: AlertDialog.Builder
        private lateinit var dialog: AlertDialog

        override fun onPreExecute() {
            super.onPreExecute()
            builder = AlertDialog.Builder(ContextThemeWrapper(this@MainActivity, R.style.AppTheme))
            @SuppressLint("InflateParams") val view = LayoutInflater.from(this@MainActivity).inflate(R.layout.dialog_alert, null)
            builder.setView(view)
            dialog = builder.create()
            dialog.setCanceledOnTouchOutside(false)
            dialog.setCancelable(false)
            dialog.show()
        }

        override fun doInBackground(vararg integers: Int?): String? {
            var result: String?
            val operarioId = integers[0]
            Realm.getDefaultInstance().use { realm ->
                result = migration(realm, operarioId!!)
                Thread.sleep(1000)
            }
            publishProgress()
            return result
        }

        @SuppressLint("RestrictedApi")
        override fun onPostExecute(s: String) {
            super.onPostExecute(s)
            if (dialog.isShowing) {
                dialog.dismiss()
            }
            changeFragment(StartFragment.newInstance("", ""), "Inicio de Actividades")
            val titulo = if (s === "Verificar si cuentas con Internet.") "Error" else "Mensaje"
            Util.dialogMensaje(this@MainActivity, titulo, s)
        }
    }

    private fun migration(realm: Realm, operarioId: Int): String? {
        val migrationImp: MigrationImplementation = MigrationOver(realm)
        var result: String? = null
        val mCall = migrationInterface.getMigration(operarioId, Util.getVersion(this@MainActivity))

        try {
            val response = mCall.execute()
            if (response.code() == 200) {
                val migracion: Migration? = response.body() as Migration
                if (migracion != null) {
                    migrationImp.deleteAll()
                    migrationImp.save(migracion)
                    result = migracion.mensaje
                }
            } else {
                val message = Gson().fromJson(response.errorBody()?.string(), MessageError::class.java)
                result = "Codigo :" + response.code() + "\nMensaje :" + message.ExceptionMessage
            }
        } catch (e: IOException) {
            result = e.message + "\nVerificar si cuentas con Internet."
        }

        return result
    }

    private fun updateAndroid(url: String, nombre: String, title: String) {
        val builderUpdate =
                AlertDialog.Builder(ContextThemeWrapper(this@MainActivity, R.style.AppTheme))
        @SuppressLint("InflateParams") val view =
                LayoutInflater.from(this@MainActivity).inflate(R.layout.dialog_new_version, null)
        val textViewTile = view.findViewById<TextView>(R.id.textViewTitle)
        val buttonUpdate = view.findViewById<MaterialButton>(R.id.buttonUpdate)
        builderUpdate.setView(view)
        val dialogUpdate = builderUpdate.create()
        dialogUpdate.setCanceledOnTouchOutside(false)
        dialogUpdate.setCancelable(false)
        dialogUpdate.show()
        textViewTile.text = String.format("%s %s", "Nueva Versión", title)
        buttonUpdate.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED
            ) {
                download(url.trim { it <= ' ' }, nombre)
            } else {
                ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        Permission.WRITE_REQUEST
                )
            }
            dialogUpdate.dismiss()
        }
    }

    private fun download(url: String, name: String) {
        val file = File(Environment.getExternalStorageDirectory().toString() + "/download/" + name)
        if (file.exists()) {
            if (file.delete()) {
                Log.i("TAG", "deleted")
            }
        }
        val dm = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        Log.i("TAG", url)
        val mUri = Uri.parse(url)
        val r = DownloadManager.Request(mUri)
        r.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
        r.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        //r.setAllowedOverRoaming(false);
        r.setVisibleInDownloadsUi(false)
        r.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, name)
        r.setTitle(name)
        r.setMimeType("application/vnd.android.package-archive")
        val downloadId = dm.enqueue(r)
        val onComplete = object : BroadcastReceiver() {
            override fun onReceive(ctxt: Context, intent: Intent) {
                val apkFile = File(Environment.getExternalStorageDirectory().toString() + "/download/" + name)
                val install: Intent
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    val apkUri = getUriForFile(ctxt, BuildConfig.APPLICATION_ID + ".fileprovider", apkFile)
                    install = Intent(Intent.ACTION_INSTALL_PACKAGE)
                    install.data = apkUri
                    install.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                } else {
                    val apkUri = Uri.fromFile(apkFile)
                    install = Intent(Intent.ACTION_VIEW)
                    install.setDataAndType(apkUri, dm.getMimeTypeForDownloadedFile(downloadId))
                    install.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                startActivity(install)
                unregisterReceiver(this)
                finish()
            }
        }
        registerReceiver(onComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        Util.toastMensaje(this, getString(R.string.wait_download))
    }
}