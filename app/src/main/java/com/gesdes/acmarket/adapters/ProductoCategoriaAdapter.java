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

import androidx.core.app.BundleCompat;

import com.gesdes.acmarket.R;
import com.gesdes.acmarket.activities.AgregarProductoActivity;
import com.gesdes.acmarket.activities.CategoriasActivity;
import com.gesdes.acmarket.activities.ProductosListActivity;
import com.gesdes.acmarket.activities.TiendasListActivity;
import com.gesdes.acmarket.model.PedidoModel;
import com.gesdes.acmarket.model.ProductosModel;
import com.gesdes.acmarket.model.TiendasModel;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ProductoCategoriaAdapter extends BaseAdapter {

    List<ProductosModel> Productos;
    Context mContext;

    public ProductoCategoriaAdapter(List<ProductosModel> productos, Context mContext) {
        Productos = productos;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return Productos.size();
    }

    @Override
    public Object getItem(int i) {
        return Productos.get(i);
    }

    @Override
    public long getItemId(int i) {
        return Long.parseLong(Productos.get(i).PK);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view ==null){
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            view = layoutInflater.inflate(R.layout.item_producto_por_categoria, null);
        }

        final ProductosModel producto=Productos.get(i);

        final ImageView ivProducto=view.findViewById(R.id.ivImagenProductoCategoriaItem);
        TextView tvTienda=view.findViewById(R.id.tvTiendaNombreProductoCategoriaItem);
        TextView tvNompre=view.findViewById(R.id.tvNombreProductoCategoriaItem);
        TextView tvPrecio=view.findViewById(R.id.tvPrecioProductoCategoriaItem);

        if(!producto.IMAGEN.isEmpty() ){
            Picasso.with(mContext).load(producto.IMAGEN).placeholder(R.drawable.iv_placeholder)
                    .error(R.drawable.iv_placeholder).into(ivProducto);
        }

        tvTienda.setText(""+producto.TIENDA);
        tvNompre.setText(""+producto.PRODUCTO);
        DecimalFormat df = new DecimalFormat("#0.00");
        tvPrecio.setText("$ "+df.format( producto.PRECIO));


        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Intent>listaActividades=new ArrayList<>();
                PedidoModel pedido=new PedidoModel();
                pedido.PK_CATEGORIA=producto.PK_CATEGORIA;
                pedido.CLASIFICACION=producto.CATEGORIA;
                pedido.PK_TIENDA=producto.PK_TIENDA;
                pedido.TIENDA=producto.TIENDA;

                TiendasModel tienda=new TiendasModel();
                tienda.PK=pedido.PK_TIENDA;
                tienda.NOMBRE=pedido.TIENDA;
                tienda.IMAGEN=pedido.IMAGEN_TIENDA;
                tienda.LUNES=producto.TIENDA_LUNES;
                tienda.MARTES=producto.TIENDA_MARTES;
                tienda.MIERCOLES=producto.TIENDA_MIERCOLES;
                tienda.JUEVES=producto.TIENDA_JUEVES;
                tienda.VIERNES=producto.TIENDA_VIERNES;
                tienda.SABADO=producto.TIENDA_SABADO;
                tienda.DOMINGO=producto.TIENDA_DOMINGO;
                tienda.ENTREGA_LUNES=producto.ENTREGA_LUNES;
                tienda.ENTREGA_MARTES=producto.ENTREGA_MARTES;
                tienda.ENTREGA_MIERCOLES=producto.ENTREGA_MIERCOLES;
                tienda.ENTREGA_JUEVES=producto.ENTREGA_JUEVES;
                tienda.ENTREGA_VIERNES=producto.ENTREGA_VIERNES;
                tienda.ENTREGA_SABADO=producto.ENTREGA_SABADO;
                tienda.ENTREGA_DOMINGO=producto.ENTREGA_DOMINGO;
                tienda.ENTREGA_EXPRESS=producto.ENTREGA_EXPRESS;

                Bundle bolsa0=new Bundle();
                bolsa0.putString("PK_TIENDA",pedido.PK_TIENDA);
                bolsa0.putString("TIENDA",producto.TIENDA);
                bolsa0.putString("IMAGEN_TIENDA",producto.IMAGEN_TIENDA);
                bolsa0.putString("PK_TIPO",producto.PK_TIPO);
                bolsa0.putString("TIPO",producto.TIPO);
                bolsa0.putString("IMAGEN_TIPO",producto.IMAGEN_TIPO);
                bolsa0.putString("tipo","Promociones");
                bolsa0.putSerializable("TIENDA_OBJ",tienda);

                Bundle bolsa1=new Bundle();
                bolsa1.putSerializable("pedido",pedido);
                bolsa1.putString("categoria",producto.CATEGORIA);
                bolsa1.putSerializable("TIENDA_OBJ",tienda);
                //Toast.makeText(context,"id "+producto.PRODUCTO,Toast.LENGTH_LONG).show();
                Bundle bolsa=new Bundle();
                bolsa.putSerializable("producto",producto);
                bolsa.putSerializable("TIENDA_OBJ",tienda);


                Bundle bolsaTiendas=new Bundle();
                bolsaTiendas.putString("PK_TIENDA",tienda.PK);
                bolsaTiendas.putString("TIENDA",tienda.NOMBRE);
                bolsaTiendas.putString("IMAGEN_TIENDA",tienda.IMAGEN);
                bolsaTiendas.putSerializable("TIENDA_OBJ",tienda);

                Intent intent=new Intent(mContext, TiendasListActivity.class);
                intent.putExtra("bolsa",bolsaTiendas);
                listaActividades.add(intent);
                mContext.startActivity(intent);

                intent=new Intent(mContext, CategoriasActivity.class);
                intent.putExtra("bolsa",bolsa0);
                listaActividades.add(intent);
                mContext.startActivity(intent);

                intent=new Intent(mContext, ProductosListActivity.class);
                intent.putExtra("bolsa",bolsa1);
                listaActividades.add(intent);
                mContext.startActivity(intent);

                intent=new Intent(mContext, AgregarProductoActivity.class);
                intent.putExtra("bolsa",bolsa);
                listaActividades.add(intent);
                mContext.startActivity(intent);


            }
        });

        return view;
    }
}
