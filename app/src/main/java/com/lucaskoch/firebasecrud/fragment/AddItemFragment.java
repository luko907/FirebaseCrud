package com.lucaskoch.firebasecrud.fragment;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
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
import com.lucaskoch.firebasecrud.model.ItemRVModel;

import java.util.Locale;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;



public class AddItemFragment extends Fragment {

    TextInputEditText idEDT_title, idEDT_price,idEDT_description,idEDT_size;
    TextView max_price;
    ImageView idIMG_preview;
    Button idBTN_add_clothe,idBTN_upload_image;
    ProgressBar idPBLoading;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    String itemID;
    SwipeRefreshLayout add_item_swipe_container;
    AutoCompleteTextView idACT_typeDropdown,idACT_genderDropdown;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_item, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        idEDT_title = view.findViewById(R.id.idEDT_title);
        idEDT_price = view.findViewById(R.id.idEDT_price);
        idBTN_upload_image =view.findViewById(R.id.idBTN_upload_image);
        idIMG_preview = view.findViewById(R.id.idIMG_preview);
        idEDT_description = view.findViewById(R.id.idEDT_description);
        idACT_typeDropdown = view.findViewById(R.id.idACT_typeDropdown);
        idACT_genderDropdown = view.findViewById(R.id.idACT_genderDropdown);
        idEDT_size = view.findViewById(R.id.idEDT_size);

        max_price = view.findViewById(R.id.max_price);
        idBTN_add_clothe = view.findViewById(R.id.idBTN_add_clothe);
        idPBLoading = view.findViewById(R.id.idPBLoading);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("clothes");
        add_item_swipe_container = view.findViewById(R.id.add_item_swipe_container);

        idEDT_price.addTextChangedListener(new TextWatcher() {
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
        add_item_swipe_container.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                //Do your task
                idEDT_title.setText("");
                idEDT_price.setText("");
                idEDT_description.setText("");
                idEDT_title.requestFocus();


                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        add_item_swipe_container.setRefreshing(false);
                    }
                }, 300L);


            }
        });


        idBTN_add_clothe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = Objects.requireNonNull(idEDT_title.getText()).toString();
                String price = Objects.requireNonNull(idEDT_price.getText()).toString();
                String description = Objects.requireNonNull(idEDT_description.getText()).toString();
                String gender =Objects.requireNonNull(idACT_genderDropdown.getText()).toString();
                String type= Objects.requireNonNull(idACT_typeDropdown.getText()).toString();
                String img = Objects.requireNonNull(idBTN_upload_image.getText()).toString();
                String size=  Objects.requireNonNull(idEDT_size.getText()).toString();
                itemID = title.toLowerCase(Locale.ROOT);
                ItemRVModel itemRVModel = new ItemRVModel(title, img, type, gender, size, price, description,itemID);

                if (isNetworkConnected()) {
                    if (TextUtils.isEmpty(title) || TextUtils.isEmpty(price) || TextUtils.isEmpty(description)) {
                        Toast.makeText(getContext(), "Please fill empty values...", Toast.LENGTH_SHORT).show();
                   } else {
                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.child(itemRVModel.getItemID().toLowerCase(Locale.ROOT)).exists()) {
                                    /*Log.v("Tag", "ID exist : "+ snapshot.child(courseRVModel.getCourseID().toLowerCase(Locale.ROOT)));*/
                                    Toast.makeText(getContext(), "Data exist", Toast.LENGTH_SHORT).show();
                                    idEDT_title.setText("");
                                } else {
                                    databaseReference.child(itemID).setValue(itemRVModel);
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