package com.gesdes.acmarket.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gesdes.acmarket.R;
import com.gesdes.acmarket.model.TiposTiendasModel;
import com.gesdes.acmarket.utils.RoundedTransformation;
import com.squareup.picasso.Picasso;

import java.util.List;

public class TiposTiendaAdapter extends BaseAdapter {

    private  Context mContext=null;
    private  List<TiposTiendasModel>listaTipos=null;

    public TiposTiendaAdapter(Context context1,List<TiposTiendasModel>lista){
        this.mContext=context1;
        this.listaTipos=lista;
    }

    public void setListaTipos(List<TiposTiendasModel>lista){
        this.listaTipos=lista;
    }

    @Override
    public int getCount() {
        return listaTipos.size();
    }

    @Override
    public Object getItem(int i) {
        return listaTipos.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        TiposTiendasModel tipo=listaTipos.get(i);

        if (view == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            view = layoutInflater.inflate(R.layout.item_tienda_departamentos, null);
        }

        //TextView tvNombre=view.findViewById(R.id.tvNombreTiendaItem);
        ImageView ivTipo=view.findViewById(R.id.ivTiendaItem_1);
        Picasso.with(mContext).load(tipo.IMAGEN).transform(new RoundedTransformation(20, 0)).fit().placeholder(R.drawable.iv_placeholder)
                .error(R.drawable.iv_placeholder).into(ivTipo);
        //ivTipo.setImageURI(Uri.parse(tipo.IMAGEN));

        //tvNombre.setText(tipo.TIPO);
        return view;
    }
}
