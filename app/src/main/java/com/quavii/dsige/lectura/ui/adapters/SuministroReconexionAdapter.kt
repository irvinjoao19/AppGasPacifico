package com.quavii.dsige.lectura.ui.adapters

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import com.quavii.dsige.lectura.R
import com.quavii.dsige.lectura.data.model.SuministroReconexion
import com.quavii.dsige.lectura.helper.Util
import com.quavii.dsige.lectura.ui.listeners.OnItemClickListener
import kotlinx.android.synthetic.main.cardview_suministro.view.*
import java.util.*

class SuministroReconexionAdapter(private var listener: OnItemClickListener.ReconexionListener) : RecyclerView.Adapter<SuministroReconexionAdapter.ViewHolder>() {

    private var suministros = emptyList<SuministroReconexion>()
    private var suministrosList: ArrayList<SuministroReconexion> = ArrayList()

    fun addItems(list: List<SuministroReconexion>) {
        suministros = list
        suministrosList = ArrayList(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.cardview_suministro, parent, false)
        return ViewHolder(v!!)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(Objects.requireNonNull(suministrosList[position]), listener)
    }

    override fun getItemCount(): Int {
        return suministrosList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal fun bind(s: SuministroReconexion, listener: OnItemClickListener.ReconexionListener) = with(itemView) {
            textViewOrden.text = String.format("Orden : %s", s.orden)
            textViewContrato.text = String.format("C : %s", s.suministro_Numero)
            textViewMedidor.text = String.format("M : %s", s.suministro_Medidor)
            textViewCliente.text = s.suministro_Cliente
            textViewTelefono.text = String.format("Telf : %s", s.telefono)
            textViewNota.visibility = View.GONE
            textViewDireccion.text = s.suministro_Direccion
            itemView.setOnClickListener { v -> listener.onItemClick(s, v, adapterPosition) }
            imageViewMap.setOnClickListener { v -> listener.onItemClick(s, v, adapterPosition) }
            imageViewMap.visibility = View.GONE
        }
    }

    fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                return FilterResults()
            }

            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                suministrosList.clear()
                val keyword = charSequence.toString()
                if (keyword.isEmpty()) {
                    suministrosList.addAll(suministros)
                } else {
                    val filteredList = ArrayList<SuministroReconexion>()
                    for (suministro: SuministroReconexion in suministros) {
                        if (suministro.suministro_Numero.toLowerCase(Util.locale).contains(keyword) ||
                        suministro.suministro_Cliente.toLowerCase(Util.locale).contains(keyword) ||
                                suministro.suministro_Direccion.toLowerCase(Util.locale).contains(keyword) ||
                                suministro.suministro_Medidor.contains(keyword)) {
                            filteredList.add(suministro)
                        }
                    }
                    suministrosList = filteredList
                }
                notifyDataSetChanged()
            }
        }
    }
}