package com.example.uas_mobile.factories;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.uas_mobile.data.repositories.UserRepository;
import com.example.uas_mobile.ui.viewmodel.AddWorkoutViewModel;
import com.example.uas_mobile.ui.viewmodel.LoginViewModel;
import com.example.uas_mobile.ui.viewmodel.RegisterViewModel;
import com.example.uas_mobile.ui.viewmodel.WorkoutListViewModel;

public class ViewModelFactory implements ViewModelProvider.Factory{
    private UserRepository userRepository;

    public ViewModelFactory(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(LoginViewModel.class)) {
            return (T) new LoginViewModel(userRepository);
        } else if (modelClass.isAssignableFrom(RegisterViewModel.class)) {
            return (T) new RegisterViewModel(userRepository);
        } else if (modelClass.isAssignableFrom(WorkoutListViewModel.class)) {
            return (T) new WorkoutListViewModel(userRepository); // Add this line
        }else if (modelClass.isAssignableFrom(AddWorkoutViewModel.class)) {
            return (T) new AddWorkoutViewModel(userRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
