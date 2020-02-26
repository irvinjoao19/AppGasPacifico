package com.quavii.dsige.lectura.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.Toolbar
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import com.quavii.dsige.lectura.data.dao.overMethod.PhotoRepartoOver
import com.quavii.dsige.lectura.data.model.Photo
import com.quavii.dsige.lectura.helper.Util
import com.quavii.dsige.lectura.R
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import io.realm.Realm
import java.io.File
import java.util.*

class ViewPhoto : AppCompatActivity() {

    private var realm: Realm? = null
    private var photoOver: PhotoRepartoOver? = null

    private var progressBarLoad: ProgressBar? = null
    private var imageViewPhoto: ImageView? = null

    private var estado: Int? = 0
    private var nombre: String? = null
    private var Cod_Orden_Reparto: String? = null
    private var id_Cab_Reparto: Int? = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_photo)
        realm = Realm.getDefaultInstance()
        photoOver = PhotoRepartoOver(realm!!)
        val bundle = intent.extras
        if (bundle != null) {
            bindToolbar(bundle.getString("nameViewPhoto")!!)
            bindUI(bundle.getInt("envioIdReparto"))
            estado = bundle.getInt("estado")
            nombre = bundle.getString("nombre")
            Cod_Orden_Reparto = bundle.getString("Cod_Orden_Reparto")
            id_Cab_Reparto = bundle.getInt("id_cab_Reparto")
        }
    }

    private fun bindToolbar(nombre: String) {
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        Objects.requireNonNull<ActionBar>(supportActionBar).title = nombre
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            val intent = Intent(this@ViewPhoto, SuministroFormRepartoActivity::class.java)
            intent.putExtra("Cod_Orden_Reparto", Cod_Orden_Reparto)
            intent.putExtra("id_cab_Reparto", id_Cab_Reparto)
            intent.putExtra("nombre", "SuministroReparto")
            intent.putExtra("estado", estado)
            startActivity(intent)
            finish()
        }
    }
    private fun bindUI(id: Int) {

        imageViewPhoto = findViewById<View>(R.id.imageViewPhoto) as ImageView
        progressBarLoad = findViewById<View>(R.id.progressBarLoad) as ProgressBar

        val photo: Photo? = photoOver!!.photoById(id)
        val f = File(Environment.getExternalStorageDirectory(), Util.FolderImg + "/" + photo?.rutaFoto)

        Picasso.get()
                .load(f)
                .into(imageViewPhoto, object : Callback {
                    override fun onSuccess() {
                        progressBarLoad?.visibility = View.GONE
                    }

                    override fun onError(e: Exception) {


                    }
                })
    }
}
