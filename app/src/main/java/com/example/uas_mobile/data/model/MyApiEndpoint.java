package com.example.uas_mobile.data.model;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface MyApiEndpoint {
    @GET("/")
    Call<ResponseBody> getResponse();

    @POST("login")
    Call<LoginResponse> loginUser(@Body LoginRequest loginRequest);

    @POST("register")
    Call<RegisterResponse> registerUser(@Body RegisterRequest registerRequest);

    @POST("create_workout")
    Call<Workout> createWorkout(@Body Workout workout);

    @GET("get_workouts/{id}")
    Call<List<Workout>> getWorkoutByUserId(@Path("id") int userId);

    @GET("get_single_workout/{id}")
    Call<Workout> getWorkoutById(@Path("id") int id);

    @PUT("update_workout/{id}")
    Call<Workout> updateWorkout(@Path("id") int id, @Body Workout workout);

    @DELETE("delete_workout/{id}")
    Call<Void> deleteWorkout(@Path("id") int id);
}
