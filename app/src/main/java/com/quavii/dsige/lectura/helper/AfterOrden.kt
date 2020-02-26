package com.quavii.dsige.lectura.helper

import com.quavii.dsige.lectura.data.model.SuministroCortes
import com.quavii.dsige.lectura.data.model.SuministroLectura
import com.quavii.dsige.lectura.data.model.SuministroReconexion

object AfterOrden {

    fun getBeforeOrdenLectura(valor: Int, lecturas: List<SuministroLectura>): Int {

        var numero = 0

        if (valor != 1) {
            for (lectura: SuministroLectura in lecturas) {
                if (valor > lectura.orden) {
                    numero = lectura.orden
                }else{
                    break
                }
            }
        }

        return numero
    }

    fun getNextOrdenLectura(valor: Int, lecturas: List<SuministroLectura>): Int {

        var numero = 0

        if (valor != lecturas.size) {
            for (lectura: SuministroLectura in lecturas) {
                if (valor < lectura.orden) {
                    numero = lectura.orden
                    break
                }
            }
        }

        return numero
    }

    fun getBeforeOrdenCortes(valor: Int, cortes: List<SuministroCortes>): Int {

        var numero = 0

        if (valor != 1) {
            for (corte: SuministroCortes in cortes) {
                if (valor > corte.orden) {
                    numero = corte.orden
                }else{
                    break
                }
            }
        }

        return numero
    }

    fun getNextOrdenCortes(valor: Int, cortes: List<SuministroCortes>): Int {

        var numero = 0

        if (valor != cortes.size) {
            for (corte: SuministroCortes in cortes) {
                if (valor < corte.orden) {
                    numero = corte.orden
                    break
                }
            }
        }

        return numero
    }

    fun getBeforeOrdenReconexion(valor: Int, reconexiones: List<SuministroReconexion>): Int {

        var numero = 0

        if (valor != 1) {
            for (reconexion: SuministroReconexion in reconexiones) {
                if (valor > reconexion.orden) {
                    numero = reconexion.orden
                }else{
                    break
                }
            }
        }

        return numero
    }

    fun getNextOrdenReconexion(valor: Int, reconexiones: List<SuministroReconexion>): Int {

        var numero = 0

        if (valor != reconexiones.size) {
            for (reconexion: SuministroReconexion in reconexiones) {
                if (valor < reconexion.orden) {
                    numero = reconexion.orden
                    break
                }
            }
        }

        return numero
    }


}