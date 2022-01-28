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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.jakewharton.processphoenix.ProcessPhoenix;
import com.lucaskoch.firebasecrud.R;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;


public class LoginFragment extends Fragment {
    TextInputEditText idEdtUserName, idEdtUserPassword;
    Button idbtnLogin;
    ProgressBar idPBLoading;
    TextView idTVRegister;
    FirebaseAuth mAuth;
    FirebaseUser user;
    FragmentTransaction fragmentTransaction;
    SwipeRefreshLayout swipeLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        idEdtUserName = view.findViewById(R.id.idEdtUserName);
        idEdtUserPassword = view.findViewById(R.id.idEdtUserPassword);
        idbtnLogin = view.findViewById(R.id.idbtnLogin);
        idPBLoading = view.findViewById(R.id.idPBLoading);
        idTVRegister = view.findViewById(R.id.idTVRegister);
        mAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        swipeLayout = view.findViewById(R.id.login_swipe_container);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                //Do your task
                ProcessPhoenix.triggerRebirth(getContext());
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        swipeLayout.setRefreshing(false);
                    }
                }, 1000L);


            }
        });
        idTVRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegistrationFragment registrationFragment = new RegistrationFragment();
                FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frameLayout_fragment_container, registrationFragment).addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        idbtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                idPBLoading.setVisibility(View.VISIBLE);
                String userName = Objects.requireNonNull(idEdtUserName.getText()).toString();
                String pwd = Objects.requireNonNull(idEdtUserPassword.getText()).toString();
                if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(pwd)) {
                    Toast.makeText(getContext(), "Please enter your credentials", Toast.LENGTH_SHORT).show();
                    idPBLoading.setVisibility(View.GONE);
                } else {
                    if (isNetworkConnected()){
                        mAuth.signInWithEmailAndPassword(userName, pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    idPBLoading.setVisibility(View.GONE);
                                    Toast.makeText(getContext(), "Login Succesful", Toast.LENGTH_SHORT).show();
                                    HomeFragment homeFragment = new HomeFragment();
                                    FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
                                    fragmentTransaction.replace(R.id.frameLayout_fragment_container, homeFragment).addToBackStack(null);
                                    fragmentTransaction.commit();
                                } else {
                                    idPBLoading.setVisibility(View.GONE);
                                    if (user != null) {
                                        String email = user.getEmail();
                                        Toast.makeText(getContext(), "Invalid Password ", Toast.LENGTH_LONG).show();
                                        idEdtUserPassword.setText("");
                                        idEdtUserPassword.requestFocus();
                                    }else{
                                        Toast.makeText(getContext(), "Fail to login", Toast.LENGTH_LONG).show();
                                    }
                                }
                            }
                        });
                    }else{
                        Toast.makeText(getContext(), "No internet connexion", Toast.LENGTH_LONG).show();
                        Toast.makeText(getContext(), "Fail to login", Toast.LENGTH_SHORT).show();
                        idPBLoading.setVisibility(View.GONE);
                    }



                }
            }
        });

    }
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }
    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
           /* Intent i = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(i);
            this.finish();*/
        }
    }
}