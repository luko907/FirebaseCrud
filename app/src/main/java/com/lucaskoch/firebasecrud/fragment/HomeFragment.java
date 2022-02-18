package com.lucaskoch.firebasecrud.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.database.ValueEventListener;
import com.lucaskoch.firebasecrud.R;
import com.lucaskoch.firebasecrud.adapter.ItemRVAdapter;
import com.lucaskoch.firebasecrud.model.ItemRVModel;

import java.util.ArrayList;

import java.util.Objects;


public class HomeFragment extends Fragment {
    private ProgressBar idPB_homeProgressBar;
    private ArrayList<ItemRVModel> itemRVModelArrayList;
    private SwipeRefreshLayout home_swipe_container;
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId);
        FloatingActionButton idFA_btnAdd = view.findViewById(R.id.idFA_btnAdd);
        RecyclerView idRV_clothes = view.findViewById(R.id.idRV_clothes);
        itemRVModelArrayList = new ArrayList<>();
        ItemRVAdapter itemRVAdapter = new ItemRVAdapter(itemRVModelArrayList);
        idRV_clothes.setLayoutManager(new LinearLayoutManager(getContext()));
        idRV_clothes.setAdapter(itemRVAdapter);
        idPB_homeProgressBar = view.findViewById(R.id.idPB_homeProgressBar);
        idPB_homeProgressBar.setVisibility(View.VISIBLE);
        home_swipe_container = view.findViewById(R.id.home_swipe_container);







        home_swipe_container.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                //Do your task
                HomeFragment homeFragment = new HomeFragment();
                FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frameLayout_fragment_container, homeFragment).addToBackStack(null);
                fragmentTransaction.commit();
                home_swipe_container.setRefreshing(false);
            }
        });
        idFA_btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddItemFragment addItemFragment = new AddItemFragment();
                FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frameLayout_fragment_container, addItemFragment).addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                /* Log.v("Tag", "Snapshot "+ snapshot.toString());*/
                if (!snapshot.exists()) {
                    AddItemFragment addItemFragment = new AddItemFragment();
                    FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.frameLayout_fragment_container, addItemFragment).addToBackStack(null);
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
                itemRVModelArrayList.add(snapshot.getValue(ItemRVModel.class));
                itemRVAdapter.notifyDataSetChanged();
                idPB_homeProgressBar.setVisibility(View.GONE);
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