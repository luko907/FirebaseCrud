package com.lucaskoch.firebasecrud.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jakewharton.processphoenix.ProcessPhoenix;
import com.lucaskoch.firebasecrud.R;
import com.lucaskoch.firebasecrud.model.ItemRVModel;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;


public class EditProduct extends Fragment {
    private TextInputEditText idEDT_title, idEDT_price, idEDT_description;
    private TextInputLayout idTI_title,idTI_price,idTI_description;
    private TextView max_price;
    private ImageView idIMG_preview;
    private MaterialButton idBTN_upload_image, idBTN_accept,idBTN_cancel_img_preview,idBTN_cancel;
    private DatabaseReference databaseReference;
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private StorageReference storageReference;
    private String itemId;
    private SwipeRefreshLayout add_item_swipe_container;
    private AutoCompleteTextView idACT_typeDropdown, idACT_genderDropdown, idACT_sizeDropdown;
    private ActivityResultLauncher<Intent> someActivityResultLauncher;
    private String[] types;
    private String[] gender;
    private String randomUUID;
    private String[] sizes;
    private Uri imageUri = null;
    private String imageLinkFireBase;
    private Bitmap bitmap;
    private byte[] dataToSend;
    private final EditProduct.ObservableBoolean uploadImageToFirebase = new EditProduct.ObservableBoolean();
    private final EditProduct.ObservableBoolean checkImageLinkFirebase = new EditProduct.ObservableBoolean();
    String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
    FirebaseStorage storageIn = FirebaseStorage.getInstance();
    StorageReference stor = storageIn.getReference();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_product, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



        randomUUID = UUID.randomUUID().toString();
        types = getResources().getStringArray(R.array.clothe_type);
        gender = getResources().getStringArray(R.array.gender);
        sizes = getResources().getStringArray(R.array.sizes);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        idTI_title = view.findViewById(R.id.idTI_title);
        idTI_price =view.findViewById(R.id.idTI_price);
        idTI_description = view.findViewById(R.id.idTI_description);

        idEDT_title = view.findViewById(R.id.idEDT_title);
        idEDT_price = view.findViewById(R.id.idEDT_price);
        idBTN_cancel = view.findViewById(R.id.idBTN_cancel);
        idBTN_accept = view.findViewById(R.id.idBTN_accept);
        idBTN_upload_image = view.findViewById(R.id.idBTN_upload_image);
        idBTN_cancel_img_preview = view.findViewById(R.id.idBTN_cancel_img_preview);
        idIMG_preview = view.findViewById(R.id.idIMG_preview);
        idEDT_description = view.findViewById(R.id.idEDT_description);
        idACT_typeDropdown = view.findViewById(R.id.idACT_typeDropdown);
        idACT_genderDropdown = view.findViewById(R.id.idACT_genderDropdown);
        idACT_sizeDropdown = view.findViewById(R.id.idACT_sizeDropdown);
        max_price = view.findViewById(R.id.max_price);
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");
        add_item_swipe_container = view.findViewById(R.id.edit_item_swipe_container);



        Bundle bundle = this.getArguments();
        assert bundle != null;
        String imgUUID = bundle.getString("imgUUID");
        itemId = bundle.getString("itemId");



        databaseReference.child(userId).child(itemId).get().addOnSuccessListener(shot -> {
            Picasso.get().load(Objects.requireNonNull(shot.child("img").getValue()).toString()).into(idIMG_preview);
            idBTN_cancel_img_preview.setVisibility(View.VISIBLE);
            String titleTemp = Objects.requireNonNull(shot.child("title").getValue()).toString();
            String priceTemp = Objects.requireNonNull(shot.child("price").getValue()).toString();
            String descriptionTemp = Objects.requireNonNull(shot.child("description").getValue()).toString();
            String genderTemp = Objects.requireNonNull(shot.child("gender").getValue()).toString();
            String typeTemp = Objects.requireNonNull(shot.child("type").getValue()).toString();
            String imgTemp = Objects.requireNonNull(shot.child("img").getValue()).toString();
            String sizeTemp = Objects.requireNonNull(shot.child("size").getValue()).toString();
            idTI_title.setHint("");
            idTI_price.setHint("");
            idTI_description.setHint("");
            idEDT_title.setHint(Html.fromHtml("<i>" + titleTemp + "</i>"));
            idEDT_price.setHint(Html.fromHtml("<i>" + priceTemp + "</i>"));
            idEDT_description.setHint(Html.fromHtml("<i>" + descriptionTemp + "</i>"));
            idACT_typeDropdown.setText(Html.fromHtml("<i>" + typeTemp + "</i>"));
            idACT_genderDropdown.setText(Html.fromHtml("<i>" + genderTemp + "</i>"));
            idACT_sizeDropdown.setText(Html.fromHtml("<i>" + sizeTemp + "</i>"));
            ArrayAdapter<String> typeAdapter =
                    new ArrayAdapter<>(getContext(), R.layout.drowdown_template, types);
            idACT_typeDropdown.setAdapter(typeAdapter);
            ArrayAdapter<String> genderAdapter =
                    new ArrayAdapter<>(getContext(), R.layout.drowdown_template, gender);
            idACT_genderDropdown.setAdapter(genderAdapter);
            ArrayAdapter<String> sizeAdapter =
                    new ArrayAdapter<>(getContext(), R.layout.drowdown_template, sizes);
            idACT_sizeDropdown.setAdapter(sizeAdapter);
        });

        idBTN_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeFragment homeFragment = new HomeFragment();
                FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frameLayout_fragment_container, homeFragment).addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
        uploadImageToFirebase.setOnBooleanChangeListener(new EditProduct.OnBooleanChangeListener() {
            @Override
            public void onBooleanChanged(boolean newState) {
                if (newState) {
                    stor.child("images/" + userId+"/"+randomUUID).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            imageLinkFireBase = uri.toString();
                            checkImageLinkFirebase.set(true);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                        }
                    });
                }
            }
        });
        idACT_typeDropdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                idACT_typeDropdown.setTextColor(getResources().getColor(R.color.white));
            }
        });
        idACT_genderDropdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                idACT_genderDropdown.setTextColor(getResources().getColor(R.color.white));
            }
        });
        idACT_sizeDropdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                idACT_sizeDropdown.setTextColor(getResources().getColor(R.color.white));
            }
        });



        someActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            final ContentResolver cr = requireActivity().getContentResolver();
                            imageUri = Objects.requireNonNull(result.getData()).getData();
                /*            Log.v("Tag","imageUri :"+ imageUri.toString());
                            Log.v("Tag","result.getData() :"+result.getData());
                            Log.v("Tag","result.getData()).getData() :"+result.getData().getData());*/
                            bitmap = null;
                            try {
                                bitmap = MediaStore
                                        .Images
                                        .Media
                                        .getBitmap(
                                                cr,
                                                imageUri);
                                ByteArrayOutputStream temp = new ByteArrayOutputStream();
                                bitmap = resizeImage(bitmap, 600, true);
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, temp);
                                dataToSend = temp.toByteArray();
                                Log.v("Tag","dataToSend " + dataToSend);
                                idIMG_preview.setImageBitmap(bitmap);
                                idBTN_cancel_img_preview.setVisibility(View.VISIBLE);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });


        idBTN_cancel_img_preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                idIMG_preview.setImageBitmap(null);
                idBTN_cancel_img_preview.setVisibility(View.GONE);
                dataToSend = null;
            }
        });
        idBTN_upload_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();

            }
        });
        idEDT_price.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
      /*        Log.v("Tag", "Start " + start);
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

            @SuppressLint("SetTextI18n")
            @Override
            public void onRefresh() {
                //Do your task
                ArrayAdapter<String> typeAdapter =
                        new ArrayAdapter<>(getContext(), R.layout.drowdown_template, types);
                ArrayAdapter<String> genderAdapter =
                        new ArrayAdapter<>(getContext(), R.layout.drowdown_template, gender);
                ArrayAdapter<String> sizeAdapter =
                        new ArrayAdapter<>(getContext(), R.layout.drowdown_template, sizes);
                idACT_typeDropdown.setText("Choose Clothe");
                idACT_typeDropdown.setAdapter(typeAdapter);
                idACT_genderDropdown.setText("Choose Gender");
                idACT_genderDropdown.setAdapter(genderAdapter);
                idACT_sizeDropdown.setText("Choose Size");
                idACT_sizeDropdown.setAdapter(sizeAdapter);
                idEDT_title.setText("");
                idEDT_price.setText("");
                idIMG_preview.setImageBitmap(null);
                idEDT_description.setText("");
                idEDT_title.requestFocus();
                add_item_swipe_container.setRefreshing(false);
            }
        });

        idBTN_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = Objects.requireNonNull(idEDT_title.getText()).toString();
                String price = Objects.requireNonNull(idEDT_price.getText()).toString();
                String description = Objects.requireNonNull(idEDT_description.getText()).toString();
                String gender = Objects.requireNonNull(idACT_genderDropdown.getText()).toString();
                String type = Objects.requireNonNull(idACT_typeDropdown.getText()).toString();
                String img = Objects.requireNonNull(idBTN_upload_image.getText()).toString();
                String size = Objects.requireNonNull(idACT_sizeDropdown.getText()).toString();

                if (isNetworkConnected()) {
                    databaseReference.child(userId).child(title.toLowerCase(Locale.ROOT)).get().addOnSuccessListener(snapshot -> {
                        if (snapshot.getValue() != null && !TextUtils.isEmpty(title)) {
                            Toast.makeText(getContext(), "Name already exist", Toast.LENGTH_SHORT).show();
                            idEDT_title.setText("");
                            idEDT_title.requestFocus();
                        }else{
                            if (!TextUtils.isEmpty(title)) {
                                databaseReference.child(userId).child(itemId).get().addOnSuccessListener(shot -> {
                                    databaseReference.child(userId).child(title.toLowerCase(Locale.ROOT)).setValue(shot.getValue());
                                    databaseReference.child(userId).child(title.toLowerCase(Locale.ROOT)).child("title").setValue(title);
                                    databaseReference.child(userId).child(title.toLowerCase(Locale.ROOT)).child("itemID").setValue(title.toLowerCase(Locale.ROOT));
                                    if (!TextUtils.isEmpty(price)) {
                                        databaseReference.child(userId).child(title.toLowerCase(Locale.ROOT)).child("price").setValue(price);
                                    } else {
                                        databaseReference.child(userId).child(itemId).child("price").setValue(shot.child("price").getValue());
                                    }
                                    if (!TextUtils.isEmpty(description)) {
                                        databaseReference.child(userId).child(title.toLowerCase(Locale.ROOT)).child("description").setValue(description);
                                    } else {
                                        databaseReference.child(userId).child(itemId).child("description").setValue(shot.child("description").getValue());
                                    }
                                    if (gender.equals("Choose Gender")) {
                                        databaseReference.child(userId).child(itemId).child("gender").setValue(shot.child("gender").getValue());
                                    } else {
                                        databaseReference.child(userId).child(title.toLowerCase(Locale.ROOT)).child("gender").setValue(gender);
                                    }
                                    if (type.equals("Choose Clothe")) {
                                        databaseReference.child(userId).child(itemId).child("type").setValue(shot.child("type").getValue());
                                    } else {
                                        databaseReference.child(userId).child(title.toLowerCase(Locale.ROOT)).child("type").setValue(type);
                                    }
                                    if (size.equals("Choose Size")) {
                                        databaseReference.child(userId).child(itemId).child("size").setValue(shot.child("size").getValue());
                                    } else {
                                        databaseReference.child(userId).child(title.toLowerCase(Locale.ROOT)).child("size").setValue(size);
                                    }

                                    databaseReference.child(userId).child(itemId).removeValue();
                                    if (bitmap == null) {
                                        HomeFragment homeFragment = new HomeFragment();
                                        FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
                                        fragmentTransaction.replace(R.id.frameLayout_fragment_container, homeFragment).addToBackStack(null);
                                        fragmentTransaction.commit();
                                        /* ProcessPhoenix.triggerRebirth(requireContext());*/
                                        Toast.makeText(getContext(), "Uploaded", Toast.LENGTH_SHORT).show();
                                    } else {
                                        stor.child("images/" + userId+"/"+imgUUID).delete();
                                        uploadImage(dataToSend);
                                    }

                                });
                            } else {
                                databaseReference.child(userId).child(itemId).get().addOnSuccessListener(shot -> {
                                    if (!TextUtils.isEmpty(price)) {
                                        databaseReference.child(userId).child(itemId).child("price").setValue(price);
                                    } else {
                                        databaseReference.child(userId).child(itemId).child("price").setValue(shot.child("price").getValue());
                                    }
                                    if (!TextUtils.isEmpty(description)) {
                                        databaseReference.child(userId).child(itemId).child("description").setValue(description);
                                    } else {
                                        databaseReference.child(userId).child(itemId).child("description").setValue(shot.child("description").getValue());
                                    }
                                    if (gender.equals("Choose Gender")) {
                                        databaseReference.child(userId).child(itemId).child("gender").setValue(shot.child("gender").getValue());
                                    } else {
                                        databaseReference.child(userId).child(itemId).child("gender").setValue(gender);
                                    }
                                    if (type.equals("Choose Clothe")) {
                                        databaseReference.child(userId).child(itemId).child("type").setValue(shot.child("type").getValue());
                                    } else {
                                        databaseReference.child(userId).child(itemId).child("type").setValue(type);
                                    }
                                    if (size.equals("Choose Size")) {
                                        databaseReference.child(userId).child(itemId).child("size").setValue(shot.child("size").getValue());
                                    } else {
                                        databaseReference.child(userId).child(itemId).child("size").setValue(size);
                                    }
                                    if (TextUtils.isEmpty(title)) {
                                        databaseReference.child(userId).child(itemId).child("title").setValue(shot.child("title").getValue());
                                    }
                                   /* HomeFragment homeFragment = new HomeFragment();
                                    FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
                                    fragmentTransaction.replace(R.id.frameLayout_fragment_container, homeFragment).addToBackStack(null);
                                    fragmentTransaction.commit();
                                    ProcessPhoenix.triggerRebirth(requireContext());
                                    Toast.makeText(getContext(), "Uploaded", Toast.LENGTH_SHORT).show();*/
                                });
                                if (bitmap == null) {
                                    HomeFragment homeFragment = new HomeFragment();
                                    FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
                                    fragmentTransaction.replace(R.id.frameLayout_fragment_container, homeFragment).addToBackStack(null);
                                    fragmentTransaction.commit();
                                   /* ProcessPhoenix.triggerRebirth(requireContext());*/
                                    Toast.makeText(getContext(), "Uploaded", Toast.LENGTH_SHORT).show();
                                } else {
                                    stor.child("images/" + userId+"/"+imgUUID).delete();
                                    uploadImage(dataToSend);
                                }
                            }



                        }
                    });



                    checkImageLinkFirebase.setOnBooleanChangeListener(new EditProduct.OnBooleanChangeListener() {
                        @Override
                        public void onBooleanChanged(boolean newState) {
                            if (newState) {
                                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("Users").child(userId);
                                mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        /*      Log.v("Tag","imagelink" + imageLinkFireBase);*/
                                        if (TextUtils.isEmpty(title)) {
                                            mDatabase.child(itemId).child("img").setValue(imageLinkFireBase);
                                            mDatabase.child(itemId).child("imgUUID").setValue(randomUUID);
                                        } else {
                                            mDatabase.child(title.toLowerCase(Locale.ROOT)).child("img").setValue(imageLinkFireBase);
                                            mDatabase.child(title.toLowerCase(Locale.ROOT)).child("imgUUID").setValue(randomUUID);
                                        }

                                        HomeFragment homeFragment = new HomeFragment();
                                        FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
                                        fragmentTransaction.replace(R.id.frameLayout_fragment_container, homeFragment).addToBackStack(null);
                                        fragmentTransaction.commit();
                                        Toast.makeText(getContext(), "Uploaded", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }

                        }
                    });




                } else {
                    Toast.makeText(getContext(), "No internet connexion", Toast.LENGTH_LONG).show();
                }

            }


        });

    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    public void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        someActivityResultLauncher.launch(intent);
    }

    private void uploadImage(byte[] file) {
        if (file != null) {
            final ProgressDialog progressDialog = new ProgressDialog(getContext());
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            StorageReference putFileUriToFirebase = storageReference.child("images/" + userId+"/"+randomUUID);
            putFileUriToFirebase.putBytes(file)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            uploadImageToFirebase.set(true);
                            progressDialog.dismiss();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });
        }
    }


    public interface OnBooleanChangeListener {
        void onBooleanChanged(boolean newState);
    }

    public static class ObservableBoolean {
        private EditProduct.OnBooleanChangeListener listener;

        private boolean value = false;

        public void setOnBooleanChangeListener(EditProduct.OnBooleanChangeListener listener) {
            this.listener = listener;
        }

        public boolean get() {
            return value;
        }

        public void set(boolean value) {
            this.value = value;

            if (listener != null) {
                listener.onBooleanChanged(value);
            }
        }
    }

    public static Bitmap resizeImage(Bitmap realImage, float maxImageSize,
                                     boolean filter) {
        float ratio = Math.min(
                (float) maxImageSize / realImage.getWidth(),
                (float) maxImageSize / realImage.getHeight());
        int width = Math.round((float) ratio * realImage.getWidth());
        int height = Math.round((float) ratio * realImage.getHeight());

        Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width,
                height, filter);
        return newBitmap;
    }
}