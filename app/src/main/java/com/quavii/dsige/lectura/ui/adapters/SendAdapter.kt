package com.quavii.dsige.lectura.ui.adapters

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.quavii.dsige.lectura.R
import com.quavii.dsige.lectura.data.model.MenuPrincipal
import kotlinx.android.synthetic.main.cardview_menu.view.*

class SendAdapter(private val listener: OnItemClickListener) : RecyclerView.Adapter<SendAdapter.ViewHolder>() {

    private var menus = emptyList<MenuPrincipal>()

    fun addItems(list: List<MenuPrincipal>) {
        menus = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cardview_menu, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(menus[position], listener)
    }

    override fun getItemCount(): Int {
        return menus.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(m: MenuPrincipal, listener: OnItemClickListener) = with(itemView) {
            textViewTitulo.text = m.title
            imageViewPhoto.setImageResource(m.imagen)
            imageViewPhoto.badgeValue = m.cantidad
            itemView.setOnClickListener { listener.onItemClick(m, adapterPosition) }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(m: MenuPrincipal, position: Int)
    }
}