package com.gesdes.acmarket.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.List;

@Entity(tableName = "PoligonoModel")
public class PoligonoModel {
    @PrimaryKey(autoGenerate = false)
    public int PK;
    @ColumnInfo(name = "NOMBRE")
    public String NOMBRE;
    @ColumnInfo(name = "POLIGONO_VERSION")
    public int POLIGONO_VERSION;
    @ColumnInfo(name = "FECHA_C")
    public String FECHA_C;
    @Ignore
    public List<com.gesdes.acmarket.model.CoordenadasModel> COORDENADAS;
}