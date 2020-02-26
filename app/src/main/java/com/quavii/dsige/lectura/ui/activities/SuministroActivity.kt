package com.quavii.dsige.lectura.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.*
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.quavii.dsige.lectura.data.dao.interfaces.SuministroImplementation
import com.quavii.dsige.lectura.data.dao.overMethod.SuministroOver
import com.quavii.dsige.lectura.data.model.SuministroCortes
import com.quavii.dsige.lectura.data.model.SuministroLectura
import com.quavii.dsige.lectura.data.model.SuministroReconexion
import com.quavii.dsige.lectura.R
import com.quavii.dsige.lectura.R.id.action_menu
import com.quavii.dsige.lectura.R.id.action_search
import com.quavii.dsige.lectura.helper.Util
import com.quavii.dsige.lectura.ui.adapters.SuministroCortesAdapter
import com.quavii.dsige.lectura.ui.adapters.SuministroLecturaAdapter
import com.quavii.dsige.lectura.ui.adapters.SuministroReconexionAdapter
import com.quavii.dsige.lectura.ui.listeners.OnItemClickListener
import io.realm.Realm
import io.realm.RealmResults
import kotlinx.android.synthetic.main.activity_suministro.*

class SuministroActivity : AppCompatActivity(), View.OnClickListener {
    override fun onClick(v: View) {
        when (v.id) {
            R.id.fabMap -> {
                startActivity(Intent(this, PendingLocationMapsActivity::class.java).putExtra("estado", estado))
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

    lateinit var realm: Realm
    lateinit var suministroImp: SuministroImplementation

    private lateinit var recyclerView: RecyclerView
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var suministroLecturaAdapter: SuministroLecturaAdapter
    private lateinit var suministroCortesAdapter: SuministroCortesAdapter
    private lateinit var suministroReconexionAdapter: SuministroReconexionAdapter

    private var estado: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_suministro)
        realm = Realm.getDefaultInstance()
        suministroImp = SuministroOver(realm)
        val bundle = intent.extras
        if (bundle != null) {
            bindToolbar(bundle.getString("nombre"))
            estado = bundle.getInt("estado")
            bindUI(estado)
        }
    }

    private fun bindToolbar(nombre: String?) {
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar!!.title = nombre
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun bindUI(estado: Int) {
        fabMap.setOnClickListener(this)
        recyclerView = findViewById<View>(R.id.recyclerView) as RecyclerView
        layoutManager = LinearLayoutManager(this@SuministroActivity)

        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.layoutManager = layoutManager

        when {
            estado <= 2 -> {
                suministroLecturaAdapter = SuministroLecturaAdapter(object : OnItemClickListener.LecturaListener {
                    override fun onItemClick(s: SuministroLectura, v: View, position: Int) {
                        when (v.id) {
                            R.id.imageViewMap -> {
                                if (s.latitud.isNotEmpty() || s.longitud.isNotEmpty()) {
                                    startActivity(Intent(this@SuministroActivity, MapsActivity::class.java)
                                            .putExtra("latitud", s.latitud)
                                            .putExtra("longitud", s.longitud)
                                            .putExtra("title", s.suministro_Numero))
                                } else {
                                    Util.toastMensaje(this@SuministroActivity, "Este suministro no cuenta con coordenadas")
                                }
                            }
                            else -> {
                                val nombre = if (estado == 1) "Lectura" else "Relectura"
                                val intent = Intent(this@SuministroActivity, SuministroAfterActivity::class.java)
                                intent.putExtra("orden", s.orden)
                                intent.putExtra("orden_2", s.suministroOperario_Orden)
                                intent.putExtra("nombre", nombre)
                                intent.putExtra("estado", s.estado)
                                startActivity(intent)
                                finish()
                            }
                        }
                    }
                })
                recyclerView.adapter = suministroLecturaAdapter
                val suministrosLectura = suministroImp.getSuministroLectura(estado, 1, 0)
                suministrosLectura.addChangeListener { s ->
                    suministroLecturaAdapter.addItems(s)
                }
                suministroLecturaAdapter.addItems(suministrosLectura)

            }
            estado == 3 -> {
                fabMap.visibility = View.VISIBLE
                suministroCortesAdapter = SuministroCortesAdapter(object : OnItemClickListener.CorteListener {
                    override fun onItemClick(s: SuministroCortes, v: View, position: Int) {
                        when (v.id) {
                            R.id.imageViewMap -> {
                                if (s.latitud.isNotEmpty() || s.longitud.isNotEmpty()) {
                                    startActivity(Intent(this@SuministroActivity, MapsActivity::class.java)
                                            .putExtra("latitud", s.latitud)
                                            .putExtra("longitud", s.longitud)
                                            .putExtra("title", s.suministro_Numero))
                                } else {
                                    Util.toastMensaje(this@SuministroActivity, "Este suministro no cuenta con coordenadas")
                                }
                            }
                            else -> {
                                if (s.suministro_NoCortar == 1) {
                                    Toast.makeText(this@SuministroActivity, "Corte Cancelado", Toast.LENGTH_LONG).show()
                                } else {
                                    val intent = Intent(this@SuministroActivity, SuministroAfterActivity::class.java)
                                    intent.putExtra("orden", s.orden)
                                    intent.putExtra("orden_2", s.suministroOperario_Orden)
                                    intent.putExtra("nombre", "Corte")
                                    intent.putExtra("estado", s.estado)
                                    startActivity(intent)
                                    finish()
                                }
                            }
                        }
                    }
                })
                recyclerView.adapter = suministroCortesAdapter
                val suministrosCortes: RealmResults<SuministroCortes> = suministroImp.getSuministroCortes(estado, 1)
                suministrosCortes.addChangeListener { s ->
                    suministroCortesAdapter.addItems(s)
                }
                suministroCortesAdapter.addItems(suministrosCortes)
            }
            estado == 4 -> {
                fabMap.visibility = View.VISIBLE
                suministroReconexionAdapter = SuministroReconexionAdapter(object : OnItemClickListener.ReconexionListener {
                    override fun onItemClick(s: SuministroReconexion, v: View, position: Int) {
                        when (v.id) {
                            R.id.imageViewMap -> {
                                if (s.latitud.isNotEmpty() || s.longitud.isNotEmpty()) {
                                    startActivity(Intent(this@SuministroActivity, MapsActivity::class.java)
                                            .putExtra("latitud", s.latitud)
                                            .putExtra("longitud", s.longitud)
                                            .putExtra("title", s.suministro_Numero))
                                } else {
                                    Util.toastMensaje(this@SuministroActivity, "Este suministro no cuenta con coordenadas")
                                }
                            }
                            else -> {
                                val intent = Intent(this@SuministroActivity, SuministroAfterActivity::class.java)
                                intent.putExtra("orden", s.orden)
                                intent.putExtra("orden_2", s.suministroOperario_Orden)
                                intent.putExtra("nombre", "Reconexion")
                                intent.putExtra("estado", s.estado)
                                startActivity(intent)
                                finish()
                            }
                        }
                    }
                })
                recyclerView.adapter = suministroReconexionAdapter
                val suministrosReconexiones = suministroImp.getSuministroReconexion(estado, 1)
                suministrosReconexiones.addChangeListener { s ->
                    suministroReconexionAdapter.addItems(s)
                }
                suministroReconexionAdapter.addItems(suministrosReconexiones)

            }
            estado == 9 -> {
                suministroLecturaAdapter = SuministroLecturaAdapter(object : OnItemClickListener.LecturaListener {
                    override fun onItemClick(s: SuministroLectura, v: View, position: Int) {
                        when (v.id) {
                            R.id.imageViewMap -> {
                                if (s.latitud.isNotEmpty() || s.longitud.isNotEmpty()) {
                                    startActivity(Intent(this@SuministroActivity, MapsActivity::class.java)
                                            .putExtra("latitud", s.latitud)
                                            .putExtra("longitud", s.longitud)
                                            .putExtra("title", s.suministro_Numero))
                                } else {
                                    Util.toastMensaje(this@SuministroActivity, "Este suministro no cuenta con coordenadas")
                                }
                            }
                            else -> {
                                val nombre = "Reclamos"
                                val intent = Intent(this@SuministroActivity, SuministroAfterActivity::class.java)
                                intent.putExtra("orden", s.orden)
                                intent.putExtra("orden_2", s.suministroOperario_Orden)
                                intent.putExtra("nombre", nombre)
                                intent.putExtra("estado", estado)
                                startActivity(intent)
                                finish()
                            }
                        }
                    }
                })
                recyclerView.adapter = suministroLecturaAdapter
                val suministrosLecturaReclamo = suministroImp.getSuministroReclamos(estado.toString(), 1)
                suministrosLecturaReclamo.addChangeListener { s ->
                    suministroLecturaAdapter.addItems(s)
                }
                suministroLecturaAdapter.addItems(suministrosLecturaReclamo)
            }
            estado == 10 -> {
                suministroLecturaAdapter = SuministroLecturaAdapter(object : OnItemClickListener.LecturaListener {
                    override fun onItemClick(s: SuministroLectura, v: View, position: Int) {
                        when (v.id) {
                            R.id.imageViewMap -> {
                                if (s.latitud.isNotEmpty() || s.longitud.isNotEmpty()) {
                                    startActivity(Intent(this@SuministroActivity, MapsActivity::class.java)
                                            .putExtra("latitud", s.latitud)
                                            .putExtra("longitud", s.longitud)
                                            .putExtra("title", s.suministro_Numero))
                                } else {
                                    Util.toastMensaje(this@SuministroActivity, "Este suministro no cuenta con coordenadas")
                                }
                            }
                            else -> {
                                val nombre = "Lectura Recuperada"
                                val intent = Intent(this@SuministroActivity, SuministroAfterActivity::class.java)
                                intent.putExtra("orden", s.orden)
                                intent.putExtra("orden_2", s.suministroOperario_Orden)
                                intent.putExtra("nombre", nombre)
                                intent.putExtra("estado", estado)
                                startActivity(intent)
                                finish()
                            }
                        }
                    }
                })
                recyclerView.adapter = suministroLecturaAdapter
                val suministrosLecturaRecuperado = suministroImp.getSuministroLectura(estado, 1, 0)
                suministrosLecturaRecuperado.addChangeListener { s ->
                    suministroLecturaAdapter.addItems(s)
                }
                suministroLecturaAdapter.addItems(suministrosLecturaRecuperado)

            }

            else -> {
                suministroLecturaAdapter = SuministroLecturaAdapter(object : OnItemClickListener.LecturaListener {
                    override fun onItemClick(s: SuministroLectura, v: View, position: Int) {
                        when (v.id) {
                            R.id.imageViewMap -> {
                                if (s.latitud.isNotEmpty() || s.longitud.isNotEmpty()) {
                                    startActivity(Intent(this@SuministroActivity, MapsActivity::class.java)
                                            .putExtra("latitud", s.latitud)
                                            .putExtra("longitud", s.longitud)
                                            .putExtra("title", s.suministro_Numero))
                                } else {
                                    Util.toastMensaje(this@SuministroActivity, "Este suministro no cuenta con coordenadas")
                                }
                            }
                            else -> {
                                val nombre = when (estado) {
                                    6 -> "Lectura Observadas"
                                    7 -> "Lectura Manuales"
                                    8 -> "Tapas abiertas"
                                    9 -> "Reclamos"
                                    else -> ""
                                }
                                val intent = Intent(this@SuministroActivity, SuministroAfterActivity::class.java)
                                intent.putExtra("orden", s.orden)
                                intent.putExtra("orden_2", s.suministroOperario_Orden)
                                intent.putExtra("nombre", nombre)
                                intent.putExtra("estado", estado)
                                startActivity(intent)
                                finish()
                            }
                        }
                    }
                })
                recyclerView.adapter = suministroLecturaAdapter

                val suministrosLectura = when (estado) {
                    6 -> suministroImp.getSuministroLectura(1, 1, 1)
                    7 -> suministroImp.getSuministroLectura(1, 1, 0)
                    8 -> suministroImp.getSuministroLectura(1, 1, 0)
                    9 -> suministroImp.getSuministroLectura(1, 1, 0)
                    else -> null
                }

                if (suministrosLectura != null) {
                    suministrosLectura.addChangeListener { s ->
                        suministroLecturaAdapter.addItems(s)
                    }
                    suministroLecturaAdapter.addItems(suministrosLectura)
                }

            }

        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.search, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            action_search -> {
                val searchView = item.actionView as SearchView
                search(searchView)
                return true
            }
            action_menu -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun search(searchView: SearchView) {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                when {
                    estado <= 2 -> suministroLecturaAdapter.getFilter().filter(newText)
                    estado == 9 -> suministroLecturaAdapter.getFilter().filter(newText)
                    estado == 3 -> suministroCortesAdapter.getFilter().filter(newText)
                    estado == 4 -> suministroReconexionAdapter.getFilter().filter(newText)
                    estado == 6 || estado == 7 -> suministroLecturaAdapter.getFilter().filter(newText)
                }
                return true
            }
        })
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish()
        }
        return super.onKeyDown(keyCode, event)
    }
}