package com.lucaskoch.firebasecrud.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lucaskoch.firebasecrud.R;
import com.lucaskoch.firebasecrud.model.CourseRVModel;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CourseRVAdapter extends RecyclerView.Adapter<CourseRVAdapter.ViewHolder> {

    ArrayList<CourseRVModel> courseRVModelArrayList;

    public CourseRVAdapter(ArrayList<CourseRVModel> courseRVModelArrayList) {
        this.courseRVModelArrayList = courseRVModelArrayList;
    }

    @NonNull
    @Override
    public CourseRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.course_rv_item, parent, false);

        return new ViewHolder(view);

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {


        holder.id_ITcourseName.setText(courseRVModelArrayList.get(position).getCourseName() + "...");
        holder.id_TIcoursePrice.setText("$ " + courseRVModelArrayList.get(position).getCoursePrice());

        Picasso.get().load(courseRVModelArrayList.get(position).getCourseImageLink()).into(holder.id_ITimg);
    }

    @Override
    public int getItemCount() {
        return courseRVModelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView id_ITcourseName, id_TIcoursePrice;
        ImageView id_ITimg;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            id_ITcourseName = itemView.findViewById(R.id.id_ITcourseName);
            id_TIcoursePrice = itemView.findViewById(R.id.id_TIcoursePrice);
            id_ITimg = itemView.findViewById(R.id.id_ITimg);
        }
    }
}
