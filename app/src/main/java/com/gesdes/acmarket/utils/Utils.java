package com.gesdes.acmarket.utils;

import android.content.ContentProvider;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;
import android.util.TypedValue;

import androidx.annotation.NonNull;

import com.gesdes.acmarket.R;
import com.gesdes.acmarket.activities.ProductosListActivity;
import com.gesdes.acmarket.model.DireccionesModel;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class Utils {
    public static String getDireccion(Context context, Location loc) {
        String direccion="";
        //Obtener la direccion de la calle a partir de la latitud y la longitud
        if (loc.getLatitude() != 0.0 && loc.getLongitude() != 0.0) {
            try {
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                List<Address> list = geocoder.getFromLocation(
                        loc.getLatitude(), loc.getLongitude(), 1);
                if (!list.isEmpty()) {
                    Address DirCalle = list.get(0);
                    direccion=DirCalle.getAddressLine(0);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return direccion;
    }

    public static List<DireccionesModel> getLatLng0(Context context, String direccion) {
        List<DireccionesModel>lista=new ArrayList<>();
        DireccionesModel aux;
        if (!direccion.isEmpty()) {
            try {

                Locale[] locales = Locale.getAvailableLocales();
                for (Locale localeIn : locales) {
                    String country= localeIn.getCountry();
                    String langua=localeIn.getLanguage();

                }
                Locale locale=new Locale("es","MEX");
                Geocoder geocoder = new Geocoder(context, locale);
                List<Address> list = geocoder.getFromLocationName(direccion,1);
                if (!list.isEmpty()) {
                    for (Address dir:list) {
                        LatLng lo=new LatLng(dir.getLatitude(),dir.getLongitude());
                        aux=new DireccionesModel();
                        aux.LAT_LNG=lo;
                        aux.DIRECCION=dir.getAddressLine(0);
                        aux.IMAGEN="dir";
                        lista.add(aux);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return lista;
    }

    public static DireccionesModel getLatLngFromPlaceId(Context context, String placeId,String apiPlaces) {
        final DireccionesModel aux=new DireccionesModel();
        if (!placeId.isEmpty()) {
            try {

                // Initialize the SDK
                Places.initialize(context,apiPlaces);

                // Create a new Places client instance
                PlacesClient placesClient = Places.createClient(context);

                // Define a Place ID.
                //String placeId = "INSERT_PLACE_ID_HERE";
                // Specify the fields to return.
                List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);
                // Construct a request object, passing the place ID and fields array.
                FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, placeFields);

                Task<FetchPlaceResponse> pl= placesClient.fetchPlace(request);
                try {
                    Tasks.await(pl, 20000, TimeUnit.SECONDS);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (TimeoutException e) {
                    e.printStackTrace();
                }

                if(pl.isSuccessful()){
                    Place place= pl.getResult().getPlace();
                    aux.LAT_LNG=place.getLatLng();
                }

                /*
                placesClient.fetchPlace(request).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
                    @Override
                    public void onSuccess(FetchPlaceResponse fetchPlaceResponse) {
                        Place place = fetchPlaceResponse.getPlace();
                        aux.LAT_LNG=place.getLatLng();
                        Log.i("SCH_PLA", "Place found: " + place.getName());
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (e instanceof ApiException) {
                            ApiException apiException = (ApiException) e;
                            int statusCode = apiException.getStatusCode();
                            // Handle error with given status code.
                            Log.e("SCH_PLA", "Place not found: " + e.getMessage());
                        }
                    }
                });
            */
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return aux;
    }

    public static List<DireccionesModel> getPrediccion(Context context, String direccion,String apiPlaces) {
        final String TAG="SCH_DIR";
        final List<DireccionesModel>lista=new ArrayList<>();

        if (!direccion.isEmpty()) {
            try {

                // Initialize the SDK
                Places.initialize(context,apiPlaces);

                // Create a new Places client instance
                PlacesClient placesClient = Places.createClient(context);

                // Create a new token for the autocomplete session. Pass this to FindAutocompletePredictionsRequest,
                // and once again when the user makes a selection (for example when calling fetchPlace()).
                AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();
                List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS,Place.Field.LAT_LNG);

                // Create a RectangularBounds object.
                /*RectangularBounds bounds = RectangularBounds.newInstance(
                        new LatLng(-33.880490, 151.184363),
                        new LatLng(-33.858754, 151.229596));
                */
                // Use the builder to create a FindAutocompletePredictionsRequest.
                FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                        // Call either setLocationBias() OR setLocationRestriction().
                        //.setLocationBias(bounds)
                        //.setLocationRestriction(bounds)
                        //.setOrigin(new LatLng(-33.8749937, 151.2041382))
                        .setCountries("MX")
                        .setTypeFilter(TypeFilter.ADDRESS)
                        .setSessionToken(token)
                        .setQuery(direccion)
                        .build();

                Task<FindAutocompletePredictionsResponse> prediction= placesClient.findAutocompletePredictions(request);
                try {
                    Tasks.await(prediction, 20000, TimeUnit.SECONDS);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (TimeoutException e) {
                    e.printStackTrace();
                }

                if (prediction.isSuccessful()) {
                    DireccionesModel aux;

                    for (AutocompletePrediction prediction1 : prediction.getResult().getAutocompletePredictions()) {
                        Log.i(TAG, prediction1.getPlaceId());
                        Log.i(TAG, prediction1.getPrimaryText(null).toString());
                        aux=new DireccionesModel();
                        aux.DIRECCION=prediction1.getFullText(null).toString();
                        aux.IMAGEN="dir";
                        aux.PLACE_ID=prediction1.getPlaceId();
                        lista.add(aux);
                    }
                }

                /*
                placesClient.findAutocompletePredictions(request).addOnSuccessListener(new OnSuccessListener<FindAutocompletePredictionsResponse>() {
                    @Override
                    public void onSuccess(FindAutocompletePredictionsResponse findAutocompletePredictionsResponse) {
                        DireccionesModel aux;

                        for (AutocompletePrediction prediction : findAutocompletePredictionsResponse.getAutocompletePredictions()) {
                            Log.i(TAG, prediction.getPlaceId());
                            Log.i(TAG, prediction.getPrimaryText(null).toString());
                            aux=new DireccionesModel();
                            aux.DIRECCION=prediction.getFullText(null).toString();
                            aux.IMAGEN="dir";
                            aux.PLACE_ID=prediction.getPlaceId();
                            lista.add(aux);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (e instanceof ApiException) {
                            ApiException apiException = (ApiException) e;
                            Log.e(TAG, "Place not found: " + apiException.getStatusCode());
                        }
                    }
                });
*/
            }catch (Exception e){
                String error=e.toString();
            }
        }
        return lista;
    }

    public static List<DireccionesModel> getPrediccionLatLong(Context context, String direccion,String apiPlaces) {
        final String TAG="SCH_DIR";
        final List<DireccionesModel>lista=new ArrayList<>();

        if (!direccion.isEmpty()) {
            try {

                // Initialize the SDK
                Places.initialize(context,apiPlaces);

                // Create a new Places client instance
                PlacesClient placesClient = Places.createClient(context);

                // Create a new token for the autocomplete session. Pass this to FindAutocompletePredictionsRequest,
                // and once again when the user makes a selection (for example when calling fetchPlace()).
                AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();
                List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS,Place.Field.LAT_LNG);

                // Create a RectangularBounds object.
                /*RectangularBounds bounds = RectangularBounds.newInstance(
                        new LatLng(-33.880490, 151.184363),
                        new LatLng(-33.858754, 151.229596));
                */
                // Use the builder to create a FindAutocompletePredictionsRequest.
                FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                        // Call either setLocationBias() OR setLocationRestriction().
                        //.setLocationBias(bounds)
                        //.setLocationRestriction(bounds)
                        //.setOrigin(new LatLng(-33.8749937, 151.2041382))
                        .setCountries("MX")
                        .setTypeFilter(TypeFilter.ADDRESS)
                        .setSessionToken(token)
                        .setQuery(direccion)
                        .build();

                Task<FindAutocompletePredictionsResponse> prediction= placesClient.findAutocompletePredictions(request);
                try {
                    Tasks.await(prediction, 20000, TimeUnit.SECONDS);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (TimeoutException e) {
                    e.printStackTrace();
                }

                if (prediction.isSuccessful()) {
                    DireccionesModel aux;

                    for (AutocompletePrediction prediction1 : prediction.getResult().getAutocompletePredictions()) {
                        Log.i(TAG, prediction1.getPlaceId());
                        Log.i(TAG, prediction1.getPrimaryText(null).toString());
                        aux=new DireccionesModel();
                        aux.DIRECCION=prediction1.getFullText(null).toString();
                        aux.IMAGEN="dir";
                        aux.PLACE_ID=prediction1.getPlaceId();
                        lista.add(aux);
                    }
                }

                /*
                placesClient.findAutocompletePredictions(request).addOnSuccessListener(new OnSuccessListener<FindAutocompletePredictionsResponse>() {
                    @Override
                    public void onSuccess(FindAutocompletePredictionsResponse findAutocompletePredictionsResponse) {
                        DireccionesModel aux;

                        for (AutocompletePrediction prediction : findAutocompletePredictionsResponse.getAutocompletePredictions()) {
                            Log.i(TAG, prediction.getPlaceId());
                            Log.i(TAG, prediction.getPrimaryText(null).toString());
                            aux=new DireccionesModel();
                            aux.DIRECCION=prediction.getFullText(null).toString();
                            aux.IMAGEN="dir";
                            aux.PLACE_ID=prediction.getPlaceId();
                            lista.add(aux);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (e instanceof ApiException) {
                            ApiException apiException = (ApiException) e;
                            Log.e(TAG, "Place not found: " + apiException.getStatusCode());
                        }
                    }
                });
*/
            }catch (Exception e){
                String error=e.toString();
            }
        }
        return lista;
    }


/*
    public static String getDireccion(Context context, LatLng loc) {
        String direccion="";
        //Obtener la direccion de la calle a partir de la latitud y la longitud
        if (loc.lat != 0.0 && loc.lng != 0.0) {
            try {
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                List<Address> list = geocoder.getFromLocation(
                        loc.lat, loc.lng, 1);
                if (!list.isEmpty()) {
                    Address DirCalle = list.get(0);
                    direccion=DirCalle.getAddressLine(0);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return direccion;
    }*/

    public static int convertDpToPixels(float dp, Context context){
        Resources resources = context.getResources();
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                resources.getDisplayMetrics()
        );
    }

    public static Bitmap  drawable_from_url(String url) throws java.net.MalformedURLException, java.io.IOException {

        HttpURLConnection connection = (HttpURLConnection)new URL(url) .openConnection();
        connection.setRequestProperty("User-agent","Mozilla/4.0");

        connection.connect();
        InputStream input = connection.getInputStream();

        return BitmapFactory.decodeStream(input);
    }

    /** Returns a Drawable object containing the image located at 'imageWebAddress' if successful, and null otherwise.
     * (Pre: 'imageWebAddress' is non-null and non-empty;
     * method should not be called from the main/ui thread.)*/
    public static Drawable createDrawableFromUrl(String imageWebAddress)
    {
        Drawable drawable = null;

        try
        {
            InputStream inputStream = new URL(imageWebAddress).openStream();
            drawable = Drawable.createFromStream(inputStream, null);
            inputStream.close();
        }
        catch (MalformedURLException ex) {
            String error=ex.toString();
            Log.e("SCH",error); }
        catch (IOException ex) {
            String error=ex.toString();
            Log.e("SCH",error);
        }

        return drawable;
    }

    /** Returns a Bitmap object containing the image located at 'imageWebAddress'
     * if successful, and null otherwise.
     * (Pre: 'imageWebAddress' is non-null and non-empty;
     * method should not be called from the main/ui thread.)*/
    public static Bitmap createBitmapFromUrl(String imageWebAddress)
    {
        Bitmap bitmap = null;

        try
        {
            InputStream inputStream = new URL(imageWebAddress).openStream();
            bitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();
        }
        catch (MalformedURLException ex) { }
        catch (IOException ex) { }

        return bitmap;
    }

}
