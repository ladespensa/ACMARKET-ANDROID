package com.gesdes.acmarket.fragments;

import android.app.AlertDialog;
import android.app.ExpandableListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.room.Database;
import androidx.room.Room;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
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
import com.gesdes.acmarket.adapters.CategoriasExpandedAdapter;
import com.gesdes.acmarket.db.AppDatabase;
import com.gesdes.acmarket.model.CategoriasModel;
import com.gesdes.acmarket.model.GruposCategoriasModel;
import com.gesdes.acmarket.model.ProductosModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BusquedaPorCategoriaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BusquedaPorCategoriaFragment extends Fragment implements TextWatcher {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    ProgressBar progressBar;
    ExpandableListView expandableListView;
    List<GruposCategoriasModel>ListaGruposCategorias;
    List<GruposCategoriasModel>ListaGruposCategoriasAAll;
    CategoriasExpandedAdapter categoriasExpandedAdapter;
    TextView tvBuscarCategoria;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    String URL_API;

    public BusquedaPorCategoriaFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BusquedaPorCategoriaFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BusquedaPorCategoriaFragment newInstance(String param1, String param2) {
        BusquedaPorCategoriaFragment fragment = new BusquedaPorCategoriaFragment();
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

        try {
            AppDatabase db = Room.databaseBuilder(getContext(),
                    AppDatabase.class, "polar-base").allowMainThreadQueries().fallbackToDestructiveMigration().build();
            db.PedidoDetalleDao().deleteAllProductos();
        }catch (Exception e){

        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_busqueda_por_categoria, container, false);
        URL_API=getString(R.string.URL_HOST)+"CategoriasList";
        expandableListView=view.findViewById(R.id.expandableListView);
        ListaGruposCategorias= new ArrayList<>();
        ListaGruposCategoriasAAll= new ArrayList<>();
        categoriasExpandedAdapter=new CategoriasExpandedAdapter(ListaGruposCategorias,getContext());
        expandableListView.setAdapter(categoriasExpandedAdapter);
        tvBuscarCategoria=view.findViewById(R.id.etBuscarCategoriasBusquedaPorCategorias);
        progressBar=view.findViewById(R.id.pbbusquedaPorCategoria);
        tvBuscarCategoria.addTextChangedListener(this);

        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
                return true;
            }
        });



        obtenerCategoriasTiendas();

        return view;
    }

    public void obtenerCategoriasTiendas(){

        progressBar.setVisibility(View.VISIBLE);

        RequestQueue requstQueue = Volley.newRequestQueue(getContext());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL_API,null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            if(progressBar!=null){
                                progressBar.setVisibility(View.GONE);
                            }


                            int result = (int) response.get("resultado");

                            if(result == 1){
                                if(ListaGruposCategorias==null){
                                    ListaGruposCategorias=new ArrayList<>();
                                }
                                ListaGruposCategorias.clear();
                                ListaGruposCategoriasAAll.clear();
                                JSONArray categorias=response.getJSONArray("categorias");
                                CategoriasModel aux;
                                GruposCategoriasModel auxGroup;
                                Map<String,Integer>diccionario=new HashMap<>();
                                for (int i=0;i<categorias.length();i++) {
                                    JSONObject categoria= categorias.getJSONObject(i);
                                    aux=new CategoriasModel();
                                    aux.PK=categoria.getString("pk");
                                    aux.CLASIFICACION=categoria.getString("clasificacion");
                                    aux.PK_GRUPO=categoria.getInt("pK_GRUPO");
                                    aux.GRUPO=categoria.getString("grupo");
                                    aux.DESCRIPCION_GRUPO=categoria.getString("descripcioN_GRUPO");
                                    aux.IMAGEN=categoria.getString("imagen");
                                    aux.DESCRIPCION=categoria.getString("descripcion");
                                    aux.BORRADO=categoria.getString("borrado");


                                    if(!diccionario.containsKey(aux.GRUPO)){
                                        auxGroup=new GruposCategoriasModel();
                                        auxGroup.PK=aux.PK_GRUPO;
                                        auxGroup.NOMBRE=aux.GRUPO;
                                        auxGroup.DESCRIPCION=aux.DESCRIPCION_GRUPO;
                                        auxGroup.CATEGORIAS_LIST=new ArrayList<>();
                                        auxGroup.CATEGORIAS_LIST.add(aux);
                                        ListaGruposCategorias.add(auxGroup);
                                        ListaGruposCategoriasAAll.add(auxGroup);
                                        diccionario.put(aux.GRUPO,ListaGruposCategorias.size()-1);
                                    }else{

                                        int indice=0;
                                        for (Map.Entry<String, Integer> entry : diccionario.entrySet()) {
                                            String key = entry.getKey();
                                            Integer value = entry.getValue();
                                            if(key.equals(aux.GRUPO)){
                                                indice=value;
                                            }
                                        }
                                        //int indice=diccionario.getOrDefault(aux.GRUPO,0);
                                        ListaGruposCategorias.get(indice).CATEGORIAS_LIST.add(aux);
                                    }
                                }

                                categoriasExpandedAdapter.notifyDataSetChanged();
                                for (int i = ListaGruposCategorias.size()-1; i >=0; i--) { expandableListView.expandGroup(i,true); }

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

        AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
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

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {


        String s=charSequence.toString().toLowerCase();

        if(s.isEmpty()){
            ListaGruposCategorias.clear();
            ListaGruposCategorias.addAll(ListaGruposCategoriasAAll);
        }else{

            Map<String,Integer>diccionario=new HashMap<>();
            ListaGruposCategorias.clear();
            GruposCategoriasModel auxGroup;
            for(GruposCategoriasModel grupo : ListaGruposCategoriasAAll){

                for(CategoriasModel categoria : grupo.CATEGORIAS_LIST)
                {
                    if(categoria.CLASIFICACION.toLowerCase().contains(s)){
                        if(!diccionario.containsKey(categoria.GRUPO)){
                            auxGroup=new GruposCategoriasModel();
                            auxGroup.PK=categoria.PK_GRUPO;
                            auxGroup.NOMBRE=categoria.GRUPO;
                            auxGroup.DESCRIPCION=categoria.DESCRIPCION_GRUPO;
                            auxGroup.CATEGORIAS_LIST=new ArrayList<>();
                            auxGroup.CATEGORIAS_LIST.add(categoria);
                            ListaGruposCategorias.add(auxGroup);
                            diccionario.put(categoria.GRUPO,ListaGruposCategorias.size()-1);
                        }else{
                            int indice=0;
                            for (Map.Entry<String, Integer> entry : diccionario.entrySet()) {
                                String key = entry.getKey();
                                Integer value = entry.getValue();
                                if(key.equals(categoria.GRUPO)){
                                    indice=value;
                                }
                            }
                            //int indice=diccionario.getOrDefault(categoria.GRUPO,0);
                            ListaGruposCategorias.get(indice).CATEGORIAS_LIST.add(categoria);
                        }
                    }
                }

            }
        }
        categoriasExpandedAdapter.notifyDataSetChanged();
        for (i = ListaGruposCategorias.size()-1; i >=0; i--) { expandableListView.expandGroup(i,true); }

    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    public void abreMenu(View view){
        DrawerLayout drawerLayoutMain=  getActivity().findViewById(R.id.drawerLayoutMain);
        drawerLayoutMain.openDrawer(Gravity.START);
    }


}
