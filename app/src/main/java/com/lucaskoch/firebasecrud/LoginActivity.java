package com.lucaskoch.firebasecrud;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class LoginActivity extends AppCompatActivity {

    TextInputEditText idEdtUserName, idEdtUserPassword;
    Button idbtnLogin;
    ProgressBar idPBLoading;
    TextView idTVRegister;
    FirebaseAuth mAuth;
    SwipeRefreshLayout swipeLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        idEdtUserName = findViewById(R.id.idEdtUserName);
        idEdtUserPassword = findViewById(R.id.idEdtUserPassword);
        idbtnLogin = findViewById(R.id.idbtnLogin);
        idPBLoading = findViewById(R.id.idPBLoading);
        idTVRegister = findViewById(R.id.idTVRegister);
        mAuth = FirebaseAuth.getInstance();
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

        idTVRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(i);
            }
        });

        idbtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                idPBLoading.setVisibility(View.VISIBLE);
                String userName = Objects.requireNonNull(idEdtUserName.getText()).toString();
                String pwd = Objects.requireNonNull(idEdtUserPassword.getText()).toString();
                if (TextUtils.isEmpty(userName) && TextUtils.isEmpty(pwd)) {
                    Toast.makeText(LoginActivity.this, "Please enter your credentials", Toast.LENGTH_SHORT).show();
                    idPBLoading.setVisibility(View.GONE);
                } else {
                    mAuth.signInWithEmailAndPassword(userName, pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                idPBLoading.setVisibility(View.GONE);
                                Toast.makeText(LoginActivity.this, "Login Succesful", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(i);
                                finish();
                            } else {
                                idPBLoading.setVisibility(View.GONE);
                                Toast.makeText(LoginActivity.this, "Fail to login", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

    }


    //Comprobamos si el usuario ya esta logeado
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            Intent i = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(i);
            this.finish();
        }
    }
}