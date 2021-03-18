package com.gesdes.acmarket.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.os.StrictMode;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.gesdes.acmarket.R;
import com.gesdes.acmarket.activities.DireccionesActivity;
import com.gesdes.acmarket.activities.EncuestaPedidoActivity;
import com.gesdes.acmarket.activities.LoginActivity;
import com.gesdes.acmarket.activities.MainActivity;
import com.gesdes.acmarket.activities.TiendasListActivity;
import com.gesdes.acmarket.activities.TutorialActivity;
import com.gesdes.acmarket.adapters.PromocionesAdapter;
import com.gesdes.acmarket.adapters.Tiendas2Adapter;
import com.gesdes.acmarket.adapters.TiendasAdapter;
import com.gesdes.acmarket.adapters.TiposTiendaAdapter;
import com.gesdes.acmarket.db.AppDatabase;
import com.gesdes.acmarket.model.DireccionesModel;
import com.gesdes.acmarket.model.ImageSwitcherPicasso;
import com.gesdes.acmarket.model.ProductosModel;
import com.gesdes.acmarket.model.TiendasModel;
import com.gesdes.acmarket.model.TiposTiendasModel;
import com.gesdes.acmarket.utils.RoundedTransformation;
import com.gesdes.acmarket.utils.Utils;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.helpers.Util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TiendasFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TiendasFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    Context mContext;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    SharedPreferences.Editor editor;
    GridView gridView;
    Button btnTutorial;
    //List<ProductosModel>Promociones;
    List<TiendasModel>listaTiendas;
    Tiendas2Adapter tiendasAdapter;
    String URL_API,URL_COMPARTIR;
    MenuItem menuItem,menuItem1,menuItem2,menuItem3,menuItem4,menuItem5;
    //RecyclerView lvPromociones;
    //PromocionesAdapter promoAdapter;
    Button btnCompartir;
    ProgressBar progressBar;

    private ImageSwitcher imageSwitcher;
    private int[] galeria = { R.drawable.ladespensa, R.drawable.comparte, R.drawable.hourglass,R.drawable.inferiormenu };
    private List<String> galeriaString; /*= { "https://www.adslzone.net/app/uploads-adslzone.net/2019/04/borrar-fondo-imagen-930x487.jpg",
            "https://i1.wp.com/hotbook.com.mx/wp-content/uploads/2019/04/hotbook-se-revela-la-primera-imagen-de-un-agujero-negro-portada.jpg?w=1024&ssl=1",
            "https://i.blogs.es/6c558d/luna-400mpx/1366_2000.jpg" };*/
    private int posicion;
    private static final int DURACION = 5000;
    private Timer timer = null;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public TiendasFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TiendasFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TiendasFragment newInstance(String param1, String param2) {
        TiendasFragment fragment = new TiendasFragment();
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


    }
    String PK_CLIENTE="";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_tiendas, container, false);
         gridView = view.findViewById(R.id.listaTiposTiendas);
         listaTiendas=new ArrayList<>();

        SharedPreferences preferencias = getActivity().getSharedPreferences("VARIABLES", Context.MODE_PRIVATE);
        editor = preferencias.edit();
        mContext=getActivity();
        PK_CLIENTE = preferencias.getString("PK","");

        btnTutorial=view.findViewById(R.id.btnTutorialTiendasFragment);
        btnTutorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent tuto= new Intent(mContext, TutorialActivity.class);
                startActivity(tuto);
            }
        });

        tiendasAdapter = new Tiendas2Adapter(getContext(), listaTiendas);
        gridView.setAdapter(tiendasAdapter);
        progressBar=view.findViewById(R.id.pbCargandorTiendasFragment);

        /*
        LinearLayoutManager layoutManager= new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL, false);
        lvPromociones = view.findViewById(R.id.lvPromociones);
        lvPromociones.setLayoutManager(layoutManager);

        Promociones=new ArrayList<>();
        promoAdapter=new PromocionesAdapter(getContext(),Promociones);
        lvPromociones.setAdapter(promoAdapter);
        lvPromociones.setHasFixedSize(true);
        */


        URL_API=getString(R.string.URL_HOST)+"TiendasV2/Obtener";

        obtenerTiposTiendas();

        try {
            BottomNavigationView navigation = container.findViewById(R.id.bottomNavigationViewMain);
            Menu drawer_menu = navigation.getMenu();
            menuItem = drawer_menu.findItem(R.id.navigation_tiendas);

            NavigationView navigation1 = container.findViewById(R.id.leftNavigationMain);
            Menu drawer_menu1 = navigation1.getMenu();
            menuItem1 = drawer_menu1.findItem(R.id.navigation_perfil);
            menuItem2 = drawer_menu1.findItem(R.id.pedidosMainFragment);
            menuItem4 = drawer_menu1.findItem(R.id.nav_compartir);
            menuItem5 = drawer_menu1.findItem(R.id.navigation_soporte);


            if (!menuItem.isChecked()) {
                menuItem.setChecked(true);
            }
            menuItem1.setChecked(false);
            menuItem2.setChecked(false);
            menuItem3.setChecked(false);
            menuItem4.setChecked(false);
            menuItem5.setChecked(false);
        }catch (Exception e){

        }
        gridView.setOnItemClickListener(new  AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String pk=listaTiendas.get(position).PK;
                String nombre=listaTiendas.get(position).NOMBRE;
                String imagen=listaTiendas.get(position).IMAGEN;
                Bundle bolsa=new Bundle();
                bolsa.putString("PK_TIENDA", pk);
                bolsa.putString("TIENDA", nombre);
                bolsa.putString("IMAGEN_TIENDA", imagen);
                bolsa.putSerializable("TIENDA_OBJ",listaTiendas.get(position));
                Intent intento=new Intent(getContext(), TiendasListActivity.class);
                intento.putExtra("bolsa",bolsa);
                startActivity(intento);
            }
        });
        /*
        lvPromociones.addOnItemTouchListener(new RecyclerView.SimpleOnItemTouchListener());
        lvPromociones.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getContext(),"promo "+i,Toast.LENGTH_LONG).show();
            }
        });
*/
        SharedPreferences preferences =getActivity().getSharedPreferences("VARIABLES",Context.MODE_PRIVATE);
        String nombre=preferences.getString("NOMBRE","");
        String direccion=preferences.getString("Direccion","");

        //agregaPromociones();

        btnCompartir=view.findViewById(R.id.btnCompartirFr);
        btnCompartir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                compartir();
            }
        });

        try {
            ImageView ivComparte = view.findViewById(R.id.ivCompartirBakTiposTiendas);
            ivComparte.setImageBitmap(getRoundedCornerBitmap(((BitmapDrawable) ivComparte.getDrawable()).getBitmap()));
        }catch (Exception e){

        }

        try {
            AppDatabase db = Room.databaseBuilder(mContext,
                    AppDatabase.class, "polar-base").allowMainThreadQueries().fallbackToDestructiveMigration().build();
            db.PedidoDetalleDao().deleteAllProductos();
        }catch (Exception e){

        }

        /*Slider animado*/
        imageSwitcher = (ImageSwitcher) view.findViewById(R.id.imageSwitcher);
        imageSwitcher.setFactory(new ViewSwitcher.ViewFactory()
        {
            public View makeView()
            {
                ImageView imageView = new ImageView(mContext);
                imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

                return imageView;
            }
        });

        Animation fadeIn = AnimationUtils.loadAnimation(mContext, R.anim.fade_in);
        Animation fadeOut = AnimationUtils.loadAnimation(mContext, R.anim.fade_out);
        imageSwitcher.setInAnimation(fadeIn);
        imageSwitcher.setOutAnimation(fadeOut);
        /*Fin Slider animado*/

        galeriaString=new ArrayList<>();
        timer = new Timer();

        return view;
    }

    public void iniciaTimer(){

        timer.scheduleAtFixedRate(new TimerTask()
        {
            public void run()
            {
                ((Activity)mContext).runOnUiThread(new Runnable()
                {
                    public void run()
                    {
                        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                        StrictMode.setThreadPolicy(policy);
                        new SetImgDraw(mContext,imageSwitcher,posicion).execute(galeriaString.get(posicion));
                        posicion++;
                        if (posicion == galeriaString.size())
                            posicion = 0;
                    }
                });
            }
        }, 0, DURACION);
    }


    public class SetImgDraw extends AsyncTask<String, String, Drawable > {
        Context mContext;
        ProgressDialog progressDialog;
        ImageSwitcher iS;
        String dir = "";
        int posicion=0;
        Drawable drawable;


        public SetImgDraw(Context mContext, ImageSwitcher iS,int posicion) {
            this.mContext = mContext;
            this.progressDialog = progressDialog;
            this.iS = iS;
            this.posicion=posicion;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Drawable  doInBackground(String... params) {

            String lo = params[0];

             drawable = Utils.createDrawableFromUrl(params[0]);
            return drawable;
        }

        @Override
        protected void onPostExecute(Drawable drawable1) {
            super.onPostExecute(drawable1);

            try {
                Bitmap bitmap = ((BitmapDrawable) drawable1).getBitmap();
                float ratio = Math.min(
                        (float) 320 / bitmap.getWidth(),
                        (float) 280 / bitmap.getHeight());
                int width = Math.round((float) ratio * bitmap.getWidth());
                int height = Math.round((float) ratio * bitmap.getHeight());

                Bitmap newBitmap = Bitmap.createScaledBitmap(bitmap, width,
                        height, true);

                Drawable d = new BitmapDrawable(mContext.getResources(), bitmap);
                iS.setImageDrawable(d);
            }catch (Exception e){
                String ecep=e.toString();
                Log.e("SCH",ecep);
            }
        }
    }


    public void obtenerTiposTiendas(){

        JSONObject datos = new JSONObject();
        try {
            datos.put("PK_CLIENTE", PK_CLIENTE);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        progressBar.setVisibility(View.VISIBLE);

        RequestQueue requstQueue = Volley.newRequestQueue(mContext);

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
                                URL_COMPARTIR=response.getString("link");
                                String openID =response.getString("idopen");
                                String openPublicKey =response.getString("publicaopen");
                                Boolean openProduccion =response.getBoolean("openproduccion");
                                int cantidadMisPedidos =response.getInt("cantidadMisPedidos");
                                int faltaCalificacion =response.getInt("faltaCalificacion");

                                editor.putInt("CANTIDAD_MIS_PEDIDOS",cantidadMisPedidos);
                                editor.putString("LINK",URL_COMPARTIR);
                                editor.putString("PUBLIC_OPEN_KEY",openPublicKey);
                                editor.putBoolean("PRODUCCION_OPEN_PAY",openProduccion);
                                editor.putString("ID_OPEN",openID);
                                editor.commit();

                                JSONArray tiendas=response.getJSONArray("tiendas");
                                TiendasModel aux;
                                if(listaTiendas==null){
                                    listaTiendas=new ArrayList<>();
                                }
                                for (int i=0;i<tiendas.length();i++) {
                                    JSONObject tipo= tiendas.getJSONObject(i);
                                    aux=new TiendasModel();
                                    aux.PK=tipo.getString("pk");
                                    aux.NOMBRE=tipo.getString("nombre");
                                    aux.IMAGEN=tipo.getString("imagen");
                                    aux.DIRECCION=tipo.getString("direccion");
                                    aux.LATITUD=tipo.getString("latitud");
                                    aux.LONGITUD=tipo.getString("longitud");
                                    aux.LUNES=tipo.getString("lunes");
                                    aux.MARTES=tipo.getString("martes");
                                    aux.MIERCOLES=tipo.getString("miercoles");
                                    aux.JUEVES=tipo.getString("jueves");
                                    aux.VIERNES=tipo.getString("viernes");
                                    aux.SABADO=tipo.getString("sabado");
                                    aux.DOMINGO=tipo.getString("domingo");
                                    aux.ENTREGA_LUNES=tipo.getString("entregA_LUNES");
                                    aux.ENTREGA_MARTES=tipo.getString("entregA_MARTES");
                                    aux.ENTREGA_MIERCOLES=tipo.getString("entregA_MIERCOLES");
                                    aux.ENTREGA_JUEVES=tipo.getString("entregA_JUEVES");
                                    aux.ENTREGA_VIERNES=tipo.getString("entregA_VIERNES");
                                    aux.ENTREGA_SABADO=tipo.getString("entregA_SABADO");
                                    aux.ENTREGA_DOMINGO=tipo.getString("entregA_DOMINGO");
                                    aux.ENTREGA_EXPRESS=tipo.getString("entregA_EXPRESS");
                                    aux.BORRADO=tipo.getString("borrado");
                                    listaTiendas.add(aux);
                                }
                                tiendasAdapter.setListaTipos(listaTiendas);
                                tiendasAdapter.notifyDataSetChanged();
                                //gridView.setAdapter();
                                //gridView.notifyAll();

                                ViewGroup.LayoutParams layoutParams = gridView.getLayoutParams();
                                int tamaño= (listaTiendas.size()%3==0)?listaTiendas.size():listaTiendas.size()+1;
                                layoutParams.height = Utils.convertDpToPixels(tamaño*135/3,mContext); //this is in pixels
                                gridView.setLayoutParams(layoutParams);

                                if(faltaCalificacion>0){
                                    Bundle bolsa=new Bundle();
                                    bolsa.putInt("PK_PEDIDO",faltaCalificacion);
                                    Intent calificar=new Intent(mContext, EncuestaPedidoActivity.class);
                                    calificar.putExtra("bolsa",bolsa);
                                    startActivity(calificar);
                                }

                                JSONArray slider=response.getJSONArray("sliderImg");
                                galeriaString=new ArrayList<>();
                                for (int i=0;i<slider.length();i++) {
                                    JSONObject imgObj= slider.getJSONObject(i);
                                    galeriaString.add(imgObj.getString("imagen"));
                                }

                                if(galeriaString.size()>0){
                                    iniciaTimer();
                                }

/*
                                JSONArray promos=response.getJSONArray("promos");
                                Promociones.clear();
                                ProductosModel productom;
                                for (int i=0;i<promos.length();i++) {
                                    JSONObject pr= promos.getJSONObject(i);
                                    productom=new ProductosModel();
                                    productom.PK=pr.getString("pk");
                                    productom.PK_CATEGORIA=pr.getString("pK_CATEGORIA");
                                    productom.CATEGORIA=pr.getString("categoria");
                                    productom.IMAGEN_CATEGORIA=pr.getString("imageN_CATEGORIA");
                                    productom.PK_TIENDA=pr.getString("pK_TIENDA");
                                    productom.TIENDA=pr.getString("tienda");
                                    productom.PK_TIPO=pr.getString("pK_TIPO_TIENDA");
                                    productom.TIPO=pr.getString("tipo");
                                    productom.PRODUCTO=pr.getString("producto");
                                    productom.DESCRIPCION=pr.getString("descripcion");
                                    productom.STOCK=pr.getInt("stock");
                                    productom.PK_MEDIDA=pr.getString("pK_MEDIDA");
                                    productom.MEDIDA=pr.getString("medida");
                                    productom.MEDIDA_DESCRIPCION=pr.getString("medidA_DESCRIPCION");
                                    productom.IMAGEN=pr.getString("imagen");
                                    productom.IMAGEN_TIENDA=pr.getString("imageN_TIENDA");
                                    productom.IMAGEN_TIPO=pr.getString("imageN_TIPO");
                                    productom.PRECIO=pr.getDouble("precio");

                                    productom.TIENDA_LUNES=pr.getString("tiendA_LUNES");
                                    productom.TIENDA_MARTES=pr.getString("tiendA_MARTES");
                                    productom.TIENDA_MIERCOLES=pr.getString("tiendA_MIERCOLES");
                                    productom.TIENDA_JUEVES=pr.getString("tiendA_JUEVES");
                                    productom.TIENDA_VIERNES=pr.getString("tiendA_VIERNES");
                                    productom.TIENDA_SABADO=pr.getString("tiendA_SABADO");
                                    productom.TIENDA_DOMINGO=pr.getString("tiendA_DOMINGO");
                                    productom.ENTREGA_LUNES=pr.getString("entregA_LUNES");
                                    productom.ENTREGA_MARTES=pr.getString("entregA_MARTES");
                                    productom.ENTREGA_MIERCOLES=pr.getString("entregA_MIERCOLES");
                                    productom.ENTREGA_JUEVES=pr.getString("entregA_JUEVES");
                                    productom.ENTREGA_VIERNES=pr.getString("entregA_VIERNES");
                                    productom.ENTREGA_SABADO=pr.getString("entregA_SABADO");
                                    productom.ENTREGA_DOMINGO=pr.getString("entregA_DOMINGO");
                                    productom.ENTREGA_EXPRESS=pr.getString("entregA_EXPRESS");

                                    Promociones.add(productom);
                                }
                                promoAdapter.notifyDataSetChanged();
*/
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

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {

        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;

        final Paint paint = new Paint();

        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        final RectF rectF = new RectF(rect);

        final float roundPx = 30;

        paint.setAntiAlias(true);

        canvas.drawARGB(0, 0, 0, 0);

        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx*2, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
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

    LinearLayout relativeLayout;
    List<ImageView>listaImagenes;
    public void agregaPromociones(){

  /*
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.addRule(RelativeLayout.BELOW, R.id.ButtonRecalculate);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
*/
  /*
        if(listaImagenes==null){listaImagenes=new ArrayList<>();}
        listaImagenes.clear();
        for (ProductosModel pro:Promociones){

            ImageView imageView = new ImageView(mContext);
            if(!pro.IMAGEN.isEmpty() ){
                Picasso.with(mContext).load(pro.IMAGEN).placeholder(R.drawable.iv_placeholder)
                        .error(R.drawable.iv_placeholder).into(imageView);
            }
            //imageView.setImageResource(R.drawable.promo1);
            relativeLayout.addView(imageView);
            listaImagenes.add(imageView);
        }
*/
    }

    public void compartir(){
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = URL_COMPARTIR;
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Polar app");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Compartir via"));

    }

    public void abreMenu(View view){
        DrawerLayout drawerLayoutMain=  getActivity().findViewById(R.id.drawerLayoutMain);
        drawerLayoutMain.openDrawer(Gravity.START);
    }
    View ProgressView;
    ProgressBar myProgressBar;
    public void muestraCargando() {
        final Dialog dialog = new Dialog(mContext);
        dialog.setContentView(R.layout.layout_cargando);
        dialog.show();
    }

}
