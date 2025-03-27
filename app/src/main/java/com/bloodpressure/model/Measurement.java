package com.bloodpressure.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Entity class representing a blood pressure measurement
 */
@Entity
public class Measurement {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private long userId;
    private long recordTime; // timestamp
    private int periodType; // 0-morning 1-noon 2-afternoon 3-bedtime
    private int systolic;   // high pressure
    private int diastolic;  // low pressure
    private int heartRate;
    private String note;

    public Measurement(long userId, long recordTime, int periodType, int systolic, int diastolic, int heartRate, String note) {
        this.userId = userId;
        this.recordTime = recordTime;
        this.periodType = periodType;
        this.systolic = systolic;
        this.diastolic = diastolic;
        this.heartRate = heartRate;
        this.note = note;
    }

    // Getters and setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(long recordTime) {
        this.recordTime = recordTime;
    }

    public int getPeriodType() {
        return periodType;
    }

    public void setPeriodType(int periodType) {
        this.periodType = periodType;
    }

    public int getSystolic() {
        return systolic;
    }

    public void setSystolic(int systolic) {
        this.systolic = systolic;
    }

    public int getDiastolic() {
        return diastolic;
    }

    public void setDiastolic(int diastolic) {
        this.diastolic = diastolic;
    }

    public int getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(int heartRate) {
        this.heartRate = heartRate;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
} 