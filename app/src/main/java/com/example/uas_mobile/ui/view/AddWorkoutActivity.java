package com.example.uas_mobile.ui.view;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.uas_mobile.R;
import com.example.uas_mobile.data.model.Workout;
import com.example.uas_mobile.data.repositories.UserRepository;
import com.example.uas_mobile.factories.ViewModelFactory;
import com.example.uas_mobile.ui.viewmodel.AddWorkoutViewModel;
import com.example.uas_mobile.utils.Resource;
import com.example.uas_mobile.utils.TokenManager;

import java.util.Calendar;

public class AddWorkoutActivity extends AppCompatActivity {
    private AddWorkoutViewModel addWorkoutViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_workout);

        // Initialize the ViewModel
        UserRepository userRepository = new UserRepository(this);
        ViewModelFactory factory = new ViewModelFactory(userRepository);
        addWorkoutViewModel = new ViewModelProvider(this, factory).get(AddWorkoutViewModel.class);

        // Get references to the input fields
        EditText workoutNameEditText = findViewById(R.id.workout_name);
        EditText workoutDurationEditText = findViewById(R.id.workout_duration);
        EditText workoutNoteEditText = findViewById(R.id.workout_notes);
        Button dateButton = findViewById(R.id.date_button);
        TextView dateText = findViewById(R.id.date_text);

        // Get a reference to the "Add Workout" button
        Button addWorkoutButton = findViewById(R.id.add_workout_button);

        // Set a click listener on the date button
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the current date
                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                // Create a new instance of DatePickerDialog
                DatePickerDialog datePickerDialog = new DatePickerDialog(AddWorkoutActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                // Display the selected date in the date text view
                                String selectedDate = String.format("%d-%02d-%02d", year, monthOfYear + 1, dayOfMonth);
                                dateText.setText(selectedDate);
                            }
                        }, year, month, day);

// Show the DatePickerDialog
                datePickerDialog.show();
            }
        });

        // Set a click listener on the "Add Workout" button
        addWorkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the input values
                String workoutName = workoutNameEditText.getText().toString();
                int workoutDuration = Integer.parseInt(workoutDurationEditText.getText().toString());
                String workoutNotes = workoutNoteEditText.getText().toString();
                String date = dateText.getText().toString();

                Log.d("AddWorkoutActivity", "Workout Name: " + workoutName);
                Log.d("AddWorkoutActivity", "Workout Duration: " + workoutDuration);
                Log.d("AddWorkoutActivity", "Workout Notes: " + workoutNotes);
                Log.d("AddWorkoutActivity", "Date: " + date);

                // Get the user ID from the token manager
                TokenManager tokenManager = new TokenManager(AddWorkoutActivity.this); // Replace 'AddWorkoutActivity.this' with the actual context
                int userId = tokenManager.getUserId();
                Log.d("AddWorkoutActivity", "User ID from TokenManager: " + userId);

                // Create a new workout object with the user ID and input values
                Workout workout = new Workout(userId, workoutName, workoutDuration, workoutNotes, date);

                // Call createWorkout on the ViewModel
                addWorkoutViewModel.createWorkout(workout).observe(AddWorkoutActivity.this, new Observer<Resource<Workout>>() {
                    @Override
                    public void onChanged(Resource<Workout> resource) {
                        if (resource.getStatus() == Resource.Status.SUCCESS) {
                            // The workout was created successfully
                            Toast.makeText(AddWorkoutActivity.this, "Workout created", Toast.LENGTH_SHORT).show();
                            finish();
                        } else if (resource.getStatus() == Resource.Status.ERROR) {
                            // There was an error creating the workout
                            Toast.makeText(AddWorkoutActivity.this, "Error creating workout", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}
