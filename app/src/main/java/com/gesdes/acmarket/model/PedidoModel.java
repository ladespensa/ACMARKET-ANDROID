package com.gesdes.acmarket.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.List;

@Entity(tableName = "PedidoModel")
public class PedidoModel implements Serializable {

    @PrimaryKey (autoGenerate = true)
    public int PK;
    @ColumnInfo(name = "PK_CLIENTE")
    public String PK_CLIENTE;
    @ColumnInfo(name = "TIENDA")
    public String TIENDA ;
    @ColumnInfo(name = "DIRECCION")
    public String  DIRECCION;
    @ColumnInfo(name = "LATITUD")
    public String  LATITUD;
    @ColumnInfo(name = "LONGITUD")
    public String  LONGITUD;
    @ColumnInfo(name = "PK_REPARTIDOR")
    public String PK_REPARTIDOR;
    @ColumnInfo(name = "PK_ESTATUS")
    public String PK_ESTATUS;
    @ColumnInfo(name = "ESTATUS")
    public String ESTATUS;
    @ColumnInfo(name = "BORRADO")
    public String  BORRADO;
    @ColumnInfo(name = "PRECIO_ENTREGA")
    public double  PRECIO_ENTREGA;
    @ColumnInfo(name = "TOTAL")
    public double  TOTAL;
    @ColumnInfo(name = "FECHA_C")
    public String  FECHA_C;
    @ColumnInfo(name = "FECHA_M")
    public String  FECHA_M;
    @ColumnInfo(name = "FECHA_D")
    public String  FECHA_D;
    @ColumnInfo(name = "USUARIO_C")
    public String  USUARIO_C;
    @ColumnInfo(name = "USUARIO_M")
    public String  USUARIO_M;
    @ColumnInfo(name = "USUARIO_D")
    public String  USUARIO_D;
    @ColumnInfo(name = "PK_TIENDA")
    public String  PK_TIENDA;
    @ColumnInfo(name = "PK_CATEGORIA")
    public String  PK_CATEGORIA;
    @Ignore
    public String  IMAGEN;
    @Ignore
    public String  IMAGEN_TIENDA;
    @Ignore
    public String  REPARTIDOR;
    @Ignore
    public Double  COMISION_TARJETA;
    @Ignore
    public String  METODO_PAGO;
    @Ignore
    public String  HORARIO;
    @Ignore
    public String  PK_TIPO;
    @Ignore
    public String  TIPO;
    @Ignore
    public String  IMAGEN_TIPO;
    @Ignore
    public String  CLASIFICACION;

    @Ignore
    public List<PedidoDetalleModel> LISTA ;

}
