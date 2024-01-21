package com.example.uas_mobile.ui.view.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.uas_mobile.R;
import com.example.uas_mobile.data.model.Workout;
import com.example.uas_mobile.data.repositories.UserRepository;
import com.example.uas_mobile.factories.ViewModelFactory;
import com.example.uas_mobile.ui.view.AddWorkoutActivity;
import com.example.uas_mobile.ui.view.adapter.WorkoutAdapter;
import com.example.uas_mobile.ui.viewmodel.AddWorkoutViewModel;
import com.example.uas_mobile.ui.viewmodel.WorkoutListViewModel;
import com.example.uas_mobile.utils.Resource;
import com.example.uas_mobile.utils.SpacesItemDecoration;
import com.example.uas_mobile.utils.TokenManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private WorkoutListViewModel workoutListViewModel;
    private FloatingActionButton fabAddWorkout;
    private int userId;
    private RecyclerView recyclerView;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        fabAddWorkout = view.findViewById(R.id.fab_add_workout);

        UserRepository userRepository = new UserRepository(getContext());
        ViewModelFactory factory = new ViewModelFactory(userRepository);
        AddWorkoutViewModel viewModel = new ViewModelProvider(this, factory).get(AddWorkoutViewModel.class);

        fabAddWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddWorkoutActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize the ViewModel
        UserRepository userRepository = new UserRepository(getContext());
        ViewModelFactory factory = new ViewModelFactory(userRepository);
        WorkoutListViewModel viewModel = new ViewModelProvider(this, factory).get(WorkoutListViewModel.class);
        Log.d("WorkoutListFragment", "WorkoutListViewModel: " + viewModel);

        // Get the user ID of the logged-in user
        TokenManager tokenManager = new TokenManager(getContext());
        int userId = tokenManager.getUserId();
        Log.d("WorkoutListFragment", "User ID: " + userId);
        if (userId == -1) {
            // No user is logged in
            return;
        }

        ProgressBar progressBar = view.findViewById(R.id.progress_bar);

        // Get a reference to the RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);

        // Create an empty adapter for the RecyclerView
        WorkoutAdapter adapter = new WorkoutAdapter(new ArrayList<>());

        // Set the adapter on the RecyclerView
        recyclerView.setAdapter(adapter);

        // Use a GridLayoutManager for the RecyclerView
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3)); // Change the number to adjust the number of columns

        // Add a SpacesItemDecoration
        int spaceInPixels = 10;
        recyclerView.addItemDecoration(new SpacesItemDecoration(spaceInPixels));

        // Observe the LiveData from the ViewModel
        viewModel.getWorkoutByUserId(userId).observe(getViewLifecycleOwner(), new Observer<Resource<List<Workout>>>() {
            @Override
            public void onChanged(Resource<List<Workout>> resource) {
                if (resource.getStatus() == Resource.Status.SUCCESS) {
                    progressBar.setVisibility(View.GONE);
                    // Get the list of workouts
                    List<Workout> workouts = resource.getData();

                    // Update the adapter's data
                    adapter.setWorkouts(workouts);
                    adapter.notifyDataSetChanged();
                } else if (resource.getStatus() == Resource.Status.ERROR) {
                    progressBar.setVisibility(View.GONE);
                    String errorMessage = resource.getMessage();
                    Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
                } else if (resource.getStatus() == Resource.Status.LOADING) {
                    progressBar.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            // The fragment is no longer hidden (i.e., it's visible to the user)
            // Refresh the data
            if (workoutListViewModel != null) {
                workoutListViewModel.getWorkoutByUserId(userId);
            }
        }
    }
}