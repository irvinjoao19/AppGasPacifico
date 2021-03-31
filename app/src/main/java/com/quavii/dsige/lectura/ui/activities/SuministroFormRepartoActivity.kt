package com.quavii.dsige.lectura.ui.activities

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.provider.MediaStore
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.Toolbar
import android.util.Log
import android.view.*
import android.view.Menu
import android.widget.*
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.quavii.dsige.lectura.data.dao.interfaces.PhotoImplementation
import com.quavii.dsige.lectura.data.dao.interfaces.RegistroImplementation
import com.quavii.dsige.lectura.data.dao.interfaces.SuministroImplementation
import com.quavii.dsige.lectura.data.dao.overMethod.PhotoOver
import com.quavii.dsige.lectura.data.dao.overMethod.PhotoRepartoOver
import com.quavii.dsige.lectura.data.dao.overMethod.RegistroOver
import com.quavii.dsige.lectura.data.dao.overMethod.SuministroOver
import com.quavii.dsige.lectura.data.model.*
import com.quavii.dsige.lectura.helper.HelperDialog
import com.quavii.dsige.lectura.helper.Gps
import com.quavii.dsige.lectura.helper.Util
import com.quavii.dsige.lectura.R
import com.quavii.dsige.lectura.ui.adapters.PhotoRepartoAdapter
import com.google.android.material.button.MaterialButton
import com.quavii.dsige.lectura.ui.listeners.OnItemClickListener
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_suministro_form_reparto.*
import java.io.File
import java.util.*

class SuministroFormRepartoActivity : AppCompatActivity(), View.OnClickListener {

    override fun onClick(v: View?) {
        when (v) {
            buttonSaved -> {
                if (cantidad!! >= 1) {
                    val s: SuministroReparto = suministroOver.suministroRepartoDatos(barcode_code)
                    val repartoId = suministroOver.getRegistroReparto(s.id_Reparto)
                    startActivity(Intent(this, RepartoReciboFormActivity::class.java)
                            .putExtra("repartoId", repartoId)
                            .putExtra("recibo", s.Suministro_Numero_reparto)
                            .putExtra("operarioId", s.id_Operario_Reparto)
                            .putExtra("cliente", s.Cliente_Reparto)
                            .putExtra("validation", validation))
                    finish()
                } else {
                    HelperDialog.MensajeOk(this, "Mensaje", "Se requiere foto")
                }
            }
            imageView -> {
                if (tipo!! == 5) {
                    if (cantidad!! < 2) {
                        createImage()
                    } else {
                        Util.dialogMensaje(this, "Mensaje", "Maximo 2 fotos")
                    }
                }
            }
        }
    }

    lateinit var realm: Realm

    private lateinit var play: MediaPlayer
    private lateinit var play_3: MediaPlayer

    private var photoRepartoOver: PhotoRepartoOver? = null
    lateinit var photoRepartoAdapter: PhotoRepartoAdapter

    private lateinit var photoImp: PhotoImplementation
    private lateinit var registroImp: RegistroImplementation

    private lateinit var suministroOver: SuministroImplementation
    private lateinit var registroOver: RegistroOver

    private var cantidad: Int? = 0
    private lateinit var gps: Gps
    private var tipo: Int? = 0
    private lateinit var folder: File
    private lateinit var image: File

    var nameImg: String = ""
    var direction: String = ""

    var barcode_code: String = ""
    var Cod_Orden_Reparto: String = ""
    var id_Cab_Reparto: Int = 0
    var validation: Int = 0

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("fileName", direction)
        outState.putString("nameImg", nameImg)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_suministro_form_reparto)

        if (savedInstanceState != null) {
            direction = savedInstanceState.getString("fileName")!!
            nameImg = savedInstanceState.getString("nameImg")!!
        }

        realm = Realm.getDefaultInstance()
        photoRepartoOver = PhotoRepartoOver(realm)
        suministroOver = SuministroOver(realm)
        registroOver = RegistroOver(realm)
        registroImp = RegistroOver(realm)
        photoImp = PhotoOver(realm)
        val bundle = intent.extras
        if (bundle != null) {
            Cod_Orden_Reparto = bundle.getString("Cod_Orden_Reparto")!!
            id_Cab_Reparto = bundle.getInt("id_cab_Reparto")
            bindUI(bundle.getString("suministroNumeroReparto")!!)
        }
    }

    private fun bindUI(suministroNumeroReparto: String) {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "SuministroReparto"

        play_3 = MediaPlayer.create(this@SuministroFormRepartoActivity, R.raw.ic_error)
        play = MediaPlayer.create(this@SuministroFormRepartoActivity, R.raw.alerta_foto)

        photoRepartoAdapter = PhotoRepartoAdapter(object : OnItemClickListener.PhotoListener {
            override fun onItemClick(f: Photo, view: View, position: Int) {
                showPopupMenu(f, view)
            }
        })
        val layoutManager = LinearLayoutManager(this@SuministroFormRepartoActivity)
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = photoRepartoAdapter

        buttonSaved.setOnClickListener(this)
        imageView.setOnClickListener(this)
        textViewTitle.text = getString(R.string.codigoManual)
        suministroReparto.text = String.format("Cuenta Contrato : %s", suministroNumeroReparto)
        generateReparto(suministroNumeroReparto)
    }

    private fun generateReparto(barCode: String) {
        val reparto: SuministroReparto? = suministroOver.getCodigoBarra(barCode, 1)
        if (reparto != null) {
            gps = Gps(this@SuministroFormRepartoActivity)
            textView.text = reparto.Cliente_Reparto
            textView1.text = reparto.Direccion_Reparto
            textView2.text = reparto.CodigoBarra
            textView3.text = reparto.Suministro_Medidor_reparto
            tipo = reparto.estado
            barcode_code = barCode
            cardViewRegistro.visibility = View.GONE
            validation = reparto.foto_Reparto
            play.start()
            suministroOver.repartoSaved(registroImp.getRegistroIdentity(), reparto.id_Reparto, reparto.id_Operario_Reparto, Util.getFechaActual(), gps.latitude.toString(), gps.longitude.toString(), reparto.id_observacion.toString(), 0)
            cardViewRegistro.visibility = View.VISIBLE
            bindListFoto(reparto.id_Reparto, tipo!!)
            cardViewDescripcion.visibility = View.VISIBLE
        } else {
            cardViewDescripcion.visibility = View.GONE
            play_3.start()
        }
    }

    private fun createImage() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(Objects.requireNonNull(this@SuministroFormRepartoActivity).packageManager) != null) {
            folder = Util.getFolder(this)
            nameImg = Util.getFechaActualRepartoPhoto(id_Cab_Reparto, Cod_Orden_Reparto) + ".jpg"
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
            startActivityForResult(takePictureIntent, 1)
        }
    }

    private fun bindListFoto(id: Int, tipo: Int) {
        val photos = photoRepartoOver!!.photoAllById(id, tipo)
        cantidad = photos.size
        photos.addChangeListener { result ->
            cantidad = result.size
            photoRepartoAdapter.addItems(result)
        }
        photoRepartoAdapter.addItems(photos)
    }

    private fun deletePhoto(id: Int) {
        val builder = AlertDialog.Builder(ContextThemeWrapper(this, R.style.AppTheme))
        @SuppressLint("InflateParams") val v = LayoutInflater.from(this).inflate(R.layout.dialog_message, null)
        val textViewTitle: TextView = v.findViewById(R.id.textViewTitle)
        val textViewMessage: TextView = v.findViewById(R.id.textViewMessage)
        val buttonCancelar: MaterialButton = v.findViewById(R.id.buttonCancelar)
        val buttonAceptar: MaterialButton = v.findViewById(R.id.buttonAceptar)
        builder.setView(v)
        val dialog = builder.create()
        dialog.show()

        textViewTitle.text = String.format("%s", "Mensaje")
        textViewMessage.text = String.format("%s", "Estas seguro de Eliminar esta foto ?")
        buttonAceptar.setOnClickListener {
            photoRepartoOver!!.delete(id)
            suministroOver.suministroRepartoUpdate(barcode_code, 1)
            cardViewRegistro.visibility = View.VISIBLE
            dialog.cancel()
        }
        buttonCancelar.setOnClickListener {
            dialog.cancel()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) {
            generateImage()
        }
    }

    private fun generateImage() {
        val image: Observable<Boolean> = Util.generateImageAsync(direction)
        image.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Boolean> {
                    override fun onComplete() {
                        Log.i("ERROR PHOTO", "EXITOSO")
                    }

                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onNext(t: Boolean) {
                        if (t) {
                            val suministroReparto: SuministroReparto? = suministroOver.suministroRepartoDatos(barcode_code)
                            saveFotoReparto(suministroReparto!!.id_Reparto, nameImg)
//                            bindListFoto(suministroReparto.id_Reparto, tipo!!)
                        }
                    }

                    override fun onError(e: Throwable) {
                        Log.e("ERROR PHOTO", e.toString())
                        Util.toastMensaje(this@SuministroFormRepartoActivity, "Volver a intentarlo")
                    }
                })
    }

    private fun saveFotoReparto(i: Int, nameImg: String) {
        val gps = Gps(this@SuministroFormRepartoActivity)
        if (gps.isLocationEnabled()) {
            if (gps.latitude.toString() == "0.0" || gps.longitude.toString() == "0.0") {
                gps.showAlert(this@SuministroFormRepartoActivity)
            } else {
                val photo: Photo? = Photo(photoImp.getPhotoIdentity(), 0, i, nameImg, Util.getFechaActual(), 5, 1, gps.latitude.toString(), gps.longitude.toString())
                photoImp.save(photo!!)
            }
        } else {
            gps.showSettingsAlert(this@SuministroFormRepartoActivity)
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish()
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun showPopupMenu(p: Photo, v: View) {
        val popupMenu = PopupMenu(this, v)
        popupMenu.menu.add(0, Menu.FIRST, 0, getText(R.string.ver))
        popupMenu.menu.add(1, Menu.FIRST + 1, 1, getText(R.string.deletePhoto))
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                1 -> {
                    val intent = Intent(this@SuministroFormRepartoActivity, ViewPhoto::class.java)
                    intent.putExtra("nameViewPhoto", p.rutaFoto)
                    intent.putExtra("envioIdReparto", p.iD_Foto)
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