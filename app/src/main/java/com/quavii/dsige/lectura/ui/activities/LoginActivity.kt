package com.quavii.dsige.lectura.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ContextThemeWrapper
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import com.quavii.dsige.lectura.data.dao.interfaces.LoginImplementation
import com.quavii.dsige.lectura.data.dao.overMethod.LoginOver
import com.quavii.dsige.lectura.data.apiServices.ConexionRetrofit
import com.quavii.dsige.lectura.data.model.Login
import com.quavii.dsige.lectura.helper.MessageError
import com.quavii.dsige.lectura.helper.Util
import com.quavii.dsige.lectura.R
import com.quavii.dsige.lectura.data.apiServices.ApiServices
import com.quavii.dsige.lectura.helper.Permission
import com.google.gson.Gson
import com.quavii.dsige.lectura.data.dao.interfaces.MigrationImplementation
import com.quavii.dsige.lectura.data.dao.overMethod.MigrationOver
import com.quavii.dsige.lectura.data.model.Migration
import com.quavii.dsige.lectura.helper.HelperDialog
import com.quavii.dsige.lectura.ui.services.EnableGpsService
import com.quavii.dsige.lectura.ui.services.SendDataMovilService
import com.quavii.dsige.lectura.ui.services.SendLocationService
import com.quavii.dsige.lectura.ui.services.SyncCortesReconexionesService
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_login.*
import java.io.IOException

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        var cantidad = 0
        when (requestCode) {
            1 -> {
                for (valor: Int in grantResults) {
                    if (valor == PackageManager.PERMISSION_DENIED) {
                        cantidad += 1
                    }
                }
                if (cantidad >= 1) {
                    buttonEnviar.visibility = View.GONE
                    messagePermission()
                } else {
                    buttonEnviar.visibility = View.VISIBLE
                }
            }
        }
    }

    private lateinit var loginInterfaces: ApiServices

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        bindUI()
        if (Build.VERSION.SDK_INT >= 23) {
            permision()
        }
    }

    private fun permision() {
        if (!Permission.hasPermissions(this@LoginActivity, *Permission.PERMISSIONS)) {
            ActivityCompat.requestPermissions(this@LoginActivity, Permission.PERMISSIONS, Permission.PERMISSION_ALL)
        }
    }

    private fun bindUI() {
        loginInterfaces = ConexionRetrofit.api.create(ApiServices::class.java)
        textViewVersion.text = String.format("Versión : %s", Util.getVersion(this))
        buttonEnviar.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        if (v?.id == R.id.buttonEnviar) {
            val user = editTextUser.text.toString()
            val password = editTextPass.text.toString()
            if (user.isEmpty() || TextUtils.isEmpty(user) && password.isEmpty() || TextUtils.isEmpty(password)) {
                editTextUserError.let { Util.toggleTextInputLayoutError(it, "Ingrese un Usuario") }
            } else {
                this.editTextUserError.let { Util.toggleTextInputLayoutError(it, null) }
                if (password == "" || password.isEmpty()) {
                    this.editTextPassError.let { Util.toggleTextInputLayoutError(it, "Ingrese una contraseña.") }
                } else {
                    this.editTextPassError.let { Util.toggleTextInputLayoutError(it, null) }
                    EnterMain().execute(user, password)
                }
            }
        }
    }

    private fun goToMainActivity(realm: Realm, user: String, password: String): String? {
        var result: String?
        val login: Login?
        val loginImp: LoginImplementation = LoginOver(realm)
        val migrationImp: MigrationImplementation = MigrationOver(realm)
        val loginCall = if (Build.VERSION.SDK_INT >= 29) {
            loginInterfaces.getLogin(user, password, password, Util.getVersion((this@LoginActivity)), Util.getToken(this@LoginActivity)!!)
        } else {
            loginInterfaces.getLogin(user, password, Util.getImei(this@LoginActivity), Util.getVersion((this@LoginActivity)), Util.getToken(this@LoginActivity)!!)
        }
        try {
            val response = loginCall.execute()
            when {
                response.code() == 200 -> {
                    login = response.body() as Login
                    result = if (login.mensaje == "Go") {
                        loginImp.save(login)
                        migration(migrationImp, login.iD_Operario)
                    } else {
                        "pass"
                    }
                }
                response.code() == 404 -> result = "users"
                else -> {
                    val message = Gson().fromJson(response.errorBody()?.string(), MessageError::class.java)
                    result = "Codigo :" + response.code() + "\nMensaje :" + message.ExceptionMessage
                }
            }
        } catch (e: IOException) {
            result = e.message + "\nVerificar si cuentas con Internet."
        }
        return result
    }

    @SuppressLint("StaticFieldLeak")
    private inner class EnterMain : AsyncTask<String, Void, String>() {

        lateinit var builder: AlertDialog.Builder
        private var dialog: AlertDialog? = null

        override fun onPreExecute() {
            super.onPreExecute()
            builder = AlertDialog.Builder(ContextThemeWrapper(this@LoginActivity, R.style.AppTheme))
            @SuppressLint("InflateParams") val view = LayoutInflater.from(this@LoginActivity).inflate(R.layout.dialog_login, null)
            builder.setView(view)
            dialog = builder.create()
            dialog!!.setCanceledOnTouchOutside(false)
            dialog!!.show()
        }

        override fun doInBackground(vararg string: String): String? {
            var result: String?
            val user = string[0]
            val password = string[1]
            Realm.getDefaultInstance().use { realm ->
                result = goToMainActivity(realm, user, password)
                Thread.sleep(1000)
            }
            publishProgress()
            return result
        }

        @SuppressLint("RestrictedApi")
        override fun onPostExecute(s: String?) {
            super.onPostExecute(s)
            if (dialog != null) {
                if (dialog!!.isShowing) {
                    dialog!!.dismiss()
                }
            }
            if (s != null) {
                when (s) {
                    "pass" -> editTextPassError.error = "Contraseña Incorrecta"
                    "users" -> editTextUserError.error = "Usuario no existe."
                    "Sincronización Completada." -> {
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    }
                    "Actualizar Versión del Aplicativo." -> Util.dialogMensaje(this@LoginActivity, "Mensaje", s)
                    else -> {
                        HelperDialog.MensajeOk(this@LoginActivity, "Error", s)
                    }
                }
            }
        }
    }

    private fun messagePermission() {
        val builder = AlertDialog.Builder(ContextThemeWrapper(this@LoginActivity, R.style.AppTheme))

        builder.setTitle("Permisos Denegados")
        builder.setMessage("Debes de aceptar los permisos para poder acceder al aplicativo.")
        builder.setPositiveButton("Aceptar") { dialogInterface, _ ->
            permision()
            dialogInterface.dismiss()
        }
        val dialog: AlertDialog = builder.create()
        dialog.setCanceledOnTouchOutside(false)
        dialog.setCancelable(false)
        dialog.show()
    }


    private fun migration(migrationImp: MigrationImplementation, operarioId: Int): String? {
        val migrationInterface: ApiServices = ConexionRetrofit.api.create(ApiServices::class.java)
        var result: String? = null
        val mCall = migrationInterface.getMigration(operarioId, Util.getVersion(this@LoginActivity))

        try {
            val response = mCall.execute()
            if (response.code() == 200) {
                val migracion: Migration? = response.body() as Migration
                if (migracion != null) {
                    migrationImp.deleteAll()
                    migrationImp.save(migracion)
                    startService()
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

    private fun startService() {
        startService(Intent(this, SyncCortesReconexionesService::class.java))
        startService(Intent(this, EnableGpsService::class.java))
        startService(Intent(this, SendLocationService::class.java))
        startService(Intent(this, SendDataMovilService::class.java))
    }
}