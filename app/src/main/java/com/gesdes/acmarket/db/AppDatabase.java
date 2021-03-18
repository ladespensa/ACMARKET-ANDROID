package com.gesdes.acmarket.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.gesdes.acmarket.model.CoordenadasModel;
import com.gesdes.acmarket.model.CoordenadasModelDao;
import com.gesdes.acmarket.model.PedidoModelDao;
import com.gesdes.acmarket.model.PedidoDetalleDao;
import com.gesdes.acmarket.model.PedidoDetalleModel;
import com.gesdes.acmarket.model.PedidoModel;
import com.gesdes.acmarket.model.PoligonoModel;
import com.gesdes.acmarket.model.PoligonoModelDao;

@Database(entities = {PedidoModel.class,PedidoDetalleModel.class, PoligonoModel.class, CoordenadasModel.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract PedidoModelDao PedidoDao();
    public abstract PedidoDetalleDao PedidoDetalleDao();
    public abstract PoligonoModelDao PoligonoModelDao ();
    public abstract CoordenadasModelDao CoordenadasModelDao();
}

