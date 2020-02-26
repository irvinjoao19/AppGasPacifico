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
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ContextThemeWrapper
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder

import com.quavii.dsige.lectura.R
import com.quavii.dsige.lectura.data.model.*
import com.quavii.dsige.lectura.data.viewModel.StartViewModel
import com.quavii.dsige.lectura.helper.Gps
import com.quavii.dsige.lectura.helper.Util
import com.quavii.dsige.lectura.ui.activities.ListClientsActivity
import com.quavii.dsige.lectura.ui.activities.SuministroActivity
import com.quavii.dsige.lectura.ui.activities.SuministroRepartoActivity
import com.quavii.dsige.lectura.ui.adapters.ServicioAdapter
import com.quavii.dsige.lectura.ui.listeners.OnItemClickListener
import com.quavii.dsige.lectura.ui.services.DistanceService
import com.quavii.dsige.lectura.ui.services.SendRepartoServices
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.realm.RealmList
import kotlinx.android.synthetic.main.fragment_start.*
import java.io.File

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class StartFragment : Fragment() {

    lateinit var startViewModel: StartViewModel
    lateinit var folder: File
    lateinit var image: File

    var online: Int = 0
    var usuarioId: Int = 0
    var nameImg: String = ""
    var direction: String = ""
    var nameServices: String = ""
    var stateServices: Int = 0

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
        return inflater.inflate(R.layout.fragment_start, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startViewModel = ViewModelProviders.of(this).get(StartViewModel::class.java)
        startViewModel.initialRealm()
        val l = startViewModel.getLogin()
        usuarioId = l.iD_Operario
        online = l.operario_EnvioEn_Linea
        bindUI()
        message()
    }

    private fun bindUI() {
        val layoutManager = LinearLayoutManager(context!!)
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.layoutManager = layoutManager
        val serviciodapter = ServicioAdapter(object : OnItemClickListener.ServicesListener {
            override fun onItemClick(s: Servicio, v: View, position: Int) {
                when (s.id_servicio) {
                    5 -> tipoReparto(s.id_servicio, s.nombre_servicio)
                    7 -> startActivity(Intent(context, ListClientsActivity::class.java)) // Util.toastMensaje(context!!, "En Desarrollo")
                    else -> inicioTrabajo(s.id_servicio, s.nombre_servicio)
                }
            }
        })
        recyclerView.adapter = serviciodapter
        val services = startViewModel.getServicio()
        serviciodapter.addItems(services)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                StartFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }

    private fun inicioTrabajo(state: Int, name: String) {
        nameServices = name
        stateServices = state
        val inicio: Registro? = startViewModel.getGoTrabajo(usuarioId, Util.getFecha(), state, "INICIO")
        if (inicio != null) {
//            val final: Registro? = registroImp.getInicioFinTrabajo(usuarioId, Util.getFecha(), "FIN")
//            if (final != null) {
//                Util.toastMensaje(context!!, "Distribucción Cerrada")
//            } else {
            if (state == 1) {
                tipoLectura()
            } else {
                val intent = Intent(context!!, SuministroActivity::class.java)
                intent.putExtra("estado", state)
                intent.putExtra("nombre", name)
                startActivity(intent)
            }
//            }
        } else {
            dialogInicioTrabajo(state)
        }
    }

    private fun tipoLectura() {
        val builder = AlertDialog.Builder(ContextThemeWrapper(context, R.style.AppTheme))
        @SuppressLint("InflateParams") val v = LayoutInflater.from(context).inflate(R.layout.dialog_lectura, null)
        val linearLayoutNormales: LinearLayout = v.findViewById(R.id.linearLayoutNormales)
        val linearLayoutObservadas: LinearLayout = v.findViewById(R.id.linearLayoutObservadas)
        val linearLayoutReclamos: LinearLayout = v.findViewById(R.id.linearLayoutReclamos)
        val textViewCountNormales: TextView = v.findViewById(R.id.textViewCountNormales)
        val textViewCountObservadas: TextView = v.findViewById(R.id.textViewCountObservadas)
        val textViewCountReclamos = v.findViewById<TextView>(R.id.textViewCountReclamos)
        val buttonAceptar: MaterialButton = v.findViewById(R.id.buttonAceptar)
        builder.setView(v)
        val dialog = builder.create()
        dialog.show()

        val count = startViewModel.getLecturaOnCount(1, 0)
        val valor: Int? = count!!.toInt()
        if (valor != 0) {
            textViewCountNormales.visibility = View.VISIBLE
            textViewCountNormales.text = valor.toString()
            linearLayoutNormales.setOnClickListener {
                val intent = Intent(context, SuministroActivity::class.java)
                intent.putExtra("estado", 1)
                intent.putExtra("nombre", "Lectura")
                startActivity(intent)
                dialog.dismiss()
            }
        }

        val countObservada = startViewModel.getLecturaOnCount(1, 1)
        val valorObservada: Int? = countObservada!!.toInt()
        if (valorObservada != 0) {
            textViewCountObservadas.visibility = View.VISIBLE
            textViewCountObservadas.text = valorObservada.toString()
            linearLayoutObservadas.setOnClickListener {
                val intent = Intent(context, SuministroActivity::class.java)
                intent.putExtra("estado", 6)
                intent.putExtra("nombre", "Lectura Observadas")
                startActivity(intent)
                dialog.dismiss()
            }
        }

        val countReclamos = startViewModel.getLecturaReclamoOnCount(1, "9")
        val valorReclamos: Int? = countReclamos!!.toInt()
        if (valorReclamos != 0) {
            textViewCountReclamos.visibility = View.VISIBLE
            textViewCountReclamos.text = valorReclamos.toString()
            linearLayoutReclamos.setOnClickListener {
                val intent = Intent(context!!, SuministroActivity::class.java)
                intent.putExtra("estado", 9)
                intent.putExtra("nombre", "Reclamos")
                startActivity(intent)
            }
        }
        buttonAceptar.setOnClickListener {
            dialog.dismiss()
        }
    }

    private fun tipoReparto(state: Int, name: String) {
        nameServices = name
        stateServices = state
        val inicio: Registro? = startViewModel.getGoTrabajo(usuarioId, Util.getFecha(), 11, "INICIO")
        if (inicio != null) {
            val final: Registro? = startViewModel.getGoTrabajo(usuarioId, Util.getFecha(), 11, "FIN")
            if (final != null) {
                Util.toastMensaje(context!!, "Distribucción Cerrada")
            } else {
                val intent = Intent(context, SuministroRepartoActivity::class.java)
                intent.putExtra("estado", state)
                intent.putExtra("nombre", name)
                intent.putExtra("usuarioId", usuarioId)
                startActivity(intent)
            }
        } else {
            dialogInicioTrabajo(state)
        }
    }

    private fun dialogInicioTrabajo(code: Int) {
        val dialog = MaterialAlertDialogBuilder(context)
                .setTitle("Mensaje")
                .setMessage(String.format("%s", "Deseas Iniciar TRABAJO ?. Debes tomarte una Selfie"))
                .setPositiveButton("Tomar Selfie") { dialog, _ ->
                    createImage(code)
                    dialog.dismiss()
                }
                .setNegativeButton("Salir") { dialog, _ ->
                    dialog.cancel()
                }
        dialog.show()
    }

    private fun createImage(code: Int) {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        takePictureIntent.putExtra("android.intent.extras.CAMERA_FACING", 1)
        if (takePictureIntent.resolveActivity(context!!.packageManager) != null) {
            folder = Util.getFolder()
            nameImg = Util.getFechaActualRepartoPhoto(usuarioId, "INICIO") + ".jpg"
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
            startActivityForResult(takePictureIntent, code)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode != 1) {
                Util.toastMensaje(context!!, "Espere....")
            }
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
                            if (code == 5) {
                                saveRegistroReparto(nameImg)
                            } else {
                                saveRegistro(nameImg, code)
                            }
                        }
                    }

                    override fun onError(e: Throwable) {
                        Util.toastMensaje(context!!, "Volver a intentarlo")
                    }
                })
    }

    private fun saveRegistroReparto(nameImg: String) {
        val gps = Gps(context!!)
        if (gps.isLocationEnabled()) {
            if (gps.latitude.toString() == "0.0" || gps.longitude.toString() == "0.0") {
                gps.showAlert(context!!)
            } else {
                val photos: RealmList<Photo> = RealmList()
                val photo: Photo? = Photo(startViewModel.getPhotoIdentity(), 0, usuarioId, nameImg, Util.getFechaActual(), 11, 1, gps.latitude.toString(), gps.longitude.toString())
                photos.add(photo)
                val registro = Registro(startViewModel.getRegistroIdentity(), usuarioId, usuarioId, Util.getFechaActual(), gps.latitude.toString(), gps.longitude.toString(), 11, 1, "INICIO", nameImg, photos)
                startViewModel.saveZonaPeligrosa(registro)
                context!!.startService(Intent(context!!, DistanceService::class.java))
                context!!.startService(Intent(context!!, SendRepartoServices::class.java))
                val intent = Intent(context, SuministroRepartoActivity::class.java)
                intent.putExtra("estado", stateServices)
                intent.putExtra("nombre", nameServices)
                intent.putExtra("usuarioId", usuarioId)
                startActivity(intent)
            }
        } else {
            gps.showSettingsAlert(context!!)
        }
    }

    private fun saveRegistro(nameImg: String, tipo: Int) {
        val gps = Gps(context!!)
        if (gps.isLocationEnabled()) {
            if (gps.latitude.toString() == "0.0" || gps.longitude.toString() == "0.0") {
                gps.showAlert(context!!)
            } else {
                val photos: RealmList<Photo> = RealmList()
                val photo: Photo? = Photo(startViewModel.getPhotoIdentity(), 0, usuarioId, nameImg, Util.getFechaActual(), tipo, 1, gps.latitude.toString(), gps.longitude.toString())
                photos.add(photo)
                val registro = Registro(startViewModel.getRegistroIdentity(), usuarioId, usuarioId, Util.getFechaActual(), gps.latitude.toString(), gps.longitude.toString(), tipo, 1, "INICIO", "", photos)
                startViewModel.saveZonaPeligrosa(registro)
                if (tipo == 1) {
                    tipoLectura()
                } else {
                    if (online == 1) {
                        startViewModel.sendSelfie(tipo, "INICIO")
                    } else {
                        val intent = Intent(context!!, SuministroActivity::class.java)
                        intent.putExtra("estado", stateServices)
                        intent.putExtra("nombre", nameServices)
                        startActivity(intent)
                    }
                }
            }
        } else {
            gps.showSettingsAlert(context!!)
        }
    }

    private fun message() {
        startViewModel.error.observe(this, androidx.lifecycle.Observer<String> { s ->
            if (s != null) {
                Util.dialogMensaje(context!!, "Mensaje", s)
            }
        })

        startViewModel.success.observe(this, androidx.lifecycle.Observer<String> { s ->
            if (s != null) {
                val intent = Intent(context!!, SuministroActivity::class.java)
                intent.putExtra("estado", stateServices)
                intent.putExtra("nombre", nameServices)
                startActivity(intent)
            }
        })
    }
}