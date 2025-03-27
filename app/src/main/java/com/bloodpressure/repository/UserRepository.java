package com.bloodpressure.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.bloodpressure.dao.UserDao;
import com.bloodpressure.database.BloodPressureDatabase;
import com.bloodpressure.model.User;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserRepository {
    private UserDao userDao;
    private ExecutorService executorService;
    private LiveData<List<User>> allUsers;

    public UserRepository(Application application) {
        BloodPressureDatabase database = BloodPressureDatabase.getInstance(application);
        userDao = database.userDao();
        allUsers = userDao.getAllUsers();
        executorService = Executors.newFixedThreadPool(2);
    }

    public void insert(User user, InsertCallback callback) {
        executorService.execute(() -> {
            long userId = userDao.insert(user);
            if (callback != null) {
                callback.onUserInserted(userId);
            }
        });
    }

    public void update(User user) {
        executorService.execute(() -> userDao.update(user));
    }

    public void delete(User user) {
        executorService.execute(() -> userDao.delete(user));
    }

    public LiveData<List<User>> getAllUsers() {
        return allUsers;
    }

    public void getUserById(long userId, DataCallback<User> callback) {
        executorService.execute(() -> {
            User user = userDao.getUserById(userId);
            callback.onDataLoaded(user);
        });
    }

    public void getDefaultUser(DataCallback<User> callback) {
        executorService.execute(() -> {
            User user = userDao.getDefaultUser();
            callback.onDataLoaded(user);
        });
    }

    public interface DataCallback<T> {
        void onDataLoaded(T data);
    }

    public interface InsertCallback {
        void onUserInserted(long userId);
    }
} 