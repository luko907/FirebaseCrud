package com.lucaskoch.firebasecrud;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lucaskoch.firebasecrud.model.CourseRVModel;

import java.util.Objects;


public class AddCourseFragment extends Fragment {

    TextInputEditText idEdtCourseName, idEdtCoursePrice, idEdtCourseSuitedFor, idEdtCourseImageLink, idEdtCourseLink, idEdtCourseDescription;
    Button idbtnAddCourse;
    ProgressBar idPBLoading;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    String courseID;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_course, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        idEdtCourseName = view.findViewById(R.id.idEdtCourseName);
        idEdtCoursePrice = view.findViewById(R.id.idEdtCoursePrice);
        idEdtCourseSuitedFor = view.findViewById(R.id.idEdtCourseSuitedFor);
        idEdtCourseImageLink = view.findViewById(R.id.idEdtCourseImageLink);
        idEdtCourseLink = view.findViewById(R.id.idEdtCourseLink);
        idEdtCourseDescription = view.findViewById(R.id.idEdtCourseDescription);
        idbtnAddCourse = view.findViewById(R.id.idbtnAddCourse);
        idPBLoading = view.findViewById(R.id.idPBLoading);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Courses");

        idbtnAddCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String courseName = Objects.requireNonNull(idEdtCourseName.getText()).toString();
                String coursePrice = Objects.requireNonNull(idEdtCoursePrice.getText()).toString();
                String courseSuitedFor = Objects.requireNonNull(idEdtCourseSuitedFor.getText()).toString();
                String courseImageLink = Objects.requireNonNull(idEdtCourseImageLink.getText()).toString();
                String courseLink = Objects.requireNonNull(idEdtCourseLink.getText()).toString();
                String courseDescription = Objects.requireNonNull(idEdtCourseDescription.getText()).toString();
                courseID = courseName;

                CourseRVModel courseRVModel = new CourseRVModel(courseName,coursePrice,courseSuitedFor,courseImageLink,courseLink,courseDescription,courseName);
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        databaseReference.child(courseID).setValue(courseRVModel);
                        Toast.makeText(getContext(), "Course Added...", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Error is " + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

    }
}