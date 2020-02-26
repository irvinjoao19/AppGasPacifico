package com.quavii.dsige.lectura.ui.adapters

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import com.quavii.dsige.lectura.data.model.SuministroReparto
import com.quavii.dsige.lectura.R
import com.quavii.dsige.lectura.helper.Util
import com.quavii.dsige.lectura.ui.listeners.OnItemClickListener
import kotlinx.android.synthetic.main.cardview_suministro.view.*
import java.util.*

open class SuministroRepartoAdapter(private val listener: OnItemClickListener.RepartoListener) : RecyclerView.Adapter<SuministroRepartoAdapter.ViewHolder>() {

    private var repartos = emptyList<SuministroReparto>()
    private var repartosList: ArrayList<SuministroReparto> = ArrayList()

    fun addItems(list: List<SuministroReparto>) {
        repartos = list
        repartosList = ArrayList(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.cardview_suministro, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(repartosList[position], listener)
    }

    override fun getItemCount(): Int {
        return repartosList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(r: SuministroReparto, listener: OnItemClickListener.RepartoListener) = with(itemView) {
            textViewOrden.text = String.format("Orden : %s", r.Cod_Orden_Reparto)
            textViewContrato.text = String.format("C : %s", r.Suministro_Numero_reparto)
            textViewMedidor.text = String.format("M : %s", r.Suministro_Medidor_reparto)
            textViewCliente.text = r.Cliente_Reparto
            textViewTelefono.text = String.format("Telf : %s", r.telefono)
            textViewNota.text = r.nota
            textViewDireccion.text = r.Direccion_Reparto
            itemView.setOnClickListener { v -> listener.onItemClick(r, v, adapterPosition) }
            imageViewMap.setOnClickListener { v -> listener.onItemClick(r, v, adapterPosition) }
        }
    }

    fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                return FilterResults()
            }

            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                repartosList.clear()
                val keyword = charSequence.toString()
                if (keyword.isEmpty()) {
                    repartosList.addAll(repartos)
                } else {
                    val filteredList = ArrayList<SuministroReparto>()
                    for (r: SuministroReparto in repartos) {
                        if (r.Cliente_Reparto.toLowerCase(Util.locale).contains(keyword) ||
                                r.Direccion_Reparto.toLowerCase(Util.locale).contains(keyword) ||
                                r.Suministro_Numero_reparto.contains(keyword) ||
                                r.Suministro_Medidor_reparto.contains(keyword)) {
                            filteredList.add(r)
                        }
                    }
                    repartosList = filteredList
                }
                notifyDataSetChanged()
            }
        }
    }
}