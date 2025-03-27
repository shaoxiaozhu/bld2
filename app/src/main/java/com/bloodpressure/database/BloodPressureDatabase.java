package com.bloodpressure.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.bloodpressure.dao.MeasurementDao;
import com.bloodpressure.dao.UserDao;
import com.bloodpressure.model.Measurement;
import com.bloodpressure.model.User;

@Database(entities = {User.class, Measurement.class}, version = 1, exportSchema = false)
public abstract class BloodPressureDatabase extends RoomDatabase {

    private static BloodPressureDatabase instance;

    public abstract UserDao userDao();
    public abstract MeasurementDao measurementDao();

    public static synchronized BloodPressureDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    BloodPressureDatabase.class,
                    "blood_pressure_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
} 