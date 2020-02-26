package com.quavii.dsige.lectura.ui.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.quavii.dsige.lectura.data.model.TipoLectura;
import com.quavii.dsige.lectura.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SpinnerTipoLecturaAdapter extends ArrayAdapter<TipoLectura> {

    private Context context;
    private int layout;
    private List<TipoLectura> tipoLecturas;

    public SpinnerTipoLecturaAdapter(Context context, int layout, List<TipoLectura> tipoLecturas) {
        super(context, layout, tipoLecturas);
        this.context = context;
        this.layout = layout;
        this.tipoLecturas = tipoLecturas;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @NotNull
    @Override
    public View getView(int position, View view, @NonNull ViewGroup parent) {

        return getDropDownView(position, view, parent);
    }

    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
        ViewHolder vh;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(layout, null);
            vh = new ViewHolder();
            vh.Id = convertView.findViewById(R.id.textViewId);
            vh.Nombre = convertView.findViewById(R.id.textViewNombre);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        TipoLectura area = tipoLecturas.get(position);
        vh.Id.setText(String.valueOf(area.getID_TipoLectura()));
        vh.Nombre.setText(area.getTipoLectura_Descripcion());
        return convertView;
    }

    public class ViewHolder {
        TextView Id;
        TextView Nombre;
    }

}
