package com.example.uas_mobile.ui.view.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uas_mobile.R;
import com.example.uas_mobile.data.model.Workout;
import com.example.uas_mobile.ui.view.DetailWorkoutActivity;

import java.util.List;

public class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder> {
    private List<Workout> workouts;

    public WorkoutAdapter(List<Workout> workouts) {
        this.workouts = workouts;
    }

    @NonNull
    @Override
    public WorkoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.workout_item, parent, false);
        return new WorkoutViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkoutViewHolder holder, int position) {
        Workout workout = workouts.get(position);
        // Bind the workout data to the view holder
        holder.bind(workout);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), DetailWorkoutActivity.class);
                intent.putExtra("workoutId", workout.getId());
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return workouts.size();
    }

    public void setWorkouts(List<Workout> workouts) {
        this.workouts = workouts;
    }

    static class WorkoutViewHolder extends RecyclerView.ViewHolder {
        TextView workoutName;
        TextView workoutDuration;
        TextView workoutNotes;
        TextView workoutDate;

        public WorkoutViewHolder(@NonNull View itemView) {
            super(itemView);
            workoutName = itemView.findViewById(R.id.workout_name);
            workoutDuration = itemView.findViewById(R.id.workout_duration);
            workoutNotes = itemView.findViewById(R.id.workout_notes);
            workoutDate = itemView.findViewById(R.id.workout_date);
        }

        public void bind(Workout workout) {
            workoutName.setText(workout.getType());
            workoutDuration.setText(String.valueOf(workout.getDuration()));
            workoutNotes.setText(workout.getNotes());
            workoutDate.setText(workout.getDate());
        }
    }
}
