package com.example.uas_mobile.ui.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.uas_mobile.R;
import com.example.uas_mobile.data.model.LoginRequest;
import com.example.uas_mobile.data.model.LoginResponse;
import com.example.uas_mobile.data.repositories.UserRepository;
import com.example.uas_mobile.factories.ViewModelFactory;
import com.example.uas_mobile.ui.viewmodel.LoginViewModel;
import com.example.uas_mobile.utils.Resource;
import com.example.uas_mobile.utils.TokenManager;

public class LoginActivity extends AppCompatActivity {
    private LoginViewModel loginViewModel;
    private EditText usernameEditText;
    private EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        Button loginButton = findViewById(R.id.loginButton);
        TextView forgotPassword = findViewById(R.id.forgotPassword);
        TextView registerTextView = findViewById(R.id.register);

        TokenManager tokenManager = new TokenManager(this);
        String token = tokenManager.getToken();

        // Save the userId
        int userId = tokenManager.getUserId();
        tokenManager.saveUserId(userId);

        if (token != null) {
            // Token exists, redirect to MainActivity
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        if (userId != -1) {
            // User is logged in, redirect to MainActivity
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {

        }

        // Initialize ViewModel
        UserRepository userRepository = new UserRepository(this); // Replace with your UserRepository instance
        ViewModelFactory viewModelFactory = new ViewModelFactory(userRepository);
        loginViewModel = new ViewModelProvider(this, viewModelFactory).get(LoginViewModel.class);
        
        loginViewModel.getLoginResponse().observe(this, resource -> {
            Log.d("LoginActivity", "loginResponse observed: " + resource);
            if (resource != null) {
                Log.d("LoginActivity", "loginResponse status: " + resource.getStatus());
                if (resource.getStatus() == Resource.Status.SUCCESS) {
                    LoginResponse loginResponse = resource.getData();
                    tokenManager.saveToken(loginResponse.getToken());
                    Log.d("LoginActivity", "Login successful: " + loginResponse.toString());
                    loginViewModel.getNavigateToHomeEvent().call();
                    navigateToMainActivity();
                    Log.d("LoginActivity", "navigateToHomeEvent called");
                } else if (resource.getStatus() == Resource.Status.ERROR) {
                    String errorMessage = resource.getMessage();
                    Log.d("LoginActivity", "Login error: " + errorMessage);
                    if ("Unauthorized".equals(errorMessage)) {
                        errorMessage = "Incorrect username or password. Please try again.";
                    }
                    Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
                }
            }
        });

        loginViewModel.getNavigateToHomeEvent().observe(this, aVoid -> navigateToMainActivity());

        loginViewModel.getNavigateToRegisterEvent().observe(this, aVoid -> navigateToRegister());

        // Set click listener for login button
        loginButton.setOnClickListener(v -> {
            Log.d("LoginActivity", "Login button clicked");
            String username = usernameEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            LoginRequest loginRequest = new LoginRequest(username, password);
            loginViewModel.loginUser(loginRequest);
        });

        registerTextView.setOnClickListener(v -> loginViewModel.navigateToRegister());
    }

    private void navigateToRegister() {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    private void navigateToMainActivity() {
        Log.d("LoginActivity", "Navigating to MainActivity");
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
