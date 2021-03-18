package com.gesdes.acmarket.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "CoordenadasModel")
public class CoordenadasModel {
    @PrimaryKey(autoGenerate = false)
    public int PK;
    @ColumnInfo(name="PK_POLIGONO")
    public int PK_POLIGONO;
    @ColumnInfo(name = "NOMBRE")
    public Double LATITUD;
    @ColumnInfo(name = "POLIGONO_VERSION")
    public Double LONGITUD;
    @ColumnInfo(name = "FECHA_C")
    public String FECHA_C;

}
