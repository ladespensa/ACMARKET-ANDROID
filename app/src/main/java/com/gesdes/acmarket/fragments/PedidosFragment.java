package com.gesdes.acmarket.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.gesdes.acmarket.R;
import com.gesdes.acmarket.activities.LoginActivity;
import com.gesdes.acmarket.activities.PedidoDetalleActivity;
import com.gesdes.acmarket.activities.ProductosListActivity;
import com.gesdes.acmarket.adapters.PedidosAdapter;
import com.gesdes.acmarket.adapters.TiposTiendaAdapter;
import com.gesdes.acmarket.model.PedidoDetalleModel;
import com.gesdes.acmarket.model.PedidoModel;
import com.gesdes.acmarket.model.ProductoPedidoModel;
import com.gesdes.acmarket.model.TiposTiendasModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PedidosFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PedidosFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    MenuItem menuItem;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    ListView listaView;
    List<PedidoModel> listaPedidos;
    PedidosAdapter pedidosAdapter;
    String URL_API,pkCliente;
    TextView tvDireccion,tvNombre;
    ProgressBar progressBar;

    public PedidosFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PedidosFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PedidosFragment newInstance(String param1, String param2) {
        PedidosFragment fragment = new PedidosFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        /*TODO_SERGIO
        BottomNavigationView navigation = getActivity().findViewById(R.id.bottomNavigationView);
        Menu drawer_menu = navigation.getMenu();
        menuItem = drawer_menu.findItem(R.id.navigation_pedidos);
*/

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_pedidos, container, false);
        progressBar = view.findViewById(R.id.pbCargandoPedidos);
        listaView = view.findViewById(R.id.listaPedidosFragment);
        listaPedidos=new ArrayList<>();
        pedidosAdapter = new PedidosAdapter(getContext(), listaPedidos);
        listaView.setAdapter(pedidosAdapter);


        URL_API=getString(R.string.URL_HOST)+"obtenerMisPedidos";

        SharedPreferences preferences =getActivity().getSharedPreferences("VARIABLES", Context.MODE_PRIVATE);
        pkCliente=preferences.getString("PK","");

        listaView.setOnItemClickListener(new  AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PedidoModel pedido=new PedidoModel();
                pedido=listaPedidos.get(position);
                Bundle bolsa=new Bundle();
                bolsa.putSerializable("pedido", pedido);
                Intent intento=new Intent(getContext(), PedidoDetalleActivity.class);
                intento.putExtra("bolsa",bolsa);
                startActivity(intento);
            }
        });


        obtenerPedidos();

        /*TODO_SERGIO
        if(!menuItem.isChecked())
        {
            menuItem.setChecked(true);
        }
        */
        return view;
    }


    public void obtenerPedidos(){


        progressBar.setVisibility(View.VISIBLE);

        JSONObject datos = new JSONObject();
        try {
            datos.put("PK_CLIENTE",pkCliente);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        RequestQueue requstQueue = Volley.newRequestQueue(getActivity());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL_API,datos,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            if(progressBar!=null){
                                progressBar.setVisibility(View.GONE);
                            }

                            int result = (int) response.get("resultado");

                            if(result == 1){
                                JSONArray tipos=response.getJSONArray("pedidos");
                                PedidoModel aux;
                                List<PedidoDetalleModel>detalle;
                                PedidoDetalleModel aux2;

                                if(listaPedidos==null){
                                    listaPedidos=new ArrayList<>();
                                }
                                for (int i=0;i<tipos.length();i++) {
                                    JSONObject tipo= tipos.getJSONObject(i);
                                    aux=new PedidoModel();
                                    aux.PK=tipo.getInt("pk");
                                    aux.PK_CLIENTE=tipo.getString("pK_CLIENTE");
                                    aux.PK_TIENDA=tipo.getString("pK_TIENDA");
                                    aux.TIENDA=tipo.getString("tienda");
                                    aux.IMAGEN_TIENDA=tipo.getString("imageN_TIENDA");
                                    aux.DIRECCION=tipo.getString("direccion");
                                    aux.LATITUD=tipo.getString("latitud");
                                    aux.LONGITUD=tipo.getString("longitud");
                                    aux.PK_REPARTIDOR=tipo.getString("pK_REPARTIDOR");
                                    aux.REPARTIDOR=tipo.getString("repartidor");
                                    aux.PK_ESTATUS=tipo.getString("pK_ESTATUS");
                                    aux.ESTATUS=tipo.getString("estatus");
                                    aux.BORRADO=tipo.getString("borrado");
                                    aux.PRECIO_ENTREGA=tipo.getDouble("envio");
                                    aux.COMISION_TARJETA=tipo.getDouble("comisioN_TARJETA");
                                    aux.METODO_PAGO=tipo.getString("metodO_PAGO");
                                    aux.TOTAL=tipo.getDouble("total");
                                    aux.FECHA_C=tipo.getString("fechA_C");
                                    aux.HORARIO=tipo.getString("fechA_ENTREGA");
                                    /*
                                    aux.PK=tipo.getInt("pk");
                                    aux.PK_CLIENTE=tipo.getString("pK_CLIENTE");
                                    aux.TIENDA=tipo.getString("tienda");
                                    //aux.IMAGEN=tipo.getString("imagen");
                                    aux.DIRECCION=tipo.getString("direccion");
                                    aux.LATITUD=tipo.getString("latitud");
                                    aux.LONGITUD=tipo.getString("longitud");
                                    aux.PK_REPARTIDOR=tipo.getString("pK_REPARTIDOR");
                                    aux.PK_ESTATUS=tipo.getString("pK_ESTATUS");
                                    aux.ESTATUS=tipo.getString("estatus");
                                    aux.BORRADO=tipo.getString("borrado");
                                    aux.PRECIO_ENTREGA=tipo.getDouble("envio");
                                    aux.TOTAL=tipo.getDouble("total");*/
                                    detalle=new ArrayList<>();

                                    JSONArray productos= tipo.getJSONArray("lista");
                                    for (int j=0;j<productos.length();j++) {
                                        JSONObject object=productos.getJSONObject(j);
                                        aux2 = new PedidoDetalleModel();
                                        aux2.PK_PRODUCTO=object.getString("pK_PRODUCTO");
                                        aux2.PRODUCTO=object.getString("producto");
                                        aux2.CANTIDAD=object.getDouble("cantidad");
                                        aux2.IMAGEN=object.getString("imagen");
                                        aux2.PRECIO=object.getDouble("precio");
                                        aux2.DETALLES=object.getString("detalles");
                                        detalle.add(aux2);
                                    }
                                    aux.LISTA=detalle;
                                    listaPedidos.add(aux);
                                }
                                pedidosAdapter.setListaPedidos(listaPedidos);
                                pedidosAdapter.notifyDataSetChanged();
                                //gridView.setAdapter();
                                //gridView.notifyAll();
                            }else{

                                String error=response.getString("mensaje");
                                _ShowAlert("Error",error);

                            }

                        }catch (JSONException e){
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        if(progressBar!=null){
                            progressBar.setVisibility(View.GONE);
                        }
                        Log.e("Rest Response",error.toString());
                    }
                }
        ){
            //here I want to post data to sever
        };

        int MY_SOCKET_TIMEOUT_MS = 15000;
        int maxRetries = 2;
        jsonObjectRequest.setRetryPolicy(new
                DefaultRetryPolicy(
                        MY_SOCKET_TIMEOUT_MS,
                        maxRetries,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )
        );

        requstQueue.add(jsonObjectRequest);

    }


    private void _ShowAlert(String title, String mensaje){

        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
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

    public void cerrarSesion(View view){
        SharedPreferences preferencias =getActivity().getSharedPreferences("VARIABLES", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferencias.edit();
        editor.putBoolean("LOGUEADO",false);
        Intent main=new Intent(getContext(), LoginActivity.class);
        main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(main);
        getActivity().finish();
    }

}
