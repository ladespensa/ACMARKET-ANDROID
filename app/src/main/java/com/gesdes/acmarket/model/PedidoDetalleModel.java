package com.gesdes.acmarket.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "PedidoDetalleModel")
public class PedidoDetalleModel implements Serializable  {

    @PrimaryKey (autoGenerate = true)
    public int PK ;
    @ColumnInfo(name = "PK_PEDIDO")
    public String PK_PEDIDO;
    @ColumnInfo(name = "PK_PRODUCTO")
    public String PK_PRODUCTO;
    @ColumnInfo(name = "PRODUCTO")
    public String PRODUCTO;
    @ColumnInfo(name = "DESCRIPCION")
    public String DESCRIPCION;
    @ColumnInfo(name = "TIENDA")
    public String TIENDA;
    @ColumnInfo(name = "PRECIO")
    public double PRECIO;
    @ColumnInfo(name = "CANTIDAD")
    public double CANTIDAD;
    @ColumnInfo(name = "DETALLES")
    public String DETALLES;
    @ColumnInfo(name = "BORRADO")
    public String BORRADO;
    @ColumnInfo(name = "FECHA_C")
    public String FECHA_C;
    @ColumnInfo(name = "FECHA_M")
    public String FECHA_M;
    @ColumnInfo(name = "FECHA_D")
    public String FECHA_D;
    @ColumnInfo(name = "USUARIO_C")
    public String USUARIO_C;
    @ColumnInfo(name = "USUARIO_M")
    public String USUARIO_M;
    @ColumnInfo(name = "USUARIO_D")
    public String USUARIO_D;
    @Ignore
    public String IMAGEN;
}
