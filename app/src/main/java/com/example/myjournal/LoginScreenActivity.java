package com.example.myjournal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginScreenActivity extends AppCompatActivity implements View.OnClickListener {

    private Button createAccount_btn,signIn_btn;
    private ProgressBar progressBar_login;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        createAccount_btn = findViewById(R.id.signUp_btn);
        signIn_btn = findViewById(R.id.signIn_btn);

        signIn_btn.setOnClickListener(this);
        createAccount_btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case (R.id.signUp_btn):
                startActivity(new Intent(LoginScreenActivity.this,CreateAccountActivity.class));
                break;
            case (R.id.signIn_btn):
                startActivity(new Intent(LoginScreenActivity.this, DashboardActivity.class));
                break;
        }
    }
}