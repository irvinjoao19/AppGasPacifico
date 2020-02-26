package com.quavii.dsige.lectura.helper;

import com.quavii.dsige.lectura.data.model.DetalleGrupo;

import java.util.List;

public class SpinnerSelection {

    public static int getSpinnerValueDetalleGrupo(List<DetalleGrupo> array, int text) {
        int index = 0;
        for (int i = 0; i < array.size(); i++) {
            if (array.get(i).getID_DetalleGrupo() == text) {
                index = i;
                break;
            }
        }
        return index;
    }

}
