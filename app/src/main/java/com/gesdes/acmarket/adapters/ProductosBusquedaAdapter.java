package com.gesdes.acmarket.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gesdes.acmarket.R;
import com.gesdes.acmarket.activities.AgregarProductoActivity;
import com.gesdes.acmarket.activities.CategoriasActivity;
import com.gesdes.acmarket.activities.ProductosListActivity;
import com.gesdes.acmarket.model.PedidoModel;
import com.gesdes.acmarket.model.ProductosModel;
import com.gesdes.acmarket.model.TiendasModel;
import com.gesdes.acmarket.utils.RoundedTransformation;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ProductosBusquedaAdapter extends RecyclerView.Adapter<ProductosBusquedaAdapter.ProductosBusquedaItemViewHolder> {

    Context mContext;
    List<ProductosModel> listaProductos;

    public ProductosBusquedaAdapter(Context mContext, List<ProductosModel> listaProductos) {
        this.mContext = mContext;
        this.listaProductos = listaProductos;
    }

    @NonNull
    @Override
    public ProductosBusquedaItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_producto,parent,false);
        return new ProductosBusquedaItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductosBusquedaAdapter.ProductosBusquedaItemViewHolder holder, int position) {
        ProductosModel producto = listaProductos.get(position);
        ImageView ivPromo=holder.itemView.findViewById(R.id.ivProductoImgItem);

        holder.bind(producto);
    }

    @Override
    public int getItemCount() {
        return listaProductos.size();
    }
    public static class ProductosBusquedaItemViewHolder extends RecyclerView.ViewHolder {
        View item;
        private ImageView image;
        private TextView tvPrecio;
        private TextView tvNombre;

        Context context;
        public ProductosBusquedaItemViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.ivProductoImgItem);
            tvPrecio = itemView.findViewById(R.id.tvPrecioProductoItem);
            tvNombre = itemView.findViewById(R.id.tvProductoNombreItem);
            this.context= itemView.getContext();
            item=itemView;

        }
        public void bind(final ProductosModel producto) {

            if(!producto.IMAGEN.isEmpty() ){
                Picasso.with(context).load(producto.IMAGEN)
                        .transform(new RoundedTransformation(20, 0))
                        .placeholder(R.drawable.iv_placeholder)
                        .error(R.drawable.iv_placeholder).into(image);
            }

            tvNombre.setText(producto.PRODUCTO);
            DecimalFormat df = new DecimalFormat("#0.00");
            tvPrecio.setText("$ "+df.format( producto.PRECIO));

            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    List<Intent>listaActividades=new ArrayList<>();

                    TiendasModel tienda=new TiendasModel();
                    tienda.PK=producto.PK_TIENDA;
                    tienda.NOMBRE=producto.TIENDA;
                    tienda.IMAGEN=producto.IMAGEN_TIENDA;
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

                    PedidoModel pedido=new PedidoModel();
                    pedido.PK_CATEGORIA=producto.PK_CATEGORIA;
                    pedido.CLASIFICACION=producto.CATEGORIA;
                    pedido.PK_TIENDA=producto.PK_TIENDA;
                    pedido.TIENDA=producto.TIENDA;
                    pedido.IMAGEN_TIENDA=producto.IMAGEN_TIENDA;
                    pedido.PK_TIPO=producto.PK_TIPO;
                    pedido.TIPO=producto.TIPO;
                    pedido.IMAGEN_TIPO=producto.IMAGEN_TIPO;

                    Bundle bolsa00=new Bundle();
                    bolsa00.putString("PK_TIENDA",pedido.PK_TIENDA);
                    bolsa00.putString("TIENDA",producto.TIENDA);
                    bolsa00.putString("IMAGEN_TIENDA",producto.IMAGEN_TIENDA);
                    bolsa00.putSerializable("TIENDA_OBJ",tienda);

                    Bundle bolsa0=new Bundle();
                    bolsa0.putString("PK_TIENDA",pedido.PK_TIENDA);
                    bolsa0.putString("TIENDA",producto.TIENDA);
                    bolsa0.putString("IMAGEN_TIENDA",producto.IMAGEN_TIENDA);
                    bolsa0.putString("PK_TIPO",producto.PK_TIPO);
                    bolsa0.putString("TIPO",producto.TIPO);
                    bolsa0.putString("IMAGEN_TIPO",producto.IMAGEN_TIPO);
                    bolsa0.putSerializable("TIENDA_OBJ",tienda);

                    Bundle bolsa1=new Bundle();
                    bolsa1.putSerializable("pedido",pedido);
                    bolsa1.putString("categoria",producto.CATEGORIA);
                    bolsa1.putSerializable("TIENDA_OBJ",tienda);

                    Bundle bolsa=new Bundle();
                    bolsa.putSerializable("producto",producto);
                    bolsa.putSerializable("TIENDA_OBJ",tienda);

                    Intent intent=new Intent(context, CategoriasActivity.class);
                    intent.putExtra("bolsa",bolsa0);
                    listaActividades.add(intent);
                    context.startActivity(intent);

                    intent=new Intent(context, ProductosListActivity.class);
                    intent.putExtra("bolsa",bolsa1);
                    listaActividades.add(intent);
                    context.startActivity(intent);

                    intent=new Intent(context, AgregarProductoActivity.class);
                    intent.putExtra("bolsa",bolsa);
                    listaActividades.add(intent);
                    context.startActivity(intent);


                }
            });

        }
    }
}
