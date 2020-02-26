package com.quavii.dsige.lectura.ui.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.provider.MediaStore
import android.util.Log
import android.view.*
import androidx.appcompat.widget.*
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.quavii.dsige.lectura.data.model.SuministroReparto
import com.quavii.dsige.lectura.R
import com.quavii.dsige.lectura.data.model.Photo
import com.quavii.dsige.lectura.data.model.Registro
import com.quavii.dsige.lectura.data.viewModel.EnvioViewModel
import com.quavii.dsige.lectura.helper.Gps
import com.quavii.dsige.lectura.helper.Util
import com.quavii.dsige.lectura.ui.adapters.SuministroRepartoAdapter
import com.quavii.dsige.lectura.ui.listeners.OnItemClickListener
import com.quavii.dsige.lectura.ui.services.DistanceService
import com.quavii.dsige.lectura.ui.services.SendRepartoServices
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.realm.RealmList
import kotlinx.android.synthetic.main.activity_suministro_reparto.*
import java.io.File
import java.util.*

class SuministroRepartoActivity : AppCompatActivity(), View.OnClickListener {
    override fun onClick(v: View) {
        when (v.id) {
            R.id.fabCloseReparto -> mensajeConfirmacion()
            R.id.fabMap -> startActivity(Intent(this, PendingLocationMapsActivity::class.java).putExtra("estado", estado))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.search, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        menu.findItem(R.id.action_menu).isVisible = false
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_search -> {
                val searchView = item.actionView as SearchView
                search(searchView)
                return true
            }
            R.id.action_menu -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    lateinit var suministroRepartoAdapter: SuministroRepartoAdapter
    lateinit var envioViewModel: EnvioViewModel

    lateinit var folder: File
    lateinit var image: File

    var usuarioId: Int = 0
    var nameImg: String = ""
    var direction: String = ""
    var estado: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_suministro_reparto)
        envioViewModel = ViewModelProviders.of(this).get(EnvioViewModel::class.java)
        envioViewModel.initialRealm()
        val bundle = intent.extras
        if (bundle != null) {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            estado = bundle.getInt("estado")
            usuarioId = bundle.getInt("usuarioId")
            bindToolbar(bundle.getString("nombre")!!)
            bindUI()
        }
    }

    private fun bindToolbar(nombre: String) {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = nombre
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun bindUI() {
        fabCloseReparto.setOnClickListener(this)
        fabMap.setOnClickListener(this)

        val layoutManager = LinearLayoutManager(this@SuministroRepartoActivity)
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.layoutManager = layoutManager

        suministroRepartoAdapter = SuministroRepartoAdapter(object : OnItemClickListener.RepartoListener {
            override fun onItemClick(r: SuministroReparto, v: View, position: Int) {
                when (v.id) {
                    R.id.imageViewMap -> {
                        if (r.latitud.isNotEmpty() || r.longitud.isNotEmpty()) {
                            startActivity(Intent(this@SuministroRepartoActivity, MapsActivity::class.java)
                                    .putExtra("latitud", r.latitud)
                                    .putExtra("longitud", r.longitud)
                                    .putExtra("title", r.Suministro_Numero_reparto))
                        } else {
                            Util.toastMensaje(this@SuministroRepartoActivity, "Este suministro no cuenta con coordenadas")
                        }
                    }
                }
            }
        })
        recyclerView.adapter = suministroRepartoAdapter
        val r = envioViewModel.getSuministroReparto()
        r.addChangeListener { s ->
            suministroRepartoAdapter.addItems(s)
        }
        suministroRepartoAdapter.addItems(r)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish()
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun search(searchView: SearchView) {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                suministroRepartoAdapter.getFilter().filter(newText)
                return true
            }
        })
    }

    private fun mensajeConfirmacion() {
        val dialog = MaterialAlertDialogBuilder(this)
                .setTitle("Mensaje")
                .setMessage(String.format("%s ?.", "Seguro de cerrar el TRABAJO ?. Recuerda que al tomar la selfie no podras ingresar a la lista de Distribución"))
                .setPositiveButton("Entiendo") { dialog, _ ->
                    createImage()
                    dialog.dismiss()
                }
                .setNegativeButton("Salir") { dialog, _ ->
                    dialog.cancel()
                }
        dialog.show()
    }

    private fun createImage() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        takePictureIntent.putExtra("android.intent.extras.CAMERA_FACING", 1)
        if (takePictureIntent.resolveActivity(Objects.requireNonNull(this).packageManager) != null) {
            folder = Util.getFolder()
            nameImg = Util.getFechaActualRepartoPhoto(usuarioId, "FIN") + ".jpg"
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            Util.toastMensaje(this, "Cerrando Distribución ....")
            generateImage()
        }
    }

    private fun generateImage() {
        val image: Observable<Boolean> = Util.generateImageAsync(direction)
        image.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : io.reactivex.Observer<Boolean> {
                    override fun onComplete() {

                    }

                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onNext(t: Boolean) {
                        if (t) {
                            saveRegistro(nameImg)
                        }
                    }

                    override fun onError(e: Throwable) {
                        Log.e("ERROR PHOTO", e.toString())
                        Util.toastMensaje(this@SuministroRepartoActivity, "Volver a intentarlo")
                    }
                })
    }

    private fun saveRegistro(nameImg: String) {
        val gps = Gps(this)
        if (gps.isLocationEnabled()) {
            if (gps.latitude.toString() == "0.0" || gps.longitude.toString() == "0.0") {
                gps.showAlert(this@SuministroRepartoActivity)
            } else {
                val photos: RealmList<Photo> = RealmList()
                val photo: Photo? = Photo(envioViewModel.getPhotoIdentity(), 0, usuarioId, nameImg, Util.getFechaActual(), 11, 1, gps.latitude.toString(), gps.longitude.toString())
                photos.add(photo)
                val registro = Registro(envioViewModel.getRegistroIdentity(), usuarioId, usuarioId, Util.getFechaActual(), gps.latitude.toString(), gps.longitude.toString(), 11, 1, "FIN", nameImg, photos)
                envioViewModel.saveZonaPeligrosa(registro)
                stopService(Intent(this, DistanceService::class.java))
                stopService(Intent(this, SendRepartoServices::class.java))
                finish()
            }
        } else {
            gps.showSettingsAlert(this@SuministroRepartoActivity)
        }
    }
}