package com.lucaskoch.firebasecrud;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
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

public class RegistrationActivity extends AppCompatActivity {

    TextInputEditText idEdtUserName, idEdtUserPassword, idEdtUserConfirmPassword;
    Button idbtnRegister;
    ProgressBar idPBLoading;
    FirebaseAuth mAuth;
    TextView idTVLogin;
    SwipeRefreshLayout swipeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);


        idEdtUserName = findViewById(R.id.idEdtUserName);
        idbtnRegister = findViewById(R.id.idbtnRegister);
        idEdtUserPassword = findViewById(R.id.idEdtUserPassword);
        idEdtUserConfirmPassword = findViewById(R.id.idEdtUserConfirmPassword);
        idPBLoading = findViewById(R.id.idPBLoading);
        idTVLogin = findViewById(R.id.idTVLogin);

        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
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
                Intent i = new Intent(RegistrationActivity.this, LoginActivity.class);
                startActivity(i);
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
                    Toast.makeText(RegistrationActivity.this, "Please check this password", Toast.LENGTH_SHORT).show();

                } else if (TextUtils.isEmpty(userName) && TextUtils.isEmpty(pwd) && TextUtils.isEmpty(cnfPwd)) {
                    Toast.makeText(RegistrationActivity.this, "Please add your credentials..", Toast.LENGTH_SHORT).show();
                } else {
                    mAuth.createUserWithEmailAndPassword(userName, pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                idPBLoading.setVisibility(View.GONE);
                                Toast.makeText(RegistrationActivity.this, "User Registered..", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(RegistrationActivity.this, LoginActivity.class);
                                startActivity(i);
                                finish();
                            } else {
                                idPBLoading.setVisibility(View.GONE);
                                Toast.makeText(RegistrationActivity.this, "Fail to register", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}