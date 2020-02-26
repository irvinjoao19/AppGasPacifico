package com.quavii.dsige.lectura.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.quavii.dsige.lectura.R
import com.quavii.dsige.lectura.data.model.Motivo
import com.quavii.dsige.lectura.ui.listeners.OnItemClickListener
import io.realm.RealmResults
import kotlinx.android.synthetic.main.cardview_combo.view.*

class MotivoAdapter(private val listener: OnItemClickListener.MotivoListener) :
        RecyclerView.Adapter<MotivoAdapter.ViewHolder>() {

    private var motivos = emptyList<Motivo>()

    fun addItems(list: RealmResults<Motivo>) {
        motivos = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.cardview_combo, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(motivos[position], listener)
    }

    override fun getItemCount(): Int {
        return motivos.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(m: Motivo, listener: OnItemClickListener.MotivoListener) = with(itemView) {
            textViewNombre.text = String.format("%s", m.descripcion)
            itemView.setOnClickListener { view -> listener.onItemClick(m, view, adapterPosition) }
        }
    }
}