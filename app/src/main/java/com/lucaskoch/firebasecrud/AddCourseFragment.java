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

import com.google.android.material.textfield.TextInputEditText;


public class AddCourseFragment extends Fragment {

    TextInputEditText idEdtCourseName, idEdtCoursePrice, idEdtCourseSuitedFor, idEdtCourseImageLink, idEdtCourseLink, idEdtCourseDescription;
    Button idbtnAddCourse;
    ProgressBar idPBLoading;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_course, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }
}