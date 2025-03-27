package com.bloodpressure.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.bloodpressure.model.Measurement;

import java.util.List;

@Dao
public interface MeasurementDao {
    @Insert
    long insert(Measurement measurement);

    @Update
    void update(Measurement measurement);

    @Delete
    void delete(Measurement measurement);

    @Query("SELECT * FROM Measurement WHERE userId = :userId ORDER BY recordTime DESC")
    LiveData<List<Measurement>> getAllMeasurements(long userId);
    
    @Query("SELECT * FROM Measurement WHERE userId = :userId AND recordTime BETWEEN :start AND :end ORDER BY recordTime ASC")
    List<Measurement> getMeasurementsByPeriod(long userId, long start, long end);
    
    @Query("SELECT * FROM Measurement WHERE userId = :userId AND periodType = :periodType ORDER BY recordTime DESC")
    LiveData<List<Measurement>> getMeasurementsByPeriodType(long userId, int periodType);
    
    @Query("SELECT AVG(systolic) FROM Measurement WHERE userId = :userId AND recordTime BETWEEN :start AND :end")
    float getAverageSystolic(long userId, long start, long end);
    
    @Query("SELECT AVG(diastolic) FROM Measurement WHERE userId = :userId AND recordTime BETWEEN :start AND :end")
    float getAverageDiastolic(long userId, long start, long end);
    
    @Query("SELECT AVG(heartRate) FROM Measurement WHERE userId = :userId AND recordTime BETWEEN :start AND :end")
    float getAverageHeartRate(long userId, long start, long end);
    
    @Query("SELECT * FROM Measurement WHERE userId = :userId ORDER BY recordTime DESC LIMIT 1")
    Measurement getLatestMeasurement(long userId);
} 