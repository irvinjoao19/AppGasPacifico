package com.quavii.dsige.lectura.ui.adapters

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.quavii.dsige.lectura.data.model.*
import com.quavii.dsige.lectura.R
import com.quavii.dsige.lectura.ui.listeners.OnItemClickListener
import io.realm.Realm
import kotlinx.android.synthetic.main.cardview_menu.view.*

class ServicioAdapter(private var listener: OnItemClickListener.ServicesListener) : RecyclerView.Adapter<ServicioAdapter.ViewHolder>() {

    private var servicios = emptyList<Servicio>()

    fun addItems(list: List<Servicio>) {
        servicios = list
        notifyDataSetChanged()
    }

    var realm: Realm? = Realm.getDefaultInstance()
    var count: Long? = null
    var activo: Int? = 1
    var lectura: Int? = 1
    var relectura: Int? = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.cardview_menu, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(servicios[position], listener)
    }

    override fun getItemCount(): Int {
        return servicios.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal fun bind(s: Servicio, listener: OnItemClickListener.ServicesListener) = with(itemView) {
            when (s.nombre_servicio) {
                "Lectura" -> {
                    count = realm?.where(SuministroLectura::class.java)?.distinct("iD_Suministro")?.equalTo("activo", activo)?.equalTo("estado", lectura)?.count()
                    val valor: Int? = count?.toInt()
                    imageViewPhoto.setImageResource(R.drawable.ic_lectura)
                    imageViewPhoto.badgeValue = valor!!
                    textViewTitulo.text = s.nombre_servicio
                }
                "Relectura" -> {
                    count = realm?.where(SuministroLectura::class.java)?.distinct("iD_Suministro")?.equalTo("activo", activo)?.equalTo("estado", relectura)?.count()
                    val valor: Int? = count?.toInt()
                    imageViewPhoto.badgeValue = valor!!
                    imageViewPhoto.setImageResource(R.drawable.ic_relectura)
                    textViewTitulo.text = s.nombre_servicio
                }
                "Cortes" -> {
                    count = realm?.where(SuministroCortes::class.java)?.distinct("iD_Suministro")?.equalTo("activo", activo)?.count()
                    val valor: Int? = count?.toInt()
                    imageViewPhoto.badgeValue = valor!!
                    imageViewPhoto.setImageResource(R.drawable.ic_cortes)
                    textViewTitulo.text = s.nombre_servicio
                }
                "Reconexiones" -> {
                    count = realm?.where(SuministroReconexion::class.java)?.distinct("iD_Suministro")?.equalTo("activo", activo)?.count()
                    val valor: Int? = count?.toInt()
                    imageViewPhoto.badgeValue = valor!!
                    imageViewPhoto.setImageResource(R.drawable.ic_reconexiones)
                    textViewTitulo.text = s.nombre_servicio
                }
                "Distribucion" -> {
                    count = realm?.where(SuministroReparto::class.java)?.distinct("id_Reparto")?.equalTo("activo", activo)?.count()
                    val valor: Int? = count?.toInt()
                    imageViewPhoto.badgeValue = valor!!
                    imageViewPhoto.setImageResource(R.drawable.ic_reparto)
                    textViewTitulo.text = s.nombre_servicio
                }
                "Grandes Clientes" -> {
                    val estado = 7
                    count = realm?.where(GrandesClientes::class.java)?.notEqualTo("estado", estado)?.count()
                    val valor: Int? = count?.toInt()
                    imageViewPhoto.badgeValue = valor!!
                    imageViewPhoto.setImageResource(R.drawable.ic_users_group)
                    textViewTitulo.text = s.nombre_servicio
                }
                else -> textViewTitulo.text = s.nombre_servicio
            }
            itemView.setOnClickListener { v -> listener.onItemClick(s, v, adapterPosition) }
        }
    }
}