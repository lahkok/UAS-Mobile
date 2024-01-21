package com.example.uas_mobile.data.repositories;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.uas_mobile.data.model.LoginRequest;
import com.example.uas_mobile.data.model.LoginResponse;
import com.example.uas_mobile.data.model.MyApiEndpoint;
import com.example.uas_mobile.data.model.RegisterRequest;
import com.example.uas_mobile.data.model.RegisterResponse;
import com.example.uas_mobile.data.model.Workout;
import com.example.uas_mobile.utils.Resource;
import com.example.uas_mobile.utils.TokenManager;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UserRepository {
    private final MyApiEndpoint api;
    private final MutableLiveData<Resource<LoginResponse>> loginResponse = new MutableLiveData<>();
    private final MutableLiveData<Resource<RegisterResponse>> registerResponse = new MutableLiveData<>();
    private final Context context;

    public UserRepository(Context context) {
        this.context = context;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.1.2:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        api = retrofit.create(MyApiEndpoint.class);
    }

    public LiveData<Resource<LoginResponse>> loginUser(LoginRequest loginRequest) {
        Log.d("UserRepository", "Starting login operation with: " + loginRequest.toString());

        api.loginUser(loginRequest).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    Log.d("UserRepository", "Login operation successful");
                    LoginResponse loginResponseData = response.body();

                    TokenManager tokenManager = new TokenManager(context); // Replace 'context' with the actual context
                    tokenManager.saveToken(loginResponseData.getToken());

                    int userId = loginResponseData.getUserId();

                    tokenManager.saveUserId(userId);
                    loginResponse.postValue(Resource.success(loginResponseData));
                } else {
                    // Parse the error message from the server
                    String errorMessage;
                    try {
                        String responseBody = response.errorBody().string();
                        JSONObject jObjError = new JSONObject(responseBody);
                        errorMessage = jObjError.getString("error");
                    } catch (Exception e) {
                        errorMessage = response.message();
                    }
                    Log.d("UserRepository", "Login operation failed: " + errorMessage);
                    loginResponse.postValue(Resource.error(errorMessage, null)); // Error
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.e("UserRepository", "Login operation failed", t);
                loginResponse.postValue(Resource.error(t.getMessage(), null)); // Error
            }
        });

        return loginResponse;
    }

    public LiveData<Resource<RegisterResponse>> registerUser(RegisterRequest registerRequest) {
        Log.d("UserRepository", "Starting register operation with: " + registerRequest.toString());

        api.registerUser(registerRequest).enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                if (response.isSuccessful()) {
                    Log.d("UserRepository", "Register operation successful");
                    registerResponse.postValue(Resource.success(response.body())); // Success
                } else {
                    // Parse the error message from the server
                    String errorMessage;
                    try {
                        String responseBody = response.errorBody().string();
                        JSONObject jObjError = new JSONObject(responseBody);
                        errorMessage = jObjError.getString("error");
                    } catch (Exception e) {
                        errorMessage = response.message();
                    }
                    Log.d("UserRepository", "Register operation failed: " + errorMessage);
                    registerResponse.postValue(Resource.error(errorMessage, null)); // Error
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                Log.e("UserRepository", "Register operation failed", t);
                registerResponse.postValue(Resource.error(t.getMessage(), null)); // Error
            }
        });

        return registerResponse;
    }

    public LiveData<Resource<Workout>> createWorkout(Workout workout) {
        MutableLiveData<Resource<Workout>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        Log.d("UserRepository", "Creating workout for user ID: " + workout.getUserId());

        Gson gson = new Gson();
        String workoutJson = gson.toJson(workout);

        // Log the Workout object
        Log.d("UserRepository", "Workout object: " + workoutJson);

        api.createWorkout(workout).enqueue(new Callback<Workout>() {
            @Override
            public void onResponse(Call<Workout> call, Response<Workout> response) {
                // Convert the Workout object to a JSON string
                if (response.isSuccessful()) {
                    Log.d("UserRepository", "Create workout operation successful");
                    result.postValue(Resource.success(response.body())); // Success
                } else {
                    // Parse the error message from the server
                    String errorMessage;
                    try {
                        String responseBody = response.errorBody().string();
                        JSONObject jObjError = new JSONObject(responseBody);
                        errorMessage = jObjError.getString("error");
                    } catch (Exception e) {
                        errorMessage = response.message();
                    }
                    Log.d("UserRepository", "Create workout operation failed: " + errorMessage);
                    result.postValue(Resource.error(errorMessage, null)); // Error
                }
            }

            @Override
            public void onFailure(Call<Workout> call, Throwable t) {
                Log.e("UserRepository", "Create workout operation failed", t);
                result.postValue(Resource.error(t.getMessage(), null)); // Error
            }
        });

        return result;
    }

    public LiveData<Resource<List<Workout>>> getWorkoutByUserId(int userId) {
        MutableLiveData<Resource<List<Workout>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        Log.d("UserRepository", "Starting API call to get workouts for user " + userId);

        api.getWorkoutByUserId(userId).enqueue(new Callback<List<Workout>>() {
            @Override
            public void onResponse(Call<List<Workout>> call, Response<List<Workout>> response) {
                if (response.isSuccessful()) {
                    Log.d("UserRepository", "API call successful. Response: " + response.body());
                    result.postValue(Resource.success(response.body())); // Success
                } else {
                    // Parse the error message from the server
                    String errorMessage;
                    try {
                        String responseBody = response.errorBody().string();
                        JSONObject jObjError = new JSONObject(responseBody);
                        errorMessage = jObjError.getString("error");
                    } catch (Exception e) {
                        errorMessage = response.message();
                    }
                    Log.d("UserRepository", "API call failed with response: " + errorMessage);
                    result.postValue(Resource.error(errorMessage, null)); // Error
                }
            }

            @Override
            public void onFailure(Call<List<Workout>> call, Throwable t) {
                Log.e("UserRepository", "Get workout operation failed", t);
                result.postValue(Resource.error(t.getMessage(), null)); // Error
            }
        });

        return result;
    }

    public LiveData<Resource<Workout>> getWorkoutById(int id) {
        MutableLiveData<Resource<Workout>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        api.getWorkoutById(id).enqueue(new Callback<Workout>() {
            @Override
            public void onResponse(Call<Workout> call, Response<Workout> response) {
                if (response.isSuccessful()) {
                    result.postValue(Resource.success(response.body()));
                } else {
                    String errorMessage;
                    try {
                        String responseBody = response.errorBody().string();
                        JSONObject jObjError = new JSONObject(responseBody);
                        errorMessage = jObjError.getString("error");
                    } catch (Exception e) {
                        errorMessage = response.message();
                    }
                    result.postValue(Resource.error(errorMessage, null));
                }
            }

            @Override
            public void onFailure(Call<Workout> call, Throwable t) {
                result.postValue(Resource.error(t.getMessage(), null));
            }
        });

        return result;
    }

    public LiveData<Resource<Workout>> updateWorkout(int id, Workout workout) {
        MutableLiveData<Resource<Workout>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        api.updateWorkout(id, workout).enqueue(new Callback<Workout>() {
            @Override
            public void onResponse(Call<Workout> call, Response<Workout> response) {
                if (response.isSuccessful()) {
                    Log.d("UserRepository", "Update workout operation successful");
                    result.postValue(Resource.success(response.body())); // Success
                } else {
                    // Parse the error message from the server
                    String errorMessage;
                    try {
                        String responseBody = response.errorBody().string();
                        JSONObject jObjError = new JSONObject(responseBody);
                        errorMessage = jObjError.getString("error");
                    } catch (Exception e) {
                        errorMessage = response.message();
                    }
                    Log.d("UserRepository", "Update workout operation failed: " + errorMessage);
                    result.postValue(Resource.error(errorMessage, null)); // Error
                }
            }

            @Override
            public void onFailure(Call<Workout> call, Throwable t) {
                Log.e("UserRepository", "Update workout operation failed", t);
                result.postValue(Resource.error(t.getMessage(), null)); // Error
            }
        });

        return result;
    }

    public LiveData<Resource<Void>> deleteWorkout(int id) {
        MutableLiveData<Resource<Void>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        api.deleteWorkout(id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("UserRepository", "Delete workout operation successful");
                    result.postValue(Resource.success(null)); // Success
                } else {
                    // Parse the error message from the server
                    String errorMessage;
                    try {
                        String responseBody = response.errorBody().string();
                        JSONObject jObjError = new JSONObject(responseBody);
                        errorMessage = jObjError.getString("error");
                    } catch (Exception e) {
                        errorMessage = response.message();
                    }
                    Log.d("UserRepository", "Delete workout operation failed: " + errorMessage);
                    result.postValue(Resource.error(errorMessage, null)); // Error
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("UserRepository", "Delete workout operation failed", t);
                result.postValue(Resource.error(t.getMessage(), null)); // Error
            }
        });

        return result;
    }
}
