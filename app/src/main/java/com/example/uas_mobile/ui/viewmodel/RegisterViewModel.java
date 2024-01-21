package com.example.uas_mobile.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.example.uas_mobile.data.model.RegisterRequest;
import com.example.uas_mobile.data.model.RegisterResponse;
import com.example.uas_mobile.data.repositories.UserRepository;
import com.example.uas_mobile.utils.Resource;

public class RegisterViewModel extends ViewModel {
    private final UserRepository userRepository;
    private final MutableLiveData<Resource<RegisterResponse>> registerResponse = new MutableLiveData<>();

    public RegisterViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public LiveData<Resource<RegisterResponse>> getRegisterResponse() {
        return registerResponse;
    }

    public void registerUser(RegisterRequest registerRequest) {
        registerResponse.setValue(Resource.loading(null)); // Set the Resource to loading state
        userRepository.registerUser(registerRequest).observeForever(new Observer<Resource<RegisterResponse>>() {
            @Override
            public void onChanged(Resource<RegisterResponse> responseResource) {
                registerResponse.setValue(responseResource); // Update the Resource with the response from the repository
            }
        });
    }
}
