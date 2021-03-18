package com.gesdes.acmarket.model;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface PedidoModelDao {
    @Query("SELECT * FROM PedidoModel")
    List<PedidoModel> getAll();

    @Query("SELECT * FROM PedidoModel WHERE PK IN (:pedidosPks)")
    List<PedidoModel> loadAllByIds(int[] pedidosPks);

    @Insert
    void insertAll(PedidoModel... pedidoModels);

    @Delete
    void delete(PedidoModel pedidoModel);

}
