package com.gesdes.acmarket.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.room.Room;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.Settings;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.autofill.AutofillId;
import android.view.autofill.AutofillManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.gesdes.acmarket.R;
import com.gesdes.acmarket.db.AppDatabase;
import com.gesdes.acmarket.model.ClienteModel;
import com.gesdes.acmarket.model.CoordenadasModel;
import com.gesdes.acmarket.model.DireccionesModel;
import com.gesdes.acmarket.model.LocationService;
import com.gesdes.acmarket.model.PedidoDetalleModel;
import com.gesdes.acmarket.model.PoligonoModel;
import com.gesdes.acmarket.model.ProductosModel;
import com.gesdes.acmarket.model.TiendasModel;
import com.gesdes.acmarket.model.lineasPoligono;
import com.gesdes.acmarket.utils.Utils;
import com.gesdes.acmarket.utils.permisos;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.dynamic.IObjectWrapper;
import com.google.android.gms.internal.maps.zzw;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.api.client.json.Json;
import com.google.common.collect.Maps;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.security.Provider;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.gesdes.acmarket.utils.PolyUtil.containsLocation;
import static com.google.android.gms.maps.CameraUpdateFactory.newLatLng;
import static com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom;

public class ConfirmaDireccionActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener {

    String URL_API3, URL_API2, URL_API1;
    AppDatabase db;
    String pkCliente, LATITUD, LONGITUD;
    JSONObject datos;
    TextView tvDireccion;
    private FusedLocationProviderClient fusedLocationClient;
    ConstraintLayout clCargando;
    Location lo;
    LocationManager locationManager;
    Button btnAgregaPedido, btnCancela;
    List<PoligonoModel> POLIGONOS;
    ImageButton btnBuscar;
    String DIRECCION;
    TiendasModel TIENDA_OBJ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirma_direccion);
        //permisos.verifyStoragePermissions(this);

        // Initialize the SDK
        //Places.initialize(getApplicationContext(), getString(R.string.google_places));

        // Create a new Places client instance
        //PlacesClient placesClient = Places.createClient(this);

        POLIGONOS = new ArrayList<>();
        lo = new Location("GSM");
        URL_API3 = getString(R.string.URL_HOST) + "AgregarPedido";
        URL_API1 = getString(R.string.URL_HOST) + "ObtenerPoligonos";
        URL_API2 = getString(R.string.URL_HOST) + "ObtenerDetallePoligonosByPk";

        tvDireccion = findViewById(R.id.tvDireccionConfitmaDireccion);
        clCargando = findViewById(R.id.clPreparandoPedido);
        btnAgregaPedido = findViewById(R.id.btnFinalizaCompra);
        btnCancela = findViewById(R.id.btnCancelarPedido);
        btnBuscar = findViewById(R.id.ibBuscarDireccionConfirmaDireccion);

        tvDireccion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDir(view);
            }
        });

        SharedPreferences preferences = getSharedPreferences("VARIABLES", MODE_PRIVATE);
        pkCliente = preferences.getString("PK", null);

        /*TODO_SERGIO2
        LONGITUD = preferences.getString("LONGITUD", "");
        LATITUD = preferences.getString("LATITUD", "");
        String dire = preferences.getString("Direccion", null);
        tvDireccion.setText(dire);
        */
        db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "polar-base").allowMainThreadQueries().fallbackToDestructiveMigration().build();

        Bundle bolsa = getIntent().getBundleExtra("bolsa");
        String data = bolsa.getString("datos");
        TIENDA_OBJ=(TiendasModel) bolsa.getSerializable("TIENDA_OBJ");
        PK_COSTO_ENVIO="1";
        try {

            datos = new JSONObject(data);
            DIRECCION = datos.getString("DIRECCION");
            if(!DIRECCION.equals("Mi ubicaciòn")){
                LONGITUD = datos.getString("LONGITUD");
                LATITUD = datos.getString("LATITUD");
                lo.setLongitude(Double.valueOf(LONGITUD));
                lo.setLatitude(Double.valueOf(LATITUD));
                tvDireccion.setText(DIRECCION);
            }
            PK_COSTO_ENVIO=datos.getString("PK_COSTO_ENVIO");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    public void registra(View view) { _ShowAlertOption(); }

    public void registraProductos() {

        clCargando.setVisibility(View.VISIBLE);
        btnAgregaPedido.setEnabled(false);
        btnCancela.setEnabled(false);

        try {
            String dir = tvDireccion.getText().toString();

            if (!conServicio) {
                tvDireccion.setError("¡En está dirección no contamos con servicio por favor cambie la direccón de entrega!");
                _ShowAlert("Sin servicio", "¡En está dirección no contamos con servicio por favor cambie la direccón de entrega!");
                btnAgregaPedido.setEnabled(true);
                btnCancela.setEnabled(true);
                clCargando.setVisibility(View.GONE);
                return;
            }

            if (dir.isEmpty()) {
                tvDireccion.setError("¡Por favor verifica que este campo contenga la direccón de entrega, mueve el mapa para completar!");
                _ShowAlert("Error", "¡Por favor verifica que este campo contenga la direccón de entrega, mueve el mapa para completar!");
                btnAgregaPedido.setEnabled(true);
                btnCancela.setEnabled(true);
                clCargando.setVisibility(View.GONE);
                return;
            }
            if (lo == null || String.valueOf(lo.getLongitude()).isEmpty() || String.valueOf(lo.getLatitude()).isEmpty()) {
                tvDireccion.setError("¡Puede que no encontremos la dirección de tu posicion en el mapa, intenta mover un poco el mapa!");
                _ShowAlert("Error", "¡Puede que no encontremos la dirección de tu posicion en el mapa, intenta mover un poco el mapa!");
                btnAgregaPedido.setEnabled(true);
                btnCancela.setEnabled(true);
                clCargando.setVisibility(View.GONE);
                return;
            }

            datos.remove("DIRECCION");
            datos.remove("LATITUD");
            datos.remove("LONGITUD");
            datos.put("DIRECCION", dir);
            datos.put("LATITUD", "" + lo.getLatitude());
            datos.put("LONGITUD", "" + lo.getLongitude());
            datos.put("PK_POLIGONO", POLIGONO);
            datos.put("FECHA_ENTREGA", DIA_ENTREGA);


        RequestQueue requstQueue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL_API3, datos,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            //progressDialog.dismiss();
                            //btnAgregaPedido.setEnabled(true);
                            //btnCancela.setEnabled(true);

                            int result = (int) response.get("resultado");

                            if (result == 1) {
                                db.PedidoDetalleDao().deleteAllProductos();

                                DIA_ENTREGA=response.getString("entrega");

                                Bundle bolsa=new Bundle();
                                bolsa.putString("DIA_ENTREGA",DIA_ENTREGA);
                                Intent intento = new Intent(getApplicationContext(), PedidoRealizadoActivity.class);
                                intento.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                intento.putExtra("bolsa",bolsa);
                                startActivity(intento);
                                finish();
                                //_ShowAlert("Listo","Pedido enviado");

                            } else {
                                clCargando.setVisibility(View.GONE);
                                String error = response.getString("mensaje");
                                _ShowAlert("Error", error);
                                btnAgregaPedido.setEnabled(true);
                                btnCancela.setEnabled(true);

                            }

                        } catch (JSONException e) {
                            btnAgregaPedido.setEnabled(true);
                            btnCancela.setEnabled(true);
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        clCargando.setVisibility(View.GONE);
                        btnAgregaPedido.setEnabled(true);
                        btnCancela.setEnabled(true);
                        //progressDialog.dismiss();
                        Log.e("Rest Response", error.toString());
                    }
                }
        ) {
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

        } catch (Exception e) {
            btnAgregaPedido.setEnabled(false);
            btnCancela.setEnabled(false);
            e.toString();
            _ShowAlert("Error", "Ocurrio un problema al intentar atender pedido intente con otra ubicación");
            return;
        }
    }

    private void _ShowAlert(String title, String mensaje) {

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

    Boolean conServicio = false;

    public void agregaPoligono() {
        actualizaDibujoPoligonos();
/*TODO_SERGIO
        Polygon polygon1 = mMap.addPolygon(new PolygonOptions()
                .clickable(true)
                .addAll(lineasPoligono.getpuntos()));
        polygon1.setTag("beta");
        stylePolygon(polygon1);
        if(containsLocation(marker.getPosition(),polygon1.getPoints(),true)){
            btnAgregaPedido.setEnabled(true);
            conServicio=true;
            Toast.makeText(this,"contenido",Toast.LENGTH_SHORT).show();
        }else{
            btnAgregaPedido.setEnabled(false);
            conServicio=false;
            Toast.makeText(this,"no",Toast.LENGTH_SHORT).show();
        }
*/
    }

    GoogleMap mMap;

    Boolean TextoDefecto=false;

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        googleMap.setIndoorEnabled(false);

        obtenerPoligonosLista();
        if (!DIRECCION.equals("Mi ubicaciòn") && !LATITUD.isEmpty() && !LONGITUD.isEmpty()) {
            LatLng ubicacion = new LatLng(Double.valueOf(LATITUD), Double.valueOf(LONGITUD));
            mMap.moveCamera(newLatLngZoom(ubicacion, 18.0f));
            marker = new MarkerOptions();
            marker.position(ubicacion);
            marker.title("Entregar");
            mMap.addMarker(marker);
            TextoDefecto=true;
        } else {
            LatLng ubicacion = new LatLng(19.415127, -98.140905);
            mMap.moveCamera(newLatLngZoom(ubicacion, 12.0f));
            marker = new MarkerOptions();
            marker.position(ubicacion);
            marker.title("Entregar");
            mMap.addMarker(marker);
        }

        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {

                ProgressDialog pd = null;

                if (lo == null) {
                    lo = new Location("GSM");
                }
                lo.setLatitude(marker.getPosition().latitude);
                lo.setLongitude(marker.getPosition().longitude);

                if(!TextoDefecto) {
                    new getDireccionBack(ConfirmaDireccionActivity.this, tvDireccion,lo).execute(lo);
                }else {
                    agregaPoligono();
                    TextoDefecto=false;
                }
                /*
                final ProgressDialog progressDialog = new ProgressDialog(ConfirmaDireccionActivity.this,
                        R.style.Theme_AppCompat_Light_Dialog);
                progressDialog.setIndeterminate(true);
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setMessage("Obteniendo dirección del marcador...");
                progressDialog.show();

                if (lo == null) {
                    lo = new Location("GSM");
                }
                lo.setLatitude(marker.getPosition().latitude);
                lo.setLongitude(marker.getPosition().longitude);
                String dir = Utils.getDireccion(ConfirmaDireccionActivity.this, lo);
                tvDireccion.setText(dir);

                agregaPoligono();
                progressDialog.dismiss();
                */
            }
        });

        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                mMap.clear();
                marker = new MarkerOptions();
                marker.position(mMap.getCameraPosition().target);
                mMap.addMarker(marker);
            }
        });

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        pedirPermisos();


    }

    public void enableLocationUser() {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivityForResult(intent, REQUEST_CODE_LOCATION);
    }

    int REQUEST_LOCATION = 1;
    int REQUEST_CODE_LOCATION = 2;

    public void pedirPermisos() {

        //int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION);
        int permission3 = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int permission4 = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);

        if (//(permission != PackageManager.PERMISSION_GRANTED) ||
                (permission3 != PackageManager.PERMISSION_GRANTED)
                        || (permission4 != PackageManager.PERMISSION_GRANTED)
        ) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    //Manifest.permission.ACCESS_BACKGROUND_LOCATION
            }, 1);
        } else {
            if (mMap != null) {
                mMap.setMyLocationEnabled(true);
            }
            locationSend();
        }
    }

    public void locationSend() {
        if (DIRECCION.equals("Mi ubicaciòn")) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            final ProgressDialog progressDialog = new ProgressDialog(ConfirmaDireccionActivity.this,
                    R.style.Theme_AppCompat_Light_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Obteniendo ubicación inicial...");
            progressDialog.show();


            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {

                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                lo = location;
                                String dir = Utils.getDireccion(getApplicationContext(), location);
                                SharedPreferences preferencias = getSharedPreferences("VARIABLES", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferencias.edit();
                                editor.putString("Lat", String.valueOf(location.getLatitude()));
                                editor.putString("Lon", String.valueOf(location.getLongitude()));
                                editor.putString("Direccion", dir);
                                editor.commit();

                                LATITUD = String.valueOf(lo.getLatitude());
                                LONGITUD = String.valueOf(lo.getLongitude());

                                // Agregamos un marcador en Lima y movemos la cámara
                                mMap.clear();
                                agregaPoligono();
                                LatLng ubicacion = new LatLng(location.getLatitude(), location.getLongitude());
                                mMap.moveCamera(newLatLngZoom(ubicacion, 18.0f));
                                marker = new MarkerOptions();
                                marker.position(ubicacion);
                                marker.title("Entregar");
                                mMap.addMarker(marker);


                            }
                            progressDialog.dismiss();
                        }
                    });
        }
    }

    MarkerOptions marker;

    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] grantResults) {
        if (requestCode == REQUEST_LOCATION) {
            if (grantResults.length == 2
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                locationSend();
                if (mMap != null) {
                    mMap.setMyLocationEnabled(true);
                }
            } else {
                Toast.makeText(this, "Por favor es necesario activar gps para mejor experiencia al usar ACMarket", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void cancela(View view) {
        finish();
    }

    boolean primera = false;

    @Override
    public void onLocationChanged(Location location) {
        //Toast.makeText(this,"Location: "+location,Toast.LENGTH_SHORT).show();
        if (location != null && primera) {
            primera = false;
            lo = location;
            String dir = Utils.getDireccion(getApplicationContext(), location);
            SharedPreferences preferencias = getSharedPreferences("VARIABLES", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferencias.edit();
            editor.putString("Lat", String.valueOf(location.getLatitude()));
            editor.putString("Lon", String.valueOf(location.getLongitude()));
            editor.putString("Direccion", dir);
            editor.commit();

            LATITUD = String.valueOf(lo.getLatitude());
            LONGITUD = String.valueOf(lo.getLongitude());

            // Agregamos un marcador en Lima y movemos la cámara
            mMap.clear();
            agregaPoligono();
            LatLng ubicacion = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.moveCamera(newLatLngZoom(ubicacion, 12.0f));
            marker = new MarkerOptions();
            marker.position(ubicacion);
            marker.title("Entregar");
            mMap.addMarker(marker);

        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
        //Toast.makeText(this,"onStatusChanged ",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderEnabled(String s) {
        //Toast.makeText(this,"onProviderEnabled "+s,Toast.LENGTH_SHORT).show();
        if (mMap != null)
            mMap.setMyLocationEnabled(true);
    }

    @Override
    public void onProviderDisabled(String s) {
        Toast.makeText(this, s + " deshabilitado por favor habilitar para mejorar funcionanmiento " + s, Toast.LENGTH_SHORT).show();
        if (mMap != null)
            mMap.setMyLocationEnabled(false);

        enableLocationUser();

    }

    private static final int COLOR_BLACK_ARGB = 0xff000000;
    private static final int COLOR_WHITE_ARGB = 0xccffffff;
    private static final int COLOR_GREEN_ARGB = 0x2200D891;
    private static final int COLOR_PURPLE_ARGB = 0xff81C784;
    private static final int COLOR_ORANGE_ARGB = 0x11f27640;
    private static final int COLOR_BLUE_ARGB = 0xffF9A825;

    private static final int POLYGON_STROKE_WIDTH_PX = 1;
    private static final int PATTERN_DASH_LENGTH_PX = 20;
    private static final int PATTERN_GAP_LENGTH_PX = 20;
    private static final PatternItem DOT = new Dot();
    private static final PatternItem DASH = new Dash(PATTERN_DASH_LENGTH_PX);
    private static final PatternItem GAP = new Gap(PATTERN_GAP_LENGTH_PX);

    // Create a stroke pattern of a gap followed by a dash.
    private static final List<PatternItem> PATTERN_POLYGON_ALPHA = Arrays.asList(GAP, DASH);

    // Create a stroke pattern of a dot followed by a gap, a dash, and another gap.
    private static final List<PatternItem> PATTERN_POLYGON_BETA =
            Arrays.asList(DOT, GAP, DASH, GAP);

    private void stylePolygon(Polygon polygon) {
        String type = "alpha";
        // Get the data object stored with the polygon.
        if (polygon.getTag() != null) {
            type = polygon.getTag().toString();
        }
        type = "beta";
        List<PatternItem> pattern = null;
        int strokeColor = COLOR_BLACK_ARGB;
        int fillColor = COLOR_WHITE_ARGB;

        switch (type) {
            // If no type is given, allow the API to use the default.
            case "alpha":
                // Apply a stroke pattern to render a dashed line, and define colors.
                pattern = PATTERN_POLYGON_ALPHA;
                strokeColor = COLOR_GREEN_ARGB;
                fillColor = COLOR_PURPLE_ARGB;
                break;
            case "beta":
                // Apply a stroke pattern to render a line of dots and dashes, and define colors.
                pattern = PATTERN_POLYGON_BETA;
                strokeColor = COLOR_WHITE_ARGB;
                fillColor = COLOR_GREEN_ARGB;
                break;
        }

        //polygon.setStrokePattern(pattern);
        polygon.setStrokeWidth(POLYGON_STROKE_WIDTH_PX);
        polygon.setStrokeColor(strokeColor);
        polygon.setFillColor(fillColor);
    }

    public boolean estaDentro(List<LatLng> puntos, LatLng latLng) {

        if (!puntos.contains(latLng)) {
            return false;
        }

        int numPoints = puntos.size();
        boolean inPoly = false;
        int i;
        int j = numPoints - 1;

        for (i = 0; i < numPoints; i++) {
            LatLng vertex1 = puntos.get(i);
            LatLng vertex2 = puntos.get(j);

            if (vertex1.longitude < latLng.longitude && vertex2.longitude >= latLng.longitude || vertex2.longitude < latLng.longitude && vertex1.longitude >= latLng.longitude) {
                if (vertex1.latitude + (latLng.latitude - vertex1.latitude) / (vertex2.latitude - vertex1.latitude) * (vertex2.latitude - vertex1.latitude) < latLng.latitude) {
                    inPoly = !inPoly;
                }
            }

            j = i;
        }

        return inPoly;
    }

    ;

    public void obtenerPoligonosLista() {

        final ProgressDialog progressDialog = new ProgressDialog(ConfirmaDireccionActivity.this,
                R.style.Theme_AppCompat_Light_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Obteniendo zonas disponibles...");
        progressDialog.show();

        RequestQueue requstQueue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL_API1, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            if(progressDialog!=null){
                                progressDialog.dismiss();
                            }
                            //btnAgregaPedido.setEnabled(true);
                            //btnCancela.setEnabled(true);

                            int result = (int) response.get("resultado");

                            if (result == 1) {

                                int[] pksObtenidos;

                                JSONArray poligonosJson = response.getJSONArray("poligonos");
                                int i = 0;
                                PoligonoModel aux;
                                pksObtenidos = new int[poligonosJson.length()];
                                for (i = 0; i < poligonosJson.length(); i++) {
                                    JSONObject obj = poligonosJson.getJSONObject(i);
                                    aux = new PoligonoModel();
                                    aux.PK = obj.getInt("pk");
                                    aux.NOMBRE = obj.getString("nombre");
                                    aux.POLIGONO_VERSION = obj.getInt("poligonO_VERSION");
                                    aux.FECHA_C = obj.getString("fechA_C");
                                    POLIGONOS.add(aux);
                                    pksObtenidos[i] = aux.PK;
                                }

                                //obtengo los poligonos que ya no estan en la base
                                List<PoligonoModel> lista = db.PoligonoModelDao().getAllByNotContainsPks(pksObtenidos);

                                //Borro los poligonos que ya no estan el el servidor
                                for (PoligonoModel po : lista) {
                                    db.CoordenadasModelDao().deleteCoordenadasByPkPoligono(po.PK);
                                    db.PoligonoModelDao().delete(po);
                                }

                                //Obtengo los poligonos que si existen y comparo la version si es mayor entonces los guardo en la lista de los que se tienen que actualizar
                                List<PoligonoModel> listaActualizar = new ArrayList<>();
                                for (PoligonoModel p1 : POLIGONOS) {
                                    PoligonoModel p2 = db.PoligonoModelDao().loadPoligonoByPk(p1.PK);
                                    if (p2 == null) {
                                        listaActualizar.add(p1);
                                    } else if (p1.POLIGONO_VERSION > p2.POLIGONO_VERSION) {
                                        listaActualizar.add(p1);
                                    }
                                }

                                //Si hay poligonos para actualizar los mando a el servicio para traerme su detalle
                                if (listaActualizar.size() > 0) {
                                    actualizaPoligonos(listaActualizar);
                                }

                            }

                        } catch (JSONException e) {
                            btnAgregaPedido.setEnabled(true);
                            btnCancela.setEnabled(true);
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Log.e("Rest Response", error.toString());
                    }
                }
        ) {
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

    public void actualizaPoligonos(List<PoligonoModel> poligonoActualizar) {

        final ProgressDialog progressDialog = new ProgressDialog(ConfirmaDireccionActivity.this,
                R.style.Theme_AppCompat_Light_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Obteniendo detalle de zonas disponibles...");
        progressDialog.show();

        JSONObject datosObj = new JSONObject();
        try {
            JSONArray objetos = new JSONArray();
            JSONObject aux;
            for (PoligonoModel po : poligonoActualizar) {
                aux = new JSONObject();
                aux.put("PK", po.PK);
                objetos.put(aux);
            }
            datosObj.put("POLIGONOS", objetos);


            RequestQueue requstQueue = Volley.newRequestQueue(this);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL_API2, datosObj,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            try {
                                progressDialog.dismiss();
                                //btnAgregaPedido.setEnabled(true);
                                //btnCancela.setEnabled(true);

                                int result = (int) response.get("resultado");

                                if (result == 1) {

                                    List<PoligonoModel> listaObtenidos = new ArrayList<>();
                                    PoligonoModel aux;
                                    JSONArray poligonosArrayJson = response.getJSONArray("poligonos");
                                    int i;
                                    int j;
                                    for (i = 0; i < poligonosArrayJson.length(); i++) {
                                        JSONObject obj = poligonosArrayJson.getJSONObject(i);
                                        aux = new PoligonoModel();
                                        aux.PK = obj.getInt("pk");
                                        aux.NOMBRE = obj.getString("nombre");
                                        aux.POLIGONO_VERSION = obj.getInt("poligonO_VERSION");
                                        aux.FECHA_C = obj.getString("fechA_C");
                                        aux.COORDENADAS = new ArrayList<>();
                                        JSONArray coordenadasJson = obj.getJSONArray("coordenadas");
                                        CoordenadasModel aux2;
                                        for (j = 0; j < coordenadasJson.length(); j++) {
                                            JSONObject obj2 = coordenadasJson.getJSONObject(j);
                                            aux2 = new CoordenadasModel();
                                            aux2.PK = obj2.getInt("pk");
                                            aux2.PK_POLIGONO = obj2.getInt("pK_POLIGONO");
                                            aux2.LATITUD = obj2.getDouble("latitud");
                                            aux2.LONGITUD = obj2.getDouble("longitud");
                                            aux2.FECHA_C = obj2.getString("fechA_C");
                                            aux.COORDENADAS.add(aux2);
                                        }

                                        listaObtenidos.add(aux);
                                        if (db.PoligonoModelDao().countPoligonoByPk(aux.PK) > 0) {
                                            db.CoordenadasModelDao().deleteCoordenadasByPkPoligono(aux.PK);
                                            db.CoordenadasModelDao().insertAll(aux.COORDENADAS);
                                            db.PoligonoModelDao().update(aux);
                                        } else {
                                            List<PoligonoModel> listInser = new ArrayList<>();
                                            listInser.add(aux);
                                            db.PoligonoModelDao().insertAll(listInser);
                                            db.CoordenadasModelDao().insertAll(aux.COORDENADAS);
                                        }
                                    }
                                    actualizaDibujoPoligonos();
                                }
                            } catch (JSONException e) {
                                //btnAgregaPedido.setEnabled(true);
                                //btnCancela.setEnabled(true);
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressDialog.dismiss();
                            Log.e("Rest Response", error.toString());
                        }
                    }
            ) {
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
        } catch (Exception e) {
        }
    }

    List<Polygon> POLIGONOS_LIST;

    public void actualizaDibujoPoligonos() {
        POLIGONOS_LIST = new ArrayList<>();
        List<LatLng> puntos = new ArrayList<>();
        List<Integer>pks=new ArrayList<>();
        try{
        pks.add(Integer.parseInt(TIENDA_OBJ.LUNES));}catch (Exception e){}
        try{pks.add(Integer.parseInt(TIENDA_OBJ.MARTES));}catch (Exception e){}
        try{pks.add(Integer.parseInt(TIENDA_OBJ.MIERCOLES)); }catch (Exception e){}
        try{pks.add(Integer.parseInt(TIENDA_OBJ.JUEVES));}catch (Exception e){}
        try{pks.add(Integer.parseInt(TIENDA_OBJ.VIERNES));}catch (Exception e){}
        try{pks.add(Integer.parseInt(TIENDA_OBJ.SABADO));}catch (Exception e){}
        try{pks.add(Integer.parseInt(TIENDA_OBJ.DOMINGO));}catch (Exception e){}
        List<PoligonoModel> poligonosList = db.PoligonoModelDao().getAllByContainsPks(pks);
        int i, j;
        for (i = 0; i < poligonosList.size(); i++) {

            PoligonoModel aux = poligonosList.get(i);
            aux.COORDENADAS = db.CoordenadasModelDao().loadAllByPkPoligono(aux.PK);
            puntos = new ArrayList<>();
            for (CoordenadasModel cor : aux.COORDENADAS) {

                puntos.add(new LatLng(cor.LATITUD, cor.LONGITUD));
            }

            Polygon polygon1 = mMap.addPolygon(new PolygonOptions()
                    .clickable(true)
                    .addAll(puntos));
            polygon1.setTag(aux.PK);
            stylePolygon(polygon1);

            POLIGONOS_LIST.add(polygon1);

        }
/*
        Polygon polygon1 = mMap.addPolygon(new PolygonOptions()
                .clickable(true)
                .addAll(lineasPoligono.getpuntos()));
        polygon1.setTag("beta");
        stylePolygon(polygon1);
*/
        conServicio = false;
        for (i = 0; i < POLIGONOS_LIST.size(); i++) {
            if (containsLocation(marker.getPosition(), POLIGONOS_LIST.get(i).getPoints(), true)) {
                POLIGONO=POLIGONOS_LIST.get(i).getTag().toString();
                btnAgregaPedido.setEnabled(true);
                conServicio = true;
                if(PK_COSTO_ENVIO.equals("2")){//todo_sergio100 HARDCODE SI ES 2 .- EXPRESS SE ENTREGA MAÑANA
                    DIA_ENTREGA=TIENDA_OBJ.ENTREGA_EXPRESS;
                }else {//SINO ENTONCES EL DIA QUE CORRESPONDE AL POLIGONO
                    if(POLIGONO.equals(TIENDA_OBJ.LUNES)){DIA_ENTREGA=TIENDA_OBJ.ENTREGA_LUNES;}
                    else if(POLIGONO.equals(TIENDA_OBJ.MARTES)){DIA_ENTREGA=TIENDA_OBJ.ENTREGA_MARTES;}
                    else if(POLIGONO.equals(TIENDA_OBJ.MIERCOLES)){DIA_ENTREGA=TIENDA_OBJ.ENTREGA_MIERCOLES;}
                    else if(POLIGONO.equals(TIENDA_OBJ.JUEVES)){DIA_ENTREGA=TIENDA_OBJ.ENTREGA_JUEVES;}
                    else if(POLIGONO.equals(TIENDA_OBJ.VIERNES)){DIA_ENTREGA=TIENDA_OBJ.ENTREGA_VIERNES;}
                    else if(POLIGONO.equals(TIENDA_OBJ.SABADO)){DIA_ENTREGA=TIENDA_OBJ.ENTREGA_SABADO;}
                    else if(POLIGONO.equals(TIENDA_OBJ.DOMINGO)){DIA_ENTREGA=TIENDA_OBJ.ENTREGA_DOMINGO;}
                }

                //Toast.makeText(this, "contenido: ", Toast.LENGTH_SHORT).show();
            }/*else{
                btnAgregaPedido.setEnabled(false);
                conServicio=false;
                Toast.makeText(this,"no",Toast.LENGTH_SHORT).show();
            }*/
        }

    }

    String POLIGONO="-1",DIA_ENTREGA="",PK_COSTO_ENVIO;

    public class getDireccionBack extends AsyncTask<Location, Void, Void> {
        Context mContext;
        TextView tvDireccion;
        Location locationOld;
        String dir = "";

        public getDireccionBack(Context mContext, TextView tvDireccion,Location locationOldpara) {
            this.mContext = mContext;
            this.tvDireccion = tvDireccion;
            this.locationOld=locationOldpara;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Location... params) {

            locationOld = params[0];

            if (locationOld == null) {
                locationOld = new Location("GSM");
            }
            locationOld.setLatitude(marker.getPosition().latitude);
            locationOld.setLongitude(marker.getPosition().longitude);
            dir = Utils.getDireccion(mContext, locationOld);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(locationOld.getLatitude()==lo.getLatitude() && locationOld.getLongitude()==lo.getLongitude()){
                tvDireccion.setText(dir);
                agregaPoligono();
            }
        }
    }


    public class getDireccionBackFromTv extends AsyncTask<String, String, String> {
        Context mContext;
        ProgressDialog progressDialog;
        TextView tvDireccion;
        String dir = "";

        public getDireccionBackFromTv(Context mContext, ProgressDialog progressDialog, TextView tvDireccion) {
            this.mContext = mContext;
            this.progressDialog = progressDialog;
            this.tvDireccion = tvDireccion;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(mContext,
                    R.style.Theme_AppCompat_Light_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setMessage("Obteniendo direcciónes...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            String lo = params[0];

            List<DireccionesModel> lista = Utils.getLatLng0(mContext, lo);
            return lista.get(0).DIRECCION;
        }

        @Override
        protected void onPostExecute(String string) {
            super.onPostExecute(string);
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            tvDireccion.setText(string);
            agregaPoligono();
        }
    }


    int AUTOCOMPLETE_REQUEST_CODE = 1;
    List<Place.Field> fields;

    public void getDir(View view) {
/*
        fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);

        List<String> lista = new ArrayList<>();
        lista.add("MX");
        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN, fields).setCountries(lista)
                .setTypeFilter(TypeFilter.ADDRESS)
                .build(this);
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
*/
        preguntaDireccion();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Log.i("SCH", "Place: " + place.getName() + ", " + place.getId());

                if(place!=null) {
                    mMap.clear();
                    agregaPoligono();

                    lo.setLatitude(place.getLatLng().latitude);
                    lo.setLongitude(place.getLatLng().longitude);
                    tvDireccion.setText("" + place.getAddress());
                    marker = new MarkerOptions();
                    marker.position(new LatLng(place.getLatLng().latitude, place.getLatLng().longitude));
                    marker.title("Entregar");
                    mMap.addMarker(marker);
                    mMap.moveCamera(newLatLngZoom(new LatLng(place.getLatLng().latitude, place.getLatLng().longitude), 18.0f));
                }

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i("SCH", status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }

            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(tvDireccion.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(btnBuscar.getWindowToken(), 0);

        }else if (requestCode == REQUEST_CODE_DIRECCION) {
            if(progressDialog !=null && progressDialog.isShowing()){
                progressDialog.dismiss();
            }
            String latitud="0",direccion="",longitud="0";
            if (resultCode == RESULT_OK) {
                String returnedResult = data.getData().toString();
                try {
                    JSONObject obj=new JSONObject(returnedResult);
                    direccion=obj.getString("DIRECCION");
                    if(!direccion.equals("Mi ubicaciòn")){
                        latitud=obj.getString("LATITUD");
                        longitud=obj.getString("LONGITUD");
                        mMap.clear();
                        agregaPoligono();

                        lo.setLatitude(Double.valueOf(latitud));
                        lo.setLongitude(Double.valueOf(longitud));
                        tvDireccion.setText("" + direccion);
                        marker = new MarkerOptions();
                        marker.position(new LatLng(lo.getLatitude(), lo.getLongitude()));
                        marker.title("Entregar");
                        mMap.addMarker(marker);
                        mMap.moveCamera(newLatLngZoom(new LatLng(lo.getLatitude(), lo.getLongitude()), 18.0f));
                        TextoDefecto=true;
                    }else{
                        DIRECCION=direccion;
                        locationSend();
                    }
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(tvDireccion.getWindowToken(), 0);
                    imm.hideSoftInputFromWindow(btnBuscar.getWindowToken(), 0);

                } catch (JSONException e) {
                    e.printStackTrace();
                }



            }
        }
    }


    int REQUEST_CODE_DIRECCION = 100;
    ProgressDialog progressDialog;
    public void preguntaDireccion(){

        progressDialog = new ProgressDialog(this,
                R.style.Theme_AppCompat_Light_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Obteniendo direcciòn...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        Intent intento=new Intent(this,DireccionesActivity.class);
        startActivityForResult(intento,REQUEST_CODE_DIRECCION);

    }

    private void _ShowAlertOption(){

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        registraProductos();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };
        String mensaje="",mensaje1="";
        if(PK_COSTO_ENVIO.equals("2"))//todo_sergio100 HARDCODE SI ES 2 .- EXPRESS SE ENTREGA MAÑANA
        {
            String dia="";
            try{
                dia=DIA_ENTREGA.split(" ")[0];
            }catch (Exception e){

            }
            mensaje="Escogiste el servicio EXPRESS tu pedido te llegarà el dìa de mañana "+dia;
            mensaje1="Escogiste el servicio <b>EXPRESS</b> tu pedido te llegarà el dìa de mañana<br><b><u>"+dia+"</b></u>";
        }else{
            String dia="";
            try{
                dia=DIA_ENTREGA.split(" ")[0];
            }catch (Exception e){

            }
            mensaje="Con el servicio normal tus entregas SIEMPRE en esta ubicación seran los dias "+dia;
            mensaje1="Con el servicio <b>NORMAL</b> tus entregas <b>SIEMPRE</b> en esta ubicación serán los dias<br> <b><u>"+dia+"</b></u>";
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //builder.setMessage(mensaje+"\nTu pedido llegará: "+DIA_ENTREGA+" ¿deseas continuar?")
        builder.setMessage(Html.fromHtml( "<p style=\"text-align: center\">"+mensaje1+"<br>Tu pedido llegará:<br><b><u>"+DIA_ENTREGA+"</b></u><br> ¿deseas continuar?</p>"))
                .setPositiveButton("Si", dialogClickListener)
                .setNegativeButton("No", dialogClickListener)
                .setCancelable(false).show();
    }


}
