package com.bloodpressure.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Entity class representing a user profile
 */
@Entity
public class User {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private String name;
    private int age;
    private boolean isMale;
    private String healthHistory;
    private String medications;
    private boolean hasHypertension;

    public User(String name, int age, boolean isMale, String healthHistory, String medications, boolean hasHypertension) {
        this.name = name;
        this.age = age;
        this.isMale = isMale;
        this.healthHistory = healthHistory;
        this.medications = medications;
        this.hasHypertension = hasHypertension;
    }

    // Getters and setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public boolean isMale() {
        return isMale;
    }

    public void setMale(boolean male) {
        isMale = male;
    }

    public String getHealthHistory() {
        return healthHistory;
    }

    public void setHealthHistory(String healthHistory) {
        this.healthHistory = healthHistory;
    }

    public String getMedications() {
        return medications;
    }

    public void setMedications(String medications) {
        this.medications = medications;
    }

    public boolean isHasHypertension() {
        return hasHypertension;
    }

    public void setHasHypertension(boolean hasHypertension) {
        this.hasHypertension = hasHypertension;
    }
} 