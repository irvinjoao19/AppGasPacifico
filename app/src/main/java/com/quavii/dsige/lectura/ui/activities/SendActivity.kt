package com.quavii.dsige.lectura.ui.activities

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ContextThemeWrapper
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.quavii.dsige.lectura.data.model.*
import com.quavii.dsige.lectura.helper.HelperDialog
import com.quavii.dsige.lectura.R
import com.quavii.dsige.lectura.data.viewModel.EnvioViewModel
import com.quavii.dsige.lectura.ui.adapters.SendAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class SendActivity : AppCompatActivity() {

    lateinit var textViewRegistro: TextView
    lateinit var textViewPhoto: TextView
    lateinit var recyclerView: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var sendAdapter: SendAdapter

    lateinit var builder: AlertDialog.Builder
    private var dialog: AlertDialog? = null
    lateinit var envioViewModel: EnvioViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send)
        envioViewModel = ViewModelProvider(this).get(EnvioViewModel::class.java)
        envioViewModel.initialRealm()
        bindUI()
        message()
    }

    private fun bindUI() {
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Envio de Pendientes"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        textViewRegistro = findViewById(R.id.textViewRegistro)
        textViewPhoto = findViewById(R.id.textViewPhoto)
        recyclerView = findViewById(R.id.recyclerView)
        layoutManager = LinearLayoutManager(this)
        sendAdapter = SendAdapter(object : SendAdapter.OnItemClickListener {
            override fun onItemClick(m: MenuPrincipal, position: Int) {
                confirmSend(m.cantidad)
            }
        })
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = sendAdapter

        val resultados = envioViewModel.getResultRegistro()
//        resultados.addChangeListener { r ->
//            val menus: ArrayList<MenuPrincipal> = ArrayList()
//            menus.add(MenuPrincipal(1, "Enviar Registros", r.size, R.mipmap.ic_registro))
//            textViewRegistro.text = String.format("%s %s", "Registros", r.size)
//            sendAdapter.addItems(menus)
//        }
        val menuPrincipals: ArrayList<MenuPrincipal> = ArrayList()
        menuPrincipals.add(MenuPrincipal(1, "Enviar Registros", resultados.size, R.mipmap.ic_registro))
        textViewRegistro.text = String.format("%s %s", "Registros", resultados.size)
        sendAdapter.addItems(menuPrincipals)


        val photos = envioViewModel.getResultPhoto()
//        photos.addChangeListener { f ->
//            textViewPhoto.text = String.format("%s %s", "Fotos ", f.size)
//        }
        textViewPhoto.text = String.format("%s %s", "Fotos ", photos.size)
    }

    private fun confirmSend(count: Int) {
        val materialDialog = MaterialAlertDialogBuilder(this)
                .setTitle("Mensaje")
                .setMessage(String.format("%s", "Antes de enviar asegurate de contar con internet !.\nDeseas enviar los Registros ?."))
                .setPositiveButton("Aceptar") { dialog, _ ->
                    if (count > 0) {
                        envioViewModel.sendData(this)
                        load()
                    } else {
                        HelperDialog.MensajeOk(this@SendActivity, "Mensaje", "No cuentas con ningún Registro")
                    }
                    dialog.dismiss()
                }.setNegativeButton("Cancelar") { dialog, _ ->
                    dialog.dismiss()
                }
        materialDialog.show()
    }

    private fun load() {
        builder = AlertDialog.Builder(ContextThemeWrapper(this, R.style.AppTheme))
        @SuppressLint("InflateParams") val view =
                LayoutInflater.from(this).inflate(R.layout.dialog_login, null)
        builder.setView(view)
        val textViewTitle: TextView = view.findViewById(R.id.textViewTitle)
        textViewTitle.text = String.format("%s", "Enviando...")
        dialog = builder.create()
        dialog!!.setCanceledOnTouchOutside(false)
        dialog!!.setCancelable(false)
        dialog!!.show()
    }

    private fun message() {
        envioViewModel.error.observe(this, { s ->
            if (s != null) {
                if (dialog != null) {
                    if (dialog!!.isShowing) {
                        dialog!!.dismiss()
                    }
                }
                HelperDialog.MensajeOk(this@SendActivity, "Mensaje", s)
            }
        })

        envioViewModel.success.observe(this, { s ->
            if (s != null) {
                if (dialog != null) {
                    if (dialog!!.isShowing) {
                        dialog!!.dismiss()
                    }
                }
                val resultados = envioViewModel.getResultRegistro()
                val menuPrincipals: ArrayList<MenuPrincipal> = ArrayList()
                menuPrincipals.add(MenuPrincipal(1, "Enviar Registros", resultados.size, R.mipmap.ic_registro))
                textViewRegistro.text = String.format("%s %s", "Registros", resultados.size)
                sendAdapter.addItems(menuPrincipals)
                val photos = envioViewModel.getResultPhoto()
                textViewPhoto.text = String.format("%s %s", "Fotos ", photos.size)
                HelperDialog.MensajeOk(this@SendActivity, "Mensaje", s)
            }
        })
    }
    // TODO ANTIGUO RX JAVA 15/08/2019

    // TODO ANTIGUO GITHUB 16/01/2019
}