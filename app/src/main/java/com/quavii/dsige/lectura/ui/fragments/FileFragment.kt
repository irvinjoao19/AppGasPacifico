package com.quavii.dsige.lectura.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ContextThemeWrapper
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager

import com.quavii.dsige.lectura.R
import com.quavii.dsige.lectura.data.model.GrandesClientes
import com.quavii.dsige.lectura.data.viewModel.ClienteViewModel
import com.quavii.dsige.lectura.helper.Util
import kotlinx.android.synthetic.main.fragment_file.*

private const val ARG_PARAM1 = "param1"

class FileFragment : Fragment(), View.OnClickListener {
    override fun onClick(v: View) {
        when (v.id) {
            R.id.fabVerificate -> if (c.estado == 7) {
                load()
                clienteViewModel.verificateFile(c)
            } else {
                clienteViewModel.setError("Favor de completar el primer formulario.")
            }
            R.id.fabClose -> {
                activity!!.finish()
            }
        }
    }

    lateinit var clienteViewModel: ClienteViewModel
    lateinit var c: GrandesClientes
    private var clienteId: Int = 0
    lateinit var builder: AlertDialog.Builder
    private var dialog: AlertDialog? = null
    private var viewPager: ViewPager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            clienteId = it.getInt(ARG_PARAM1)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_file, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        clienteViewModel = ViewModelProvider(this).get(ClienteViewModel::class.java)
        clienteViewModel.initialRealm()
        fabVerificate.setOnClickListener(this)
        fabClose.setOnClickListener(this)
        bindUI()
        message()
    }

    override fun onStart() {
        super.onStart()
        val list = clienteViewModel.getClienteById(clienteId)
        c = list
    }

    private fun bindUI() {
        viewPager = activity!!.findViewById(R.id.viewPager)
        c = clienteViewModel.getClienteById(clienteId)
        editTextCodigoEMR.setText(c.codigoEMR)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: Int) =
                FileFragment().apply {
                    arguments = Bundle().apply {
                        putInt(ARG_PARAM1, param1)
                    }
                }
    }

    private fun load() {
        builder = AlertDialog.Builder(ContextThemeWrapper(context, R.style.AppTheme))
        @SuppressLint("InflateParams") val view =
                LayoutInflater.from(context).inflate(R.layout.dialog_login, null)
        builder.setView(view)
        val textViewTitle: TextView = view.findViewById(R.id.textViewTitle)
        textViewTitle.text = String.format("%s", "Verificando...")
        dialog = builder.create()
        dialog!!.setCanceledOnTouchOutside(false)
        dialog!!.setCancelable(false)
        dialog!!.show()
    }

    private fun message() {
        clienteViewModel.mensajeError.observe(viewLifecycleOwner, { s ->
            if (s != null) {
                if (dialog != null) {
                    if (dialog!!.isShowing) {
                        dialog!!.dismiss()
                    }
                }


                if (s == "Favor de completar el primer formulario.") {
                    Util.dialogMensaje(context!!, "Mensaje", s)
                    viewPager?.currentItem = 0
                } else {
                    textViewMensaje.text = s
                }
            }
        })

        clienteViewModel.mensajeSuccess.observe(viewLifecycleOwner, { s ->
            if (s != null) {
                if (dialog != null) {
                    if (dialog!!.isShowing) {
                        dialog!!.dismiss()
                    }
                }
                fabVerificate.visibility = View.GONE
                fabClose.visibility = View.VISIBLE
                textViewMensaje.text = s
//                Util.dialogMensaje(context!!, "Mensaje", s)
            }
        })
    }
}