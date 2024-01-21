package com.example.uas_mobile.ui.view;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.example.uas_mobile.R;
import com.example.uas_mobile.data.model.Workout;
import com.example.uas_mobile.data.repositories.UserRepository;
import com.example.uas_mobile.ui.viewmodel.WorkoutListViewModel;
import com.example.uas_mobile.utils.Resource;

import java.util.Calendar;

public class DetailWorkoutActivity extends AppCompatActivity {
    private WorkoutListViewModel workoutListViewModel;
    private Workout workout;
    private Button updateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_detail);

        updateButton = findViewById(R.id.update_button);

        if (updateButton == null) {
            throw new RuntimeException("Update button not found");
        }

        // Get the workout ID from the intent
        int workoutId = getIntent().getIntExtra("workoutId", 0);

        // Create an instance of WorkoutListViewModel
        UserRepository userRepository = new UserRepository(this);
        workoutListViewModel = new WorkoutListViewModel(userRepository);

        Button dateButton = findViewById(R.id.date_button_dtl);
        TextView dateText = findViewById(R.id.date_text_dtl);

        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the current date
                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                // Create a new instance of DatePickerDialog
                DatePickerDialog datePickerDialog = new DatePickerDialog(DetailWorkoutActivity.this,
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

        // Use the getWorkoutByUserId method of WorkoutListViewModel to get the LiveData object
        LiveData<Resource<Workout>> workoutLiveData = workoutListViewModel.getWorkoutById(workoutId);

        EditText workoutNameEditText = findViewById(R.id.workout_name_edt);
        EditText workoutDurationEditText = findViewById(R.id.workout_duration_edt);
        EditText workoutNotesEditText = findViewById(R.id.workout_notes_edt);

        // Observe the LiveData object to get the workout data when it changes
        workoutLiveData.observe(this, new Observer<Resource<Workout>>() {
            @Override
            public void onChanged(Resource<Workout> resource) {
                if (resource != null && resource.getData() != null) {
                    // Get the workout data
                    workout = resource.getData();

                    updateButton.setEnabled(true);

                    // Update the UI with the workout data
                    workoutNameEditText.setText(workout.getType());

                    workoutDurationEditText.setText(String.valueOf(workout.getDuration())); // Assuming getDuration() returns an int

                    workoutNotesEditText.setText(workout.getNotes());

                    // Update the date text view with the workout date
                    dateText.setText(workout.getDate());
                }
            }
        });

        // Get the update button
        Button updateButton = findViewById(R.id.update_button);

        // Set a click listener on the "Update Workout" button
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the input values
                String workoutName = workoutNameEditText.getText().toString();
                int workoutDuration = Integer.parseInt(workoutDurationEditText.getText().toString());
                String workoutNotes = workoutNotesEditText.getText().toString();
                String date = dateText.getText().toString();

                // Update the workout object with the new input values
                workout.setType(workoutName);
                workout.setDuration(workoutDuration);
                workout.setNotes(workoutNotes);
                workout.setDate(date);

                // Call updateWorkout on the ViewModel
                workoutListViewModel.updateWorkout(workout.getId(), workout).observe(DetailWorkoutActivity.this, new Observer<Resource<Workout>>() {
                    @Override
                    public void onChanged(Resource<Workout> resource) {
                        if (resource.getStatus() == Resource.Status.SUCCESS) {
                            // The workout was updated successfully
                            Toast.makeText(DetailWorkoutActivity.this, "Workout updated", Toast.LENGTH_SHORT).show();
                            finish();
                        } else if (resource.getStatus() == Resource.Status.ERROR) {
                            // There was an error updating the workout
                            Toast.makeText(DetailWorkoutActivity.this, "Error updating workout", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        Button deleteButton = findViewById(R.id.delete_button);

        // Set a click listener on the "Delete Workout" button
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call deleteWorkout on the ViewModel
                workoutListViewModel.deleteWorkout(workout.getId()).observe(DetailWorkoutActivity.this, new Observer<Resource<Void>>() {
                    @Override
                    public void onChanged(Resource<Void> resource) {
                        if (resource.getStatus() == Resource.Status.SUCCESS) {
                            // The workout was deleted successfully
                            Toast.makeText(DetailWorkoutActivity.this, "Workout deleted", Toast.LENGTH_SHORT).show();
                            finish();
                        } else if (resource.getStatus() == Resource.Status.ERROR) {
                            // There was an error deleting the workout
                            Toast.makeText(DetailWorkoutActivity.this, "Error deleting workout", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}
