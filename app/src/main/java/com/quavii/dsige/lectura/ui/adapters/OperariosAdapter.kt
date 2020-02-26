package com.quavii.dsige.lectura.ui.adapters

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Switch
import android.widget.TextView
import com.quavii.dsige.lectura.R
import com.quavii.dsige.lectura.data.model.Operario
import io.realm.RealmResults
import java.util.*

class OperariosAdapter(private var operarios: RealmResults<Operario>, private var layout: Int?, private var listener: OnItemClickListener?) : RecyclerView.Adapter<OperariosAdapter.ViewHolder>() {

    private var operarioList: ArrayList<Operario> = ArrayList(operarios)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = layout?.let { LayoutInflater.from(parent.context).inflate(it, parent, false) }
        return ViewHolder(v!!)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        listener?.let { holder.bind(Objects.requireNonNull(operarioList[position]), it) }
    }

    override fun getItemCount(): Int {
        return operarioList.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val swichLecturaManual: Switch = itemView.findViewById(R.id.swichLecturaManual)
        private val textViewOperario: TextView = itemView.findViewById(R.id.textViewOperario)
        internal fun bind(operario: Operario, listener: OnItemClickListener) {
            textViewOperario.text = operario.operarioNombre
            swichLecturaManual.isChecked = operario.lecturaManual == 1
            swichLecturaManual.setOnCheckedChangeListener { a, b ->
                if (a.isPressed) {
                    listener.onCheckedChanged(operario, adapterPosition, b)
                }
            }
        }
    }

    fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                return FilterResults()
            }

            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                operarioList.clear()
                val keyword = charSequence.toString()
                if (keyword.isEmpty()) {
                    operarioList.addAll(operarios)
                } else {
                    val filteredList = ArrayList<Operario>()
                    for (operario: Operario in operarios) {
                        if (operario.operarioNombre.toLowerCase().contains(keyword) ||
                                operario.operarioId.toString().contains(keyword)
                        ) {
                            filteredList.add(operario)
                        }
                    }
                    operarioList = filteredList
                }
                notifyDataSetChanged()
            }
        }
    }

    interface OnItemClickListener {
        fun onCheckedChanged(operario: Operario, position: Int, b: Boolean)
    }
}