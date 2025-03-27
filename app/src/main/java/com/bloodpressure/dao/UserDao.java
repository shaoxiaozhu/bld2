package com.bloodpressure.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.bloodpressure.model.User;

import java.util.List;

@Dao
public interface UserDao {
    @Insert
    long insert(User user);

    @Update
    void update(User user);

    @Delete
    void delete(User user);

    @Query("SELECT * FROM User WHERE id = :userId")
    User getUserById(long userId);

    @Query("SELECT * FROM User")
    LiveData<List<User>> getAllUsers();
    
    @Query("SELECT * FROM User LIMIT 1")
    User getDefaultUser();
} 