package com.quavii.dsige.lectura.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ContextThemeWrapper
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.quavii.dsige.lectura.R
import com.quavii.dsige.lectura.data.model.Photo
import com.quavii.dsige.lectura.data.viewModel.PhotoViewModel
import com.quavii.dsige.lectura.helper.AfterOrden
import com.quavii.dsige.lectura.helper.Util
import com.quavii.dsige.lectura.ui.adapters.FirmAdapter
import com.quavii.dsige.lectura.ui.listeners.OnItemClickListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.activity_reconexion_firm.*
import kotlinx.android.synthetic.main.activity_reconexion_firm.toolbar

class ReconexionFirmActivity : AppCompatActivity(), View.OnClickListener {

    override fun onClick(v: View) {
        when (v.id) {
            R.id.fabFirm ->
                startActivity(Intent(this, FirmActivity::class.java)
                        .putExtra("envioId", receive)
                        .putExtra("tipo", tipo)
                        .putExtra("online", online)
                        .putExtra("orden", orden)
                        .putExtra("orden_2", order2)
                        .putExtra("suministro", suministro)
                        .putExtra("tipoFirma", tipoFirma))
            R.id.fabSend -> {
                if (online == 1) {
                    confirmSend()
                } else {
                    photoViewModel.updateRegistroDesplaza(receive, tipo)
                    photoViewModel.updateActivoSuministroReconexion(receive)
                    siguienteOrden(tipo)
                }
            }
        }
    }

    lateinit var photoViewModel: PhotoViewModel
    var tipo: Int = 0
    var receive: Int = 0
    var online: Int = 0
    var orden: Int = 0
    var order2: Int = 0
    var suministro: String = ""
    var tipoFirma: String = "C"
    var titulo: String = ""
    var estado: Int = 0
    var fechaAsignacion: String = ""

    lateinit var builder: AlertDialog.Builder
    var dialog: AlertDialog? = null

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            val intent = Intent(this, PhotoActivity::class.java)
            intent.putExtra("envioId", receive)
            intent.putExtra("nombre", titulo)
            intent.putExtra("orden", orden)
            intent.putExtra("orden_2", order2)
            intent.putExtra("tipo", tipo)
            intent.putExtra("estado", estado)
            intent.putExtra("suministro", suministro)
            intent.putExtra("fechaAsignacion", fechaAsignacion)
            startActivity(intent)
            finish()
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reconexion_firm)
        val b = intent.extras
        if (b != null) {
            receive = b.getInt("envioId")
            tipo = b.getInt("tipo")
            online = b.getInt("online")
            orden = b.getInt("orden")
            order2 = b.getInt("orden_2")
            suministro = b.getString("suministro")!!
            titulo = b.getString("nombre")!!
            estado = b.getInt("estado")
            fechaAsignacion = b.getString("fechaAsignacion")!!
            bindUI()
            message()
            success()
            errorDialog()
        }
    }

    private fun bindUI() {
        fabFirm.setOnClickListener(this)
        fabSend.setOnClickListener(this)
        fabSend.visibility = View.GONE

        if (online == 0) {
            fabSend.text = String.format("%s", "Guardar")
        }

        photoViewModel = ViewModelProviders.of(this).get(PhotoViewModel::class.java)
        photoViewModel.initialRealm()
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Reconexion de Firmas"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            val intent = Intent(this, PhotoActivity::class.java)
            intent.putExtra("envioId", receive)
            intent.putExtra("nombre", titulo)
            intent.putExtra("orden", orden)
            intent.putExtra("orden_2", order2)
            intent.putExtra("tipo", tipo)
            intent.putExtra("estado", estado)
            intent.putExtra("suministro", suministro)
            intent.putExtra("fechaAsignacion", fechaAsignacion)
            startActivity(intent)
            finish()
        }

        val layoutManager = LinearLayoutManager(this)
        val firmAdapter = FirmAdapter(object : OnItemClickListener.PhotoListener {
            override fun onItemClick(f: Photo, view: View, position: Int) {
                deletePhoto(f)
            }
        })

        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = firmAdapter

        val r = photoViewModel.getPhotoFirm(receive)
        r.addChangeListener { t ->
            openSend(t)
            firmAdapter.addItems(t)
        }
        firmAdapter.addItems(r)
        openSend(r)
    }

    private fun openSend(p: List<Photo>?) {
        when (p?.size) {
            1 -> {
                fabFirm.visibility = View.GONE
                fabSend.visibility = View.VISIBLE
            }
            else -> {
                fabFirm.visibility = View.VISIBLE
                fabSend.visibility = View.GONE
            }
        }

        if (p != null) {
            for (t: Photo in p) {
                tipoFirma = if (t.tipoFirma == "O") "C" else "O"
            }
        } else {
            tipoFirma = "C"
        }

//        val registro = photoViewModel.getRegistro(order2, tipo)
//        if (registro.codigo_Resultado != "79") {
//            tipoFirma = "O"
//            when (p?.size) {
//                1 -> {
//                    fabFirm.visibility = View.GONE
//                    fabSend.visibility = View.VISIBLE
//                }
//            }
//        }
    }

    private fun load(title: String) {
        builder = AlertDialog.Builder(ContextThemeWrapper(this, R.style.AppTheme))
        @SuppressLint("InflateParams") val view =
                LayoutInflater.from(this).inflate(R.layout.dialog_login, null)
        builder.setView(view)
        val textViewTitle: TextView = view.findViewById(R.id.textViewTitle)
        textViewTitle.text = title
        dialog = builder.create()
        dialog!!.setCanceledOnTouchOutside(false)
        dialog!!.setCancelable(false)
        dialog!!.show()
    }

    private fun deletePhoto(p: Photo) {
        val dialog = MaterialAlertDialogBuilder(this)
                .setTitle("Mensaje")
                .setMessage(String.format("Deseas eliminar la firma del %s ?.", if (p.tipoFirma == "O") "Operario" else "Cliente"))
                .setPositiveButton("Aceptar") { dialog, _ ->
                    photoViewModel.deletePhoto(p.iD_Foto)
                    dialog.dismiss()
                }
                .setNegativeButton("Cancelar") { dialog, _ ->
                    dialog.cancel()
                }
        dialog.show()
    }

    private fun message() {
        photoViewModel.error.observe(this, Observer<String> { s ->
            if (s != null) {
                if (dialog != null) {
                    if (dialog!!.isShowing) {
                        dialog!!.dismiss()
                    }
                }
                Util.toastMensaje(this, s)
            }
        })
    }

    private fun success() {
        photoViewModel.success.observe(this, Observer<String> { s ->
            if (s != null) {
                Util.toastMensaje(this, s)
                if (s != "Firma Eliminada") {
                    if (dialog != null) {
                        if (dialog!!.isShowing) {
                            dialog!!.dismiss()
                        }
                    }
                    photoViewModel.updateActivoSuministroReconexion(receive)
                    siguienteOrden(tipo)
                }
            }
        })
    }

    private fun errorDialog() {
        photoViewModel.errorDialog.observe(this, Observer<String> { s ->
            if (s != null) {
                if (dialog != null) {
                    if (dialog!!.isShowing) {
                        dialog!!.dismiss()
                    }
                }

                val dialog = MaterialAlertDialogBuilder(this)
                        .setTitle("Mensaje")
                        .setMessage(String.format("%s", s))
                        .setPositiveButton("Aceptar") { dialog, _ ->
                            photoViewModel.sendData(suministro, order2, tipo)
                            load("Enviando...")
                            dialog.dismiss()
                        }
                        .setNegativeButton("Siguiente") { dialog, _ ->
                            photoViewModel.updateRegistroDesplaza(receive, tipo)
                            photoViewModel.updateActivoSuministroReconexion(receive)
                            siguienteOrden(tipo)
                            photoViewModel.setError("Guardado en Pendientes")
                            dialog.cancel()
                        }
                dialog.show()
            }
        })
    }

    private fun siguienteOrden(tipo: Int) {
        when (tipo) {
            4 -> {
//                val suministrosReconexion = photoViewModel.getSuministroReconexion(tipo, 1)
//                val returnOrden = AfterOrden.getNextOrdenReconexion(orden, suministrosReconexion)
//                if (returnOrden == 0) {
                val intent = Intent(this@ReconexionFirmActivity, SuministroActivity::class.java)
                intent.putExtra("nombre", "Reconexion")
                intent.putExtra("estado", tipo)
                startActivity(intent)
                finish()
//                } else {
//                    val ordenReconexion = photoViewModel.buscarReconexionesByOrden(returnOrden, 1)
//                    val intent = Intent(this@ReconexionFirmActivity, SuministroAfterActivity::class.java)
//                    intent.putExtra("orden", returnOrden)
//                    intent.putExtra("orden_2", ordenReconexion.suministroOperario_Orden)
//                    intent.putExtra("nombre", "Reconexion")
//                    intent.putExtra("estado", tipo)
//                    startActivity(intent)
//                    finish()
//                }
            }
        }
    }

    private fun confirmSend() {
        val dialog = MaterialAlertDialogBuilder(this)
                .setTitle("Mensaje")
                .setMessage(String.format("%s", "Estas seguro de enviar ?."))
                .setPositiveButton("Aceptar") { dialog, _ ->
                    photoViewModel.sendData(suministro, order2, tipo)
                    load("Enviando...")
                    dialog.dismiss()
                }
                .setNegativeButton("Cancelar") { dialog, _ ->
                    dialog.cancel()
                }
        dialog.show()
    }
}