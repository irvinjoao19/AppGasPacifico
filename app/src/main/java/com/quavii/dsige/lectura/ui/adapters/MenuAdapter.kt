package com.quavii.dsige.lectura.ui.adapters

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.quavii.dsige.lectura.R

class MenuAdapter(var titles: Array<String>?, var images: IntArray?, var listener: OnItemClickListener?) : RecyclerView.Adapter<MenuAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cardview_menu, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(titles!!, images!!, position, listener!!)
    }

    override fun getItemCount(): Int {
        return titles!!.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageViewPhoto: ImageView = itemView.findViewById(R.id.imageViewPhoto)
        private val textViewTitulo: TextView = itemView.findViewById(R.id.textViewTitulo)

        fun bind(string: Array<String>, images: IntArray, position: Int, listener: OnItemClickListener) {
            imageViewPhoto.setImageResource(images[position])
            textViewTitulo.text = string[position]
            itemView.setOnClickListener { listener.onItemClick(string[position], adapterPosition) }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(strings: String, position: Int)
    }
}