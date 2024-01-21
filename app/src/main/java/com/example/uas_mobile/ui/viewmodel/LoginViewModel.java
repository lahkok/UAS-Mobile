package com.example.uas_mobile.ui.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.example.uas_mobile.data.model.LoginRequest;
import com.example.uas_mobile.data.model.LoginResponse;
import com.example.uas_mobile.data.repositories.UserRepository;
import com.example.uas_mobile.utils.Resource;
import com.example.uas_mobile.utils.SingleLiveEvent;

public class LoginViewModel extends ViewModel {
    private final UserRepository userRepository;
    private final MutableLiveData<Resource<LoginResponse>> loginResponse = new MutableLiveData<>();
    private final SingleLiveEvent<Void> navigateToHomeEvent = new SingleLiveEvent<>();
    private final SingleLiveEvent<Void> navigateToRegisterEvent = new SingleLiveEvent<>();

    public LoginViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void loginUser(LoginRequest loginRequest) {
        Log.d("LoginViewModel", "loginUser called with: " + loginRequest.toString());
        userRepository.loginUser(loginRequest).observeForever(new Observer<Resource<LoginResponse>>() {
            @Override
            public void onChanged(Resource<LoginResponse> resource) {
                loginResponse.setValue(resource);
            }
        });
        Log.d("LoginViewModel", "loginUser completed");
    }


    public LiveData<Resource<LoginResponse>> getLoginResponse() {
        return loginResponse;
    }

    public SingleLiveEvent<Void> getNavigateToHomeEvent() {
        return navigateToHomeEvent;
    }

    public SingleLiveEvent<Void> getNavigateToRegisterEvent() {
        return navigateToRegisterEvent;
    }

    public void navigateToRegister() {
        navigateToRegisterEvent.call();
    }
}
