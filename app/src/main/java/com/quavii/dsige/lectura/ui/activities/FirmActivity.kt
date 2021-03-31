package com.quavii.dsige.lectura.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import android.util.DisplayMetrics
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.quavii.dsige.lectura.R
import com.quavii.dsige.lectura.data.model.Photo
import com.quavii.dsige.lectura.data.viewModel.PhotoViewModel
import com.quavii.dsige.lectura.helper.Gps
import com.quavii.dsige.lectura.helper.Util
import kotlinx.android.synthetic.main.activity_firm.*

class FirmActivity : AppCompatActivity(), View.OnClickListener {

    override fun onClick(v: View) {
        when (v.id) {
            R.id.fabFirma -> {
                if (paintView.validDraw()) {
                    val gps = Gps(this@FirmActivity)
                    if (gps.isLocationEnabled()) {
                        if (gps.latitude.toString() == "0.0" || gps.longitude.toString() == "0.0") {
                            gps.showAlert(this@FirmActivity)
                        } else {
                            val name = paintView.save(this, receive, tipo, tipoFirma)
                            p.iD_Foto = photoViewModel.getPhotoIdentity()
                            p.conformidad = 2
                            p.iD_Suministro = receive
                            p.rutaFoto = name
                            p.fecha_Sincronizacion_Android = Util.getFechaActual()
                            p.tipo = tipo
                            p.estado = 1
                            p.latitud = gps.latitude.toString()
                            p.longitud = gps.longitude.toString()
                            p.firm = 1
                            p.tipoFirma = tipoFirma
                            photoViewModel.validatePhoto(p)
                        }
                    } else {
                        gps.showSettingsAlert(this@FirmActivity)
                    }
                } else {
                    photoViewModel.setError("Debes de Firmar.")
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.firma, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.clear -> {
                paintView.clear()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    lateinit var photoViewModel: PhotoViewModel
    lateinit var p: Photo
    private var tipo: Int = 0
    private var receive: Int = 0
    private var online: Int = 0
    private var orden: Int = 0
    private var order2: Int = 0
    private var suministro: String = ""
    private var tipoFirma: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_firm)
        p = Photo()
        val bundle = intent.extras
        if (bundle != null) {
            receive = bundle.getInt("envioId")
            tipo = bundle.getInt("tipo")
            online = bundle.getInt("online")
            orden = bundle.getInt("orden")
            order2 = bundle.getInt("orden_2")
            suministro = bundle.getString("suministro")!!
            tipoFirma = bundle.getString("tipoFirma")!!
        }
        bindUI()
        message()
        success()
    }

    private fun bindUI() {
        photoViewModel = ViewModelProvider(this).get(PhotoViewModel::class.java)
        photoViewModel.initialRealm()
        fabFirma.setOnClickListener(this)
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar!!.title = String.format("Firma del %s", if (tipoFirma == "O") "Operario" else "Cliente")
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }
        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)
        paintView.init(metrics)
    }

    private fun message() {
        photoViewModel.error.observe(this, { s ->
            if (s != null) {
                Util.toastMensaje(this, s)
            }
        })
    }

    private fun success() {
        photoViewModel.success.observe(this, { s ->
            if (s != null) {
                finish()
            }
        })
    }
}