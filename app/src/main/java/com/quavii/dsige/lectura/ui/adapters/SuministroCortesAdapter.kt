package com.quavii.dsige.lectura.ui.adapters

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import androidx.core.content.ContextCompat
import com.quavii.dsige.lectura.R
import com.quavii.dsige.lectura.data.model.SuministroCortes
import com.quavii.dsige.lectura.helper.Util
import com.quavii.dsige.lectura.ui.listeners.OnItemClickListener
import kotlinx.android.synthetic.main.cardview_suministro.view.*
import java.util.*

class SuministroCortesAdapter(private var listener: OnItemClickListener.CorteListener) : RecyclerView.Adapter<SuministroCortesAdapter.ViewHolder>() {

    private var suministros = emptyList<SuministroCortes>()
    private var suministrosList: ArrayList<SuministroCortes> = ArrayList()

    fun addItems(list: List<SuministroCortes>) {
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
        internal fun bind(s: SuministroCortes, listener: OnItemClickListener.CorteListener) = with(itemView) {
            textViewOrden.text = String.format("Orden : %s", s.orden)
            textViewContrato.text = String.format("C : %s", s.suministro_Numero)
            textViewMedidor.text = String.format("M : %s", s.suministro_Medidor)
            textViewCliente.text = s.suministro_Cliente
            textViewTelefono.visibility = View.GONE
            textViewNota.visibility = View.GONE
            textViewDireccion.text = s.suministro_Direccion

            textViewUnidadLectura.visibility = View.VISIBLE
            textViewAvisoCorte.visibility = View.VISIBLE
            textViewUnidadLectura.text = String.format("U.L : %s", s.suministro_UnidadLectura)
            textViewAvisoCorte.text =  String.format("A.C : %s", s.avisoCorte)
            itemView.setOnClickListener { v -> listener.onItemClick(s, v, adapterPosition) }
            imageViewMap.visibility = View.GONE

            if (s.suministro_NoCortar == 1) {
                cardViewPrincipal.setCardBackgroundColor(ContextCompat.getColor(itemView.context, R.color.colorAccent))
            } else {
                cardViewPrincipal.setCardBackgroundColor(ContextCompat.getColor(itemView.context, R.color.colorWhite))
            }

            itemView.setOnClickListener { v -> listener.onItemClick(s, v, adapterPosition) }
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
                    val filteredList = ArrayList<SuministroCortes>()
                    for (suministro: SuministroCortes in suministros) {
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