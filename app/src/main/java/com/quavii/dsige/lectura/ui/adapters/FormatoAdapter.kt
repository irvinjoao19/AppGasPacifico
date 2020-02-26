package com.quavii.dsige.lectura.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.quavii.dsige.lectura.R
import com.quavii.dsige.lectura.data.model.Formato
import com.quavii.dsige.lectura.ui.listeners.OnItemClickListener
import io.realm.RealmResults
import kotlinx.android.synthetic.main.cardview_combo.view.*

class FormatoAdapter(private val listener: OnItemClickListener.FormatoListener) :
        RecyclerView.Adapter<FormatoAdapter.ViewHolder>() {

    private var format = emptyList<Formato>()

    fun addItems(list: RealmResults<Formato>) {
        format = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.cardview_combo, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(format[position], listener)
    }

    override fun getItemCount(): Int {
        return format.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(f: Formato, listener: OnItemClickListener.FormatoListener) = with(itemView) {
            textViewNombre.text = String.format("%s",f.nombre)
            itemView.setOnClickListener { view -> listener.onItemClick(f, view, adapterPosition) }
        }
    }
}