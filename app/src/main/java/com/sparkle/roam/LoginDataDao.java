package com.sparkle.roam;



import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface LoginDataDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(LoginData loginData);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void update(LoginData loginData);

    @Query("SELECT * FROM login_data")
    List<LoginData> getAlldata();

    @Delete
    void delete(LoginData loginData);
}
