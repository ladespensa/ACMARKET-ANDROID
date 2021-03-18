package com.gesdes.acmarket.model;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.gesdes.acmarket.model.PedidoDetalleModel;

import java.util.List;

@Dao
public interface PedidoDetalleDao {
    @Query("SELECT * FROM PedidoDetalleModel")
    List<PedidoDetalleModel> getAll();

    @Query("SELECT PK_PRODUCTO FROM PedidoDetalleModel GROUP BY PK_PRODUCTO")
    List<String> getAllPks();

    @Query("SELECT * FROM PedidoDetalleModel WHERE PK_PRODUCTO=:pkProducto")
    PedidoDetalleModel getProductoByPk(String pkProducto);

    @Query("SELECT * FROM PedidoDetalleModel WHERE PK IN (:pedidoDetallePks)")
    List<PedidoDetalleModel> loadAllByIds(int[] pedidoDetallePks);

    @Insert
    void insertAll(PedidoDetalleModel pedidoDetalleModels);

    @Delete
    void delete(PedidoDetalleModel pedidoDetalleModel);

    @Query("DELETE FROM PedidoDetalleModel")
    void deleteAllProductos();

    @Query("DELETE FROM PedidoDetalleModel WHERE PK_PRODUCTO=:pkProducto")
    void deleteProductosByPk(String pkProducto);

    @Query("SELECT SUM(CANTIDAD) FROM PedidoDetalleModel WHERE PK_PRODUCTO = :pkProducto")
    double getCantidadFromProducto(String pkProducto);

    @Query("SELECT CANTIDAD FROM PedidoDetalleModel WHERE PK_PRODUCTO = :pkProducto")
    double getCantidadFromProductoSplit(String pkProducto);

    @Update
    void update(PedidoDetalleModel pedidoDetalleModel);

}
