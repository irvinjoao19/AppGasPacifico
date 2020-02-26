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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder

import com.quavii.dsige.lectura.R
import com.quavii.dsige.lectura.data.model.MenuPrincipal
import com.quavii.dsige.lectura.data.viewModel.EnvioViewModel
import com.quavii.dsige.lectura.helper.HelperDialog
import com.quavii.dsige.lectura.ui.adapters.SendAdapter
import kotlinx.android.synthetic.main.fragment_send.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class SendFragment : Fragment() {

    lateinit var sendAdapter: SendAdapter

    lateinit var builder: AlertDialog.Builder
    var dialog: AlertDialog? = null

    lateinit var envioViewModel: EnvioViewModel


    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_send, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        envioViewModel = ViewModelProviders.of(this).get(EnvioViewModel::class.java)
        envioViewModel.initialRealm()
        bindUI()
        message()
    }

    private fun bindUI() {
        val layoutManager = LinearLayoutManager(context)
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
        val materialDialog = MaterialAlertDialogBuilder(context)
                .setTitle("Mensaje")
                .setMessage(String.format("%s", "Antes de enviar asegurate de contar con internet !.\nDeseas enviar los Registros ?."))
                .setPositiveButton("Aceptar") { dialog, _ ->
                    if (count > 0) {
                        envioViewModel.sendData()
                        load()
                    } else {
                        HelperDialog.MensajeOk(context, "Mensaje", "No cuentas con ningún Registro")
                    }
                    dialog.dismiss()
                }.setNegativeButton("Cancelar") { dialog, _ ->
                    dialog.dismiss()
                }
        materialDialog.show()
    }

    private fun load() {
        builder = AlertDialog.Builder(ContextThemeWrapper(context, R.style.AppTheme))
        @SuppressLint("InflateParams") val view =
                LayoutInflater.from(context).inflate(R.layout.dialog_login, null)
        builder.setView(view)
        val textViewTitle: TextView = view.findViewById(R.id.textViewTitle)
        textViewTitle.text = String.format("%s","Enviando...")
        dialog = builder.create()
        dialog!!.setCanceledOnTouchOutside(false)
        dialog!!.setCancelable(false)
        dialog!!.show()
    }

    private fun message() {
        envioViewModel.error.observe(this, Observer<String> { s ->
            if (s != null) {
                if (dialog != null) {
                    if (dialog!!.isShowing) {
                        dialog!!.dismiss()
                    }
                }
                HelperDialog.MensajeOk(context, "Mensaje", s)
            }
        })

        envioViewModel.success.observe(this, Observer<String> { s ->
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
                HelperDialog.MensajeOk(context, "Mensaje", s)
            }
        })
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                SendFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}