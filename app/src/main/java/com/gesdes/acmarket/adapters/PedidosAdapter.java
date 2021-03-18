package com.gesdes.acmarket.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gesdes.acmarket.R;
import com.gesdes.acmarket.model.PedidoModel;
import com.gesdes.acmarket.model.ProductosModel;
import com.gesdes.acmarket.utils.RoundedTransformation;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.List;

public class PedidosAdapter extends BaseAdapter {

    Context mContext;
    List<PedidoModel>listaPedidos;
    public PedidosAdapter(Context contex,List<PedidoModel>pedidos){
        this.mContext=contex;
        this.listaPedidos=pedidos;
    }

    public void setListaPedidos(List<PedidoModel>pedidos){
        this.listaPedidos=pedidos;
    }

    @Override
    public int getCount() {
        return listaPedidos.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        PedidoModel pedido=listaPedidos.get(i);

        if (view == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            view = layoutInflater.inflate(R.layout.item_pedido, null);
        }

        TextView tvEstatus=view.findViewById(R.id.tvEstatusPedidoItem);
        TextView tvIdPedido=view.findViewById(R.id.tvIdPedidoItem);
        TextView tvNombre=view.findViewById(R.id.tvNombrePedidoItem);
        TextView tvDireccion=view.findViewById(R.id.tvDireccionPedidoItem);
        TextView tvTotal=view.findViewById(R.id.tvTotalpedidoItem);
        TextView tvEntregadoPor=view.findViewById(R.id.tvNombrePedidoPor);
        ImageView ivTienda=view.findViewById(R.id.ivTiendaPedidoItem);
        TextView tvHorario=view.findViewById(R.id.tvHorarioPedidoItem);

        tvEstatus.setText("Estado: "+pedido.ESTATUS);
        tvIdPedido.setText(""+pedido.PK);
        tvNombre.setText(pedido.TIENDA);
        tvDireccion.setText(""+pedido.DIRECCION);
        DecimalFormat df = new DecimalFormat("#0.00");
        tvTotal.setText("Total: $ "+df.format(pedido.TOTAL));
        tvEntregadoPor.setText(""+pedido.REPARTIDOR);
        tvHorario.setText(""+pedido.HORARIO);

        if(!pedido.IMAGEN_TIENDA.isEmpty()){
            Picasso.with(mContext).load(pedido.IMAGEN_TIENDA).transform(new RoundedTransformation(20, 0)).fit().placeholder(R.drawable.iv_placeholder)
                    .error(R.drawable.iv_placeholder).into(ivTienda);
        }


        return view;
    }
}
