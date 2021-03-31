package com.quavii.dsige.lectura.ui.adapters

import android.annotation.SuppressLint
import android.os.Environment
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.quavii.dsige.lectura.data.model.Photo
import com.quavii.dsige.lectura.helper.Util
import com.quavii.dsige.lectura.R
import com.quavii.dsige.lectura.ui.adapters.PhotoAdapter.ViewHolder
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import io.realm.RealmResults
import kotlinx.android.synthetic.main.cardview_photo.view.*
import java.io.File
import java.util.*

class PhotoAdapter(private var photos: RealmResults<Photo>?, private var listener: OnItemClickListener?) : RecyclerView.Adapter<ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.cardview_photo, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (Objects.requireNonNull<Photo>(photos?.get(position)).isValid) {
            listener?.let { holder.bind(Objects.requireNonNull<Photo>(photos?.get(position)), it) }
        }
    }

    override fun getItemCount(): Int {
        return photos!!.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        internal fun bind(photo: Photo, listener: OnItemClickListener) = with(itemView) {
            val f = File(Util.getFolder(itemView.context), photo.rutaFoto)
            Picasso.get()
                    .load(f)
                    .into(imageViewPhoto, object : Callback {
                        override fun onSuccess() {
                            progress.visibility = View.GONE
                        }

                        override fun onError(e: Exception) {

                        }
                    })

            textViewPhoto.text = photo.rutaFoto
            itemView.setOnClickListener { v -> listener.onItemClick(photo, v, adapterPosition) }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(photo: Photo, view: View, position: Int)
    }
}