package com.quavii.dsige.lectura.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.Toolbar
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import com.quavii.dsige.lectura.data.dao.overMethod.PhotoOver
import com.quavii.dsige.lectura.data.model.Photo
import com.quavii.dsige.lectura.helper.Util
import com.quavii.dsige.lectura.R
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import io.realm.Realm
import java.io.File
import java.util.*

class ShowPhotoActivity : AppCompatActivity() {

    override fun onDestroy() {
        super.onDestroy()
        realm?.close()
    }

    private var realm: Realm? = null
    private var photoOver: PhotoOver? = null

    private var progressBarLoad: ProgressBar? = null
    private var imageViewPhoto: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_photo)
        realm = Realm.getDefaultInstance()
        photoOver = PhotoOver(realm!!)
        val bundle = intent.extras
        if (bundle != null) {
            bindToolbar(bundle.getString("nombre")!!)
            bindUI(bundle.getInt("envioId"))
        }
    }

    private fun bindToolbar(nombre: String) {
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        Objects.requireNonNull<ActionBar>(supportActionBar).title = nombre
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }
    }

    private fun bindUI(id: Int) {

        imageViewPhoto = findViewById<View>(R.id.imageViewPhoto) as ImageView?
        progressBarLoad = findViewById<View>(R.id.progressBarLoad) as ProgressBar?
        val photo: Photo? = photoOver!!.photoById(id)
        val f = File(Util.getFolder(this), photo?.rutaFoto)

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
