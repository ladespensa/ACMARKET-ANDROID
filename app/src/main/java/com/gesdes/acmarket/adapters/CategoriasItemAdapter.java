package com.gesdes.acmarket.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gesdes.acmarket.R;
import com.gesdes.acmarket.activities.ProductosPorCategoriaActivity;
import com.gesdes.acmarket.model.CategoriasModel;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CategoriasItemAdapter extends BaseAdapter {

    Context mContext;
    private List<CategoriasModel> categoriasModelsList;

    public CategoriasItemAdapter(Context mContext, List<CategoriasModel> categoriasModelsList) {
        this.mContext = mContext;
        this.categoriasModelsList = categoriasModelsList;
    }

    @Override
    public int getCount() {
        return categoriasModelsList.size();
    }

    @Override
    public Object getItem(int i) {
        return categoriasModelsList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return Long.parseLong(categoriasModelsList.get(i).PK);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final CategoriasModel detailInfo = (CategoriasModel) getItem(i);
        if (view == null) {
            LayoutInflater inf = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inf.inflate(R.layout.item_categorias_busqueda, null);
        }
        ImageView ivCategoria = (ImageView) view.findViewById(R.id.ivCategoriaItemBusqueda);
        TextView titleCategoria = (TextView) view.findViewById(R.id.tvTitleItemCategoriaBusqueda);
        titleCategoria.setText(detailInfo.CLASIFICACION);
        if(!detailInfo.IMAGEN.isEmpty() ){
            Picasso.with(mContext).load(detailInfo.IMAGEN).placeholder(R.drawable.iv_placeholder)
                    .error(R.drawable.iv_placeholder).into(ivCategoria);
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bolsa=new Bundle();
                bolsa.putSerializable("CATEGORIA",detailInfo);
                Intent intento=new Intent(mContext, ProductosPorCategoriaActivity.class);
                intento.putExtra("bolsa",bolsa);
                mContext.startActivity(intento);
                //Toast.makeText(mContext,""+detailInfo.CLASIFICACION,Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}
