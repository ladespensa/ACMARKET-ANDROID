package com.gesdes.acmarket.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gesdes.acmarket.R;
import com.gesdes.acmarket.model.TiendasModel;
import com.gesdes.acmarket.utils.RoundedTransformation;
import com.squareup.picasso.Picasso;

import java.util.List;

public class TiendasAdapter extends BaseAdapter {

    private Context mContext=null;
    private List<TiendasModel> listaTiendas=null;

    public TiendasAdapter(Context context1,List<TiendasModel>lista){
        this.mContext=context1;
        this.listaTiendas=lista;
    }

    public void setListaTipos(List<TiendasModel>lista){
        this.listaTiendas=lista;
    }

    @Override
    public int getCount() {
        return listaTiendas.size();
    }

    @Override
    public Object getItem(int i) {
        return listaTiendas.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        TiendasModel tipo=listaTiendas.get(i);

        if (view == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            view = layoutInflater.inflate(R.layout.item_tienda2, null);
        }

        TextView tvNombre=view.findViewById(R.id.tvNombreTiendaItem);
        TextView tvHorario=view.findViewById(R.id.tvHorarioTiendaItem);
        ImageView ivTipo=view.findViewById(R.id.ivTienda2);
        Picasso.with(mContext).load(tipo.IMAGEN).transform(new RoundedTransformation(20, 0)).fit().placeholder(R.drawable.iv_placeholder)
                .error(R.drawable.iv_placeholder).into(ivTipo);
        //ivTipo.setImageURI(Uri.parse(tipo.IMAGEN));
        tvNombre.setText(tipo.NOMBRE);
        //tvHorario.setText("Horario: "+tipo.APERTURA+" - "+tipo.CIERRE);
        tvHorario.setText("");
        return view;
    }
}
