package com.bloodpressure.repository;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.bloodpressure.dao.MeasurementDao;
import com.bloodpressure.database.BloodPressureDatabase;
import com.bloodpressure.model.Measurement;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MeasurementRepository {
    private MeasurementDao measurementDao;
    private ExecutorService executorService;

    public MeasurementRepository(Application application) {
        BloodPressureDatabase database = BloodPressureDatabase.getInstance(application);
        measurementDao = database.measurementDao();
        executorService = Executors.newFixedThreadPool(4);
    }

    public void insert(Measurement measurement) {
        executorService.execute(() -> measurementDao.insert(measurement));
    }

    public void update(Measurement measurement) {
        executorService.execute(() -> measurementDao.update(measurement));
    }

    public void delete(Measurement measurement) {
        executorService.execute(() -> measurementDao.delete(measurement));
    }

    public LiveData<List<Measurement>> getAllMeasurements(long userId) {
        return measurementDao.getAllMeasurements(userId);
    }

    public LiveData<List<Measurement>> getMeasurementsByPeriodType(long userId, int periodType) {
        return measurementDao.getMeasurementsByPeriodType(userId, periodType);
    }

    public void getMeasurementsByPeriod(long userId, long start, long end, DataCallback<List<Measurement>> callback) {
        executorService.execute(() -> {
            List<Measurement> measurements = measurementDao.getMeasurementsByPeriod(userId, start, end);
            callback.onDataLoaded(measurements);
        });
    }

    public void getAverageSystolic(long userId, long start, long end, DataCallback<Float> callback) {
        executorService.execute(() -> {
            float avg = measurementDao.getAverageSystolic(userId, start, end);
            callback.onDataLoaded(avg);
        });
    }

    public void getAverageDiastolic(long userId, long start, long end, DataCallback<Float> callback) {
        executorService.execute(() -> {
            float avg = measurementDao.getAverageDiastolic(userId, start, end);
            callback.onDataLoaded(avg);
        });
    }

    public void getAverageHeartRate(long userId, long start, long end, DataCallback<Float> callback) {
        executorService.execute(() -> {
            float avg = measurementDao.getAverageHeartRate(userId, start, end);
            callback.onDataLoaded(avg);
        });
    }

    public void getLatestMeasurement(long userId, DataCallback<Measurement> callback) {
        executorService.execute(() -> {
            Measurement measurement = measurementDao.getLatestMeasurement(userId);
            callback.onDataLoaded(measurement);
        });
    }

    public interface DataCallback<T> {
        void onDataLoaded(T data);
    }
} 