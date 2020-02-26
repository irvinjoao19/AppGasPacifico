package com.quavii.dsige.lectura.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.quavii.dsige.lectura.R
import com.quavii.dsige.lectura.data.model.MenuPrincipal
import com.quavii.dsige.lectura.data.model.RegistroRecibo
import com.quavii.dsige.lectura.data.viewModel.RepartoViewModel
import com.quavii.dsige.lectura.ui.activities.FirmRepartoActivity
import com.quavii.dsige.lectura.ui.adapters.FirmRepartoAdapter
import com.quavii.dsige.lectura.ui.services.AlertRepartoSleepService
import com.quavii.dsige.lectura.ui.services.DistanceService
import io.realm.RealmResults
import kotlinx.android.synthetic.main.fragment_firm.*

private const val ARG_PARAM1 = "param1"

class FirmFragment : Fragment(), View.OnClickListener {
    override fun onClick(v: View) {
        fabSave.visibility = View.VISIBLE
        fabFirm.visibility = View.GONE
        when (v.id) {
            R.id.fabFirm -> {
                val b = repartoViewModel.getRegistroByFk(repartoId)
                if (b != null) {
                    startActivity(Intent(context, FirmRepartoActivity::class.java).putExtra("repartoId", repartoId))
                } else {
                    repartoViewModel.setError("Completar el primer formulario")
                }
            }
            R.id.fabSave -> {
                repartoViewModel.updateRepartoEnvio(repartoId)
                context!!.stopService(Intent(context!!, AlertRepartoSleepService::class.java))
                context!!.startService(Intent(context!!, DistanceService::class.java))
                activity!!.finish()
            }
        }
    }

    var repartoId: Int = 0

    lateinit var firmAdapter: FirmRepartoAdapter
    lateinit var repartoViewModel: RepartoViewModel
    lateinit var r: RegistroRecibo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            repartoId = it.getInt(ARG_PARAM1)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_firm, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        repartoViewModel = ViewModelProviders.of(this).get(RepartoViewModel::class.java)
        repartoViewModel.initialRealm()
        fabFirm.setOnClickListener(this)
        fabSave.setOnClickListener(this)
        bindUI()
    }

    override fun onStart() {
        super.onStart()
        val list = repartoViewModel.getRegistroRecibidoAll(repartoId)
        getFirm(list)
    }

    private fun bindUI() {
        val layoutManager = LinearLayoutManager(context)
        firmAdapter = FirmRepartoAdapter()
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = firmAdapter
        val list = repartoViewModel.getRegistroRecibidoAll(repartoId)
        getFirm(list)
    }

    private fun getFirm(list: RealmResults<RegistroRecibo>) {
        if (list.size != 0) {
            val r = list[0]
            if (r != null) {
                if (r.firmaCliente.isNotEmpty()) {
                    val firm = ArrayList<MenuPrincipal>()
                    firm.add(MenuPrincipal(1, r.firmaCliente, 1, 0))
                    firmAdapter.addItems(firm)
                    fabSave.visibility = View.VISIBLE
                    fabFirm.visibility = View.GONE
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(repartoId: Int) =
                FirmFragment().apply {
                    arguments = Bundle().apply {
                        putInt(ARG_PARAM1, repartoId)
                    }
                }
    }
}
