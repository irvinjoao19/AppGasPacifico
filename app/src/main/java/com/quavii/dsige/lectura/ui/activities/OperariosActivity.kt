package com.quavii.dsige.lectura.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.*
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import com.quavii.dsige.lectura.R
import com.quavii.dsige.lectura.data.apiServices.ApiServices
import com.quavii.dsige.lectura.data.dao.interfaces.LoginImplementation
import com.quavii.dsige.lectura.data.dao.overMethod.LoginOver
import com.quavii.dsige.lectura.data.apiServices.ConexionRetrofit
import com.quavii.dsige.lectura.data.model.Mensaje
import com.quavii.dsige.lectura.data.model.Operario
import com.quavii.dsige.lectura.ui.adapters.OperariosAdapter
import io.realm.Realm
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class OperariosActivity : AppCompatActivity() {

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.search_operario, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.action_search -> {
                val searchView = item.actionView as SearchView
                search(searchView)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private lateinit var operariosInterface: ApiServices

    private lateinit var progressBar: ProgressBar
    private lateinit var recyclerView: androidx.recyclerview.widget.RecyclerView
    private lateinit var layoutManager: androidx.recyclerview.widget.RecyclerView.LayoutManager
    private lateinit var operariosAdapter: OperariosAdapter

    private lateinit var realm: Realm
    private lateinit var loginImp: LoginImplementation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_operarios)
        operariosInterface = ConexionRetrofit.api.create(ApiServices::class.java)
        realm = Realm.getDefaultInstance()
        loginImp = LoginOver(realm)
        bindToolbar()
        bindUI()
        getListAuditoriaCall()
    }

    private fun bindToolbar() {
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        Objects.requireNonNull<ActionBar>(supportActionBar).title = "Lista de Operarios"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun bindUI() {
        progressBar = findViewById(R.id.progressBar)
        recyclerView = findViewById(R.id.recyclerView)
        layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this@OperariosActivity)
    }

    private fun getListAuditoriaCall() {
        val listCall: Call<List<Operario>> = operariosInterface.getOperarios()
        listCall.enqueue(object : Callback<List<Operario>> {
            override fun onFailure(call: Call<List<Operario>>, t: Throwable) {

            }

            override fun onResponse(call: Call<List<Operario>>, response: Response<List<Operario>>) {
                progressBar.visibility = View.GONE

                val operarios: List<Operario>? = response.body()
                if (operarios != null) {
                    loginImp.saveOperarios(operarios)
                    showOperarios()
                }
            }
        })
    }

    private fun showOperarios() {
        val operarios = loginImp.getAllOperarios()
        operarios.addChangeListener { _ ->
            operariosAdapter.notifyDataSetChanged()
        }
        operariosAdapter = OperariosAdapter(operarios, R.layout.cardview_operarios, object : OperariosAdapter.OnItemClickListener {
            override fun onCheckedChanged(operario: Operario, position: Int, b: Boolean) {
                if (b) {
                    updateOperario(1, operario)
                } else {
                    updateOperario(0, operario)
                }
            }
        })
        recyclerView.itemAnimator = androidx.recyclerview.widget.DefaultItemAnimator()
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = operariosAdapter
    }

    private fun updateOperario(value: Int, operario: Operario) {

        val operarioCall = operariosInterface.updateOperario(operario.operarioId, value)
        operarioCall.enqueue(object : Callback<Mensaje> {
            override fun onFailure(call: Call<Mensaje>, t: Throwable) {

            }

            override fun onResponse(call: Call<Mensaje>, response: Response<Mensaje>) {
                val mensaje = response.body()
                if (mensaje != null) {
                    loginImp.updateOperario(operario, value)
                } else {
                    Toast.makeText(this@OperariosActivity, "Error", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun search(searchView: SearchView) {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                operariosAdapter.getFilter().filter(newText)
                return true
            }
        })
        searchView.setOnCloseListener {
            operariosAdapter.notifyDataSetChanged()

            false
        }
    }
}