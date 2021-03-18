package com.gesdes.acmarket.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.graphics.drawable.DrawableCompat;

import com.gesdes.acmarket.R;
import com.gesdes.acmarket.model.CategoriasModel;
import com.gesdes.acmarket.model.DireccionesModel;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class DireccionesAdapter extends BaseAdapter {
    List<DireccionesModel> listaDir;
    Context mContext;

    public DireccionesAdapter(List<DireccionesModel> listaDir, Context mContext) {
        this.listaDir = listaDir;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return listaDir.size();
    }

    @Override
    public Object getItem(int i) {
        return listaDir.get(i);
    }

    @Override
    public long getItemId(int i) {
        return listaDir.get(i).PK;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final DireccionesModel dir=listaDir.get(i);

        if (view == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            view = layoutInflater.inflate(R.layout.item_direcciones, null);
        }

        TextView tvDireccion=view.findViewById(R.id.itemTextDireccion);
        ImageView ivDireccion=view.findViewById(R.id.ivItemDireccion);
        tvDireccion.setText(""+dir.DIRECCION);
        if(!dir.IMAGEN.isEmpty() && dir.IMAGEN.equals("gps")){
            Drawable da= view.getResources().getDrawable( R.drawable.gps);
            Drawable wrappedDrawable = DrawableCompat.wrap(da);
            DrawableCompat.setTint(wrappedDrawable, view.getResources().getColor(R.color.green_light));
            ivDireccion.setImageDrawable(da);
            /*
            Picasso.with(mContext).load(dir.IMAGEN).placeholder(R.drawable.iv_placeholder)
                    .error(R.drawable.iv_placeholder).into(ivDireccion);*/
        }else if(!dir.IMAGEN.isEmpty() && dir.IMAGEN.equals("dir")){
            Drawable da= view.getResources().getDrawable( R.drawable.marker);
            ivDireccion.setImageDrawable(da);
        }
        /*
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject bolsa=new JSONObject();

                if(dir.LAT_LNG!=null){
                    try {
                        bolsa.put("LATITUD",dir.LAT_LNG.latitude);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        bolsa.put("LATITUD",dir.LAT_LNG.longitude);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    bolsa.put("DIRECCION",dir.DIRECCION);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Intent data = new Intent();
                String text = "Result to be returned...."
                data.setData(Uri.parse(text));
                mContext.setResult(.RESULT_OK, data);
                finish();
                //Toast.makeText(mContext,"Lat:"+dir.LAT_LNG.latitude+", Lng:"+dir.LAT_LNG.longitude,Toast.LENGTH_SHORT).show();
            }
        });*/

        return view;
    }
}
