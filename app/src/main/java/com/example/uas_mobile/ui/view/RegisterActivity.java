package com.example.uas_mobile.ui.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.uas_mobile.R;
import com.example.uas_mobile.data.model.RegisterRequest;
import com.example.uas_mobile.data.model.RegisterResponse;
import com.example.uas_mobile.data.repositories.UserRepository;
import com.example.uas_mobile.factories.ViewModelFactory;
import com.example.uas_mobile.ui.viewmodel.LoginViewModel;
import com.example.uas_mobile.ui.viewmodel.RegisterViewModel;
import com.example.uas_mobile.utils.Resource;

public class RegisterActivity extends AppCompatActivity {
    private EditText usernameEditText;
    private EditText emailEditText;
    private EditText nameEditText;
    private EditText passwordEditText;
    private Button registerButton;
    private RegisterViewModel registerViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        usernameEditText = findViewById(R.id.usernameEditText);
        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        registerButton = findViewById(R.id.registerButton);

        UserRepository userRepository = new UserRepository(this); // Replace with your UserRepository instance
        ViewModelFactory viewModelFactory = new ViewModelFactory(userRepository);
        registerViewModel = new ViewModelProvider(this, viewModelFactory).get(RegisterViewModel.class);

        registerViewModel.getRegisterResponse().observe(this, resource -> {
            if (resource != null) {
                if (resource.getStatus() == Resource.Status.SUCCESS) {
                    Toast.makeText(RegisterActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                    navigateToLoginActivity();
                } else if (resource.getStatus() == Resource.Status.ERROR) {
                    // Handle error
                    Toast.makeText(RegisterActivity.this, "Registration failed: " + resource.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Set the onClickListener for the register button
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a RegisterRequest with the input from the EditTexts
                RegisterRequest registerRequest = new RegisterRequest(
                        usernameEditText.getText().toString(),
                        passwordEditText.getText().toString(),
                        nameEditText.getText().toString(),
                        emailEditText.getText().toString()
                );

                // Call the registerUser method in the ViewModel
                registerViewModel.registerUser(registerRequest);
            }
        });
    }

    private void navigateToLoginActivity() {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
    }
}
