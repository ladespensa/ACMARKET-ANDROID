package com.gesdes.acmarket.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gesdes.acmarket.R;
import com.gesdes.acmarket.model.CategoriasModel;
import com.gesdes.acmarket.model.TiendasModel;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CategoriasAdapter extends BaseAdapter {

    private Context mContext=null;
    private List<CategoriasModel> listaCategorias=null;

    public CategoriasAdapter(Context context1,List<CategoriasModel>lista){
        this.mContext=context1;
        this.listaCategorias=lista;
    }

    public void setListaTipos(List<CategoriasModel>lista){
        this.listaCategorias=lista;
    }
    @Override
    public int getCount() {
        return listaCategorias.size();
    }

    @Override
    public Object getItem(int i) {
        return listaCategorias.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        CategoriasModel categoria=listaCategorias.get(i);

        if (view == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            view = layoutInflater.inflate(R.layout.item_tienda, null);
        }

        TextView tvNombre=view.findViewById(R.id.tvNombreTiendaItem);
        ImageView ivTipo=view.findViewById(R.id.ivTiendaItem);
        if(!categoria.IMAGEN.isEmpty() ){
            Picasso.with(mContext).load(categoria.IMAGEN).placeholder(R.drawable.iv_placeholder)
                .error(R.drawable.iv_placeholder).into(ivTipo);
        }
        //ivTipo.setImageURI(Uri.parse(tipo.IMAGEN));
        tvNombre.setText(categoria.CLASIFICACION);
        return view;
    }
}
