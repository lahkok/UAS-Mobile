package com.example.uas_mobile.ui.view.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.uas_mobile.R;
import com.example.uas_mobile.ui.view.LoginActivity;
import com.example.uas_mobile.ui.view.adapter.ProfileAdapter;
import com.example.uas_mobile.utils.ProfileItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private RecyclerView recyclerView;
    private ProfileAdapter adapter;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        recyclerView = view.findViewById(R.id.profile_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        List<ProfileItem> profileItems = new ArrayList<>();
        profileItems.add(new ProfileItem("Profile", null));
        profileItems.add(new ProfileItem("Setting", Arrays.asList("Sub-item 1", "Sub-item 2")));
        profileItems.add(new ProfileItem("Logout", null));
        adapter = new ProfileAdapter(profileItems);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener((item, position) -> {
            if (item.getTitle().equals("Logout")) {
                // Clear user data
                SharedPreferences preferences = getActivity().getSharedPreferences("token_pref", Context.MODE_PRIVATE);
                preferences.edit().clear().apply();

                // Navigate to login screen
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            } else if (item.getSubItems() != null && !item.getSubItems().isEmpty()) {
                // If the item has sub-items, toggle its expanded state
                item.setExpanded(!item.isExpanded());
                adapter.notifyItemChanged(position);
            }

            // Handle click
            Log.d("ProfileFragment", "Item clicked: " + item);
        });

        return view;
    }

}