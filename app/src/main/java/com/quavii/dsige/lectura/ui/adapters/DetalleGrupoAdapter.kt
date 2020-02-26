package com.quavii.dsige.lectura.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.quavii.dsige.lectura.R
import com.quavii.dsige.lectura.data.model.DetalleGrupo
import com.quavii.dsige.lectura.ui.listeners.OnItemClickListener
import io.realm.RealmResults
import kotlinx.android.synthetic.main.cardview_combo.view.*

class DetalleGrupoAdapter(private val listener: OnItemClickListener.DetalleGrupoListener) :
        RecyclerView.Adapter<DetalleGrupoAdapter.ViewHolder>() {

    private var detalleGrupo = emptyList<DetalleGrupo>()

    fun addItems(list: RealmResults<DetalleGrupo>) {
        detalleGrupo = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.cardview_combo, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(detalleGrupo[position], listener)
    }

    override fun getItemCount(): Int {
        return detalleGrupo.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(d: DetalleGrupo, listener: OnItemClickListener.DetalleGrupoListener) = with(itemView) {
            textViewNombre.text = String.format("%s", d.descripcion)
            itemView.setOnClickListener { view -> listener.onItemClick(d, view, adapterPosition) }
        }
    }
}