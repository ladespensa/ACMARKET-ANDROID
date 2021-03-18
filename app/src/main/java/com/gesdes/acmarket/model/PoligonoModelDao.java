package com.gesdes.acmarket.model;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface PoligonoModelDao {
    @Query("SELECT * FROM PoligonoModel")
    List<com.gesdes.acmarket.model.PoligonoModel> getAll();

    @Query("SELECT * FROM PoligonoModel WHERE NOT PK IN (:pksPoligonos)")
    List<com.gesdes.acmarket.model.PoligonoModel> getAllByNotContainsPks(int [] pksPoligonos);

    @Query("SELECT * FROM PoligonoModel WHERE PK IN (:pksPoligonos)")
    List<com.gesdes.acmarket.model.PoligonoModel> getAllByContainsPks(List<Integer> pksPoligonos);

    @Query("SELECT * FROM PoligonoModel WHERE PK IN (:pk)")
    com.gesdes.acmarket.model.PoligonoModel loadPoligonoByPk(int pk);

    @Query("SELECT Count(*) FROM PoligonoModel WHERE PK =:pk")
    int countPoligonoByPk(int pk);

    @Insert
    void insertAll(List<com.gesdes.acmarket.model.PoligonoModel> poligonoModels);

    @Delete
    void delete(com.gesdes.acmarket.model.PoligonoModel poligonoModel);

    @Query("DELETE FROM PoligonoModel")
    void deleteAllPoligonos();

    @Query("DELETE FROM PoligonoModel WHERE PK=:pk")
    void deletePoligonoByPk(int pk);

    @Update
    void update(com.gesdes.acmarket.model.PoligonoModel poligonoModel);

}
