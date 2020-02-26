package com.quavii.dsige.lectura.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.quavii.dsige.lectura.R
import com.quavii.dsige.lectura.data.model.GrandesClientes
import com.quavii.dsige.lectura.data.viewModel.ClienteViewModel
import com.quavii.dsige.lectura.data.viewModel.RepartoViewModel
import com.quavii.dsige.lectura.ui.adapters.ClientesAdapter
import com.quavii.dsige.lectura.ui.listeners.OnItemClickListener
import kotlinx.android.synthetic.main.activity_list_clients.*

class ListClientsActivity : AppCompatActivity() {

    lateinit var clienteViewModel: ClienteViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_clients)
        bindUI()
    }

    private fun bindUI() {
        clienteViewModel = ViewModelProviders.of(this).get(ClienteViewModel::class.java)
        clienteViewModel.initialRealm()

        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Grandes Cliente"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        val clienteAdapter = ClientesAdapter(object : OnItemClickListener.ClientesListener {
            override fun onItemClick(c: GrandesClientes, v: View, position: Int) {
                startActivity(Intent(this@ListClientsActivity, BigClientsActivity::class.java)
                        .putExtra("clienteId", c.clienteId))
            }
        })

        val layoutManager = LinearLayoutManager(this)
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = clienteAdapter

        val clientes = clienteViewModel.getGrandesClientes()
        clienteAdapter.addItems(clientes)
    }
}
