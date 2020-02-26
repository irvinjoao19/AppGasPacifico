package com.quavii.dsige.lectura.ui.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;
import com.quavii.dsige.lectura.data.model.DetalleGrupo;
import com.quavii.dsige.lectura.R;

public class SpinnerDetalleGrupoAdapter extends ArrayAdapter<DetalleGrupo> {

    private Context context;
    private int layout;
    private List<DetalleGrupo> detalleGrupos;

    public SpinnerDetalleGrupoAdapter(Context context, int layout, List<DetalleGrupo> detalleGrupos) {
        super(context, layout, detalleGrupos);
        this.context = context;
        this.layout = layout;
        this.detalleGrupos = detalleGrupos;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @NonNull
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
            vh.Photo = convertView.findViewById(R.id.textViewPideFoto);
            vh.Lectura = convertView.findViewById(R.id.textViewPideLectura);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        DetalleGrupo detalles = detalleGrupos.get(position);
        vh.Id.setText(String.valueOf(detalles.getID_DetalleGrupo()));
        vh.Nombre.setText(detalles.getDescripcion());
        vh.Photo.setText(detalles.getPideFoto());
        vh.Lectura.setText(detalles.getPideLectura());
        return convertView;
    }

    public class ViewHolder {
        TextView Id;
        TextView Nombre;
        TextView Lectura;
        TextView Photo;
    }
}
