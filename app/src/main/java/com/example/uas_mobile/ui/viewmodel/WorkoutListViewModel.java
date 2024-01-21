package com.example.uas_mobile.ui.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.uas_mobile.data.model.Workout;
import com.example.uas_mobile.data.repositories.UserRepository;
import com.example.uas_mobile.utils.Resource;

import java.util.List;

public class WorkoutListViewModel extends ViewModel {
    private final UserRepository userRepository;

    public WorkoutListViewModel(@NonNull UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public LiveData<Resource<List<Workout>>> getWorkoutByUserId(int userId) {
        return userRepository.getWorkoutByUserId(userId);
    }

    public LiveData<Resource<Workout>> getWorkoutById(int id) {
        return userRepository.getWorkoutById(id);
    }

    public LiveData<Resource<Workout>> updateWorkout(int id, Workout workout) {
        return userRepository.updateWorkout(id, workout);
    }

    public LiveData<Resource<Void>> deleteWorkout(int id) {
        return userRepository.deleteWorkout(id);
    }
}
