package com.lucaskoch.firebasecrud.fragment;

import static androidx.core.app.NavUtils.navigateUpTo;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lucaskoch.firebasecrud.R;
import com.lucaskoch.firebasecrud.model.CourseRVModel;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;



public class AddCourseFragment extends Fragment {

    TextInputEditText idEdtCourseName, idEdtCoursePrice, idEdtCourseSuitedFor, idEdtCourseImageLink, idEdtCourseLink, idEdtCourseDescription;
    TextView max_price;
    Button idbtnAddCourse;
    ProgressBar idPBLoading;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    String courseID;
    SwipeRefreshLayout add_course_swipe_container;


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
        max_price = view.findViewById(R.id.max_price);
        idbtnAddCourse = view.findViewById(R.id.idbtnAddCourse);
        idPBLoading = view.findViewById(R.id.idPBLoading);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Courses");
        add_course_swipe_container = view.findViewById(R.id.add_course_swipe_container);

        idEdtCoursePrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
/*                Log.v("Tag", "Start " + start);
                Log.v("Tag", "Before " + before);*/
                if ((start - before) < 4) {
                    max_price.setVisibility(View.GONE);
                } else {
                    max_price.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        add_course_swipe_container.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                //Do your task
                idEdtCourseName.setText("");
                idEdtCoursePrice.setText("");
                idEdtCourseSuitedFor.setText("");
                idEdtCourseImageLink.setText("");
                idEdtCourseLink.setText("");
                idEdtCourseDescription.setText("");
                idEdtCourseName.requestFocus();


                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        add_course_swipe_container.setRefreshing(false);
                    }
                }, 300L);


            }
        });


        idbtnAddCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String courseName = Objects.requireNonNull(idEdtCourseName.getText()).toString();
                String coursePrice = Objects.requireNonNull(idEdtCoursePrice.getText()).toString();
                String courseSuitedFor = Objects.requireNonNull(idEdtCourseSuitedFor.getText()).toString();
                String courseImageLink = Objects.requireNonNull(idEdtCourseImageLink.getText()).toString();
                String courseLink = Objects.requireNonNull(idEdtCourseLink.getText()).toString();
                String courseDescription = Objects.requireNonNull(idEdtCourseDescription.getText()).toString();
                courseID = courseName.toLowerCase(Locale.ROOT);
                CourseRVModel courseRVModel = new CourseRVModel(courseName, coursePrice, courseSuitedFor, courseImageLink, courseLink, courseDescription, courseName);

                if (isNetworkConnected()) {
                    boolean imageResponse = Patterns.WEB_URL.matcher(courseImageLink).matches();
                    if (TextUtils.isEmpty(courseName) || TextUtils.isEmpty(coursePrice) || TextUtils.isEmpty(courseSuitedFor) || TextUtils.isEmpty(courseImageLink) || TextUtils.isEmpty(courseLink) || TextUtils.isEmpty(courseDescription)) {
                        Toast.makeText(getContext(), "Please fill empty values...", Toast.LENGTH_SHORT).show();
                    } else if (!imageResponse) {
                        Toast.makeText(getContext(), "Invalid URL image ...", Toast.LENGTH_SHORT).show();
                        idEdtCourseImageLink.setText("");
                        idEdtCourseImageLink.requestFocus();
                   } else {
                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.child(courseRVModel.getCourseID().toLowerCase(Locale.ROOT)).exists()) {
                                    /*Log.v("Tag", "ID exist : "+ snapshot.child(courseRVModel.getCourseID().toLowerCase(Locale.ROOT)));*/
                                    Toast.makeText(getContext(), "Data exist", Toast.LENGTH_SHORT).show();
                                    idEdtCourseName.setText("");
                                } else {
                                    databaseReference.child(courseID).setValue(courseRVModel);
                                    Toast.makeText(getContext(), "Course Added...", Toast.LENGTH_SHORT).show();
                                    HomeFragment homeFragment = new HomeFragment();
                                    FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
                                    fragmentTransaction.replace(R.id.frameLayout_fragment_container, homeFragment).addToBackStack(null);
                                    fragmentTransaction.commit();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }

                } else {
                    Toast.makeText(getContext(), "No internet connexion", Toast.LENGTH_LONG).show();
                }


            }

            private boolean isNetworkConnected() {
                ConnectivityManager cm = (ConnectivityManager)requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                return cm.getActiveNetworkInfo() != null;
            }
        });

    }


}