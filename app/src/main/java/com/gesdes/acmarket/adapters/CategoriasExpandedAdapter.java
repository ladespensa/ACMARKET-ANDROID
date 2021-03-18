package com.gesdes.acmarket.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.gesdes.acmarket.R;
import com.gesdes.acmarket.model.CategoriasModel;
import com.gesdes.acmarket.model.GruposCategoriasModel;
import com.gesdes.acmarket.utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CategoriasExpandedAdapter extends BaseExpandableListAdapter {

    List<GruposCategoriasModel>listaGrupos;
    Context mContext;

    public CategoriasExpandedAdapter(List<GruposCategoriasModel> listaGrupos, Context context) {
        this.listaGrupos = listaGrupos;
        this.mContext = context;
    }

    @Override
    public int getGroupCount() {
        return listaGrupos.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return 1;//listaGrupos.get(i).CATEGORIAS_LIST.size();
    }

    @Override
    public Object getGroup(int i) {
        return listaGrupos.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return listaGrupos.get(i).CATEGORIAS_LIST.get(i1);
    }

    @Override
    public long getGroupId(int i) {
        return listaGrupos.get(i).PK;
    }

    @Override
    public long getChildId(int i, int i1) {
        return Long.parseLong(listaGrupos.get(i).CATEGORIAS_LIST.get(i1).PK);
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        GruposCategoriasModel headerInfo = (GruposCategoriasModel) getGroup(i);
        if (view == null) {
            LayoutInflater inf = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inf.inflate(R.layout.item_group_header, null);
        }

        TextView heading = (TextView) view.findViewById(R.id.groupTitleCategorias);
        heading.setText(headerInfo.NOMBRE);

        return view;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        CategoriasModel detailInfo = (CategoriasModel) getChild(i,i1);
        List<CategoriasModel>list= ((GruposCategoriasModel)getGroup(i)).CATEGORIAS_LIST;
        if (view == null) {
            LayoutInflater inf = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inf.inflate(R.layout.item_grid_categorias_list, null);
        }
        /*
        ImageView ivCategoria = (ImageView) view.findViewById(R.id.ivCategoriaItemBusqueda);
        TextView titleCategoria = (TextView) view.findViewById(R.id.tvTitleItemCategoriaBusqueda);
        titleCategoria.setText(detailInfo.CLASIFICACION);
        if(!detailInfo.IMAGEN.isEmpty() ){
            Picasso.with(mContext).load(detailInfo.IMAGEN).placeholder(R.drawable.iv_placeholder)
                    .error(R.drawable.iv_placeholder).into(ivCategoria);
        }
        return view;*/
        GridView gv = (GridView) view.findViewById(R.id.itemGridViewCategoriasListBusqueda);
        gv.setAdapter(new CategoriasItemAdapter(mContext,list)); //Changed
        //gv.setMinimumHeight(list.size()/3*60);
        ViewGroup.LayoutParams layoutParams = gv.getLayoutParams();
        int tamaño= (list.size()%2==0)?list.size():list.size()+1;
        layoutParams.height = Utils.convertDpToPixels(tamaño*120/2,mContext); //this is in pixels
        gv.setLayoutParams(layoutParams);
        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }
}
