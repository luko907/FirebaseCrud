package com.lucaskoch.firebasecrud;


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

        swipeLayout = view.findViewById(R.id.swipe_container);
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
        mAuth = FirebaseAuth.getInstance();
        idTVLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginFragment loginFragment = new LoginFragment();
                FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frameLayout_fragment_container, loginFragment);
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

                if (!pwd.equals(cnfPwd)) {
                    Toast.makeText(getContext(), "Please check this password", Toast.LENGTH_SHORT).show();

                } else if (TextUtils.isEmpty(userName) && TextUtils.isEmpty(pwd) && TextUtils.isEmpty(cnfPwd)) {
                    Toast.makeText(getContext(), "Please add your credentials..", Toast.LENGTH_SHORT).show();
                    idPBLoading.setVisibility(View.GONE);
                } else {
                    mAuth.createUserWithEmailAndPassword(userName, pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                idPBLoading.setVisibility(View.GONE);
                                Toast.makeText(getContext(), "User Registered..", Toast.LENGTH_SHORT).show();
                               /* Intent i = new Intent(RegistrationActivity.this, LoginActivity.class);
                                startActivity(i);
                                finish();*/
                            } else {
                                idPBLoading.setVisibility(View.GONE);
                                Toast.makeText(getContext(), "Fail to register", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}