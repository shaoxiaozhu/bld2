package com.bloodpressure.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.bloodpressure.model.User;
import com.bloodpressure.repository.UserRepository;

import java.util.List;

public class UserViewModel extends AndroidViewModel {
    private UserRepository repository;
    private LiveData<List<User>> allUsers;
    private MutableLiveData<User> currentUser = new MutableLiveData<>();

    public UserViewModel(@NonNull Application application) {
        super(application);
        repository = new UserRepository(application);
        allUsers = repository.getAllUsers();
    }

    public void insert(User user) {
        repository.insert(user, userId -> {
            user.setId(userId);
            currentUser.postValue(user);
        });
    }

    public void update(User user) {
        repository.update(user);
        currentUser.postValue(user);
    }

    public void delete(User user) {
        repository.delete(user);
    }

    public LiveData<List<User>> getAllUsers() {
        return allUsers;
    }

    public LiveData<User> getCurrentUser() {
        return currentUser;
    }

    public void loadUserById(long userId) {
        repository.getUserById(userId, user -> currentUser.postValue(user));
    }

    public void loadDefaultUser() {
        repository.getDefaultUser(user -> {
            if (user != null) {
                currentUser.postValue(user);
            } else {
                // Create a default user if none exists
                User defaultUser = new User("Default User", 50, true, "", "", false);
                insert(defaultUser);
            }
        });
    }
} 