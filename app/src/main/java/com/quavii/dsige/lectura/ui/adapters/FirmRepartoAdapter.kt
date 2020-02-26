package com.quavii.dsige.lectura.ui.adapters

import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.quavii.dsige.lectura.R
import com.quavii.dsige.lectura.data.model.MenuPrincipal
import com.quavii.dsige.lectura.helper.Util
import kotlinx.android.synthetic.main.cardview_firm_reparto.view.*
import java.io.File

class FirmRepartoAdapter : RecyclerView.Adapter<FirmRepartoAdapter.ViewHolder>() {

    private var menus = emptyList<MenuPrincipal>()

    fun addItems(list: List<MenuPrincipal>) {
        menus = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cardview_firm_reparto, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(menus[position])
    }

    override fun getItemCount(): Int {
        return menus.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(m: MenuPrincipal) = with(itemView) {
            val f = File(Environment.getExternalStorageDirectory(), Util.FolderImg + "/" + m.title)
            Picasso.get()
                    .load(f)
                    .into(imageViewFirm, object : Callback {
                        override fun onSuccess() {
                            progress.visibility = View.GONE
                            imageViewFirm.visibility = View.VISIBLE
                        }

                        override fun onError(e: Exception) {
                            imageViewFirm.visibility = View.VISIBLE
                        }
                    })
        }
    }
}