package com.example.myjournal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import Utils.JournalAPI;

public class CreateAccountActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    private CollectionReference collectionReference = firestore.collection("Users");

    private EditText email_crtAcct,password_crtAcct,username_crtAcct;
    private ProgressBar progressBar_crtAcct;
    private Button crtAcct_Btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        firebaseAuth = FirebaseAuth.getInstance();

        crtAcct_Btn = findViewById(R.id.createAccount_btn);

        email_crtAcct = findViewById(R.id.email_crt_acct);
        username_crtAcct = findViewById(R.id.username_crtAcct);
        password_crtAcct = findViewById(R.id.password_crt_acct);

        progressBar_crtAcct = findViewById(R.id.progressBar_crt_acct);

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                currentUser = firebaseAuth.getCurrentUser();

                if (currentUser!=null){

                }
                else{

                }
            }
        };

        crtAcct_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!TextUtils.isEmpty(email_crtAcct.getText().toString().trim()) && !TextUtils.isEmpty(password_crtAcct.getText().toString().trim()) &&
                        !TextUtils.isEmpty(username_crtAcct.getText().toString().trim())){

                    String email = email_crtAcct.getText().toString().trim();
                    String username = username_crtAcct.getText().toString().trim();
                    String password = password_crtAcct.getText().toString().trim();

                    createUserEmailAccount(email , password , username);
                }else{
                    Toast.makeText(CreateAccountActivity.this, "Empty fields not allowed!", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void createUserEmailAccount(String email, String password, final String username){
        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(username)){

            progressBar_crtAcct.setVisibility(View.VISIBLE);

            firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){

                        currentUser = firebaseAuth.getCurrentUser();

                        final String currentUserId = currentUser.getUid();

                        Map<String,String> params = new HashMap<>();

                        params.put("userId" , currentUserId);
                        params.put("username" , username);

                        collectionReference.add(params).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                documentReference.get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.getResult().exists()){
                                                    progressBar_crtAcct.setVisibility(View.INVISIBLE);
                                                    Intent intent = new Intent(CreateAccountActivity.this , DashboardActivity.class);

                                                    String name = task.getResult().getString("username");

                                                    JournalAPI journalAPI = JournalAPI.getInstance();

                                                    journalAPI.setUserId(currentUserId);
                                                    journalAPI.setUsername(name);

                                                    intent.putExtra("username" , name);
                                                    intent.putExtra("userId" , currentUserId);

                                                    startActivity(intent);
                                                    finish();
                                                }
                                            }
                                        });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });


                    }else
                    {
                       //something went wrong!

                    }
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        currentUser = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }
}