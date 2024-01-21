package com.example.uas_mobile.ui.viewmodel;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.uas_mobile.data.model.Workout;
import com.example.uas_mobile.data.repositories.UserRepository;
import com.example.uas_mobile.utils.Resource;

public class AddWorkoutViewModel extends ViewModel {
    private final UserRepository userRepository;

    public AddWorkoutViewModel(@NonNull UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public LiveData<Resource<Workout>> createWorkout(Workout workout) {
        Log.d("AddWorkoutViewModel", "Creating workout: " + workout.toString());
        return userRepository.createWorkout(workout);
    }
}
