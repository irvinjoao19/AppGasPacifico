package com.quavii.dsige.lectura.ui.activities

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.provider.MediaStore
import android.speech.RecognizerIntent
import android.text.*
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.Toolbar
import android.util.Log
import android.view.*
import android.view.Menu
import android.widget.*
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.quavii.dsige.lectura.data.dao.interfaces.*
import com.quavii.dsige.lectura.data.dao.overMethod.*
import com.quavii.dsige.lectura.data.model.*
import com.quavii.dsige.lectura.R
import com.quavii.dsige.lectura.helper.*
import com.quavii.dsige.lectura.ui.adapters.MotivoAdapter
import com.quavii.dsige.lectura.ui.services.SendRegisterService
import com.quavii.dsige.lectura.ui.listeners.OnItemClickListener
import com.google.android.material.button.MaterialButton
import com.quavii.dsige.lectura.ui.adapters.DetalleGrupoAdapter
import com.quavii.dsige.lectura.ui.adapters.MenuItemAdapter
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import io.realm.RealmResults
import kotlinx.android.synthetic.main.activity_suministro_after.*
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class SuministroAfterActivity : AppCompatActivity(), View.OnClickListener {

    override fun onClick(v: View) {
        when (v.id) {
            R.id.buttonGrabar -> {
                val valConfirm: String? = editTextLectura.text.toString()
                val lectura: Int = if (valConfirm.isNullOrEmpty()) 0 else valConfirm.toInt()
                gps = Gps(this@SuministroAfterActivity)
                if (gps.isLocationEnabled()) {
                    if (gps.latitude.toString() == "0.0" || gps.longitude.toString() == "0.0") {
                        gps.showAlert(this@SuministroAfterActivity)
                    } else {
                        Util.hideKeyboard(this)
                        registro_Latitud = gps.latitude.toString()
                        registro_Longitud = gps.longitude.toString()
                        registro_Lectura = editTextLectura.text.toString()
                        registro_Confirmar_Lectura = editTextLectura.text.toString()
                        registro_Observacion = editTextObservacion.text.toString()
                        responsable = editTextResponsable.text.toString()
                        parentesco = editTextParentesco.text.toString()
                        precinto = editTextPrecinto.text.toString()

                        if (pideLectura == "SI") {
                            if (editTextLectura.text.toString().isEmpty()) {
                                Util.toastMensaje(this, "Digite Lectura..")
                                return
                            }
                        }

                        if (estado == 3 || estado == 4) {
                            if (fotoConfirmacion == 0) {
                                Util.toastMensaje(this, "Debes tomar foto de inicio.")
                                return
                            }
                        }

                        if (precinto.trim().isEmpty()) {
                            Util.toastMensaje(this, "Digitar precinto.")
                            return
                        }

                        if (registro_Desplaza.isNotEmpty()) {
                            when (estado) {
                                1, 10 -> validarLectura(lectura, 1)
//                                    if (grupo_Incidencia_Codigo == "2" || grupo_Incidencia_Codigo == "17" || grupo_Incidencia_Codigo == "21") {
//
//                                    if (editTextNumeroConstancia.text.toString().isNotEmpty()) {
//                                        registro_Constancia = editTextNumeroConstancia.text.toString()
//                                        validarLectura(lectura, 1)
//                                    } else {
//                                        editTextNumeroConstancia.error = "Ingrese Número de Constancia"
//                                        editTextNumeroConstancia.requestFocus()
//                                    }
//                                } else {
//
//                                }
                                3 ->
//                                    if (parentId != 0) {
                                    if (codigo_Resultado.isNotEmpty()) {
                                        val sCortes: SuministroCortes? = suministroImp.suministroCortesByOrden(ordenOperario)
                                        if (sCortes?.suministro_NoCortar != 1) {
                                            if (pideLectura == "SI") {
                                                if (editTextLectura.text.toString().isEmpty()) {
                                                    editTextLectura.error = "Ingrese Lectura"
                                                    editTextLectura.requestFocus()
                                                } else {
                                                    saveRegistro("1", 2)
                                                    goToPhoto()
                                                }
                                            } else {
                                                saveRegistro("1", 2)
                                                goToPhoto()
                                            }
                                        } else {
                                            Util.toastMensaje(this, "Corte Cancelado")
                                        }
                                    } else {
                                        Util.toastMensaje(this, "Eliga una causa")
                                    }
//                                    } else {
//                                        Util.toastMensaje(this, "Eliga un resultado")
//                                    }
                                4 -> if (motivoId != 0) {
//                                    if (parentId != 0) {
                                    if (codigo_Resultado.isNotEmpty()) {
                                        if (pideLectura == "SI") {
                                            if (editTextLectura.text.toString().isEmpty()) {
                                                editTextLectura.error = "Ingrese Lectura"
                                                editTextLectura.requestFocus()
                                            } else {
                                                saveRegistro("1", 2)
                                                goToPhoto()
                                            }
                                        } else {
                                            saveRegistro("1", 2)
                                            goToPhoto()
                                        }
                                    } else {
                                        Util.toastMensaje(this, "Eliga una causa")
                                    }
//                                    } else {
//                                        Util.toastMensaje(this, "Eliga un resultado")
//                                    }
                                } else {
                                    Util.toastMensaje(this, "Eliga un motivo")
                                }
                                7 -> validarLectura(lectura, 0)
                                else -> {
                                    saveRegistro("1", 2)
                                    goToPhoto()
                                }
                            }
                        } else {
                            Util.toastMensaje(this, "Eliga una Ubicación de Medidor")
                        }
                    }
                } else {
                    gps.showSettingsAlert(this@SuministroAfterActivity)
                }
            }
            R.id.editTextMotivo -> dialogSpinner(1)
            R.id.editTextDialogObservacion -> dialogSpinner(2)
            R.id.editTextArtefacto -> dialogSpinner(3)
            R.id.editTextResultado -> dialogSpinner(4)
            R.id.editTextUbicacion -> dialogSpinner(5)
            R.id.editTextCausa -> dialogSpinner(6)
//                if (parentId != 0) {
//                dialogSpinner(6)
//            } else {
//                Util.toastMensaje(this, "Eliga un resultado")
//            }
            R.id.imageViewMap -> {
                if (latitud.isNotEmpty() || longitud.isNotEmpty()) {
                    startActivity(Intent(this@SuministroAfterActivity, MapsActivity::class.java)
                            .putExtra("latitud", latitud)
                            .putExtra("longitud", longitud)
                            .putExtra("title", suministro_Numero))
                } else {
                    Util.toastMensaje(this@SuministroAfterActivity, "Este suministro no cuenta con coordenadas")
                }
            }
            R.id.buttonPhoto -> if (fotoConfirmacion == 0) {
                createImage()
            } else {
                Util.toastMensaje(this, "Ya contienes una foto de inicio")
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.lista, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Util.hideKeyboard(this)
        when (item.itemId) {
            R.id.before -> beforeOrAfterLectura(tipo, estado, "BEFORE")
            R.id.after -> beforeOrAfterLectura(tipo, estado, "NEXT")
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

    lateinit var folder: File
    lateinit var image: File

    var nameImg: String = ""
    var direction: String = ""

    private lateinit var realm: Realm
    private lateinit var suministroImp: SuministroImplementation
    private lateinit var comboImp: ComboImplementation
    private lateinit var registroImp: RegistroImplementation
    lateinit var photoImp: PhotoImplementation
    private lateinit var loginImp: LoginImplementation
    private lateinit var gps: Gps
    private lateinit var builder: AlertDialog.Builder
    private lateinit var dialog: AlertDialog

    private var envioId: Int? = 0
    private var lecturaMax: Double? = 0.0
    private var lecturaMin: Double? = 0.0

    //We declare the variables to save the record
    private var iD_Registro: Int = 0
    private var iD_Operario: Int = 0
    private var iD_Suministro: Int = 0
    private var suministroNumero: Int = 0
    private var iD_TipoLectura: Int = 0
    private var registro_Fecha_SQLITE: String = ""
    private var registro_Latitud: String = ""
    private var registro_Longitud: String = ""
    private var registro_Lectura: String = ""
    private var registro_Confirmar_Lectura: String = ""
    private var registro_Observacion: String = ""
    private var grupo_Incidencia_Codigo: String = ""
    private var registro_TipoProceso: String = ""
    private var fecha_Sincronizacion_Android: String = ""
    private var registro_Constancia: String = ""
    private var registro_Desplaza: String = ""
    private var codigo_Resultado: String = ""
    private var tipo: Int = 0
    private var orden: Int = 0
    private var ordenOperario: Int = 0
    private var titulo: String = ""
    private var estado: Int = 0
    private var lecturaManual: Int = 0
    private var motivoId: Int = 0
    private var parentId: Int = 0
    private var online: Int? = 0

    // For Lectura
    private var lecturaAnterior: Double? = 0.0
    var pidePhoto: String = ""
    var pideLectura: String = ""
    private var tipoCliente: Int = 0
    // Se utiliza para Lectura Recuperadas
    private var recuperada = 0

    var latitud = ""
    var longitud = ""
    var suministro_Numero = ""

    var contrato: String = ""
    var fechaAsignacion: String = ""
    var fotoConfirmacion: Int = 0

    var responsable: String = ""
    var parentesco: String = ""
    var precinto: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_suministro_after)
        realm = Realm.getDefaultInstance()
        suministroImp = SuministroOver(realm)
        comboImp = ComboOver(realm)
        registroImp = RegistroOver(realm)
        photoImp = PhotoOver(realm)
        loginImp = LoginOver(realm)
        val bundle = intent.extras
        if (bundle != null) {
            titulo = bundle.getString("nombre")!!
            orden = bundle.getInt("orden")
            ordenOperario = bundle.getInt("orden_2")
            estado = bundle.getInt("estado")
            bindToolbar(titulo)
            bindUI(estado, ordenOperario)
        }
    }

    private fun bindToolbar(nombre: String) {
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        Objects.requireNonNull<ActionBar>(supportActionBar).title = nombre
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            Util.hideKeyboard(this)
            val intent = Intent(this@SuministroAfterActivity, SuministroActivity::class.java)
            intent.putExtra("nombre", titulo)
            intent.putExtra("estado", estado)
            startActivity(intent)
            finish()
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            val intent = Intent(this@SuministroAfterActivity, SuministroActivity::class.java)
            intent.putExtra("nombre", titulo)
            intent.putExtra("estado", estado)
            startActivity(intent)
            finish()
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun bindUI(estado: Int, orden: Int) {
        tipo = if (estado == 1 || estado == 7 || estado == 6 || estado == 10) 1 else estado
        recuperada = if (estado == 10) 10 else 0
        lecturaManual = if (estado == 7) 1 else 0
        // we enter the values
        val login: Login = loginImp.login!!
        val registro: Registro? = registroImp.getRegistro(orden, tipo, recuperada)

        online = login.operario_EnvioEn_Linea
        iD_Operario = login.iD_Operario

        if (registro != null) {
            val motivo: Motivo? = comboImp.getMotivosById(registro.motivoId)
            if (motivo != null) {
                motivoId = motivo.codigo
                editTextMotivo.setText(motivo.descripcion)
            }
            registro_Desplaza = registro.registro_Desplaza

            when (registro_Desplaza) {
                "1" -> editTextUbicacion.setText(String.format("%s", "Externo"))
                "2" -> editTextUbicacion.setText(String.format("%s", "Interno"))
                "3" -> editTextUbicacion.setText(String.format("%s", "Sotano"))
                "4" -> editTextUbicacion.setText(String.format("%s", "Azotea"))
            }
            editTextObservacion.setText(registro.registro_Observacion)
            editTextResponsable.setText(registro.responsable)
            editTextParentesco.setText(registro.parentesco)
            editTextPrecinto.setText(registro.precinto)

            iD_Registro = registro.iD_Registro
            iD_TipoLectura = registro.iD_TipoLectura
        }

        val type = InputType.TYPE_CLASS_NUMBER
        editTextLectura.inputType = type

        buttonGrabar.setOnClickListener(this)
        editTextMotivo.setOnClickListener(this)
        editTextUbicacion.setOnClickListener(this)
        imageViewMap.setOnClickListener(this)
        editTextLectura.setText(registro?.registro_Lectura)
        Util.showKeyboard(editTextLectura, this)
        editTextLectura.setOnEditorActionListener { v, _, _ ->
            if (v.text.toString().isNotEmpty()) {
                Util.hideKeyboard(this)
                dialogSpinner(5)
            }
            true
        }

        if (estado <= 2 || estado == 7 || estado == 6 || estado == 9 || estado == 10) {

            val cantidad = registroImp.getAllRegistro(1).size
            if (cantidad >= 5) {
                startService(Intent(this, SendRegisterService::class.java))
            }

            val lecturaEstado = if (estado == 1 || estado == 7 || estado == 6 || estado == 9 || estado == 10) 1 else estado
//            buttonFound.text = String.format("%s", "Suministro Encontrado")
            val detalleGrupoList: RealmResults<DetalleGrupo> = comboImp.getDetalleGrupoByLectura(lecturaEstado)

            val d: DetalleGrupo? = detalleGrupoList[0]
            if (d != null) {
                pidePhoto = d.pideFoto
                pideLectura = d.pideLectura
                grupo_Incidencia_Codigo = d.iD_DetalleGrupo.toString()
                editTextDialogObservacion.setText(d.descripcion)
//                if (grupo_Incidencia_Codigo == "2" || grupo_Incidencia_Codigo == "17" || grupo_Incidencia_Codigo == "21") {
//                    editTextNumeroConstancia.visibility = View.VISIBLE
//                } else {
//                    editTextNumeroConstancia.visibility = View.GONE
//                }
                if (pideLectura == "NO") {
                    editTextLectura.text = null
                    editTextLectura.isEnabled = false
                } else {
                    editTextLectura.isEnabled = true
                }
            }

            // TODO NUEVO
            editTextDialogObservacion.setOnClickListener(this)

            if (registro?.grupo_Incidencia_Codigo != null) {
                val detalleGrupo = comboImp.getDetalleGrupoById(registro.grupo_Incidencia_Codigo.toInt())
                pidePhoto = detalleGrupo.pideFoto
                pideLectura = detalleGrupo.pideLectura

                editTextDialogObservacion.setText(detalleGrupo.descripcion)
//                if (grupo_Incidencia_Codigo == "2" || grupo_Incidencia_Codigo == "17" || grupo_Incidencia_Codigo == "21") {
//                    editTextNumeroConstancia.visibility = View.VISIBLE
//                    editTextNumeroConstancia.setText(registro.registro_Constancia)
//                } else {
//                    editTextNumeroConstancia.visibility = View.GONE
//                }
                if (pideLectura == "NO") {
                    editTextLectura.text = null
                    editTextLectura.isEnabled = false
                } else {
                    editTextLectura.isEnabled = true
                }
            }

            textViewContrato.visibility = View.GONE
            linearLayoutCorte2.visibility = View.GONE
            linearLayout.visibility = View.VISIBLE
            val sLectura: SuministroLectura? = suministroImp.suministroLecturaByOrden(orden)
            if (sLectura != null) {
                envioId = sLectura.iD_Suministro
                lecturaMax = sLectura.suministro_LecturaMaxima.toDouble()
                lecturaMin = sLectura.suministro_LecturaMinima.toDouble()

                textViewContrato.text = String.format("C : %s", sLectura.suministro_Numero)
                contrato = sLectura.suministro_Numero
                fechaAsignacion = sLectura.fechaAsignacion
                textViewMedidor.text = String.format("Medidor : %s", sLectura.suministro_Medidor)
                textViewCliente.text = sLectura.suministro_Cliente
                textViewDireccion.text = sLectura.suministro_Direccion
                textViewOrden.text = String.format("Orden : %s", sLectura.orden)

                // For Lectura
                iD_Suministro = sLectura.iD_Suministro
                registro_TipoProceso = sLectura.suministro_TipoProceso
                fecha_Sincronizacion_Android = Util.getFechaActual()
                lecturaAnterior = sLectura.lecturaAnterior.toDouble()
                suministroNumero = sLectura.suministro_Numero.toInt()
                tipoCliente = sLectura.tipoCliente

                textViewTelefono.visibility = View.VISIBLE
                textViewNota.visibility = View.VISIBLE
                textViewTelefono.text = String.format("Telf : %s", sLectura.telefono)
                textViewNota.text = sLectura.nota

                latitud = sLectura.latitud
                longitud = sLectura.longitud
                suministro_Numero = sLectura.suministro_Numero
            }
        } else if (estado == 3) {
            textInputLayoutArtefacto.hint = String.format("%s", "Motivo")
            textViewUnidadLectura.visibility = View.VISIBLE
            textViewAvisoCorte.visibility = View.VISIBLE
            val sCortes: SuministroCortes? = suministroImp.suministroCortesByOrden(orden)
            if (sCortes != null) {
                envioId = sCortes.iD_Suministro
                lecturaMax = sCortes.suministro_LecturaMaxima.toDouble()
                lecturaMin = sCortes.suministro_LecturaMinima.toDouble()
                textViewContrato.text = String.format("C : %s", sCortes.suministro_Numero)
                contrato = sCortes.suministro_Numero
                fechaAsignacion = sCortes.fechaAsignacion
                textViewMedidor.text = String.format("Medidor : %s", sCortes.suministro_Medidor)
                textViewCliente.text = sCortes.suministro_Cliente
                textViewDireccion.text = sCortes.suministro_Direccion
                textViewOrden.text = String.format("Orden : %s", sCortes.orden)
                textViewUnidadLectura.text = String.format("U.L : %s", sCortes.suministro_UnidadLectura)
                textViewAvisoCorte.text = String.format("A.C : %s", sCortes.avisoCorte)
                // For Corte
                iD_Suministro = sCortes.iD_Suministro
                registro_TipoProceso = sCortes.suministro_TipoProceso
                suministroNumero = sCortes.suministro_Numero.toInt()

                latitud = sCortes.latitud
                longitud = sCortes.longitud
                suministro_Numero = sCortes.suministro_Numero
            }
        } else if (estado == 4) {
            textInputLayoutMotivo.visibility = View.VISIBLE
            textViewConexion.visibility = View.VISIBLE
            val sCortes: SuministroReconexion? = suministroImp.suministroReconexionByOrden(orden)
            if (sCortes != null) {
                envioId = sCortes.iD_Suministro
                lecturaMax = sCortes.suministro_LecturaMaxima.toDouble()
                lecturaMin = sCortes.suministro_LecturaMinima.toDouble()
                textViewContrato.text = String.format("C : %s", sCortes.suministro_Numero)
                contrato = sCortes.suministro_Numero
                fechaAsignacion = sCortes.fechaAsignacion
                textViewMedidor.text = String.format("Medidor : %s", sCortes.suministro_Medidor)
                textViewCliente.text = sCortes.suministro_Cliente
                textViewDireccion.text = sCortes.suministro_Direccion
                textViewOrden.text = String.format("Orden : %s", sCortes.orden)
                textViewTelefono.visibility = View.VISIBLE
                textViewTelefono.text = String.format("Telf : %s", sCortes.telefono)
                textViewConexion.text = String.format("Hora de vencimiento : %s", sCortes.primeraReconexion)

                // For Corte
                iD_Suministro = sCortes.iD_Suministro
                registro_TipoProceso = sCortes.suministro_TipoProceso
                suministroNumero = sCortes.suministro_Numero.toInt()
                latitud = sCortes.latitud
                longitud = sCortes.longitud
                suministro_Numero = sCortes.suministro_Numero
            }
        }

        if (estado == 3 || estado == 4) {
            textViewContrato.visibility = View.VISIBLE
            linearLayoutCorte2.visibility = View.VISIBLE
            linearLayout.visibility = View.GONE
            buttonPhoto.visibility = View.VISIBLE
            // TODO NUEVO
            editTextArtefacto.setOnClickListener(this)
            editTextResultado.setOnClickListener(this)
            editTextCausa.setOnClickListener(this)
            buttonPhoto.setOnClickListener(this)

            val photos = photoImp.photoAllBySuministro(envioId!!, tipo, 0)
            fotoConfirmacion = photos.size
            photos.addChangeListener { result, _ ->
                fotoConfirmacion = result.size
            }

            if (fotoConfirmacion == 0) {
                createImage()
            }

            val detalleGrupoMotivoList = comboImp.getDetalleGrupoByMotivo(estado, "1")
            val de: DetalleGrupo? = detalleGrupoMotivoList[0]
            if (de != null) {
                pidePhoto = de.pideFoto
                pideLectura = de.pideLectura
                grupo_Incidencia_Codigo = de.iD_DetalleGrupo.toString()
                editTextArtefacto.setText(de.descripcion)
            }
//            val detalleGrupoResultadoList = comboImp.getDetalleGrupoByResultado(estado, "7")
//            val dr: DetalleGrupo? = detalleGrupoResultadoList[0]
//            if (dr != null) {
//                pidePhoto = dr.pideFoto
//                pideLectura = dr.pideLectura
//                codigo_Resultado = dr.iD_DetalleGrupo.toString()
//                editTextCausa.setText(dr.descripcion)
//            }

            if (registro?.grupo_Incidencia_Codigo != null) {
                if (registro.parentId != 0) {
                    val parentGrupo: DetalleGrupo = comboImp.getDetalleGrupoById(registro.parentId)
                    parentId = parentGrupo.iD_DetalleGrupo
                    editTextResultado.setText(parentGrupo.descripcion)
                    if (parentId == 55) {
                        editTextCausa.isEnabled = false
                    }
                }

                val detalleGrupo = comboImp.getDetalleGrupoById(registro.grupo_Incidencia_Codigo.toInt())
                grupo_Incidencia_Codigo = registro.grupo_Incidencia_Codigo
                editTextArtefacto.setText(detalleGrupo.descripcion)

                if (registro.codigo_Resultado.isNotEmpty()) {
                    val detalleGrupoResultado = comboImp.getDetalleGrupoById(registro.codigo_Resultado.toInt())
                    editTextCausa.setText(detalleGrupoResultado.descripcion)
                    codigo_Resultado = registro.codigo_Resultado
                }
            }
        }
    }

    private fun validarLectura(lecturaNueva: Int, estado: Int) {
//        val result = lecturaNueva - lecturaAnterior!! se retiro
        val result = lecturaNueva - lecturaAnterior!!
        if (estado == 1) {
            if (tipoCliente == 0) {
                if (lecturaMin!! < result && lecturaMax!! > result) {
                    if (pidePhoto == "SI") {
                        if (pideLectura == "SI") {
                            confirmLectura()
                        } else {
                            saveRegistro("1", 2)
                            goToPhoto()
                        }
                    } else {
                        saveRegistro("0", 1)
                        updateData(envioId!!, tipo)
                        beforeOrAfterLectura(tipo, estado, "NEXT")
                        //  openAdditional("")
                    }
                } else {
                    if (pideLectura == "SI") {
                        confirmLectura()
                    } else {
                        saveRegistro("1", 2)
                        goToPhoto()
                    }
                }
            } else {
                if (pideLectura == "SI") {
                    confirmLectura()
                } else {
                    saveRegistro("1", 2)
                    goToPhoto()
                }
            }
        } else {
            saveRegistro("0", 1)
            updateData(envioId!!, tipo)
            beforeOrAfterLectura(tipo, estado, "NEXT")
            //    if (lecturaMin!! < lecturaNueva && lecturaMax!! > lecturaNueva) {
            //        openAdditional("Esta lectura esta dentro del rango.")
            //    } else {
            //        openAdditional("Esta lectura esta fuera del rango.")
            //    }
        }
    }

    private fun saveRegistro(tieneFoto: String, estado: Int) {
        registro_Fecha_SQLITE = Util.getFechaActual()
        fecha_Sincronizacion_Android = registro_Fecha_SQLITE
        val registro: Registro? = Registro(
                registroImp.getRegistroIdentity(), iD_Registro, iD_Operario, iD_Suministro, suministroNumero, iD_TipoLectura,
                registro_Fecha_SQLITE, registro_Latitud, registro_Longitud, registro_Lectura, registro_Confirmar_Lectura, registro_Observacion,
                grupo_Incidencia_Codigo, tieneFoto, registro_TipoProceso, fecha_Sincronizacion_Android, registro_Constancia, registro_Desplaza, codigo_Resultado,
                if (recuperada == 10) 10 else tipo, ordenOperario, estado, lecturaManual, motivoId, parentId, responsable, parentesco, precinto)
        registroImp.save(registro!!)
    }

    private fun confirmLectura() {
        builder = AlertDialog.Builder(ContextThemeWrapper(this, R.style.AppTheme))
        @SuppressLint("InflateParams") val v = LayoutInflater.from(this).inflate(R.layout.dialog_confirm, null)
        val editTextLecturaConfirm = v.findViewById<TextView>(R.id.editTextLecturaConfirm)
        val buttonCancelar: MaterialButton = v.findViewById(R.id.buttonCancelar)
        val buttonAceptar: MaterialButton = v.findViewById(R.id.buttonAceptar)

        buttonAceptar.setOnClickListener {
            val confirm = editTextLecturaConfirm.text.toString()
            if (confirm == editTextLectura.text.toString()) {
                saveRegistro("1", 2)
                goToPhoto()
                dialog.dismiss()
            } else {
                editTextLecturaConfirm.error = "Lectura no es igual"
                editTextLecturaConfirm.requestFocus()
            }
        }
        buttonCancelar.setOnClickListener {
            dialog.cancel()
        }
        builder.setView(v)
        dialog = builder.create()
        dialog.show()
    }

    private fun updateData(receive: Int, tipo: Int) {
        when {
            tipo <= 2 -> suministroImp.updateActivoSuministroLectura(receive, 0)
            tipo == 3 -> suministroImp.updateActivoSuministroCortes(receive, 0)
            tipo == 4 -> suministroImp.updateActivoSuministroReconexion(receive, 0)
        }
    }

    private fun goToPhoto() {
        val intent = Intent(this, PhotoActivity::class.java)
        intent.putExtra("envioId", envioId)
        intent.putExtra("orden", orden)
        intent.putExtra("orden_2", ordenOperario)
        intent.putExtra("tipo", if (estado == 10) 10 else tipo)
        intent.putExtra("estado", estado)
        intent.putExtra("nombre", titulo)
        intent.putExtra("suministro", contrato.trim())
        intent.putExtra("fechaAsignacion", fechaAsignacion.trim())
        startActivity(intent)
        finish()
    }

    private fun isNumeric(strNum: String): Boolean {
        try {
            val d = Integer.parseInt(strNum)
            Log.i("TAG", d.toString())
        } catch (nfe: NumberFormatException) {
            return false
        } catch (nfe: NullPointerException) {
            return false
        }
        return true
    }

    private fun beforeOrAfterLectura(tipo: Int, estado: Int, position: String) {
        Util.hideKeyboard(this)
        when {
            tipo <= 2 -> {
                val suministrosLectura = when (estado) {
                    6 -> suministroImp.getSuministroLectura(1, 1, 1)
                    10 -> suministroImp.getSuministroLectura(estado, 1, 0)
                    else -> suministroImp.getSuministroLectura(tipo, 1, 0)
                }
                val nombre = when (estado) {
                    1 -> "Lectura"
                    2 -> "Relectura"
                    6 -> "Lectura Observadas"
                    7 -> "Lectura Manuales"
                    10 -> "Lectura Recuperadas"
                    else -> {
                        ""
                    }
                }
                val returnOrden = if (position == "NEXT") {
                    AfterOrden.getNextOrdenLectura(orden, suministrosLectura)
                } else {
                    AfterOrden.getBeforeOrdenLectura(orden, suministrosLectura)
                }
                if (returnOrden == 0) {
                    val intent = Intent(this@SuministroAfterActivity, SuministroActivity::class.java)
                    intent.putExtra("nombre", nombre)
                    intent.putExtra("estado", estado)
                    startActivity(intent)
                    finish()
                } else {
                    val ordenLecttura = suministroImp.buscarLecturaByOrden(returnOrden, 1)
                    val intent = Intent(this@SuministroAfterActivity, SuministroAfterActivity::class.java)
                    intent.putExtra("orden", returnOrden)
                    intent.putExtra("orden_2", ordenLecttura.suministroOperario_Orden)
                    intent.putExtra("nombre", nombre)
                    intent.putExtra("estado", estado)
                    startActivity(intent)
                    finish()
                }
            }
            tipo == 9 -> {
                val suministrosLectura = suministroImp.getSuministroReclamos("9", 1)
                val returnOrden = if (position == "NEXT") {
                    AfterOrden.getNextOrdenLectura(orden, suministrosLectura)
                } else {
                    AfterOrden.getBeforeOrdenLectura(orden, suministrosLectura)
                }
                if (returnOrden == 0) {
                    val intent = Intent(this@SuministroAfterActivity, SuministroActivity::class.java)
                    intent.putExtra("nombre", "Reclamos")
                    intent.putExtra("estado", estado)
                    startActivity(intent)
                    finish()
                } else {
                    val ordenLecttura = suministroImp.buscarLecturaByOrden(returnOrden, 1)
                    val intent = Intent(this@SuministroAfterActivity, SuministroAfterActivity::class.java)
                    intent.putExtra("orden", returnOrden)
                    intent.putExtra("orden_2", ordenLecttura.suministroOperario_Orden)
                    intent.putExtra("nombre", "Reclamos")
                    intent.putExtra("estado", estado)
                    startActivity(intent)
                    finish()
                }
            }
            tipo == 3 -> {
                val suministrosCortes = suministroImp.getSuministroCortes(tipo, 1)
                val returnOrden = if (position == "NEXT") {
                    AfterOrden.getNextOrdenCortes(orden, suministrosCortes)
                } else {
                    AfterOrden.getBeforeOrdenCortes(orden, suministrosCortes)
                }
                if (returnOrden == 0) {
                    val intent = Intent(this@SuministroAfterActivity, SuministroActivity::class.java)
                    intent.putExtra("nombre", "Corte")
                    intent.putExtra("estado", tipo)
                    startActivity(intent)
                    finish()
                } else {
                    val ordenCortes = suministroImp.buscarCortesByOrden(returnOrden, 1)
                    val intent = Intent(this@SuministroAfterActivity, SuministroAfterActivity::class.java)
                    intent.putExtra("orden", returnOrden)
                    intent.putExtra("orden_2", ordenCortes.suministroOperario_Orden)
                    intent.putExtra("nombre", "Corte")
                    intent.putExtra("estado", tipo)
                    startActivity(intent)
                    finish()
                }
            }
            tipo == 4 -> {
                val suministrosReconexion = suministroImp.getSuministroReconexion(tipo, 1)
                val returnOrden = if (position == "NEXT") {
                    AfterOrden.getNextOrdenReconexion(orden, suministrosReconexion)
                } else {
                    AfterOrden.getBeforeOrdenReconexion(orden, suministrosReconexion)
                }
                if (returnOrden == 0) {
                    val intent = Intent(this@SuministroAfterActivity, SuministroActivity::class.java)
                    intent.putExtra("nombre", "Reconexion")
                    intent.putExtra("estado", tipo)
                    startActivity(intent)
                    finish()
                } else {
                    val ordenReconexion = suministroImp.buscarReconexionesByOrden(returnOrden, 1)
                    val intent = Intent(this@SuministroAfterActivity, SuministroAfterActivity::class.java)
                    intent.putExtra("orden", returnOrden)
                    intent.putExtra("orden_2", ordenReconexion.suministroOperario_Orden)
                    intent.putExtra("nombre", "Reconexion")
                    intent.putExtra("estado", tipo)
                    startActivity(intent)
                    finish()
                }
            }
        }
    }

    private fun dialogSpinner(tipo: Int) {
        val builder = androidx.appcompat.app.AlertDialog.Builder(ContextThemeWrapper(this, R.style.AppTheme))
        @SuppressLint("InflateParams") val v = LayoutInflater.from(this).inflate(R.layout.dialog_combo, null)
        val textViewTitulo: TextView = v.findViewById(R.id.textViewTitulo)
        val recyclerView: RecyclerView = v.findViewById(R.id.recyclerView)
        val layoutManager = LinearLayoutManager(this)
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.layoutManager = layoutManager
        recyclerView.addItemDecoration(DividerItemDecoration(recyclerView.context, DividerItemDecoration.VERTICAL))
        builder.setView(v)
        val dialog = builder.create()
        dialog.show()

        when (tipo) {
            1 -> {
                textViewTitulo.text = String.format("%s", "Motivo")
                val motivoAdapter = MotivoAdapter(object : OnItemClickListener.MotivoListener {
                    override fun onItemClick(m: Motivo, view: View, position: Int) {
                        motivoId = m.codigo
                        editTextMotivo.setText(m.descripcion)
                        dialog.dismiss()
                    }
                })
                recyclerView.adapter = motivoAdapter
                val motivos = comboImp.getMotivos()
                motivoAdapter.addItems(motivos)
            }
            2 -> {
                textViewTitulo.text = String.format("%s", "Codigo de Observación")
                val detalleGrupoAdapter = DetalleGrupoAdapter(object : OnItemClickListener.DetalleGrupoListener {
                    override fun onItemClick(d: DetalleGrupo, view: View, position: Int) {
                        pidePhoto = d.pideFoto
                        pideLectura = d.pideLectura
                        grupo_Incidencia_Codigo = d.iD_DetalleGrupo.toString()
                        editTextDialogObservacion.setText(d.descripcion)
//                        if (grupo_Incidencia_Codigo == "2" || grupo_Incidencia_Codigo == "17" || grupo_Incidencia_Codigo == "21") {
//                            editTextNumeroConstancia.visibility = View.VISIBLE
//                        } else {
//                            editTextNumeroConstancia.visibility = View.GONE
//                        }
                        if (pideLectura == "NO") {
                            editTextLectura.text = null
                            editTextLectura.isEnabled = false
                        } else {
                            editTextLectura.isEnabled = true
                        }

                        if (d.ubicaMedidor != 0) {
                            registro_Desplaza = d.ubicaMedidor.toString()
                            when (d.ubicaMedidor) {
                                1 -> editTextUbicacion.setText(String.format("%s", "Externo"))
                                2 -> editTextUbicacion.setText(String.format("%s", "Interno"))
                                3 -> editTextUbicacion.setText(String.format("%s", "Sotano"))
                                4 -> editTextUbicacion.setText(String.format("%s", "Azotea"))
                            }
                        }

                        dialog.dismiss()
                    }
                })
                recyclerView.adapter = detalleGrupoAdapter
                val lecturaEstado = if (estado == 1 || estado == 7 || estado == 6 || estado == 9 || estado == 10) 1 else estado
                val detalleGrupoList: RealmResults<DetalleGrupo> = comboImp.getDetalleGrupoByLectura(lecturaEstado)
                detalleGrupoAdapter.addItems(detalleGrupoList)
            }
            3 -> {
                if (estado == 3) {
                    textViewTitulo.text = String.format("%s", "Motivo")
                } else {
                    textViewTitulo.text = String.format("%s", "Artefacto")
                }

                val detalleGrupoAdapter = DetalleGrupoAdapter(object : OnItemClickListener.DetalleGrupoListener {
                    override fun onItemClick(d: DetalleGrupo, view: View, position: Int) {
                        pidePhoto = d.pideFoto
                        pideLectura = d.pideLectura
                        grupo_Incidencia_Codigo = d.iD_DetalleGrupo.toString()
                        editTextArtefacto.setText(d.descripcion)
                        dialog.dismiss()
                    }
                })
                recyclerView.adapter = detalleGrupoAdapter
                val detalleGrupoMotivoList = comboImp.getDetalleGrupoByMotivo(estado, "1")
                detalleGrupoAdapter.addItems(detalleGrupoMotivoList)
            }
            4 -> {
                textViewTitulo.text = String.format("%s", "Resultado")
                val detalleGrupoAdapter = DetalleGrupoAdapter(object : OnItemClickListener.DetalleGrupoListener {
                    override fun onItemClick(d: DetalleGrupo, view: View, position: Int) {
                        parentId = d.iD_DetalleGrupo
                        editTextResultado.setText(d.descripcion)
                        when (grupo_Incidencia_Codigo) {
                            "47" -> if (parentId == 55) {
                                val detalle: DetalleGrupo? = comboImp.getDetalleGrupoById(89)
                                if (detalle != null) {
                                    pidePhoto = detalle.pideFoto
                                    pideLectura = detalle.pideLectura
                                    codigo_Resultado = detalle.iD_DetalleGrupo.toString()
                                    editTextCausa.setText(detalle.descripcion)
                                    if (pideLectura == "NO") {
                                        editTextLectura.text = null
                                        editTextLectura.isEnabled = false
                                    } else {
                                        editTextLectura.isEnabled = true
                                    }
                                }
                            }
                            "50" -> if (parentId == 55) {
                                val detalle: DetalleGrupo? = comboImp.getDetalleGrupoById(90)
                                if (detalle != null) {
                                    pidePhoto = detalle.pideFoto
                                    pideLectura = detalle.pideLectura
                                    codigo_Resultado = detalle.iD_DetalleGrupo.toString()
                                    editTextCausa.setText(detalle.descripcion)
                                    if (pideLectura == "NO") {
                                        editTextLectura.text = null
                                        editTextLectura.isEnabled = false
                                    } else {
                                        editTextLectura.isEnabled = true
                                    }
                                }
                            }
                        }

                        if (parentId != 55) {
                            pidePhoto = ""
                            pideLectura = ""
                            codigo_Resultado = ""
                            editTextCausa.text = null
                            editTextCausa.isEnabled = true

                            if (parentId == 92) {
                                editTextArtefacto.setText("")
                                editTextArtefacto.isEnabled = false
                            }
                            if (parentId == 93) {
                                editTextLectura.text = null
                                editTextLectura.isEnabled = true
                                grupo_Incidencia_Codigo = ""
                                editTextArtefacto.setText("")
                                editTextArtefacto.isEnabled = true
                            }
                        }
                        dialog.dismiss()
                    }
                })
                recyclerView.adapter = detalleGrupoAdapter
                val detalleGrupoResultadoList = comboImp.getDetalleGrupoByParentId(if (estado == 3) 1 else 2)
                detalleGrupoAdapter.addItems(detalleGrupoResultadoList)
            }
            5 -> {
                textViewTitulo.text = String.format("%s", "Ubicación del Medidor")
                val menuAdapter = MenuItemAdapter(object : OnItemClickListener.MenuListener {
                    override fun onItemClick(m: MenuPrincipal, v: View, position: Int) {
                        registro_Desplaza = m.menuId.toString()
                        editTextUbicacion.setText(m.title)
                        dialog.dismiss()
                    }
                })
                recyclerView.itemAnimator = DefaultItemAnimator()
                recyclerView.layoutManager = layoutManager
                recyclerView.adapter = menuAdapter

                val menus: ArrayList<MenuPrincipal> = ArrayList()
                menus.add(MenuPrincipal(1, "Externo", 0, 0))
                menus.add(MenuPrincipal(2, "Interno", 0, 0))
                if (estado != 3 && estado != 4) {
                    menus.add(MenuPrincipal(3, "Sotano", 0, 0))
                    menus.add(MenuPrincipal(4, "Azotea", 0, 0))
                }
                menuAdapter.addItems(menus)
            }
            6 -> {
                textViewTitulo.text = String.format("%s", "Causa")
                val detalleGrupoAdapter = DetalleGrupoAdapter(object : OnItemClickListener.DetalleGrupoListener {
                    override fun onItemClick(d: DetalleGrupo, view: View, position: Int) {
                        pidePhoto = d.pideFoto
                        pideLectura = d.pideLectura
                        codigo_Resultado = d.iD_DetalleGrupo.toString()
                        editTextCausa.setText(d.descripcion)

                        if (pideLectura == "NO") {
                            editTextLectura.text = null
                            editTextLectura.isEnabled = false
                        } else {
                            editTextLectura.isEnabled = true
                        }
                        dialog.dismiss()
                    }
                })
                recyclerView.adapter = detalleGrupoAdapter

//                val detalleGrupoResultadoList = comboImp.getDetalleGrupoByParentId(parentId)
                val detalleGrupoResultadoList = comboImp.getDetalleGrupoByResultado(estado, "7")
                detalleGrupoAdapter.addItems(detalleGrupoResultadoList)
            }
        }
    }

    private fun createImage() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(Objects.requireNonNull(this).packageManager) != null) {
            folder = Util.getFolder()
            nameImg = Util.getFechaActualForPhoto(contrato.toInt(), tipo) + ".jpg"
            image = File(folder, nameImg)
            direction = "$folder/$nameImg"
            val uriSavedImage = Uri.fromFile(image)
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage)

            if (Build.VERSION.SDK_INT >= 24) {
                try {
                    val m = StrictMode::class.java.getMethod("disableDeathOnFileUriExposure")
                    m.invoke(null)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            startActivityForResult(takePictureIntent, Permission.CAMERA_REQUEST)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Permission.SPEECH_REQUEST && resultCode == RESULT_OK) {
            val result: ArrayList<String>? = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val y = result?.get(0)!!.replace(" ", "")
            if (isNumeric(y)) {
                editTextLectura.setText(y)
            } else {
                Toast.makeText(this, "Solo valores numericos", Toast.LENGTH_LONG).show()
            }
        } else if (requestCode == 1 && resultCode == RESULT_OK) {
            generateImage()
        }
    }

    private fun generateImage() {
        val image: Observable<Boolean> = Util.generateImageAsync(direction, fechaAsignacion)
        image.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Boolean> {
                    override fun onComplete() {
                        Log.i("PHOTO", "EXITOSO")
                    }

                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onNext(t: Boolean) {
                        if (t) {
                            saveRegistro("1", 2)
                            saveDetalleFotoInspeccion(envioId!!, nameImg)
//                            dialogSpinner(4)
                        }
                    }

                    override fun onError(e: Throwable) {
                        Log.e("ERROR PHOTO", e.toString())
                        Util.toastMensaje(this@SuministroAfterActivity, "Volver a intentarlo")
                    }
                })
    }

    private fun saveDetalleFotoInspeccion(i: Int, nameImg: String) {
        val gps = Gps(this)
        if (gps.isLocationEnabled()) {
            if (gps.latitude.toString() == "0.0" || gps.longitude.toString() == "0.0") {
                gps.showAlert(this)
            } else {
                val photo: Photo? = Photo(photoImp.getPhotoIdentity(), 0, i, nameImg, Util.getFechaActual(), tipo, 1, gps.latitude.toString(), gps.longitude.toString())
                photoImp.save(photo!!)
                fotoConfirmacion = 1
            }
        } else {
            gps.showSettingsAlert(this@SuministroAfterActivity)
        }
    }
}