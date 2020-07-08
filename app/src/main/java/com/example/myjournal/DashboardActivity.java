package com.example.myjournal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.sql.Time;
import java.util.Date;

import Utils.Journal;
import Utils.JournalAPI;
import io.grpc.Context;

public class DashboardActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int GALLERY_CODE = 1;
    private ImageView background_imageView, upload_imageView;
    private Button savePost_btn;
    private EditText title_et,desc_et;
    private TextView name_tv,today_tv;

    private ProgressBar progressBar_dash;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser firebaseUser;

    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = firestore.collection("Journal");
    private StorageReference storageReference;

    private String currentUsername , currentUserId;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        firebaseAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        background_imageView = findViewById(R.id.imageView_dash);
        upload_imageView = findViewById(R.id.upload_dash_imageView);

        savePost_btn = findViewById(R.id.savePost_btn);

        title_et = findViewById(R.id.title_et_dash);
        desc_et = findViewById(R.id.desc_et_dash);

        name_tv = findViewById(R.id.name_dash);
       // today_tv = findViewById(R.id.today_dash);

        progressBar_dash = findViewById(R.id.progressBar_dash);

        savePost_btn.setOnClickListener(this);
        upload_imageView.setOnClickListener(this);

        if (JournalAPI.getInstance() != null){
            currentUsername = JournalAPI.getInstance().getUsername();
            currentUserId = JournalAPI.getInstance().getUserId();

            name_tv.setText(currentUsername);
        }

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                firebaseUser = firebaseAuth.getCurrentUser();

                if (firebaseUser != null){

                }else{

                }
            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK){
            if (data != null){
                imageUri = data.getData();
                background_imageView.setImageURI(imageUri); //setting background image

            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case (R.id.savePost_btn):
                saveJournal();
                break;
            case (R.id.upload_dash_imageView):
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent , GALLERY_CODE);
                break;
        }
    }

    private void saveJournal() {
        progressBar_dash.setVisibility(View.VISIBLE);

        final String title = title_et.getText().toString().trim();
        final String thought = desc_et.getText().toString().trim();

        if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(thought) && imageUri != null){

            final StorageReference filepath = storageReference.child("journal_images")
                    .child("my_image_" + Timestamp.now().getSeconds());

            filepath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                    filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            String imageUrl = uri.toString();
                            Journal journal = new Journal();

                            journal.setTitle(title);
                            journal.setThought(thought);
                            journal.setImageURL(imageUrl);
                            journal.setTimeAdded(new Timestamp(new Date()));
                            journal.setUserId(currentUserId);
                            journal.setUsername(currentUsername);

                            collectionReference.add(journal).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Toast.makeText(DashboardActivity.this, "Success!", Toast.LENGTH_SHORT).show();
                                    progressBar_dash.setVisibility(View.INVISIBLE);
                                    startActivity(new Intent(DashboardActivity.this,JournalListActivity.class));
                                    finish();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(DashboardActivity.this, "Failure!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBar_dash.setVisibility(View.INVISIBLE);
                }
            });
        }
        else{
            progressBar_dash.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (firebaseAuth != null){
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}