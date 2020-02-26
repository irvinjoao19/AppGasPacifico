package com.quavii.dsige.lectura.data.viewModel

import android.os.Environment
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException
import com.quavii.dsige.lectura.data.apiServices.ApiServices
import com.quavii.dsige.lectura.data.apiServices.ConexionRetrofit
import com.quavii.dsige.lectura.data.dao.interfaces.*
import com.quavii.dsige.lectura.data.dao.overMethod.*
import com.quavii.dsige.lectura.data.model.GrandesClientes
import com.quavii.dsige.lectura.data.model.Marca
import com.quavii.dsige.lectura.data.model.Mensaje
import com.quavii.dsige.lectura.helper.MessageError
import com.quavii.dsige.lectura.helper.Util
import io.reactivex.CompletableObserver
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import io.realm.RealmResults
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Converter
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

class ClienteViewModel : ViewModel() {

    val mensajeError: MutableLiveData<String> = MutableLiveData()
    val mensajeSuccess: MutableLiveData<String> = MutableLiveData()

    lateinit var realm: Realm
    lateinit var registroImp: RegistroImplementation
    lateinit var suministroImp: SuministroImplementation
    lateinit var apiServices: ApiServices

    fun initialRealm() {
        realm = Realm.getDefaultInstance()
        registroImp = RegistroOver(realm)
        suministroImp = SuministroOver(realm)
        apiServices = ConexionRetrofit.api.create(ApiServices::class.java)
    }

    fun setError(s: String) {
        mensajeError.value = s
    }

    fun getGrandesClientes(): RealmResults<GrandesClientes> {
        return suministroImp.getGrandesClientes()
    }

    fun getClienteById(id: Int): GrandesClientes {
        return suministroImp.getClienteById(id)
    }

    fun getMarca(): RealmResults<Marca> {
        return suministroImp.getMarca()
    }

    fun getNameMarcaById(id: Int): String {
        return suministroImp.getMarcaById(id).nombre
    }

    // camara del cliente
    fun validateCliente1(c: GrandesClientes): Boolean {
        if (c.fechaRegistroInicio.isEmpty() || c.fechaRegistroInicio == "01/01/1900") {
            mensajeError.value = "Iniciar fecha de Inicio"
            return false
        }

        if (c.clientePermiteAcceso.isEmpty()) {
            mensajeError.value = "Seleccione tipo de cliente"
            return false
        }
        return true
    }

    // primera camara cuando el cliente no permite acceso
    fun validateCliente2(c: GrandesClientes): Boolean {
        if (c.fechaRegistroInicio.isEmpty() || c.fechaRegistroInicio == "01/01/1900") {
            mensajeError.value = "Iniciar fecha de Inicio"
            return false
        }

        if (c.clientePermiteAcceso.isEmpty()) {
            mensajeError.value = "Seleccione tipo de cliente"
            return false
        }

        if (c.porMezclaExplosiva.isEmpty()) {
            mensajeError.value = "Ingresar Mezcla explosiva."
            return false
        }

        if (c.vManoPresionEntrada.isEmpty()) {
            mensajeError.value = "Ingresar Valor manometro presión entrada."
            return false
        }
        return true
    }

    // 2da camara
    fun validateCliente3(c: GrandesClientes): Boolean {

        if (c.fechaRegistroInicio.isEmpty() || c.fechaRegistroInicio == "01/01/1900") {
            mensajeError.value = "Iniciar fecha de Inicio"
            return false
        }

        if (c.clientePermiteAcceso.isEmpty()) {
            mensajeError.value = "Seleccione tipo de cliente"
            return false
        }

        if (c.porMezclaExplosiva.isEmpty()) {
            mensajeError.value = "Ingresar Mezcla explosiva."
            return false
        }

        if (c.vManoPresionEntrada.isEmpty()) {
            mensajeError.value = "Ingresar Valor manometro presión entrada."
            return false
        }

        if (c.vManoPresionEntrada.isNotEmpty()) {
            if (c.fotovManoPresionEntrada.isEmpty()) {
                mensajeError.value = "Tomar foto valor manometro presión entrada."
                return false
            }
        }

        if (c.marcaCorrectorId == 0) {
            mensajeError.value = "Ingresar Marca de corrector."
            return false
        }

        if (c.vVolumenSCorreMedidor.isEmpty()) {
            mensajeError.value = "Ingresar valor de volumen sin corregir del medidor."
            return false
        }

        return true
    }

    // 3era camara
    fun validateCliente4(c: GrandesClientes): Boolean {

        if (c.fechaRegistroInicio.isEmpty() || c.fechaRegistroInicio == "01/01/1900") {
            mensajeError.value = "Iniciar fecha de Inicio"
            return false
        }

        if (c.clientePermiteAcceso.isEmpty()) {
            mensajeError.value = "Seleccione tipo de cliente"
            return false
        }

        if (c.porMezclaExplosiva.isEmpty()) {
            mensajeError.value = "Ingresar Mezcla explosiva."
            return false
        }

        if (c.vManoPresionEntrada.isEmpty()) {
            mensajeError.value = "Ingresar Valor manometro presión entrada."
            return false
        }

        if (c.marcaCorrectorId == 0) {
            mensajeError.value = "Ingresar Marca de corrector."
            return false
        }

        if (c.vVolumenSCorreMedidor.isEmpty()) {
            mensajeError.value = "Ingresar valor de volumen sin corregir del medidor."
            return false
        }

        if (c.vVolumenSCorreMedidor.isNotEmpty()) {
            if (c.fotovVolumenSCorreMedidor.isEmpty()) {
                mensajeError.value = "Ingresar foto de valor de volumen sin corregir del medidor."
                return false
            }
        }

        if (c.vVolumenSCorreUC.isEmpty()) {
            mensajeError.value = "Ingresar valor de volumen sin corregir de la unidad correctora."
            return false
        }

        val a = c.vVolumenSCorreMedidor.toInt() - c.vVolumenSCorreUC.toInt()
        if (a <= 0 || a > 10) {
            if (c.confirmarVolumenSCorreUC != c.vVolumenSCorreUC) {
                mensajeError.value = "Confirmar lectura"
                return false
            }
        }

        return true
    }

    // 4ta camara
    fun validateCliente5(c: GrandesClientes): Boolean {

        if (c.fechaRegistroInicio.isEmpty() || c.fechaRegistroInicio == "01/01/1900") {
            mensajeError.value = "Iniciar fecha de Inicio"
            return false
        }

        if (c.porMezclaExplosiva.isEmpty()) {
            mensajeError.value = "Ingresar Mezcla explosiva."
            return false
        }

        if (c.vManoPresionEntrada.isEmpty()) {
            mensajeError.value = "Ingresar Valor manometro presión entrada."
            return false
        }

        if (c.vManoPresionEntrada.isNotEmpty()) {
            if (c.fotovManoPresionEntrada.isEmpty()) {
                mensajeError.value = "Ingresar foto valor manometro presión entrada."
                return false
            }
        }

        if (c.marcaCorrectorId == 0) {
            mensajeError.value = "Ingresar Marca de corrector."
            return false
        }

        if (c.vVolumenSCorreMedidor.isEmpty()) {
            mensajeError.value = "Ingresar valor de volumen sin corregir del medidor."
            return false
        }

        if (c.vVolumenSCorreMedidor.isNotEmpty()) {
            if (c.fotovVolumenSCorreMedidor.isEmpty()) {
                mensajeError.value = "Ingresar foto de valor de volumen sin corregir del medidor."
                return false
            }
        }

        if (c.vVolumenSCorreUC.isEmpty()) {
            mensajeError.value = "Ingresar valor de volumen sin corregir de la unidad correctora."
            return false
        }

        val a = c.vVolumenSCorreMedidor.toInt() - c.vVolumenSCorreUC.toInt()
        if (a <= 0 || a > 10) {
            if (c.confirmarVolumenSCorreUC != c.vVolumenSCorreUC) {
                mensajeError.value = "Confirmar lectura"
                return false
            }
        }

        if (c.vVolumenSCorreUC.isNotEmpty()) {
            if (c.fotovVolumenSCorreUC.isEmpty()) {
                mensajeError.value = "Ingresar foto valor de volumen sin corregir de la unidad correctora."
                return false
            }
        }

        if (c.vVolumenRegUC.isEmpty()) {
            mensajeError.value = "Ingresar valor de volumen registrador de la unidad correctora."
            return false
        }
        return true
    }

    // 5ta camara
    fun validateCliente6(c: GrandesClientes): Boolean {

        if (c.fechaRegistroInicio.isEmpty() || c.fechaRegistroInicio == "01/01/1900") {
            mensajeError.value = "Iniciar fecha de Inicio"
            return false
        }

        if (c.porMezclaExplosiva.isEmpty()) {
            mensajeError.value = "Ingresar Mezcla explosiva."
            return false
        }

        if (c.vManoPresionEntrada.isEmpty()) {
            mensajeError.value = "Ingresar Valor manometro presión entrada."
            return false
        }

        if (c.vManoPresionEntrada.isNotEmpty()) {
            if (c.fotovManoPresionEntrada.isEmpty()) {
                mensajeError.value = "Ingresar foto valor manometro presión entrada."
                return false
            }
        }

        if (c.marcaCorrectorId == 0) {
            mensajeError.value = "Ingresar Marca de corrector."
            return false
        }

        if (c.vVolumenSCorreMedidor.isEmpty()) {
            mensajeError.value = "Ingresar valor de volumen sin corregir del medidor."
            return false
        }

        if (c.vVolumenSCorreMedidor.isNotEmpty()) {
            if (c.fotovVolumenSCorreMedidor.isEmpty()) {
                mensajeError.value = "Ingresar foto de valor de volumen sin corregir del medidor."
                return false
            }
        }

        if (c.vVolumenSCorreUC.isEmpty()) {
            mensajeError.value = "Ingresar valor de volumen sin corregir de la unidad correctora."
            return false
        }

        val a = c.vVolumenSCorreMedidor.toInt() - c.vVolumenSCorreUC.toInt()
        if (a <= 0 || a > 10) {
            if (c.confirmarVolumenSCorreUC != c.vVolumenSCorreUC) {
                mensajeError.value = "Confirmar lectura"
                return false
            }
        }

        if (c.vVolumenSCorreUC.isNotEmpty()) {
            if (c.fotovVolumenSCorreUC.isEmpty()) {
                mensajeError.value = "Ingresar foto valor de volumen sin corregir de la unidad correctora."
                return false
            }
        }

        if (c.vVolumenRegUC.isEmpty()) {
            mensajeError.value = "Ingresar valor de volumen registrador de la unidad correctora."
            return false
        }

        if (c.vVolumenRegUC.isNotEmpty()) {
            if (c.fotovVolumenRegUC.isEmpty()) {
                mensajeError.value = "Ingresar foto valor de volumen registrador de la unidad correctora."
                return false
            }
        }

        if (c.vPresionMedicionUC.isEmpty()) {
            mensajeError.value = "Ingresar valor de la presión de medición de uc."
            return false
        }
        return true
    }

    // 6ta Camara
    fun validateCliente7(c: GrandesClientes): Boolean {

        if (c.fechaRegistroInicio.isEmpty() || c.fechaRegistroInicio == "01/01/1900") {
            mensajeError.value = "Iniciar fecha de Inicio"
            return false
        }

        if (c.porMezclaExplosiva.isEmpty()) {
            mensajeError.value = "Ingresar Mezcla explosiva."
            return false
        }

        if (c.vManoPresionEntrada.isEmpty()) {
            mensajeError.value = "Ingresar Valor manometro presión entrada."
            return false
        }

        if (c.vManoPresionEntrada.isNotEmpty()) {
            if (c.fotovManoPresionEntrada.isEmpty()) {
                mensajeError.value = "Ingresar foto valor manometro presión entrada."
                return false
            }
        }

        if (c.marcaCorrectorId == 0) {
            mensajeError.value = "Ingresar Marca de corrector."
            return false
        }

        if (c.vVolumenSCorreMedidor.isEmpty()) {
            mensajeError.value = "Ingresar valor de volumen sin corregir del medidor."
            return false
        }

        if (c.vVolumenSCorreMedidor.isNotEmpty()) {
            if (c.fotovVolumenSCorreMedidor.isEmpty()) {
                mensajeError.value = "Ingresar foto de valor de volumen sin corregir del medidor."
                return false
            }
        }

        if (c.vVolumenSCorreUC.isEmpty()) {
            mensajeError.value = "Ingresar valor de volumen sin corregir de la unidad correctora."
            return false
        }

        val a = c.vVolumenSCorreMedidor.toInt() - c.vVolumenSCorreUC.toInt()
        if (a <= 0 || a > 10) {
            if (c.confirmarVolumenSCorreUC != c.vVolumenSCorreUC) {
                mensajeError.value = "Confirmar lectura"
                return false
            }
        }

        if (c.vVolumenSCorreUC.isNotEmpty()) {
            if (c.fotovVolumenSCorreUC.isEmpty()) {
                mensajeError.value = "Ingresar foto valor de volumen sin corregir de la unidad correctora."
                return false
            }
        }

        if (c.vVolumenRegUC.isEmpty()) {
            mensajeError.value = "Ingresar valor de volumen registrador de la unidad correctora."
            return false
        }

        if (c.vVolumenRegUC.isNotEmpty()) {
            if (c.fotovVolumenRegUC.isEmpty()) {
                mensajeError.value = "Ingresar foto valor de volumen registrador de la unidad correctora."
                return false
            }
        }

        if (c.vPresionMedicionUC.isEmpty()) {
            mensajeError.value = "Ingresar valor de la presión de medición de uc."
            return false
        }

        if (c.vPresionMedicionUC.isNotEmpty()) {
            if (c.fotovPresionMedicionUC.isEmpty()) {
                mensajeError.value = "Ingresar foto valor de la presión de medición de uc."
                return false
            }
        }

        if (c.vTemperaturaMedicionUC.isEmpty()) {
            mensajeError.value = "Ingresar valor de la temperatura de medicion de la uc."
            return false
        }
        return true
    }

    // 7ta camara
    fun validateCliente8(c: GrandesClientes): Boolean {

        if (c.fechaRegistroInicio.isEmpty() || c.fechaRegistroInicio == "01/01/1900") {
            mensajeError.value = "Iniciar fecha de Inicio"
            return false
        }

        if (c.porMezclaExplosiva.isEmpty()) {
            mensajeError.value = "Ingresar Mezcla explosiva."
            return false
        }

        if (c.vManoPresionEntrada.isEmpty()) {
            mensajeError.value = "Ingresar Valor manometro presión entrada."
            return false
        }

        if (c.vManoPresionEntrada.isNotEmpty()) {
            if (c.fotovManoPresionEntrada.isEmpty()) {
                mensajeError.value = "Ingresar foto valor manometro presión entrada."
                return false
            }
        }

        if (c.marcaCorrectorId == 0) {
            mensajeError.value = "Ingresar Marca de corrector."
            return false
        }

        if (c.vVolumenSCorreMedidor.isEmpty()) {
            mensajeError.value = "Ingresar valor de volumen sin corregir del medidor."
            return false
        }

        if (c.vVolumenSCorreMedidor.isNotEmpty()) {
            if (c.fotovVolumenSCorreMedidor.isEmpty()) {
                mensajeError.value = "Ingresar foto de valor de volumen sin corregir del medidor."
                return false
            }
        }

        if (c.vVolumenSCorreUC.isEmpty()) {
            mensajeError.value = "Ingresar valor de volumen sin corregir de la unidad correctora."
            return false
        }

        val a = c.vVolumenSCorreMedidor.toInt() - c.vVolumenSCorreUC.toInt()
        if (a <= 0 || a > 10) {
            if (c.confirmarVolumenSCorreUC != c.vVolumenSCorreUC) {
                mensajeError.value = "Confirmar lectura"
                return false
            }
        }

        if (c.vVolumenSCorreUC.isNotEmpty()) {
            if (c.fotovVolumenSCorreUC.isEmpty()) {
                mensajeError.value = "Ingresar foto valor de volumen sin corregir de la unidad correctora."
                return false
            }
        }

        if (c.vVolumenRegUC.isEmpty()) {
            mensajeError.value = "Ingresar valor de volumen registrador de la unidad correctora."
            return false
        }

        if (c.vVolumenRegUC.isNotEmpty()) {
            if (c.fotovVolumenRegUC.isEmpty()) {
                mensajeError.value = "Ingresar foto valor de volumen registrador de la unidad correctora."
                return false
            }
        }

        if (c.vPresionMedicionUC.isEmpty()) {
            mensajeError.value = "Ingresar valor de la presión de medición de uc."
            return false
        }

        if (c.vPresionMedicionUC.isNotEmpty()) {
            if (c.fotovPresionMedicionUC.isEmpty()) {
                mensajeError.value = "Ingresar foto valor de la presión de medición de uc."
                return false
            }
        }

        if (c.vTemperaturaMedicionUC.isEmpty()) {
            mensajeError.value = "Ingresar valor de la temperatura de medicion de la uc."
            return false
        }

        if (c.marcaCorrectorId != 1) {
            if (c.fotovTemperaturaMedicionUC.isEmpty()) {
                mensajeError.value = "Tomar foto de la temperatura de medicion de la uc."
                return false
            }
        }

        if (c.tiempoVidaBateria.isEmpty()) {
            mensajeError.value = "Ingresar tiempo de vida de la bateria."
            return false
        }

        return true
    }

    // 8ta camara
    fun validateCliente9(c: GrandesClientes): Boolean {

        if (c.fechaRegistroInicio.isEmpty() || c.fechaRegistroInicio == "01/01/1900") {
            mensajeError.value = "Iniciar fecha de Inicio"
            return false
        }

        if (c.porMezclaExplosiva.isEmpty()) {
            mensajeError.value = "Ingresar Mezcla explosiva."
            return false
        }

        if (c.vManoPresionEntrada.isEmpty()) {
            mensajeError.value = "Ingresar Valor manometro presión entrada."
            return false
        }

        if (c.vManoPresionEntrada.isNotEmpty()) {
            if (c.fotovManoPresionEntrada.isEmpty()) {
                mensajeError.value = "Ingresar foto valor manometro presión entrada."
                return false
            }
        }

        if (c.marcaCorrectorId == 0) {
            mensajeError.value = "Ingresar Marca de corrector."
            return false
        }

        if (c.vVolumenSCorreMedidor.isEmpty()) {
            mensajeError.value = "Ingresar valor de volumen sin corregir del medidor."
            return false
        }

        if (c.vVolumenSCorreMedidor.isNotEmpty()) {
            if (c.fotovVolumenSCorreMedidor.isEmpty()) {
                mensajeError.value = "Ingresar foto de valor de volumen sin corregir del medidor."
                return false
            }
        }

        if (c.vVolumenSCorreUC.isEmpty()) {
            mensajeError.value = "Ingresar valor de volumen sin corregir de la unidad correctora."
            return false
        }

        val a = c.vVolumenSCorreMedidor.toInt() - c.vVolumenSCorreUC.toInt()
        if (a <= 0 || a > 10) {
            if (c.confirmarVolumenSCorreUC != c.vVolumenSCorreUC) {
                mensajeError.value = "Confirmar lectura"
                return false
            }
        }

        if (c.vVolumenSCorreUC.isNotEmpty()) {
            if (c.fotovVolumenSCorreUC.isEmpty()) {
                mensajeError.value = "Ingresar foto valor de volumen sin corregir de la unidad correctora."
                return false
            }
        }

        if (c.vVolumenRegUC.isEmpty()) {
            mensajeError.value = "Ingresar valor de volumen registrador de la unidad correctora."
            return false
        }

        if (c.vVolumenRegUC.isNotEmpty()) {
            if (c.fotovVolumenRegUC.isEmpty()) {
                mensajeError.value = "Ingresar foto valor de volumen registrador de la unidad correctora."
                return false
            }
        }

        if (c.vPresionMedicionUC.isEmpty()) {
            mensajeError.value = "Ingresar valor de la presión de medición de uc."
            return false
        }

        if (c.vPresionMedicionUC.isNotEmpty()) {
            if (c.fotovPresionMedicionUC.isEmpty()) {
                mensajeError.value = "Ingresar foto valor de la presión de medición de uc."
                return false
            }
        }

        if (c.vTemperaturaMedicionUC.isEmpty()) {
            mensajeError.value = "Ingresar valor de la temperatura de medicion de la uc."
            return false
        }

        if (c.marcaCorrectorId != 1) {
            if (c.fotovTemperaturaMedicionUC.isEmpty()) {
                mensajeError.value = "Tomar foto de la temperatura de medicion de la uc."
                return false
            }
        }

        if (c.tiempoVidaBateria.isEmpty()) {
            mensajeError.value = "Ingresar tiempo de vida de la bateria."
            return false
        }

        if (c.marcaCorrectorId != 1) {
            if (c.fotoTiempoVidaBateria.isEmpty()) {
                mensajeError.value = "Tomar foto tiempo de vida de la bateria."
                return false
            }
        }
        return true
    }

    fun validateCliente10(c: GrandesClientes): Boolean {

        if (c.fechaRegistroInicio.isEmpty() || c.fechaRegistroInicio == "01/01/1900") {
            mensajeError.value = "Iniciar fecha de Inicio"
            return false
        }

        if (c.clientePermiteAcceso.isEmpty()) {
            mensajeError.value = "Seleccione tipo de cliente"
            return false
        }

        if (c.clientePermiteAcceso == "NO") {
            if (c.fotoConstanciaPermiteAcceso.isEmpty()) {
                mensajeError.value = "Tomar foto del cliente no permite acceso"
                return false
            }
        } else {
            if (c.porMezclaExplosiva.isEmpty()) {
                mensajeError.value = "Ingresar Mezcla explosiva."
                return false
            }

            if (c.vManoPresionEntrada.isEmpty()) {
                mensajeError.value = "Ingresar Valor manometro presión entrada."
                return false
            }

            if (c.vManoPresionEntrada.isNotEmpty()) {
                if (c.fotovManoPresionEntrada.isEmpty()) {
                    mensajeError.value = "Ingresar foto valor manometro presión entrada."
                    return false
                }
            }

            if (c.marcaCorrectorId == 0) {
                mensajeError.value = "Ingresar Marca de corrector."
                return false
            }

            if (c.vVolumenSCorreMedidor.isEmpty()) {
                mensajeError.value = "Ingresar valor de volumen sin corregir del medidor."
                return false
            }

            if (c.vVolumenSCorreMedidor.isNotEmpty()) {
                if (c.fotovVolumenSCorreMedidor.isEmpty()) {
                    mensajeError.value = "Ingresar foto de valor de volumen sin corregir del medidor."
                    return false
                }
            }

            if (c.vVolumenSCorreUC.isEmpty()) {
                mensajeError.value = "Ingresar valor de volumen sin corregir de la unidad correctora."
                return false
            }

            val a = c.vVolumenSCorreMedidor.toInt() - c.vVolumenSCorreUC.toInt()
            if (a <= 0 || a > 10) {
                if (c.confirmarVolumenSCorreUC != c.vVolumenSCorreUC) {
                    mensajeError.value = "Confirmar lectura"
                    return false
                }
            }

            if (c.vVolumenSCorreUC.isNotEmpty()) {
                if (c.fotovVolumenSCorreUC.isEmpty()) {
                    mensajeError.value = "Ingresar foto valor de volumen sin corregir de la unidad correctora."
                    return false
                }
            }

            if (c.vVolumenRegUC.isEmpty()) {
                mensajeError.value = "Ingresar valor de volumen registrador de la unidad correctora."
                return false
            }

            if (c.vVolumenRegUC.isNotEmpty()) {
                if (c.fotovVolumenRegUC.isEmpty()) {
                    mensajeError.value = "Ingresar foto valor de volumen registrador de la unidad correctora."
                    return false
                }
            }

            if (c.vPresionMedicionUC.isEmpty()) {
                mensajeError.value = "Ingresar valor de la presión de medición de uc."
                return false
            }

            if (c.vPresionMedicionUC.isNotEmpty()) {
                if (c.fotovPresionMedicionUC.isEmpty()) {
                    mensajeError.value = "Ingresar foto valor de la presión de medición de uc."
                    return false
                }
            }

            if (c.vTemperaturaMedicionUC.isEmpty()) {
                mensajeError.value = "Ingresar valor de la temperatura de medicion de la uc."
                return false
            }

            if (c.marcaCorrectorId != 1) {
                if (c.fotovTemperaturaMedicionUC.isEmpty()) {
                    mensajeError.value = "Tomar foto de la temperatura de medicion de la uc."
                    return false
                }
            }

            if (c.tiempoVidaBateria.isEmpty()) {
                mensajeError.value = "Ingresar tiempo de vida de la bateria."
                return false
            }

            if (c.marcaCorrectorId != 1) {
                if (c.fotoTiempoVidaBateria.isEmpty()) {
                    mensajeError.value = "Tomar foto tiempo de vida de la bateria."
                    return false
                }
            }

            if (c.fotoPanomarica.isEmpty()) {
                mensajeError.value = "Ingresar foto panoramica."
                return false
            }

            if (c.tieneGabinete.isEmpty()) {
                mensajeError.value = "Ingresar tiene gabinete de temometria."
                return false
            }
        }

        return true
    }

    fun validateCliente11(c: GrandesClientes): Boolean {

        if (c.fechaRegistroInicio.isEmpty() || c.fechaRegistroInicio == "01/01/1900") {
            mensajeError.value = "Iniciar fecha de Inicio"
            return false
        }

        if (c.clientePermiteAcceso.isEmpty()) {
            mensajeError.value = "Seleccione tipo de cliente"
            return false
        }

        if (c.clientePermiteAcceso == "NO") {
            if (c.fotoConstanciaPermiteAcceso.isEmpty()) {
                mensajeError.value = "Tomar foto del cliente no permite acceso"
                return false
            }
        } else {
            if (c.porMezclaExplosiva.isEmpty()) {
                mensajeError.value = "Ingresar Mezcla explosiva."
                return false
            }

            if (c.vManoPresionEntrada.isEmpty()) {
                mensajeError.value = "Ingresar Valor manometro presión entrada."
                return false
            }

            if (c.vManoPresionEntrada.isNotEmpty()) {
                if (c.fotovManoPresionEntrada.isEmpty()) {
                    mensajeError.value = "Ingresar foto valor manometro presión entrada."
                    return false
                }
            }

            if (c.marcaCorrectorId == 0) {
                mensajeError.value = "Ingresar Marca de corrector."
                return false
            }

            if (c.vVolumenSCorreMedidor.isEmpty()) {
                mensajeError.value = "Ingresar valor de volumen sin corregir del medidor."
                return false
            }

            if (c.vVolumenSCorreMedidor.isNotEmpty()) {
                if (c.fotovVolumenSCorreMedidor.isEmpty()) {
                    mensajeError.value = "Ingresar foto de valor de volumen sin corregir del medidor."
                    return false
                }
            }

            if (c.vVolumenSCorreUC.isEmpty()) {
                mensajeError.value = "Ingresar valor de volumen sin corregir de la unidad correctora."
                return false
            }

            val a = c.vVolumenSCorreMedidor.toInt() - c.vVolumenSCorreUC.toInt()
            if (a <= 0 || a > 10) {
                if (c.confirmarVolumenSCorreUC != c.vVolumenSCorreUC) {
                    mensajeError.value = "Confirmar lectura"
                    return false
                }
            }

            if (c.vVolumenSCorreUC.isNotEmpty()) {
                if (c.fotovVolumenSCorreUC.isEmpty()) {
                    mensajeError.value = "Ingresar foto valor de volumen sin corregir de la unidad correctora."
                    return false
                }
            }

            if (c.vVolumenRegUC.isEmpty()) {
                mensajeError.value = "Ingresar valor de volumen registrador de la unidad correctora."
                return false
            }

            if (c.vVolumenRegUC.isNotEmpty()) {
                if (c.fotovVolumenRegUC.isEmpty()) {
                    mensajeError.value = "Ingresar foto valor de volumen registrador de la unidad correctora."
                    return false
                }
            }

            if (c.vPresionMedicionUC.isEmpty()) {
                mensajeError.value = "Ingresar valor de la presión de medición de uc."
                return false
            }

            if (c.vPresionMedicionUC.isNotEmpty()) {
                if (c.fotovPresionMedicionUC.isEmpty()) {
                    mensajeError.value = "Ingresar foto valor de la presión de medición de uc."
                    return false
                }
            }

            if (c.vTemperaturaMedicionUC.isEmpty()) {
                mensajeError.value = "Ingresar valor de la temperatura de medicion de la uc."
                return false
            }

            if (c.marcaCorrectorId != 1) {
                if (c.fotovTemperaturaMedicionUC.isEmpty()) {
                    mensajeError.value = "Tomar foto de la temperatura de medicion de la uc."
                    return false
                }
            }

            if (c.tiempoVidaBateria.isEmpty()) {
                mensajeError.value = "Ingresar tiempo de vida de la bateria."
                return false
            }

            if (c.marcaCorrectorId != 1) {
                if (c.fotoTiempoVidaBateria.isEmpty()) {
                    mensajeError.value = "Tomar foto tiempo de vida de la bateria."
                    return false
                }
            }

            if (c.fotoPanomarica.isEmpty()) {
                mensajeError.value = "Ingresar foto panoramica."
                return false
            }

            if (c.tieneGabinete.isEmpty()) {
                mensajeError.value = "Ingresar tiene gabinete de temometria."
                return false
            }

            if (c.tieneGabinete == "SI") {
                if (c.foroSitieneGabinete.isEmpty()) {
                    mensajeError.value = "Tomar Foto del Cabinete."
                    return false
                }
            }

            if (c.presenteCliente.isEmpty()) {
                mensajeError.value = "Ingresar si presenta cliente."
                return false
            }

            if (c.presenteCliente == "SI") {
                if (c.contactoCliente.isEmpty()) {
                    mensajeError.value = "Ingresar nombre del cliente"
                    return false
                }
            }
        }

        return true
    }

    fun updateCliente(c: GrandesClientes, mensaje: String) {
        registroImp.updateClientes(c)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : CompletableObserver {
                    override fun onComplete() {
                        if (mensaje == "Cliente Actualizado") {
                            sendCliente(c.clienteId, mensaje)
                        } else {
                            mensajeSuccess.value = mensaje
                        }
                    }

                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onError(e: Throwable) {
                        mensajeError.value = e.toString()
                    }
                })
    }

    private fun sendCliente(clienteId: Int, mensaje: String) {
        val auditorias = registroImp.getClienteById(clienteId)
        auditorias.flatMap { c ->
            val realm = Realm.getDefaultInstance()
            val b = MultipartBody.Builder()
            if (c.clientePermiteAcceso == "NO") {
                val file = File(Environment.getExternalStorageDirectory().toString() + "/" + Util.FolderImg + "/" + c.fotoConstanciaPermiteAcceso)
                if (file.exists()) {
                    b.addFormDataPart("fotos", file.name, RequestBody.create(MediaType.parse("multipart/form-data"), file))
                }
            } else {
                if (c.fotovManoPresionEntrada.isNotEmpty()) {
                    val file = File(Environment.getExternalStorageDirectory().toString() + "/" + Util.FolderImg + "/" + c.fotovManoPresionEntrada)
                    if (file.exists()) {
                        b.addFormDataPart("fotos", file.name, RequestBody.create(MediaType.parse("multipart/form-data"), file))
                    }
                }
                if (c.fotovVolumenSCorreUC.isNotEmpty()) {
                    val file2 = File(Environment.getExternalStorageDirectory().toString() + "/" + Util.FolderImg + "/" + c.fotovVolumenSCorreUC)
                    if (file2.exists()) {
                        b.addFormDataPart("fotos", file2.name, RequestBody.create(MediaType.parse("multipart/form-data"), file2))
                    }
                }
                if (c.fotovVolumenSCorreMedidor.isNotEmpty()) {
                    val file3 = File(Environment.getExternalStorageDirectory().toString() + "/" + Util.FolderImg + "/" + c.fotovVolumenSCorreMedidor)
                    if (file3.exists()) {
                        b.addFormDataPart("fotos", file3.name, RequestBody.create(MediaType.parse("multipart/form-data"), file3))
                    }
                }
                if (c.fotovVolumenRegUC.isNotEmpty()) {
                    val file4 = File(Environment.getExternalStorageDirectory().toString() + "/" + Util.FolderImg + "/" + c.fotovVolumenRegUC)
                    if (file4.exists()) {
                        b.addFormDataPart("fotos", file4.name, RequestBody.create(MediaType.parse("multipart/form-data"), file4))
                    }
                }
                if (c.fotovPresionMedicionUC.isNotEmpty()) {
                    val file5 = File(Environment.getExternalStorageDirectory().toString() + "/" + Util.FolderImg + "/" + c.fotovPresionMedicionUC)
                    if (file5.exists()) {
                        b.addFormDataPart("fotos", file5.name, RequestBody.create(MediaType.parse("multipart/form-data"), file5))
                    }
                }
                if (c.fotoTiempoVidaBateria.isNotEmpty()) {
                    val file6 = File(Environment.getExternalStorageDirectory().toString() + "/" + Util.FolderImg + "/" + c.fotoTiempoVidaBateria)
                    if (file6.exists()) {
                        b.addFormDataPart("fotos", file6.name, RequestBody.create(MediaType.parse("multipart/form-data"), file6))
                    }
                }
                if (c.fotovTemperaturaMedicionUC.isNotEmpty()) {
                    val file7 = File(Environment.getExternalStorageDirectory().toString() + "/" + Util.FolderImg + "/" + c.fotovTemperaturaMedicionUC)
                    if (file7.exists()) {
                        b.addFormDataPart("fotos", file7.name, RequestBody.create(MediaType.parse("multipart/form-data"), file7))
                    }
                }
                if (c.fotoPanomarica.isNotEmpty()) {
                    val file8 = File(Environment.getExternalStorageDirectory().toString() + "/" + Util.FolderImg + "/" + c.fotoPanomarica)
                    if (file8.exists()) {
                        b.addFormDataPart("fotos", file8.name, RequestBody.create(MediaType.parse("multipart/form-data"), file8))
                    }
                }
                if (c.foroSitieneGabinete.isNotEmpty()) {
                    val file9 = File(Environment.getExternalStorageDirectory().toString() + "/" + Util.FolderImg + "/" + c.foroSitieneGabinete)
                    if (file9.exists()) {
                        b.addFormDataPart("fotos", file9.name, RequestBody.create(MediaType.parse("multipart/form-data"), file9))
                    }
                }
            }
            val json = Gson().toJson(realm.copyFromRealm(c))
            Log.i("TAG", json)
            b.setType(MultipartBody.FORM)
            b.addFormDataPart("model", json)
            val requestBody = b.build()
            Observable.zip(Observable.just(c), apiServices.sendCliente(requestBody), BiFunction<GrandesClientes, Mensaje, Mensaje> { registro, mensaje ->
                mensaje
            })
        }.subscribeOn(Schedulers.computation())
                .delay(600, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Mensaje> {

                    override fun onSubscribe(d: Disposable) {
                        Log.i("TAG", d.toString())
                    }

                    override fun onNext(t: Mensaje) {

                    }

                    override fun onError(t: Throwable) {
                        if (t is HttpException) {
                            val body = t.response().errorBody()
                            val errorConverter: Converter<ResponseBody, MessageError> = ConexionRetrofit.api.responseBodyConverter(MessageError::class.java, arrayOfNulls<Annotation>(0))
                            try {
                                val error = errorConverter.convert(body!!)
                                mensajeError.postValue(error.Message)
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }
                        } else {
                            mensajeError.postValue(t.toString())
                        }
                    }

                    override fun onComplete() {
                        mensajeSuccess.postValue(mensaje)
                    }
                })
    }

    fun verificateFile(c: GrandesClientes) {
        apiServices.getVerificateFile(c.clienteId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Mensaje> {
                    override fun onComplete() {

                    }

                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onNext(t: Mensaje) {
                        closeFileClienteById(c.clienteId)
                        mensajeSuccess.postValue(t.mensaje)
                    }

                    override fun onError(t: Throwable) {
                        if (t is HttpException) {
                            val body = t.response().errorBody()
                            val errorConverter: Converter<ResponseBody, MessageError> = ConexionRetrofit.api.responseBodyConverter(MessageError::class.java, arrayOfNulls<Annotation>(0))
                            try {
                                val error = errorConverter.convert(body!!)
                                mensajeError.postValue(error.Message)
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }
                        } else {
                            mensajeError.postValue(t.toString())
                        }
                    }

                })
    }

    private fun closeFileClienteById(id: Int) {
        registroImp.closeFileClienteById(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : CompletableObserver {
                    override fun onComplete() {
                        Log.i("TAG", "File Actualizado")
                    }

                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onError(e: Throwable) {

                    }
                })
    }
}