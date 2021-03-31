package com.quavii.dsige.lectura.ui.fragments

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.google.android.material.button.MaterialButton

import com.quavii.dsige.lectura.R
import com.quavii.dsige.lectura.data.model.GrandesClientes
import com.quavii.dsige.lectura.data.model.Marca
import com.quavii.dsige.lectura.data.model.MenuPrincipal
import com.quavii.dsige.lectura.data.viewModel.ClienteViewModel
import com.quavii.dsige.lectura.helper.Gps
import com.quavii.dsige.lectura.helper.Util
import com.quavii.dsige.lectura.ui.adapters.MarcAdapter
import com.quavii.dsige.lectura.ui.adapters.MenuItemAdapter
import com.quavii.dsige.lectura.ui.listeners.OnItemClickListener
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_general_client.*
import java.io.File
import java.util.*

private const val ARG_PARAM1 = "param1"

class GeneralClientFragment : Fragment(), View.OnClickListener {

    override fun onClick(v: View) {
        validateCliente()
        when (v.id) {
            R.id.editTextCliente -> dialogSpinner("Cliente", 1)
            R.id.editTextCorrector -> dialogSpinner("Marca de Corrector", 2)
            R.id.editTextCabinete -> dialogSpinner("Tiene Gabinete de tememetria ? ", 3)
            R.id.editTextPresentaCliente -> dialogSpinner("Presenta Cliente ?", 4)
            R.id.buttonEMR -> {
                c.fechaRegistroInicio = Util.getFecha()
                clienteViewModel.updateCliente(context!!,c, "Registro Inicio Guardado")
            }
            R.id.fabCameraCliente -> if (clienteViewModel.validateCliente1(c)) createImage(1)
            R.id.fabCameraValorPresionEntrada -> if (clienteViewModel.validateCliente2(c)) createImage(2)
            R.id.fabCameraVolumenSinCMedidor -> if (clienteViewModel.validateCliente3(c)) createImage(3)
            R.id.fabCameraVolumenSCorregirUC -> if (clienteViewModel.validateCliente4(c)) createImage(4)
            R.id.fabCameraVolumenRegistradoUC -> if (clienteViewModel.validateCliente5(c)) createImage(5)
            R.id.fabCameraPresionMedicionUC -> if (clienteViewModel.validateCliente6(c)) createImage(6)
            R.id.fabCameraTemperaturaUC -> if (clienteViewModel.validateCliente7(c)) createImage(7)
            R.id.fabCameraTiempoVidaBateria -> if (clienteViewModel.validateCliente8(c)) createImage(8)
            R.id.textViewFotoPanoramica -> if (clienteViewModel.validateCliente9(c)) createImage(9)
            R.id.fabFotoPanoramica -> if (clienteViewModel.validateCliente9(c)) createImage(9)
            R.id.fabCameraCabinete -> if (clienteViewModel.validateCliente10(c)) createImage(10)
            R.id.fabRegister -> {
                val gps = Gps(context!!)
                if (gps.isLocationEnabled()) {
                    if (gps.latitude.toString() != "0.0" || gps.longitude.toString() != "0.0") {
                        if (clienteViewModel.validateCliente11(c)) {
                            c.estado = 6
                            c.latitud = gps.latitude.toString()
                            c.longitud = gps.longitude.toString()
                            c.comentario = editTextComentario.text.toString()
                            clienteViewModel.updateCliente(context!!,c, "Cliente Actualizado")
                            load()
                        }
                    }
                } else {
                    gps.showAlert(context!!)
                }
            }
        }
    }

    var viewPager: ViewPager? = null
    lateinit var clienteViewModel: ClienteViewModel
    private var clienteId: Int = 0

    lateinit var builder: AlertDialog.Builder
    var dialog: AlertDialog? = null

    lateinit var folder: File
    lateinit var image: File
    var nameImg: String = ""
    var direction: String = ""

    lateinit var c: GrandesClientes

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        c = GrandesClientes()
        arguments?.let {
            clienteId = it.getInt(ARG_PARAM1)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_general_client, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        clienteViewModel = ViewModelProvider(this).get(ClienteViewModel::class.java)
        clienteViewModel.initialRealm()
        buttonEMR.setOnClickListener(this)
        editTextCliente.setOnClickListener(this)
        editTextCorrector.setOnClickListener(this)
        editTextCabinete.setOnClickListener(this)
        editTextPresentaCliente.setOnClickListener(this)
        fabCameraCliente.setOnClickListener(this)
        fabCameraValorPresionEntrada.setOnClickListener(this)
        fabCameraVolumenSCorregirUC.setOnClickListener(this)
        fabCameraVolumenSinCMedidor.setOnClickListener(this)
        fabCameraVolumenRegistradoUC.setOnClickListener(this)
        fabCameraPresionMedicionUC.setOnClickListener(this)
        fabCameraTemperaturaUC.setOnClickListener(this)
        fabCameraTiempoVidaBateria.setOnClickListener(this)
        textViewFotoPanoramica.setOnClickListener(this)
        fabFotoPanoramica.setOnClickListener(this)
        fabCameraCabinete.setOnClickListener(this)
        fabRegister.setOnClickListener(this)
        bindUI()
        message()
    }

    private fun bindUI() {
        viewPager = activity!!.findViewById(R.id.viewPager)
        val g = clienteViewModel.getClienteById(clienteId)

        c.clienteId = g.clienteId
        c.fechaRegistroInicio = g.fechaRegistroInicio
        c.clientePermiteAcceso = g.clientePermiteAcceso
        when (g.clientePermiteAcceso) {
            "SI" -> editTextCliente.setText(String.format("%s", "Permite acceso"))
            "NO" -> {
                editTextCliente.setText(String.format("%s", "No permite acceso"))
                fabCameraCliente.visibility = View.VISIBLE
                linearLayoutCliente.visibility = View.GONE
            }
        }

        c.marcaCorrectorId = g.marcaCorrectorId
        if (g.marcaCorrectorId != 0) {
            editTextCorrector.setText(clienteViewModel.getNameMarcaById(g.marcaCorrectorId))
            if (g.marcaCorrectorId != 1) {
                c.fotovTemperaturaMedicionUC = g.fotovTemperaturaMedicionUC
                c.fotoTiempoVidaBateria = g.fotoTiempoVidaBateria
                fabCameraTemperaturaUC.visibility = View.VISIBLE
                fabCameraTiempoVidaBateria.visibility = View.VISIBLE
            }
        }

        editTextCabinete.setText(g.tieneGabinete)
        editTextPresentaCliente.setText(g.presenteCliente)
        if (g.presenteCliente.isNotEmpty()) {
            if (g.presenteCliente == "SI") {
                textViewContacto.visibility = View.VISIBLE
                editTextContacto.setText(g.contactoCliente)
            }
        }

        editTextCodigoEMR.setText(g.codigoEMR)
        editTextMezclaExplosiva.setText(g.porMezclaExplosiva)
        editTextValorPresionEntrada.setText(g.vManoPresionEntrada)
        editTextVolumenSCorregirUC.setText(g.vVolumenSCorreUC)
        editTextVolumenSinCMedidor.setText(g.vVolumenSCorreMedidor)
        editTextVolumenRegistradoUC.setText(g.vVolumenRegUC)
        editTextPresionMedicionUC.setText(g.vPresionMedicionUC)
        editTextTemperaturaUC.setText(g.vTemperaturaMedicionUC)
        editTextTiempoVidaBateria.setText(g.tiempoVidaBateria)
        editTextComentario.setText(g.comentario)
        showCliente(g)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: Int) =
                GeneralClientFragment().apply {
                    arguments = Bundle().apply {
                        putInt(ARG_PARAM1, param1)
                    }
                }
    }

    private fun createImage(tipo: Int) {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(context!!.packageManager) != null) {
            folder = Util.getFolder(context!!)
            nameImg = Util.getFechaActualForPhoto(c.clienteId, 7) + ".jpg"
            image = File(folder, nameImg)
            direction = "$folder/$nameImg"
            val uriSavedImage = Uri.fromFile(image)
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage)

            if (Build.VERSION.SDK_INT >= 24) {
                try {
                    val m = StrictMode::class.java.getMethod("disableDeathOnFileUriExposure")
                    m.invoke(null)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            startActivityForResult(takePictureIntent, tipo)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            generateImage(requestCode)
        }
    }

    private fun generateImage(code: Int) {
        val image: Observable<Boolean> = Util.generateImageAsync(direction)
        image.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Boolean> {
                    override fun onComplete() {

                    }

                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onNext(t: Boolean) {
                        if (t) {
                            when (code) {
                                1 -> {
                                    c.fotoConstanciaPermiteAcceso = nameImg
                                    clienteViewModel.updateCliente(context!!,c, "Foto de cliente permite acceso guardado")
                                }
                                2 -> {
                                    c.fotovManoPresionEntrada = nameImg
                                    clienteViewModel.updateCliente(context!!,c, "Foto presión entrada guardada")
                                }
                                3 -> {
                                    c.fotovVolumenSCorreMedidor = nameImg
                                    clienteViewModel.updateCliente(context!!,c, "Foto sin corregir medidor guardada.")
                                }
                                4 -> {
                                    c.fotovVolumenSCorreUC = nameImg
                                    clienteViewModel.updateCliente(context!!,c, "Foto sin corregir unidad correctora guardada")
                                }
                                5 -> {
                                    c.fotovVolumenRegUC = nameImg
                                    clienteViewModel.updateCliente(context!!,c, "Foto registrador de la unidad correctora guardada")
                                }
                                6 -> {
                                    c.fotovPresionMedicionUC = nameImg
                                    clienteViewModel.updateCliente(context!!,c, "Foto presion de medición UC guardada")
                                }
                                7 -> {
                                    c.fotovTemperaturaMedicionUC = nameImg
                                    clienteViewModel.updateCliente(context!!,c, "Foto temperatura medicion UC guardada")
                                }
                                8 -> {
                                    c.fotoTiempoVidaBateria = nameImg
                                    clienteViewModel.updateCliente(context!!,c, "Foto tiempo de bateria guardada")
                                }
                                9 -> {
                                    c.fotoPanomarica = nameImg
                                    clienteViewModel.updateCliente(context!!,c, "Foto panoramica Actualizado")
                                }
                                10 -> {
                                    c.foroSitieneGabinete = nameImg
                                    clienteViewModel.updateCliente(context!!,c, "Foto Gabinete guardada")
                                }
                            }
                        }
                    }

                    override fun onError(e: Throwable) {
                        Util.toastMensaje(context!!, "Volver a intentarlo")
                    }
                })
    }

    private fun dialogSpinner(title: String, tipo: Int) {
        val builder = AlertDialog.Builder(ContextThemeWrapper(context, R.style.AppTheme))
        @SuppressLint("InflateParams") val v = LayoutInflater.from(context).inflate(R.layout.dialog_combo, null)
        val textViewTitulo: TextView = v.findViewById(R.id.textViewTitulo)
        val recyclerView: RecyclerView = v.findViewById(R.id.recyclerView)
        textViewTitulo.text = title
        val layoutManager = LinearLayoutManager(context)
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.layoutManager = layoutManager
        recyclerView.addItemDecoration(DividerItemDecoration(recyclerView.context, DividerItemDecoration.VERTICAL))
        builder.setView(v)
        val dialog = builder.create()
        dialog.show()

        if (tipo != 2) {
            val menuAdapter = MenuItemAdapter(object : OnItemClickListener.MenuListener {
                override fun onItemClick(m: MenuPrincipal, v: View, position: Int) {
                    when (tipo) {
                        1 -> {
                            if (m.menuId == 1) {
                                c.clientePermiteAcceso = "SI"
                                fabCameraCliente.visibility = View.GONE
                                linearLayoutCliente.visibility = View.VISIBLE
                            } else {
                                c.clientePermiteAcceso = "NO"
                                fabCameraCliente.visibility = View.VISIBLE
                                linearLayoutCliente.visibility = View.GONE
                            }
                            editTextCliente.setText(m.title)
                        }
                        3 -> {
                            fabCameraCabinete.visibility = View.GONE
                            if (m.menuId == 1) {
                                fabCameraCabinete.visibility = View.VISIBLE
                            }
                            editTextCabinete.setText(m.title)
                        }
                        4 -> {
                            textViewContacto.visibility = View.GONE
                            if (m.menuId == 1) {
                                textViewContacto.visibility = View.VISIBLE
                            }
                            editTextPresentaCliente.setText(m.title)
                        }
                    }
                    dialog.dismiss()
                }
            })
            recyclerView.itemAnimator = DefaultItemAnimator()
            recyclerView.layoutManager = layoutManager
            recyclerView.adapter = menuAdapter
            when (tipo) {
                1 -> {
                    val menus: ArrayList<MenuPrincipal> = ArrayList()
                    menus.add(MenuPrincipal(1, "Permite acceso", 0, 0))
                    menus.add(MenuPrincipal(2, "No permite acceso", 0, 0))
                    menuAdapter.addItems(menus)
                }
                else -> {
                    val menus: ArrayList<MenuPrincipal> = ArrayList()
                    menus.add(MenuPrincipal(1, "SI", 0, 0))
                    menus.add(MenuPrincipal(2, "NO", 0, 0))
                    menuAdapter.addItems(menus)
                }
            }
        } else {
            val marcAdapter = MarcAdapter(object : OnItemClickListener.MarcaListener {
                override fun onItemClick(m: Marca, v: View, position: Int) {
                    fabCameraTemperaturaUC.visibility = View.GONE
                    fabCameraTiempoVidaBateria.visibility = View.GONE
                    if (m.marcaMedidorId != 1) {
                        fabCameraTemperaturaUC.visibility = View.VISIBLE
                        fabCameraTiempoVidaBateria.visibility = View.VISIBLE
                    }
                    editTextCorrector.setText(m.nombre)
                    c.marcaCorrectorId = m.marcaMedidorId
                    dialog.dismiss()
                }
            })
            recyclerView.adapter = marcAdapter
            val detalleGrupoResultadoList = clienteViewModel.getMarca()
            marcAdapter.addItems(detalleGrupoResultadoList)
        }
    }

    private fun validateCliente() {
        c.clienteId = clienteId
        c.codigoEMR = editTextCodigoEMR.text.toString()
        c.porMezclaExplosiva = editTextMezclaExplosiva.text.toString()
        c.vManoPresionEntrada = editTextValorPresionEntrada.text.toString()
        c.vVolumenSCorreUC = editTextVolumenSCorregirUC.text.toString()
        c.vVolumenSCorreMedidor = editTextVolumenSinCMedidor.text.toString()
        c.vVolumenRegUC = editTextVolumenRegistradoUC.text.toString()
        c.vPresionMedicionUC = editTextPresionMedicionUC.text.toString()
        c.vTemperaturaMedicionUC = editTextTemperaturaUC.text.toString()
        c.tiempoVidaBateria = editTextTiempoVidaBateria.text.toString()
        c.tieneGabinete = editTextCabinete.text.toString()
        c.presenteCliente = editTextPresentaCliente.text.toString()
        c.contactoCliente = editTextContacto.text.toString()
    }


    private fun showCliente(g: GrandesClientes) {
        c.fotoConstanciaPermiteAcceso = g.fotoConstanciaPermiteAcceso
        if (g.fotoConstanciaPermiteAcceso.isNotEmpty()) {
            fabCameraCliente.setImageDrawable(ContextCompat.getDrawable(context!!, R.drawable.ic_check))
        }
        c.fotovManoPresionEntrada = g.fotovManoPresionEntrada
        if (g.fotovManoPresionEntrada.isNotEmpty()) {
            fabCameraValorPresionEntrada.setImageDrawable(ContextCompat.getDrawable(context!!, R.drawable.ic_check))
        }
        c.fotovVolumenSCorreUC = g.fotovVolumenSCorreUC
        if (g.fotovVolumenSCorreUC.isNotEmpty()) {
            fabCameraVolumenSCorregirUC.setImageDrawable(ContextCompat.getDrawable(context!!, R.drawable.ic_check))
        }
        c.fotovVolumenSCorreMedidor = g.fotovVolumenSCorreMedidor
        if (g.fotovVolumenSCorreMedidor.isNotEmpty()) {
            fabCameraVolumenSinCMedidor.setImageDrawable(ContextCompat.getDrawable(context!!, R.drawable.ic_check))
        }
        c.fotovVolumenRegUC = g.fotovVolumenRegUC
        if (g.fotovVolumenRegUC.isNotEmpty()) {
            fabCameraVolumenRegistradoUC.setImageDrawable(ContextCompat.getDrawable(context!!, R.drawable.ic_check))
        }
        c.fotovPresionMedicionUC = g.fotovPresionMedicionUC
        if (g.fotovPresionMedicionUC.isNotEmpty()) {
            fabCameraPresionMedicionUC.setImageDrawable(ContextCompat.getDrawable(context!!, R.drawable.ic_check))
        }
        c.fotovTemperaturaMedicionUC = g.fotovTemperaturaMedicionUC
        if (g.fotovTemperaturaMedicionUC.isNotEmpty()) {
            fabCameraTemperaturaUC.setImageDrawable(ContextCompat.getDrawable(context!!, R.drawable.ic_check))
        }
        c.fotoTiempoVidaBateria = g.fotoTiempoVidaBateria
        if (g.fotoTiempoVidaBateria.isNotEmpty()) {
            fabCameraTiempoVidaBateria.setImageDrawable(ContextCompat.getDrawable(context!!, R.drawable.ic_check))
        }
        c.fotoPanomarica = g.fotoPanomarica
        if (g.fotoPanomarica.isNotEmpty()) {
            fabFotoPanoramica.setImageDrawable(ContextCompat.getDrawable(context!!, R.drawable.ic_check))
        }
        c.foroSitieneGabinete = g.foroSitieneGabinete
        if (g.foroSitieneGabinete.isNotEmpty()) {
            fabCameraCabinete.setImageDrawable(ContextCompat.getDrawable(context!!, R.drawable.ic_check))
        }
    }

    private fun message() {
        clienteViewModel.mensajeError.observe(viewLifecycleOwner, androidx.lifecycle.Observer<String> { s ->
            if (dialog != null) {
                if (dialog!!.isShowing) {
                    dialog!!.dismiss()
                }
            }
            if (s == "Confirmar lectura") {
                confirmLectura()
            }
            Util.toastMensaje(context!!, s)
        })

        clienteViewModel.mensajeSuccess.observe(viewLifecycleOwner, androidx.lifecycle.Observer<String> { s ->
            Util.toastMensaje(context!!, s)
            if (s == "Cliente Actualizado") {
                if (dialog != null) {
                    if (dialog!!.isShowing) {
                        dialog!!.dismiss()
                    }
                }
                viewPager?.currentItem = 1
            } else {
                val g = clienteViewModel.getClienteById(clienteId)
                c.clienteId = g.clienteId
                c.fechaRegistroInicio = g.fechaRegistroInicio
                c.clientePermiteAcceso = g.clientePermiteAcceso
                c.fotoConstanciaPermiteAcceso = g.fotoConstanciaPermiteAcceso
                c.porMezclaExplosiva = g.porMezclaExplosiva
                c.vManoPresionEntrada = g.vManoPresionEntrada
                c.fotovManoPresionEntrada = g.fotovManoPresionEntrada
                c.marcaCorrectorId = g.marcaCorrectorId
                c.fotoMarcaCorrector = g.fotoMarcaCorrector
                c.vVolumenSCorreUC = g.vVolumenSCorreUC
                c.fotovVolumenSCorreUC = g.fotovVolumenSCorreUC
                c.vVolumenSCorreMedidor = g.vVolumenSCorreMedidor
                c.fotovVolumenSCorreMedidor = g.fotovVolumenSCorreMedidor
                c.vVolumenRegUC = g.vVolumenRegUC
                c.fotovVolumenRegUC = g.fotovVolumenRegUC
                c.vPresionMedicionUC = g.vPresionMedicionUC
                c.fotovPresionMedicionUC = g.fotovPresionMedicionUC
                c.vTemperaturaMedicionUC = g.vTemperaturaMedicionUC
                c.fotovTemperaturaMedicionUC = g.fotovTemperaturaMedicionUC
                c.tiempoVidaBateria = g.tiempoVidaBateria
                c.fotoTiempoVidaBateria = g.fotoTiempoVidaBateria
                c.fotoPanomarica = g.fotoPanomarica
                c.tieneGabinete = g.tieneGabinete
                c.foroSitieneGabinete = g.foroSitieneGabinete
                c.presenteCliente = g.presenteCliente
                c.contactoCliente = g.contactoCliente
                c.latitud = g.latitud
                c.longitud = g.longitud
                c.estado = g.estado
                c.comentario = g.comentario
                showCliente(g)
            }
        })
    }

    private fun load() {
        builder = AlertDialog.Builder(ContextThemeWrapper(context, R.style.AppTheme))
        @SuppressLint("InflateParams") val view =
                LayoutInflater.from(context).inflate(R.layout.dialog_login, null)
        builder.setView(view)
        val textViewTitle: TextView = view.findViewById(R.id.textViewTitle)
        textViewTitle.text = String.format("%s", "Enviando...")
        dialog = builder.create()
        dialog!!.setCanceledOnTouchOutside(false)
        dialog!!.setCancelable(false)
        dialog!!.show()
    }

    private fun confirmLectura() {
        val builder = AlertDialog.Builder(ContextThemeWrapper(context, R.style.AppTheme))
        @SuppressLint("InflateParams") val v = LayoutInflater.from(context).inflate(R.layout.dialog_confirm, null)
        val editTextLecturaConfirm = v.findViewById<TextView>(R.id.editTextLecturaConfirm)
        val buttonCancelar: MaterialButton = v.findViewById(R.id.buttonCancelar)
        val buttonAceptar: MaterialButton = v.findViewById(R.id.buttonAceptar)
        builder.setView(v)
        val dialog = builder.create()
        dialog.show()

        buttonAceptar.setOnClickListener {
            if (editTextLecturaConfirm.text.toString().isNotEmpty()) {
                c.vVolumenSCorreUC = editTextVolumenSCorregirUC.text.toString()
                c.confirmarVolumenSCorreUC = editTextLecturaConfirm.text.toString()

                if (clienteViewModel.validateCliente4(c)){
                    clienteViewModel.updateCliente(context!!,c,"Validando Relectura")
                }


                dialog.dismiss()
            } else {
                editTextLecturaConfirm.error = "Ingrese un valor"
                editTextLecturaConfirm.requestFocus()
            }
        }
        buttonCancelar.setOnClickListener {
            dialog.dismiss()
        }

    }
}