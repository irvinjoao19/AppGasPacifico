package com.quavii.dsige.lectura.ui.activities

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.Toolbar
import android.view.View
import java.util.*
import android.provider.MediaStore
import android.content.Intent
import android.net.Uri
import android.os.*
import androidx.appcompat.view.ContextThemeWrapper
import androidx.recyclerview.widget.DefaultItemAnimator
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.Menu
import android.widget.*
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.GridLayoutManager
import com.quavii.dsige.lectura.data.dao.interfaces.*
import com.quavii.dsige.lectura.data.dao.overMethod.*
import com.quavii.dsige.lectura.data.apiServices.ConexionRetrofit
import com.quavii.dsige.lectura.data.model.*
import com.quavii.dsige.lectura.helper.*
import com.quavii.dsige.lectura.R
import com.quavii.dsige.lectura.data.apiServices.ApiServices
import com.quavii.dsige.lectura.ui.adapters.PhotoAdapter
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import io.realm.RealmResults
import kotlinx.android.synthetic.main.activity_photo.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.util.concurrent.TimeUnit

class PhotoActivity : AppCompatActivity(), View.OnClickListener {

    override fun onClick(v: View) {
        when (v.id) {
            R.id.buttonGrabar -> {
                when {
                    tipo <= 2 || tipo == 9 || tipo == 10 -> {
                        if (cantidad == 1) {
                            updateData(receive, tipo)
                        } else {
                            HelperDialog.MensajeOk(this, "Mensaje", "Se requiere 1 foto")
                        }
                    }
                    // ANTERIOR
                    tipo == 3 -> {
                        if (cantidad == 2) {
                            if (online == 1) {
                                confirmSend()
                            } else {
                                Toast.makeText(this, "Guardado", Toast.LENGTH_LONG).show()
                                updateData(receive, tipo)
                            }
                        } else {
                            HelperDialog.MensajeOk(this, "Mensaje", "Se requiere 2 fotos")
                        }
                    }
//                    tipo == 4 -> {
//                        if (cantidad == 3) {
//                            if (online == 1) {
//                                confirmSend()
//                            } else {
//                                Toast.makeText(this, "Guardado", Toast.LENGTH_LONG).show()
//                                updateData(receive, tipo, "1")
//                            }
//                        } else {
//                            HelperDialog.MensajeOk(this, "Mensaje", "Se requiere 3 fotos")
//                        }
//                    }
                    // NUEVOOOOOOOOOOOOOOOOOOOOOOOO

//                    tipo == 3 -> {
//                        if (cantidad == 2) {
//                            updateData(receive, tipo, "1")
//                        } else {
//                            HelperDialog.MensajeOk(this, "Mensaje", "Se requiere 2 fotos")
//                        }
//                    }
                    tipo == 4 -> {
                        if (cantidad == 3) {
                            updateData(receive, tipo)
                        } else {
                            HelperDialog.MensajeOk(this, "Mensaje", "Se requiere 3 fotos")
                        }
                    }

                }
            }
            R.id.buttonPhoto -> {
                when {
                    tipo <= 2 || tipo == 9 || tipo == 10 -> {
                        if (cantidad < 1) {
                            createImage()
                        } else {
                            HelperDialog.MensajeOk(this, "Mensaje", "Maximo 1 foto")
                        }
                    }
                    tipo == 3 -> {
//                        if (cantidad < 2) {
                            createImage()
//                        } else {
//                            HelperDialog.MensajeOk(this, "Mensaje", "Maximo 2 fotos")
//                        }
                    }
                    tipo == 4 -> {
                        if (cantidad < 3) {
                            createImage()
                        } else {
                            HelperDialog.MensajeOk(this, "Mensaje", "Maximo 3 fotos")
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

    private lateinit var sendInterfaces: ApiServices
    private lateinit var photoAdapter: PhotoAdapter

    lateinit var realm: Realm
    lateinit var photoImp: PhotoImplementation
    lateinit var loginImp: LoginImplementation
    lateinit var suministroImp: SuministroImplementation
    lateinit var registroImp: RegistroImplementation

    lateinit var photos: RealmResults<Photo>
    lateinit var folder: File
    lateinit var image: File
    var nameImg: String = ""
    var direction: String = ""
    var receive: Int = 0
    var tipo: Int = 0
    var cantidad: Int = 0
    var estado: Int = 0
    var parentId: Int = 0

    var registro_Desplaza: String = ""
    var orden: Int = 0
    var titulo: String = ""
    var orden_2: Int = 0
    var online: Int = 0
    var suministro: String = ""
    var fechaAsignacion: String = ""

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("fileName", direction)
        outState.putString("nameImg", nameImg)
        outState.putInt("parentId", parentId)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo)

        if (savedInstanceState != null) {
            direction = savedInstanceState.getString("fileName")!!
            nameImg = savedInstanceState.getString("nameImg")!!
            parentId = savedInstanceState.getInt("parentId")
        }

        sendInterfaces = ConexionRetrofit.api.create(ApiServices::class.java)
        realm = Realm.getDefaultInstance()
        photoImp = PhotoOver(realm)
        registroImp = RegistroOver(realm)
        suministroImp = SuministroOver(realm)
        loginImp = LoginOver(realm)

        val bundle = intent.extras
        if (bundle != null) {
            receive = bundle.getInt("envioId")
            titulo = bundle.getString("nombre")!!
            orden = bundle.getInt("orden")
            orden_2 = bundle.getInt("orden_2")
            tipo = bundle.getInt("tipo")
            estado = bundle.getInt("estado")
            suministro = bundle.getString("suministro")!!
            fechaAsignacion = bundle.getString("fechaAsignacion")!!
            bindUI(receive, tipo)
        }
        bindToolbar()
    }

    private fun bindToolbar() {
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        Objects.requireNonNull<ActionBar>(supportActionBar).title = "Ingresar Foto"
        if (tipo != 3) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            toolbar.setNavigationOnClickListener {
                val intent = Intent(this, SuministroAfterActivity::class.java)
                intent.putExtra("orden", orden)
                intent.putExtra("orden_2", orden_2)
                intent.putExtra("nombre", titulo)
                intent.putExtra("estado", estado)
                startActivity(intent)
                finish()
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (tipo != 3) {
                val intent = Intent(this, SuministroAfterActivity::class.java)
                intent.putExtra("orden", orden)
                intent.putExtra("orden_2", orden_2)
                intent.putExtra("nombre", titulo)
                intent.putExtra("estado", estado)
                startActivity(intent)
                finish()
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun bindUI(id: Int, tipo: Int) {

        val login: Login = loginImp.login!!
        online = login.operario_EnvioEn_Linea

        buttonGrabar.setOnClickListener(this)
        buttonPhoto.setOnClickListener(this)

//        NUEVO
        if (tipo == 4) {
            buttonGrabar.text = String.format("Siguiente")
        }

        val r = registroImp.getRegistroBySuministro(id)
        parentId = r.parentId

        photos = photoImp.photoAllBySuministro(id, tipo, 0)
        cantidad = photos.size
        photos.addChangeListener { result, _ ->
            cantidad = result.size
            photoAdapter.notifyDataSetChanged()
        }

        val layoutManager = GridLayoutManager(this, 2)
        photoAdapter = PhotoAdapter(photos, object : PhotoAdapter.OnItemClickListener {
            override fun onItemClick(photo: Photo, view: View, position: Int) {
                showPopupMenu(photo, view)
            }
        })
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = photoAdapter


        if (cantidad == 0) {
            createImage()
        }

        if (tipo != 2) {
            if (cantidad == 1) {
                createImage()
            }
        }
    }

    // TODO SOBRE FOTO

    private fun createImage() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(Objects.requireNonNull(this@PhotoActivity).packageManager) != null) {
            folder = Util.getFolder(this@PhotoActivity)
            nameImg = Util.getFechaActualForPhoto(suministro.toInt(), tipo) + ".jpg"
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
            startActivityForResult(takePictureIntent, Permission.CAMERA_REQUEST)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Permission.CAMERA_REQUEST && resultCode == RESULT_OK) {
            generateImage()
        }
    }

    private fun generateImage() {
        val image: Observable<Boolean> = Util.generateImageAsync(direction, fechaAsignacion)
        image.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Boolean> {
                    override fun onComplete() {
                        Log.i("PHOTO", "EXITOSO")
                    }

                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onNext(t: Boolean) {
                        if (t) {
                            saveDetalleFotoInspeccion(receive, nameImg)
                        }
                    }

                    override fun onError(e: Throwable) {
                        Log.e("ERROR PHOTO", e.toString())
                        Util.toastMensaje(this@PhotoActivity, "Volver a intentarlo")
                    }
                })
    }

    private fun saveDetalleFotoInspeccion(i: Int, nameImg: String) {
        val gps = Gps(this@PhotoActivity)
        if (gps.isLocationEnabled()) {
            if (gps.latitude.toString() == "0.0" || gps.longitude.toString() == "0.0") {
                gps.showAlert(this@PhotoActivity)
            } else {
                val photo: Photo? = Photo(photoImp.getPhotoIdentity(), 0, i, nameImg, Util.getFechaActual(), tipo, 1, gps.latitude.toString(), gps.longitude.toString())
                photoImp.save(photo!!)

                when {
                    tipo <= 2 || tipo == 9 || tipo == 10 -> {
                        if (cantidad < 1) {
                            Util.toastMensaje(this, "Siguiente Foto")
                            createImage()
                        }
                    }
                    tipo == 3 -> {
                        if (cantidad < 2) {
                            Util.toastMensaje(this, "Siguiente Foto")
                            createImage()
                        }
                    }
                    tipo == 4 -> {
                        if (cantidad < 2) {
                            Util.toastMensaje(this, "Siguiente Foto")
                            createImage()
                        }
                    }
                }
            }
        } else {
            gps.showSettingsAlert(this@PhotoActivity)
        }
    }

    private fun deletePhoto(id: Int) {
        val builder = AlertDialog.Builder(ContextThemeWrapper(this, R.style.AppTheme))
        @SuppressLint("InflateParams") val v = LayoutInflater.from(this).inflate(R.layout.dialog_message, null)
        val textViewTitle = v.findViewById<TextView>(R.id.textViewTitle)
        val textViewMessage = v.findViewById<TextView>(R.id.textViewMessage)
        val buttonCancelar = v.findViewById<Button>(R.id.buttonCancelar)
        val buttonAceptar = v.findViewById<Button>(R.id.buttonAceptar)
        textViewTitle.text = String.format("Mensaje")
        textViewMessage.text = String.format("Estas seguro de Eliminar esta foto ?")

        builder.setView(v)
        val dialog = builder.create()
        dialog.show()
        buttonAceptar.setOnClickListener {
            photoImp.delete(id, tipo)
            dialog.dismiss()
        }
        buttonCancelar.setOnClickListener {
            dialog.dismiss()
        }
    }


    private fun updateData(receive: Int, tipo: Int) {

        when {
            tipo <= 2 || tipo == 9 || tipo == 10 -> {
                registroImp.updateRegistroDesplaza(receive, tipo, 1)
                suministroImp.updateActivoSuministroLectura(receive, 0)
                siguienteOrden(tipo, estado)
            }
            //NUEVO PARA FIRMAS
//            tipo == 3 -> {
//                registroImp.updateRegistroDesplaza(receive, tipo, registro_Desplaza, 2)
//                val intent = Intent(this@PhotoActivity, FirmActivity::class.java)
//                intent.putExtra("envioId", receive)
//                intent.putExtra("tipo", tipo)
//                intent.putExtra("online", online)
//                intent.putExtra("orden", orden)
//                intent.putExtra("orden_2", orden_2)
//                intent.putExtra("suministro", suministro)
//                startActivity(intent)
//            }

            tipo == 4 -> {
//                if (parentId == 92) {
                    if (online == 1) {
                        sendDataRx(this,suministro, orden_2, tipo)
//                    SendData().execute(suministro, orden_2.toString(), tipo.toString())
                    } else {
                        registroImp.updateRegistroDesplaza(receive, tipo, 1)
                        suministroImp.updateActivoSuministroReconexion(receive, 0)
                        siguienteOrden(tipo, estado)
                    }
//                } else {
//                    registroImp.updateRegistroDesplaza(receive, tipo, 2)
//                    val intent = Intent(this@PhotoActivity, ReconexionFirmActivity::class.java)
//                    intent.putExtra("envioId", receive)
//                    intent.putExtra("tipo", tipo)
//                    intent.putExtra("online", online)
//                    intent.putExtra("orden", orden)
//                    intent.putExtra("orden_2", orden_2)
//                    intent.putExtra("suministro", suministro)
//                    intent.putExtra("estado", estado)
//                    intent.putExtra("nombre", titulo)
//                    intent.putExtra("fechaAsignacion", fechaAsignacion)
//                    startActivity(intent)
//                    finish()
//                }
            }

            // ANTERIOR
            tipo == 3 -> {
                if (online == 1) {
                    sendDataRx(this,suministro, orden_2, tipo)
//                    SendData().execute(suministro, orden_2.toString(), tipo.toString())
                } else {
                    registroImp.updateRegistroDesplaza(receive, tipo, 1)
                    suministroImp.updateActivoSuministroCortes(receive, 0)
                    siguienteOrden(tipo, estado)
                }
            }
//            tipo == 4 -> {
//                if (online == 1) {
//                    sendDataRx(suministro, orden_2, tipo)
////                    SendData().execute(suministro, orden_2.toString(), tipo.toString())
//                } else {
//                    registroImp.updateRegistroDesplaza(receive, tipo, 1)
//                    suministroImp.updateActivoSuministroReconexion(receive, 0)
//                    siguienteOrden(tipo, estado)
//                }
//            }
        }
    }

    private fun siguienteOrden(tipo: Int, estado: Int) {
        when {
            tipo <= 2 || tipo == 10 -> {
                val suministrosLectura = when (estado) {
                    6 -> suministroImp.getSuministroLectura(1, 1, 1)
                    else -> suministroImp.getSuministroLectura(tipo, 1, 0)
                }
                val nombre = when (estado) {
                    1 -> "Lectura Normales"
                    2 -> "Relectura"
                    6 -> "Lectura Observadas"
                    7 -> "Lectura Manuales"
                    10 -> "Lectura Recuperadas"
                    else -> {
                        ""
                    }
                }
                val returnOrden = AfterOrden.getNextOrdenLectura(orden, suministrosLectura)
                if (returnOrden == 0) {
                    val intent = Intent(this@PhotoActivity, SuministroActivity::class.java)
                    intent.putExtra("nombre", nombre)
                    intent.putExtra("estado", estado)
                    startActivity(intent)
                    finish()
                } else {
                    val ordenLecttura = suministroImp.buscarLecturaByOrden(returnOrden, 1)
                    val intent = Intent(this@PhotoActivity, SuministroAfterActivity::class.java)
                    intent.putExtra("orden", returnOrden)
                    intent.putExtra("orden_2", ordenLecttura.suministroOperario_Orden)
                    intent.putExtra("nombre", nombre)
                    intent.putExtra("estado", estado)
                    startActivity(intent)
                    finish()
                }
            }
            tipo == 9 -> {
                val suministrosLectura = suministroImp.getSuministroReclamos("9", 1)
                val returnOrden = AfterOrden.getNextOrdenLectura(orden, suministrosLectura)
                if (returnOrden == 0) {
                    val intent = Intent(this@PhotoActivity, SuministroActivity::class.java)
                    intent.putExtra("nombre", "Reclamos")
                    intent.putExtra("estado", estado)
                    startActivity(intent)
                    finish()
                } else {
                    val ordenLecttura = suministroImp.buscarLecturaByOrden(returnOrden, 1)
                    val intent = Intent(this@PhotoActivity, SuministroAfterActivity::class.java)
                    intent.putExtra("orden", returnOrden)
                    intent.putExtra("orden_2", ordenLecttura.suministroOperario_Orden)
                    intent.putExtra("nombre", "Reclamos")
                    intent.putExtra("estado", estado)
                    startActivity(intent)
                    finish()
                }
            }
            tipo == 3 -> {
//                val suministrosCortes = suministroImp.getSuministroCortes(tipo, 1)
//                val returnOrden = AfterOrden.getNextOrdenCortes(orden, suministrosCortes)
//                if (returnOrden == 0) {
                    val intent = Intent(this@PhotoActivity, SuministroActivity::class.java)
                    intent.putExtra("nombre", "Corte")
                    intent.putExtra("estado", tipo)
                    startActivity(intent)
                    finish()
//                } else {
//                    val ordenCortes = suministroImp.buscarCortesByOrden(returnOrden, 1)
//                    val intent = Intent(this@PhotoActivity, SuministroAfterActivity::class.java)
//                    intent.putExtra("orden", returnOrden)
//                    intent.putExtra("orden_2", ordenCortes.suministroOperario_Orden)
//                    intent.putExtra("nombre", "Corte")
//                    intent.putExtra("estado", tipo)
//                    startActivity(intent)
//                    finish()
//                }
            }
            tipo == 4 -> {
//                val suministrosReconexion = suministroImp.getSuministroReconexion(tipo, 1)
//                val returnOrden = AfterOrden.getNextOrdenReconexion(orden, suministrosReconexion)
//                if (returnOrden == 0) {
                    val intent = Intent(this@PhotoActivity, SuministroActivity::class.java)
                    intent.putExtra("nombre", "Reconexion")
                    intent.putExtra("estado", tipo)
                    startActivity(intent)
                    finish()
//                } else {
//                    val ordenReconexion = suministroImp.buscarReconexionesByOrden(returnOrden, 1)
//                    val intent = Intent(this@PhotoActivity, SuministroAfterActivity::class.java)
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

    // TODO SOBRE ENVIO ANTERIOR --

    private fun confirmSend() {
        val builder = AlertDialog.Builder(ContextThemeWrapper(this, R.style.AppTheme))
        @SuppressLint("InflateParams") val v = LayoutInflater.from(this).inflate(R.layout.dialog_message, null)
        val textViewTitle = v.findViewById<TextView>(R.id.textViewTitle)
        val textViewMessage = v.findViewById<TextView>(R.id.textViewMessage)
        val buttonCancelar = v.findViewById<Button>(R.id.buttonCancelar)
        val buttonAceptar = v.findViewById<Button>(R.id.buttonAceptar)

        textViewTitle.text = String.format("Mensaje")
        textViewMessage.text = String.format("Estas seguro de enviar")
        registro_Desplaza = "1"
        builder.setView(v)
        val dialog = builder.create()
        dialog.show()

        buttonAceptar.setOnClickListener {
            updateData(receive, tipo)
            dialog.dismiss()
        }
        buttonCancelar.setOnClickListener {
            dialog.dismiss()
        }
    }

    private fun sendDataRx(context: Context, suministro: String?, orden: Int, tipo: Int) {
        val builder = AlertDialog.Builder(ContextThemeWrapper(this@PhotoActivity, R.style.AppTheme))
        @SuppressLint("InflateParams") val view = LayoutInflater.from(this@PhotoActivity).inflate(R.layout.dialog_alert, null)
        val textViewTitulo: TextView = view.findViewById(R.id.textViewTitulo)
        val textView: TextView = view.findViewById(R.id.textView)
        textViewTitulo.text = String.format("Enviando")
        textView.text = String.format("Espere un momento")
        builder.setView(view)

        val dialog = builder.create()
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()

        val auditorias = registroImp.getRegistroByOrdenRx(orden, tipo)
        var mensaje = ""
        auditorias.flatMap { a ->
            val realm = Realm.getDefaultInstance()
            val registroImpRx: RegistroImplementation = RegistroOver(realm)
            val b = MultipartBody.Builder()
            val filePaths: ArrayList<String> = ArrayList()
            var tieneFoto = 0
            var estado = "1"

            for (p: Photo in a.photos!!) {
                if (p.rutaFoto.isNotEmpty()) {
                    val file = File(Util.getFolder(context), p.rutaFoto)
                    if (file.exists()) {
                        filePaths.add(file.toString())
                        tieneFoto++
                    } else {
                        registroImpRx.closePhotoEstado(0, p)
                        estado = "0"
                    }
                }
            }

            for (i in 0 until filePaths.size) {
                val file = File(filePaths[i])
                b.addFormDataPart("fotos", file.name, RequestBody.create(MediaType.parse("multipart/form-data"), file))
            }

            val r = registroImpRx.updateRegistroTienePhoto(tieneFoto, estado, a)
            val json = Gson().toJson(realm.copyFromRealm(r))
            Log.i("TAG", json)
            b.setType(MultipartBody.FORM)
            b.addFormDataPart("model", json)
            b.addFormDataPart("suministro", suministro!!)

            val requestBody = b.build()
            Observable.zip(Observable.just(a), sendInterfaces.sendRegistroCorteRx(requestBody), BiFunction<Registro, Mensaje, Mensaje> { registro, mensaje ->
                Log.i("TAG", "PASO AQUI•")
                registroImpRx.closeOneRegistro(registro, 0)
                mensaje
            })
        }.subscribeOn(Schedulers.io())
                .delay(600, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Mensaje> {

                    override fun onSubscribe(d: Disposable) {
                        Log.i("TAG", d.toString())
                    }

                    override fun onNext(t: Mensaje) {
                        mensaje = t.mensaje
                    }

                    override fun onError(e: Throwable) {
                        mensajeRx(mensaje, e)
                        dialog.dismiss()
                    }

                    override fun onComplete() {
                        mensajeRx(mensaje, null)
                        dialog.dismiss()
                    }
                })
    }

    private fun mensajeRx(s: String, e: Throwable?) {
        val builder = AlertDialog.Builder(ContextThemeWrapper(this@PhotoActivity, R.style.AppTheme))
        @SuppressLint("InflateParams") val v = LayoutInflater.from(this@PhotoActivity).inflate(R.layout.dialog_message, null)
        val textViewMessage: TextView = v.findViewById(R.id.textViewMessage)
        val textViewTitle: TextView = v.findViewById(R.id.textViewTitle)
        val buttonCancelar: MaterialButton = v.findViewById(R.id.buttonCancelar)
        buttonCancelar.visibility = View.GONE
        val buttonAceptar: MaterialButton = v.findViewById(R.id.buttonAceptar)
        textViewTitle.text = titulo
        textViewMessage.textSize = 18f

        builder.setView(v)
        val dialog = builder.create()
        builder.show()

        if (e != null) {
            textViewMessage.text = Util.MessageInternet
            buttonCancelar.visibility = View.VISIBLE
            buttonCancelar.text = Util.ButtonSiguiente
        } else {
            textViewMessage.text = s
        }

        buttonAceptar.setOnClickListener {
            if (e == null) {
                if (tipo == 3) {
                    suministroImp.updateActivoSuministroCortes(receive, 0)
                    siguienteOrden(tipo, estado)
                } else if (tipo == 4) {
                    suministroImp.updateActivoSuministroReconexion(receive, 0)
                    siguienteOrden(tipo, estado)
                }
            } else {
                sendDataRx(this,suministro, orden_2, tipo)
            }
            dialog.dismiss()
        }

        buttonCancelar.setOnClickListener {
            registroImp.updateRegistroDesplaza(receive, tipo, 1)
            if (tipo == 3) {
                suministroImp.updateActivoSuministroCortes(receive, 0)
                siguienteOrden(tipo, orden)
            } else if (tipo == 4) {
                suministroImp.updateActivoSuministroReconexion(receive, 0)
                siguienteOrden(tipo, orden)
            }
            Util.toastMensaje(this@PhotoActivity, "Guardado en Pendientes")
            dialog.dismiss()
        }
    }

    private fun showPopupMenu(p: Photo, v: View) {
        val popupMenu = PopupMenu(this, v)
        popupMenu.menu.add(0, Menu.FIRST, 0, getText(R.string.ver))
        popupMenu.menu.add(1, Menu.FIRST + 1, 1, getText(R.string.deletePhoto))
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                1 -> {
                    val intent = Intent(this@PhotoActivity, ShowPhotoActivity::class.java)
                    intent.putExtra("nombre", p.rutaFoto)
                    intent.putExtra("envioId", p.iD_Foto)
                    startActivity(intent)
                }
                2 -> {
                    deletePhoto(p.iD_Foto)
                }
            }
            false
        }
        popupMenu.show()
    }
}