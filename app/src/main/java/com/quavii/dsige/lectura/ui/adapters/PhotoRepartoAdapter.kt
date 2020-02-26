package com.quavii.dsige.lectura.ui.adapters

import android.os.Environment
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.quavii.dsige.lectura.data.model.Photo
import com.quavii.dsige.lectura.helper.Util
import com.quavii.dsige.lectura.R
import com.quavii.dsige.lectura.ui.adapters.PhotoRepartoAdapter.ViewHolder
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.quavii.dsige.lectura.ui.listeners.OnItemClickListener
import io.realm.RealmResults
import kotlinx.android.synthetic.main.cardview_photo.view.*
import java.io.File

class PhotoRepartoAdapter(private var listener: OnItemClickListener.PhotoListener) : RecyclerView.Adapter<ViewHolder>() {

    private var photos = emptyList<Photo>()

    fun addItems(list: List<Photo>) {
        photos = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.cardview_photo, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(photos[position], listener)
    }

    override fun getItemCount(): Int {
        return photos.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(photo: Photo, listener: OnItemClickListener.PhotoListener) = with(itemView) {
            val f = File(Environment.getExternalStorageDirectory(), Util.FolderImg + "/" + photo.rutaFoto)
            Picasso.get()
                    .load(f)
                    .into(imageViewPhoto,
                            object : Callback {
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
}