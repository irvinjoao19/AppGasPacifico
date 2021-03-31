package com.quavii.dsige.lectura.ui.activities

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.provider.MediaStore
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.Toolbar
import android.view.View
import com.quavii.dsige.lectura.data.model.Photo
import com.quavii.dsige.lectura.data.model.Registro
import com.quavii.dsige.lectura.helper.Gps
import com.quavii.dsige.lectura.helper.Util
import com.quavii.dsige.lectura.R
import io.realm.Realm
import java.io.File
import java.util.*
import android.graphics.BitmapFactory
import android.util.Log
import androidx.appcompat.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.widget.*
import com.quavii.dsige.lectura.data.dao.interfaces.*
import com.quavii.dsige.lectura.data.dao.overMethod.*
import com.quavii.dsige.lectura.ui.services.DistanceService
import com.google.android.material.button.MaterialButton
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.realm.RealmList

class SelfiViewPhoto : AppCompatActivity(), View.OnClickListener {

    private lateinit var imageView: ImageView
    private lateinit var imageViewButton: ImageView
    private lateinit var linearLayout: LinearLayout

    private lateinit var realm: Realm
    private lateinit var loginImp: LoginImplementation
    private lateinit var servicioImp: ServicioImplementation
    private lateinit var photoImp: PhotoImplementation
    private lateinit var registroImp: RegistroImplementation
    private lateinit var photoRepartoImp: PhotoRepartoImplementation

    private lateinit var folder: File
    private lateinit var image: File

    private var idUser: Int = 0
    private var nameImg: String = ""
    private var direction: String = ""
    private var registro_Latitud: String = ""
    private var registro_Longitud: String = ""
    private var titulo: String = ""

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("fileName", direction)
        outState.putString("nameImg", nameImg)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selfi_view_photo)

        if (savedInstanceState != null) {
            direction = savedInstanceState.getString("fileName")!!
            nameImg = savedInstanceState.getString("nameImg")!!
        }

        realm = Realm.getDefaultInstance()
        photoRepartoImp = PhotoRepartoOver(realm)
        loginImp = LoginOver(realm)
        servicioImp = ServicioOver(realm)
        servicioImp = ServicioOver(realm)
        photoImp = PhotoOver(realm)
        registroImp = RegistroOver(realm)

        val bundle = intent.extras
        if (bundle != null) {
            titulo = bundle.getString("repartoSelfi")!!
        }
        bindToolbar()
        binIU()
    }

    private fun bindToolbar() {
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        Objects.requireNonNull<ActionBar>(supportActionBar).title = "Selfie"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun binIU() {
        imageView = findViewById(R.id.imageView)
        imageViewButton = findViewById(R.id.imageViewButton)
        linearLayout = findViewById(R.id.linearLayout)
        imageViewButton.setOnClickListener(this)
        linearLayout.setOnClickListener(this)
        val user = loginImp.login
        idUser = user!!.iD_Operario
    }

    override fun onClick(v: View?) {
        if (v == imageViewButton) {
            val gps = Gps(this@SelfiViewPhoto)
            if (gps.isLocationEnabled()) {
                if (gps.latitude.toString() == "0.0" || gps.longitude.toString() == "0.0") {
                    gps.showAlert(this@SelfiViewPhoto)
                } else {
                    registro_Latitud = gps.latitude.toString()
                    registro_Longitud = gps.longitude.toString()
                    createImage()
                }
            } else {
                gps.showSettingsAlert(this@SelfiViewPhoto)
            }
        } else if (v == linearLayout) {
            deletePhoto()
        }
    }

    private fun createImage() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        takePictureIntent.putExtra("android.intent.extras.CAMERA_FACING", -1)
        if (takePictureIntent.resolveActivity(Objects.requireNonNull(this@SelfiViewPhoto).packageManager) != null) {
            folder = Util.getFolder(this@SelfiViewPhoto)
            nameImg = Util.getFechaActualRepartoPhoto(idUser, titulo) + ".jpg"
            image = File(folder, nameImg)
            direction = "$folder/$nameImg"
            val uriSavedImage = Uri.fromFile(image)
//            val compressedImageFile:File = Compressor(this).compressToFile(image)

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
                            saveRegistro(titulo, nameImg)
                            linearLayout.visibility = View.VISIBLE
                            imageViewButton.visibility = View.GONE
                        }
                    }

                    override fun onError(e: Throwable) {
                        Log.e("ERROR PHOTO", e.toString())
                        Util.toastMensaje(this@SelfiViewPhoto, "Volver a intentarlo")
                    }
                })
    }


    private fun saveRegistro(observacion: String, nameImg: String) {
        val gps = Gps(this@SelfiViewPhoto)
        if (gps.isLocationEnabled()) {
            if (gps.latitude.toString() == "0.0" || gps.longitude.toString() == "0.0") {
                gps.showAlert(this@SelfiViewPhoto)
            } else {
                val photos: RealmList<Photo> = RealmList()
                val photo = Photo(photoImp.getPhotoIdentity(), 0, idUser, nameImg, Util.getFechaActual(), 9, 1, gps.latitude.toString(), gps.longitude.toString())
                photos.add(photo)
                val registro = Registro(registroImp.getRegistroIdentity(), idUser, idUser, Util.getFechaActual(), registro_Latitud, registro_Longitud, 9, 1, observacion, nameImg, photos)
                if (observacion == "INICIO") {
//                    startService(Intent(this, AlertRepartoService::class.java))
                    startService(Intent(this, DistanceService::class.java))
                } else {
//                    stopService(Intent(this, AlertRepartoSleepService::class.java))
//                    stopService(Intent(this, AlertRepartoService::class.java))
                    stopService(Intent(this, DistanceService::class.java))
                }
                registroImp.saveZonaPeligrosa(registro)
            }
        } else {
            gps.showSettingsAlert(this@SelfiViewPhoto)
        }
    }

    override fun onResume() {
        val imageView: ImageView = findViewById(R.id.imageView)
        imageView.setImageBitmap(BitmapFactory.decodeFile(direction))
        super.onResume()
    }

    private fun deletePhoto() {
        var dialog: AlertDialog? = null
        val builder = AlertDialog.Builder(ContextThemeWrapper(this, R.style.AppTheme))
        @SuppressLint("InflateParams") val v = LayoutInflater.from(this).inflate(R.layout.dialog_message, null)
        val textViewTitle: TextView = v.findViewById(R.id.textViewTitle)
        val textViewMessage: TextView = v.findViewById(R.id.textViewMessage)
        val buttonCancelar: MaterialButton = v.findViewById(R.id.buttonCancelar)
        val buttonAceptar: MaterialButton = v.findViewById(R.id.buttonAceptar)
        textViewTitle.text = String.format("Mensaje")
        textViewMessage.text = String.format("Estas seguro de Eliminar esta foto ?")
        buttonAceptar.setOnClickListener {
            photoRepartoImp.deleteSelfi(nameImg)
            photoRepartoImp.deleteSelfiRegistro(nameImg)
            val file = File(direction)
            file.delete()
            sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(File(direction))))
            Toast.makeText(this@SelfiViewPhoto, "Se elimino con exito", Toast.LENGTH_SHORT).show()
            val imageView = findViewById<ImageView>(R.id.imageView)
            imageView.setImageBitmap(BitmapFactory.decodeFile(direction))
            linearLayout.visibility = View.GONE
            imageViewButton.visibility = View.VISIBLE
            dialog!!.cancel()
        }
        buttonCancelar.setOnClickListener {
            dialog!!.cancel()
        }
        builder.setView(v)
        dialog = builder.create()
        dialog!!.show()

    }
}
