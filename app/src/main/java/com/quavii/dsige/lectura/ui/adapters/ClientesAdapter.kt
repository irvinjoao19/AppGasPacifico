package com.quavii.dsige.lectura.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import androidx.recyclerview.widget.RecyclerView
import com.quavii.dsige.lectura.R
import com.quavii.dsige.lectura.data.model.GrandesClientes
import com.quavii.dsige.lectura.helper.Util
import com.quavii.dsige.lectura.ui.listeners.OnItemClickListener
import kotlinx.android.synthetic.main.cardview_clientes.view.*
import java.util.*

class ClientesAdapter(private var listener: OnItemClickListener.ClientesListener) : RecyclerView.Adapter<ClientesAdapter.ViewHolder>() {

    private var clientes = emptyList<GrandesClientes>()
    private var clientesList: ArrayList<GrandesClientes> = ArrayList()

    fun addItems(list: List<GrandesClientes>) {
        clientes = list
        clientesList = ArrayList(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.cardview_clientes, parent, false)
        return ViewHolder(v!!)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(Objects.requireNonNull(clientesList[position]), listener)
    }

    override fun getItemCount(): Int {
        return clientesList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal fun bind(s: GrandesClientes, listener: OnItemClickListener.ClientesListener) = with(itemView) {
            textViewOrden.text = String.format("Orden : %s", s.ordenLectura)
            textViewCodigoEMR.text = String.format("EMR : %s", s.codigoEMR)
            textViewCliente.text = s.nombreCliente
            textViewDireccion.text = s.direccion
            itemView.setOnClickListener { v -> listener.onItemClick(s, v, adapterPosition) }
        }
    }

    fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                return FilterResults()
            }

            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                clientesList.clear()
                val keyword = charSequence.toString()
                if (keyword.isEmpty()) {
                    clientesList.addAll(clientes)
                } else {
                    val filteredList = ArrayList<GrandesClientes>()
                    for (cliente: GrandesClientes in clientes) {
                        if (cliente.codigoEMR.toLowerCase(Locale.getDefault()).contains(keyword) ||
                                cliente.nombreCliente.toLowerCase(Util.locale).contains(keyword) ||
                                cliente.direccion.toLowerCase(Util.locale).contains(keyword)) {
                            filteredList.add(cliente)
                        }
                    }
                    clientesList = filteredList
                }
                notifyDataSetChanged()
            }
        }
    }
}