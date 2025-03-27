package com.bloodpressure.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.bloodpressure.model.Measurement;
import com.bloodpressure.repository.MeasurementRepository;

import java.util.List;

public class MeasurementViewModel extends AndroidViewModel {
    private MeasurementRepository repository;
    private MutableLiveData<List<Measurement>> periodMeasurements = new MutableLiveData<>();
    private MutableLiveData<Float> averageSystolic = new MutableLiveData<>();
    private MutableLiveData<Float> averageDiastolic = new MutableLiveData<>();
    private MutableLiveData<Float> averageHeartRate = new MutableLiveData<>();
    private MutableLiveData<Measurement> latestMeasurement = new MutableLiveData<>();

    public MeasurementViewModel(@NonNull Application application) {
        super(application);
        repository = new MeasurementRepository(application);
    }

    public void insert(Measurement measurement) {
        repository.insert(measurement);
    }

    public void update(Measurement measurement) {
        repository.update(measurement);
    }

    public void delete(Measurement measurement) {
        repository.delete(measurement);
    }

    public LiveData<List<Measurement>> getAllMeasurements(long userId) {
        return repository.getAllMeasurements(userId);
    }

    public LiveData<List<Measurement>> getMeasurementsByPeriodType(long userId, int periodType) {
        return repository.getMeasurementsByPeriodType(userId, periodType);
    }

    public LiveData<List<Measurement>> getPeriodMeasurements() {
        return periodMeasurements;
    }

    public void loadMeasurementsByPeriod(long userId, long start, long end) {
        repository.getMeasurementsByPeriod(userId, start, end, data -> periodMeasurements.postValue(data));
    }

    public LiveData<Float> getAverageSystolic() {
        return averageSystolic;
    }

    public void calculateAverageSystolic(long userId, long start, long end) {
        repository.getAverageSystolic(userId, start, end, data -> averageSystolic.postValue(data));
    }

    public LiveData<Float> getAverageDiastolic() {
        return averageDiastolic;
    }

    public void calculateAverageDiastolic(long userId, long start, long end) {
        repository.getAverageDiastolic(userId, start, end, data -> averageDiastolic.postValue(data));
    }

    public LiveData<Float> getAverageHeartRate() {
        return averageHeartRate;
    }

    public void calculateAverageHeartRate(long userId, long start, long end) {
        repository.getAverageHeartRate(userId, start, end, data -> averageHeartRate.postValue(data));
    }

    public LiveData<Measurement> getLatestMeasurement() {
        return latestMeasurement;
    }

    public void loadLatestMeasurement(long userId) {
        repository.getLatestMeasurement(userId, data -> latestMeasurement.postValue(data));
    }
} 