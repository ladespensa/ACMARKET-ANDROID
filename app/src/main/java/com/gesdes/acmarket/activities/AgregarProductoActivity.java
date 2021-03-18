package com.gesdes.acmarket.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gesdes.acmarket.R;
import com.gesdes.acmarket.db.AppDatabase;
import com.gesdes.acmarket.model.PedidoDetalleModel;
import com.gesdes.acmarket.model.PedidoModel;
import com.gesdes.acmarket.model.ProductoPedidoModel;
import com.gesdes.acmarket.model.ProductosModel;
import com.gesdes.acmarket.model.TiendasModel;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class AgregarProductoActivity extends AppCompatActivity {

    ImageView ivProducto;
    TextView tvNombre,tvPrecio,tvDescripcionProducto,tvNombreU,tvContadorAgrega;
    EditText etCantidad,etDetalle;
    ProductosModel producto;
    String URL_API;
    double cantidad=1;
    AppDatabase db;
    //ProgressBar progressBar;
    TiendasModel TIENDA_OBJ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_producto);

        tvContadorAgrega=findViewById(R.id.tvContadorProductosAgregarCarrito);
        //progressBar=findViewById(R.id.pbCargandoAgregarProducto);


        Bundle bolsa = getIntent().getBundleExtra("bolsa");
        producto = (ProductosModel) bolsa.getSerializable("producto");
        TIENDA_OBJ = (TiendasModel) bolsa.getSerializable("TIENDA_OBJ");
        URL_API=getString(R.string.URL_HOST)+"ProductosByTiendaAndCategoria";
        ivProducto=findViewById(R.id.ivProductoAgregar);
        tvNombre=findViewById(R.id.tvNombreProductoProductoAgregar);
        etCantidad=findViewById(R.id.tvCantidadProductoAgregar);
        tvPrecio=findViewById(R.id.tvPrecioProductoAgregar);
        etDetalle=findViewById(R.id.etDetalleProducto);
        tvDescripcionProducto=findViewById(R.id.tcDescripcionProductoAgregar);
        db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "polar-base").allowMainThreadQueries().fallbackToDestructiveMigration().build();
        DecimalFormat df = new DecimalFormat("#0.00");

        tvNombre.setText(producto.PRODUCTO);
        tvPrecio.setText("$ "+df.format(producto.PRECIO));
        tvDescripcionProducto.setText(producto.DESCRIPCION);
        if(!producto.IMAGEN.isEmpty() ){
            Picasso.with(this).load(producto.IMAGEN).placeholder(R.drawable.iv_placeholder)
                    .error(R.drawable.iv_placeholder).into(ivProducto);
        }
        PedidoDetalleModel pedido= db.PedidoDetalleDao().getProductoByPk(producto.PK);
        if(pedido != null && pedido.CANTIDAD >0 ){
            cantidad=pedido.CANTIDAD;
            etCantidad.setText(""+pedido.CANTIDAD);
        }else{
            etCantidad.setText("1");
        }

        if(pedido != null && pedido.DETALLES !=null && !pedido.DETALLES.isEmpty() && !pedido.DETALLES.equals("null")){
            etDetalle.setText(""+pedido.DETALLES);
        }

        SharedPreferences preferences =getSharedPreferences("VARIABLES", Context.MODE_PRIVATE);
        String nombre=preferences.getString("NOMBRE","");
        String direccion=preferences.getString("Direccion","");

    }

    public void lessProducto(View view){
        if(cantidad>1){
            cantidad--;
            etCantidad.setText(""+cantidad);
        }
    }
    public void plusProducto(View view){
        /*
        if((cantidad+1)<=producto.STOCK){
            cantidad++;
            etCantidad.setText(""+cantidad);
        }else{
            _ShowAlert("Stock máximo","El stock en tienda es de "+producto.STOCK);
        }*/
        cantidad++;
        etCantidad.setText(""+cantidad);

    }

    public void AgregarProducto(View view){

        if(cantidad>0){
        ProductoPedidoModel productoPedido=new ProductoPedidoModel();
        productoPedido.CANTIDAD=cantidad;
        productoPedido.PK_PRODUCTO=producto.PK;
        productoPedido.DETALLES=etDetalle.getText().toString();
        Bundle bolsa=new Bundle();
        bolsa.putSerializable("productoPedido",productoPedido);
        /*
        Intent intent=new Intent();
        intent.putExtra("bolsa",bolsa);
        setResult(2,intent);
        finish();*/
        PedidoDetalleModel productoD = new PedidoDetalleModel();
        productoD.CANTIDAD=cantidad;
        productoD.PRECIO=producto.PRECIO;
        productoD.PK_PRODUCTO=String.valueOf(producto.PK);
        productoD.DETALLES=productoPedido.DETALLES;
        PedidoDetalleModel pr=db.PedidoDetalleDao().getProductoByPk(productoD.PK_PRODUCTO);
        if(pr==null || pr.PK_PRODUCTO.isEmpty() || pr.PK_PRODUCTO ==null || pr.PK_PRODUCTO.equals("null")){
            db.PedidoDetalleDao().insertAll(productoD);
        }
        else{
            productoD.PK=pr.PK;
            db.PedidoDetalleDao().update(productoD);
        }
        finish();
        }else {
            etCantidad.setError("Cantidad debe de ser mayor a 0");
        }
    }


    private void _ShowAlert(String title, String mensaje){

        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(mensaje);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    public void abrirCarrito(View view){
        Bundle bolsa= new Bundle();
        bolsa.putSerializable("TIENDA_OBJ",TIENDA_OBJ);
        Intent intento=new Intent(this, com.gesdes.acmarket.activities.MiPedidoActivity.class);
        intento.putExtra("bolsa",bolsa);
        startActivity(intento);
    }


    @Override
    public void onStart() {
        super.onStart();
        List<PedidoDetalleModel> pedidos = db.PedidoDetalleDao().getAll();
        //Toast.makeText(this, "Cantidad " + pedidos.size(), Toast.LENGTH_LONG).show();
        tvContadorAgrega.setText(""+pedidos.size());

    }


}
