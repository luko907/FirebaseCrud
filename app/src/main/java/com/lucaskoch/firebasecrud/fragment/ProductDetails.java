package com.lucaskoch.firebasecrud.fragment;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.lucaskoch.firebasecrud.R;
import com.squareup.picasso.Picasso;

import java.util.Locale;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;


public class ProductDetails extends Fragment {
    TextView idTV_title, idTV_price, idTV_description, idTV_type_gender, idTV_size_number;
    ImageView idIV_img;
    SwipeRefreshLayout product_detail_swipe_container;
    MaterialButton idBTN_delete,idBTN_edit;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_product_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        Bundle bundle = this.getArguments();
        assert bundle != null;
        String itemId = bundle.getString("id");
        String title = bundle.getString("title");
        String price = bundle.getString("price");
        String image = bundle.getString("image");
        String type_gender = bundle.getString("type_gender");
        String description = bundle.getString("description");
        String size = bundle.getString("size");
        String imgUUID = bundle.getString("imgUUID");
        product_detail_swipe_container = view.findViewById(R.id.product_detail_swipe_container);
        idTV_title = view.findViewById(R.id.idTV_title);
        idTV_description = view.findViewById(R.id.idTV_description);
        idTV_size_number = view.findViewById(R.id.idTV_size_number);
        idTV_price = view.findViewById(R.id.idTV_price);
        idTV_type_gender = view.findViewById(R.id.idTV_type_gender);
        idIV_img = view.findViewById(R.id.idIV_img);
        idTV_title.setText(title);
        idTV_size_number.setText(size);
        idTV_description.setText(description);
        idTV_price.setText(price);
        idTV_type_gender.setText(type_gender);
        Picasso.get().load(image).into(idIV_img);
        product_detail_swipe_container.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                //Do your task
                product_detail_swipe_container.setRefreshing(false);
            }
        });
        idBTN_edit = view.findViewById(R.id.idBTN_edit);

        idBTN_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle2 = new Bundle();
                bundle2.putString("imgUUID",imgUUID);
                bundle2.putString("itemId",itemId);
                EditProduct editProduct = new EditProduct();
                editProduct.setArguments(bundle2);
                FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frameLayout_fragment_container, editProduct).addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        idBTN_delete = view.findViewById(R.id.idBTN_delete);
        idBTN_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alert = new AlertDialog.Builder(requireContext());
                alert.setTitle("Delete entry");
                alert.setMessage("Are you sure you want to delete?");
                alert.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {

                        FirebaseStorage storageIn = FirebaseStorage.getInstance();
                        StorageReference stor = storageIn.getReference();
                        databaseReference = firebaseDatabase.getReference("Users");
                        String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                        stor.child("images/" + userId+"/"+imgUUID).delete();
                        databaseReference.child(userId).child(itemId).removeValue();
                        HomeFragment homeFragment = new HomeFragment();
                        FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.frameLayout_fragment_container, homeFragment);
                        fragmentTransaction.commit();
                        Toast.makeText(getContext(), "Deleted", Toast.LENGTH_SHORT).show();
                    }
                });
                alert.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // close dialog
                        dialog.cancel();
                    }
                });
                alert.show();
            }
        });
    }
}