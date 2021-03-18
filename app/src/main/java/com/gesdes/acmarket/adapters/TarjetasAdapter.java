package com.gesdes.acmarket.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.graphics.drawable.DrawableCompat;

import com.gesdes.acmarket.R;
import com.gesdes.acmarket.model.DireccionesModel;
import com.gesdes.acmarket.model.TarjetasModel;

import java.util.List;

public class TarjetasAdapter extends BaseAdapter {
    List<TarjetasModel>Tarjetas;
    Context mContext;

    public TarjetasAdapter(List<TarjetasModel> tarjetas, Context mContext) {
        Tarjetas = tarjetas;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return Tarjetas.size();
    }

    @Override
    public Object getItem(int i) {
        return Tarjetas.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final TarjetasModel tarjeta=Tarjetas.get(i);

        if (view == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            view = layoutInflater.inflate(R.layout.item_tarjeta, null);
        }

        TextView tvDireccion=view.findViewById(R.id.tvTarjetaNumberitem);
        ImageView ivDireccion=view.findViewById(R.id.ivTarjetaBrandItem);
        tvDireccion.setText(""+tarjeta.CARD_NUMBRE);
        if(!tarjeta.BRAND.isEmpty() && tarjeta.BRAND.toLowerCase().equals("visa")){
            Drawable da= view.getResources().getDrawable( R.drawable.visa_24w);
            Drawable wrappedDrawable = DrawableCompat.wrap(da);
            ivDireccion.setImageDrawable(da);
        }else if(!tarjeta.BRAND.isEmpty() && tarjeta.BRAND.toLowerCase().equals("mastercard")){
            Drawable da= view.getResources().getDrawable( R.drawable.mastercard_24);
            ivDireccion.setImageDrawable(da);
        }else if(!tarjeta.BRAND.isEmpty() && tarjeta.BRAND.toLowerCase().equals("efectivo")){
            Drawable da= view.getResources().getDrawable( R.drawable.money);
            ivDireccion.setImageDrawable(da);
        }else if(!tarjeta.BRAND.isEmpty() && tarjeta.BRAND.toLowerCase().equals("terminal")){
            Drawable da= view.getResources().getDrawable( R.drawable.negocio);
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
