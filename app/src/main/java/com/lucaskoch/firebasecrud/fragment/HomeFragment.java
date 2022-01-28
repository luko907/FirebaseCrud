package com.lucaskoch.firebasecrud.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.database.ValueEventListener;
import com.lucaskoch.firebasecrud.R;
import com.lucaskoch.firebasecrud.adapter.CourseRVAdapter;
import com.lucaskoch.firebasecrud.model.CourseRVModel;

import java.util.ArrayList;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;


public class HomeFragment extends Fragment {
    RecyclerView idRVCourses;
    ProgressBar progressBar;
    FloatingActionButton idFloatBtn;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    ConstraintLayout home_constraint_layout;
    ArrayList<CourseRVModel> courseRVModelArrayList;
    SwipeRefreshLayout home_swipe_container;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        databaseReference = FirebaseDatabase.getInstance().getReference("Courses");
        idFloatBtn = view.findViewById(R.id.idFloatBtn);
        idRVCourses = view.findViewById(R.id.idRVCourses);
        courseRVModelArrayList = new ArrayList<>();
        CourseRVAdapter courseRVAdapter = new CourseRVAdapter(courseRVModelArrayList);
        idRVCourses.setLayoutManager(new LinearLayoutManager(getContext()));
        idRVCourses.setAdapter(courseRVAdapter);
        progressBar =view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        home_swipe_container = view.findViewById(R.id.home_swipe_container);
        home_swipe_container.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                //Do your task
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        home_swipe_container.setRefreshing(false);
                    }
                }, 1000L);


            }
        });
        idFloatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddCourseFragment addCourseFragment = new AddCourseFragment();
                FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frameLayout_fragment_container, addCourseFragment).addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    AddCourseFragment addCourseFragment = new AddCourseFragment();
                    FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.frameLayout_fragment_container, addCourseFragment).addToBackStack(null);
                    fragmentTransaction.commit();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        databaseReference.addChildEventListener(new ChildEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                /*Log.v("Tag", "Respnse : " + Objects.requireNonNull(Objects.requireNonNull(snapshot.getValue(CourseRVModel.class)).toString()));*/
                courseRVModelArrayList.add(snapshot.getValue(CourseRVModel.class));
                courseRVAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}