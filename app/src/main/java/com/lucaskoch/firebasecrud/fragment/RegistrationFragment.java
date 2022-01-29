package com.lucaskoch.firebasecrud.fragment;


import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.lucaskoch.firebasecrud.OnEmailCheckListener;
import com.lucaskoch.firebasecrud.R;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;


public class RegistrationFragment extends Fragment {
    TextInputEditText idEdtUserName, idEdtUserPassword, idEdtUserConfirmPassword;
    Button idbtnRegister;
    ProgressBar idPBLoading;
    FirebaseAuth mAuth;
    TextView idTVLogin;
    SwipeRefreshLayout swipeLayout;
    FirebaseUser user;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_registration, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        idEdtUserName = view.findViewById(R.id.idEdtUserName);
        idbtnRegister = view.findViewById(R.id.idbtnRegister);
        idEdtUserPassword = view.findViewById(R.id.idEdtUserPassword);
        idEdtUserConfirmPassword = view.findViewById(R.id.idEdtUserConfirmPassword);
        idPBLoading = view.findViewById(R.id.idPBLoading);
        idTVLogin = view.findViewById(R.id.idTVLogin);
        mAuth = FirebaseAuth.getInstance();
        swipeLayout = view.findViewById(R.id.registration_swipe_container);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                //Do your task
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        swipeLayout.setRefreshing(false);
                    }
                }, 1000L);


            }
        });

        idTVLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginFragment loginFragment = new LoginFragment();
                FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frameLayout_fragment_container, loginFragment).addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
        idbtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                idPBLoading.setVisibility(View.VISIBLE);
                String userName = Objects.requireNonNull(idEdtUserName.getText()).toString();
                String pwd = Objects.requireNonNull(idEdtUserPassword.getText()).toString();
                String cnfPwd = Objects.requireNonNull(idEdtUserConfirmPassword.getText()).toString();
                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
                if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(pwd) || TextUtils.isEmpty(cnfPwd)) {
                    Toast.makeText(getContext(), "Please add your credentials..", Toast.LENGTH_SHORT).show();
                    idPBLoading.setVisibility(View.GONE);

                } else if (!userName.trim().matches(emailPattern)) {
                    idPBLoading.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Invalid email address", Toast.LENGTH_SHORT).show();
                    idEdtUserPassword.setText("");
                    idEdtUserConfirmPassword.setText("");
                    idEdtUserName.requestFocus();

                } else if (pwd.length() < 6) {
                    Toast.makeText(getContext(), "Please Password Must Be At Least six characters long", Toast.LENGTH_SHORT).show();
                    idEdtUserPassword.requestFocus();
                    idPBLoading.setVisibility(View.GONE);
                } else if (!pwd.equals(cnfPwd)) {
                    idPBLoading.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Password Doesn't Match", Toast.LENGTH_SHORT).show();
                    idEdtUserPassword.setText("");
                    idEdtUserConfirmPassword.setText("");
                    idEdtUserPassword.requestFocus();
                } else {
                    if (isNetworkConnected()) {
                        isCheckEmail(userName, new OnEmailCheckListener() {
                            @Override
                            public void onSuccess(boolean isRegistered) {
                                if (isRegistered) {
                                    idPBLoading.setVisibility(View.GONE);
                                    Toast.makeText(getContext(), "Email Already Taken", Toast.LENGTH_SHORT).show();
                                    idEdtUserName.setText("");
                                    idEdtUserPassword.setText("");
                                    idEdtUserConfirmPassword.setText("");
                                    idEdtUserName.requestFocus();
                                } else {
                                    mAuth.createUserWithEmailAndPassword(userName, pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                idPBLoading.setVisibility(View.GONE);
                                                Toast.makeText(getContext(), "User Registered..", Toast.LENGTH_SHORT).show();
                                                idEdtUserPassword.setText("");
                                                idEdtUserConfirmPassword.setText("");
                                                idEdtUserName.setText("");
                                                LoginFragment loginFragment = new LoginFragment();
                                                FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
                                                fragmentTransaction.replace(R.id.frameLayout_fragment_container, loginFragment).addToBackStack(null);
                                                fragmentTransaction.commit();
                                            }
                                        }
                                    });
                                }

                            }
                        });
                    } else {
                        idPBLoading.setVisibility(View.GONE);
                        idEdtUserName.setText("");
                        idEdtUserPassword.setText("");
                        idEdtUserConfirmPassword.setText("");
                        idEdtUserName.requestFocus();
                        Toast.makeText(getContext(), "No internet connexion", Toast.LENGTH_LONG).show();
                        Toast.makeText(getContext(), "Fail to register", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    public void isCheckEmail(final String email, final OnEmailCheckListener listener) {
        mAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                boolean check = Objects.requireNonNull(task.getResult().getSignInMethods()).size() == 1;
                listener.onSuccess(check);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });

    }


    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }
}