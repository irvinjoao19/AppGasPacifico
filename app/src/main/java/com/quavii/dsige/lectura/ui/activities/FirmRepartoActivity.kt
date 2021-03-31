package com.quavii.dsige.lectura.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.quavii.dsige.lectura.R
import com.quavii.dsige.lectura.data.model.RegistroRecibo
import com.quavii.dsige.lectura.data.viewModel.RepartoViewModel
import com.quavii.dsige.lectura.helper.Util
import kotlinx.android.synthetic.main.activity_firm_reparto.fabFirma
import kotlinx.android.synthetic.main.activity_firm_reparto.paintView

class FirmRepartoActivity : AppCompatActivity(), View.OnClickListener {

    override fun onClick(v: View) {
        when (v.id) {
            R.id.fabFirma -> {
                if (paintView.validDraw()) {
                    val name = paintView.save(this, repartoId, 5, "")
                    repartoViewModel.getUpdateRegistro(repartoId, name)
                } else {
                    repartoViewModel.setError("Debes de Firmar.")
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

    lateinit var repartoViewModel: RepartoViewModel
    var r: RegistroRecibo? = null

    var repartoId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_firm_reparto)
        val b = intent.extras
        if (b != null) {
            repartoId = b.getInt("repartoId")
            bindUI()
            message()
        }
    }

    private fun bindUI() {
        repartoViewModel = ViewModelProvider(this).get(RepartoViewModel::class.java)
        repartoViewModel.initialRealm()
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Firma"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)
        paintView.init(metrics)
        fabFirma.setOnClickListener(this)
    }

    private fun message() {
        repartoViewModel.mensajeError.observe(this, Observer<String> { s ->
            if (s != null) {
                Util.toastMensaje(this, s)
            }
        })
        repartoViewModel.mensajeSuccess.observe(this, Observer<String> { s ->
            if (s != null) {
                Util.toastMensaje(this, s)
                finish()
            }
        })
    }
}
