package com.dhms.tvshow.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.dhms.tvshow.History;

import java.util.List;

@Dao
public interface HistoryDao {
    @Query("SELECT * FROM histories")
    List<History> loadAll();
    @Insert
    void insert(History history);
    @Delete
    void delete(History history);
}
