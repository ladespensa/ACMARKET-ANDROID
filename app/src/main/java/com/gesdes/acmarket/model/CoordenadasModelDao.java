package com.gesdes.acmarket.model;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface CoordenadasModelDao {
    @Query("SELECT * FROM CoordenadasModel")
    List<CoordenadasModel> getAll();

    @Query("SELECT * FROM CoordenadasModel WHERE PK_POLIGONO IN (:pkPoligono)")
    List<CoordenadasModel> loadAllByPkPoligono(int pkPoligono);

    @Insert
    void insertAll(List<CoordenadasModel> coordenadasModelList);

    @Delete
    void delete(CoordenadasModel coordenadasModel);

    @Query("DELETE FROM CoordenadasModel")
    void deleteAllCorrdenadas();

    @Query("DELETE FROM CoordenadasModel WHERE PK_POLIGONO=:pkPoligono")
    void deleteCoordenadasByPkPoligono(int pkPoligono);

}
