package com.dhms.tvshow.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.dhms.tvshow.History;

import java.util.List;

@Dao
public interface HistoryDao {
    @Query("SELECT * FROM histories")
    List<History> loadAll();
    @Insert
    long insert(History history);
    @Update
    void update(History... history);
    @Delete
    void delete(History history);
}
