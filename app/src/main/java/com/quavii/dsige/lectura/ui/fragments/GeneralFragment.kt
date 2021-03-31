package com.quavii.dsige.lectura.ui.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.view.ContextThemeWrapper
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager

import com.quavii.dsige.lectura.R
import com.quavii.dsige.lectura.data.model.*
import com.quavii.dsige.lectura.data.viewModel.RepartoViewModel
import com.quavii.dsige.lectura.helper.Util
import com.quavii.dsige.lectura.ui.adapters.FormatoAdapter
import com.quavii.dsige.lectura.ui.listeners.OnItemClickListener
import com.quavii.dsige.lectura.ui.services.DistanceService
import kotlinx.android.synthetic.main.fragment_general.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val ARG_PARAM3 = "param3"
private const val ARG_PARAM4 = "param4"
private const val ARG_PARAM5 = "param5"

class GeneralFragment : Fragment(), View.OnClickListener {

    override fun onClick(v: View) {
        Util.hideKeyboardFrom(context!!, v)
        when (v.id) {
            R.id.editTextVivienda -> dialogSpinner(2, "Vivienda")
            R.id.editTextColorFachada -> dialogSpinner(3, "Color/Fachada")
            R.id.editTextPuerta -> dialogSpinner(4, "Puerta")
            R.id.editTextColorPuerta -> dialogSpinner(5, "Color Puerta")
            R.id.editTextRecibido -> dialogSpinner(1, "Recibido")
            R.id.editTextDevuelto -> dialogSpinner(6, "Devuelto")
            R.id.fabGeneral -> validateGeneral()
        }
    }

    lateinit var repartoViewModel: RepartoViewModel
    lateinit var r: RegistroRecibo
    private var viewPager: ViewPager? = null

    private var repartoId: Int = 0
    private var recibo: String = ""
    private var operarioId: Int = 0
    private var cliente: String = ""
    private var validation: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        r = RegistroRecibo()
        arguments?.let {
            repartoId = it.getInt(ARG_PARAM1)
            recibo = it.getString(ARG_PARAM2)!!
            operarioId = it.getInt(ARG_PARAM3)
            cliente = it.getString(ARG_PARAM4)!!
            validation = it.getInt(ARG_PARAM5)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_general, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        repartoViewModel = ViewModelProvider(this).get(RepartoViewModel::class.java)
        repartoViewModel.initialRealm()
        editTextVivienda.setOnClickListener(this)
        editTextColorFachada.setOnClickListener(this)
        editTextPuerta.setOnClickListener(this)
        editTextColorPuerta.setOnClickListener(this)
        editTextRecibido.setOnClickListener(this)
        editTextDevuelto.setOnClickListener(this)
        fabGeneral.setOnClickListener(this)
        editTextRecibo.setText(recibo)
        editTextCliente.setText(cliente)
//        editTextPiso.setOnEditorActionListener(this)
        message()
        bindUI()
    }

    private fun bindUI() {
        viewPager = activity!!.findViewById(R.id.viewPager)
        val b = repartoViewModel.getRegistroByFk(repartoId)
        if (b != null) {
            r.reciboId = b.reciboId
            r.formatoCargoRecibo = b.formatoCargoRecibo
            r.formatoVivienda = b.formatoVivienda
            r.formatoCargoColor = b.formatoCargoColor
            r.formatoCargoPuerta = b.formatoCargoPuerta
            r.formatoCargoColorPuerta = b.formatoCargoColorPuerta
            r.formatoCargoDevuelto = b.formatoCargoDevuelto
            r.nombreformatoCargoRecibo = b.nombreformatoCargoRecibo
            editTextRecibido.setText(b.nombreformatoCargoRecibo)
            r.nombreformatoVivienda = b.nombreformatoVivienda
            editTextVivienda.setText(b.nombreformatoVivienda)
            r.nombreformatoCargoColor = b.nombreformatoCargoColor
            editTextColorFachada.setText(b.nombreformatoCargoColor)
            r.nombreformatoCargoPuerta = b.nombreformatoCargoPuerta
            editTextPuerta.setText(b.nombreformatoCargoPuerta)
            r.nombreformatoCargoColorPuerta = b.nombreformatoCargoColorPuerta
            editTextColorPuerta.setText(b.nombreformatoCargoColorPuerta)
            r.nombreformatoCargoDevuelto = b.nombreformatoCargoDevuelto
            editTextDevuelto.setText(b.nombreformatoCargoDevuelto)
            editTextPiso.setText(b.piso.toString())

            editTextOtrosVivienda.setText(b.otrosVivienda)
            editTextOtrosColorFachada.setText(b.otrosCargoColor)
            editTextOtrosPuerta.setText(b.otrosCargoPuerta)
            editTextOtrosColorPuerta.setText(b.otrosCargoColorPuerta)
        } else {
            r.reciboId = repartoViewModel.getRegistroReciboIdentity()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(repartoId: Int, recibo: String, operarioId: Int, cliente: String, validation: Int) =
                GeneralFragment().apply {
                    arguments = Bundle().apply {
                        putInt(ARG_PARAM1, repartoId)
                        putString(ARG_PARAM2, recibo)
                        putInt(ARG_PARAM3, operarioId)
                        putString(ARG_PARAM4, cliente)
                        putInt(ARG_PARAM5, validation)
                    }
                }
    }

    private fun dialogSpinner(tipo: Int, title: String) {
        val builder = androidx.appcompat.app.AlertDialog.Builder(ContextThemeWrapper(context, R.style.AppTheme))
        @SuppressLint("InflateParams") val v = LayoutInflater.from(context).inflate(R.layout.dialog_combo, null)
        val textViewTitulo: TextView = v.findViewById(R.id.textViewTitulo)
        val recyclerView: RecyclerView = v.findViewById(R.id.recyclerView)
        val layoutManager = LinearLayoutManager(context)
        textViewTitulo.text = title
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.layoutManager = layoutManager
        recyclerView.addItemDecoration(DividerItemDecoration(recyclerView.context, DividerItemDecoration.VERTICAL))
        builder.setView(v)
        val dialog = builder.create()
        dialog.show()

        val formatAdapter = FormatoAdapter(object : OnItemClickListener.FormatoListener {
            override fun onItemClick(f: Formato, v: View, position: Int) {
                when (tipo) {
                    1 -> {
                        r.formatoCargoRecibo = f.formatoId
                        r.nombreformatoCargoRecibo = f.nombre
                        editTextRecibido.setText(f.nombre)
                    }
                    2 -> {
                        if (f.formatoId == 11) {
                            textViewOtrosVivienda.visibility = View.VISIBLE
                            Util.showKeyboard(editTextOtrosVivienda, context!!)
                        } else {
                            textViewOtrosVivienda.visibility = View.GONE
                            editTextOtrosVivienda.text = null
                        }
                        r.formatoVivienda = f.formatoId
                        r.nombreformatoVivienda = f.nombre
                        editTextVivienda.setText(f.nombre)
                    }
                    3 -> {
                        if (f.formatoId == 16) {
                            textViewOtrosColorFachada.visibility = View.VISIBLE
                            Util.showKeyboard(editTextOtrosColorFachada, context!!)
                        } else {
                            textViewOtrosColorFachada.visibility = View.GONE
                            editTextOtrosColorFachada.text = null
                        }
                        r.formatoCargoColor = f.formatoId
                        r.nombreformatoCargoColor = f.nombre
                        editTextColorFachada.setText(f.nombre)
                    }
                    4 -> {
                        if (f.formatoId == 20) {
                            textViewOtrosPuerta.visibility = View.VISIBLE
                            Util.showKeyboard(editTextOtrosPuerta, context!!)
                        } else {
                            textViewOtrosPuerta.visibility = View.GONE
                            editTextOtrosPuerta.text = null
                        }
                        r.formatoCargoPuerta = f.formatoId
                        r.nombreformatoCargoPuerta = f.nombre
                        editTextPuerta.setText(f.nombre)
                    }
                    5 -> {
                        if (f.formatoId == 25) {
                            textViewOtrosColorPuerta.visibility = View.VISIBLE
                            Util.showKeyboard(editTextOtrosColorPuerta, context!!)
                        } else {
                            textViewOtrosColorPuerta.visibility = View.GONE
                            editTextOtrosColorPuerta.text = null
                        }
                        r.formatoCargoColorPuerta = f.formatoId
                        r.nombreformatoCargoColorPuerta = f.nombre
                        editTextColorPuerta.setText(f.nombre)
                    }
                    6 -> {
                        r.formatoCargoDevuelto = f.formatoId
                        r.nombreformatoCargoDevuelto = f.nombre
                        editTextDevuelto.setText(f.nombre)
                    }
                }
                dialog.dismiss()
            }
        })
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = formatAdapter
        val f = repartoViewModel.getFormato(tipo)
        formatAdapter.addItems(f)
    }

    private fun validateGeneral() {
        r.repartoId = repartoId
        r.operarioId = operarioId
        when {
            editTextPiso.text.toString().isEmpty() -> r.piso = 0
            else -> r.piso = editTextPiso.text.toString().toInt()
        }
        r.otrosVivienda = editTextOtrosVivienda.text.toString()
        r.otrosCargoColor = editTextOtrosColorFachada.text.toString()
        r.otrosCargoPuerta = editTextOtrosPuerta.text.toString()
        r.otrosCargoColorPuerta = editTextOtrosColorPuerta.text.toString()
        r.dniCargoRecibo = editTextDni.text.toString()
        r.parentesco = editTextParentesco.text.toString()
        r.observacionCargo = editTextObservaciones.text.toString()
        repartoViewModel.validateRegistroRecibo(r, validation)
    }

    private fun message() {
        repartoViewModel.mensajeError.observe(viewLifecycleOwner, { s ->
            if (s != null) {
                Util.hideKeyboardFrom(context!!, view!!)
                Util.toastMensaje(context!!, s)
            }
        })
        repartoViewModel.mensajeSuccess.observe(viewLifecycleOwner, { s ->
            if (s != null) {
                Util.clearNotification(context!!)
                Util.hideKeyboardFrom(context!!, view!!)
                if (validation == 2) {
                    viewPager?.currentItem = 1
                } else {
                    repartoViewModel.updateRepartoEnvio(repartoId)
                    context!!.startService(Intent(context!!, DistanceService::class.java))
                    activity!!.finish()
                }
            }
        })
    }
}