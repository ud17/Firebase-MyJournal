package com.example.myjournal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.security.PrivateKey;

import javax.security.auth.PrivateCredentialPermission;

import Utils.JournalAPI;

public class LoginScreenActivity extends AppCompatActivity implements View.OnClickListener {

    private Button createAccount_btn,signIn_btn;
    private EditText userpassword_et;
    private AutoCompleteTextView userEmail_et;
    private ProgressBar progressBar_login;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = firestore.collection("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        firebaseAuth = FirebaseAuth.getInstance();

        createAccount_btn = findViewById(R.id.signUp_btn);
        signIn_btn = findViewById(R.id.signIn_btn);

        userEmail_et = findViewById(R.id.email_et_login);
        userpassword_et = findViewById(R.id.password_et_login);

        progressBar_login = findViewById(R.id.progressBar_login);

        signIn_btn.setOnClickListener(this);
        createAccount_btn.setOnClickListener(this);

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                currentUser = firebaseAuth.getCurrentUser();

                if (currentUser != null){

                }else
                {

                }
            }
        };
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case (R.id.signUp_btn):
                startActivity(new Intent(LoginScreenActivity.this,CreateAccountActivity.class));
                break;
            case (R.id.signIn_btn):
                String email = userEmail_et.getText().toString().trim();
                String password = userpassword_et.getText().toString().trim();
                userEmailPasswordLogin(email,password);
                break;
        }
    }

    private void userEmailPasswordLogin(final String userEmail , String userPassword) {

        progressBar_login.setVisibility(View.VISIBLE);

        if (!TextUtils.isEmpty(userEmail) && !TextUtils.isEmpty(userEmail)){

            firebaseAuth.signInWithEmailAndPassword(userEmail , userPassword)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                String currentUserId = currentUser.getUid();

                                collectionReference.whereEqualTo("userId", currentUserId )
                                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                            @Override
                                            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                                if (e != null){
                                                    Log.e("Login", "onEvent: " + e.toString() );

                                                    return;
                                                }

                                                if (!queryDocumentSnapshots.isEmpty()){
                                                    JournalAPI journalAPI = JournalAPI.getInstance();

                                                    for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots){
                                                        journalAPI.setUsername(snapshot.getString("username"));
                                                        journalAPI.setUserId(snapshot.getString("userId"));
                                                    }
                                                    progressBar_login.setVisibility(View.INVISIBLE);
                                                    startActivity(new Intent(LoginScreenActivity.this,DashboardActivity.class));
                                                    finish();

                                                }

                                            }
                                        });
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("Login", "onFailure: " + e.getMessage() + " " + e.toString() );
                    userEmail_et.setError(e.getMessage());
                    userpassword_et.setText("");
                    progressBar_login.setVisibility(View.INVISIBLE);
                }
            });

        }else {
            progressBar_login.setVisibility(View.INVISIBLE);
            Toast.makeText(this, "Fields cannot be empty!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        currentUser = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (firebaseAuth != null){
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }
}