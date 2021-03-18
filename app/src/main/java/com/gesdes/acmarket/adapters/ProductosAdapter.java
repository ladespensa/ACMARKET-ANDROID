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
import com.gesdes.acmarket.model.ProductosModel;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.List;

public class ProductosAdapter extends BaseAdapter {

    private Context mContext=null;
    List<ProductosModel> listaProductos=null;
    public ProductosAdapter(Context context1,List<ProductosModel>lista){
        this.mContext=context1;
        this.listaProductos=lista;
    }

    public void setListaProductos(List<ProductosModel>lista){
        this.listaProductos=lista;
    }
    @Override
    public int getCount() {
        return listaProductos.size();
    }

    @Override
    public Object getItem(int i) {
        return listaProductos.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ProductosModel producto=listaProductos.get(i);

        if (view == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            view = layoutInflater.inflate(R.layout.item_producto, null);
        }

        TextView tvNombre=view.findViewById(R.id.tvProductoNombreItem);
        TextView tvProductoPrecio=view.findViewById(R.id.tvPrecioProductoItem);
        ImageView ivTipo=view.findViewById(R.id.ivProductoImgItem);
        if(!producto.IMAGEN.isEmpty() ){
            Picasso.with(mContext).load(producto.IMAGEN).placeholder(R.drawable.iv_placeholder)
                    .error(R.drawable.iv_placeholder).into(ivTipo);
        }

        tvNombre.setText(producto.PRODUCTO);
        DecimalFormat df = new DecimalFormat("#0.00");
        tvProductoPrecio.setText("$ "+df.format( producto.PRECIO));
        return view;
    }
}
