package com.example.myjournal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import Adapters.JournalListAdapterClass;
import Utils.Journal;
import Utils.JournalAPI;

public class JournalListActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private FirebaseAuth.AuthStateListener authStateListener;

    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private StorageReference storageReference;
    private CollectionReference collectionReference = firestore.collection("Journal");

    private RecyclerView recyclerView;
    private JournalListAdapterClass adapterClass;
    private List<Journal> journalList;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        progressBar = findViewById(R.id.progressbar_list);

        progressBar.setVisibility(View.VISIBLE);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        journalList = new ArrayList<>();

        recyclerView = findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.menu , menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case (R.id.add_thought):

                Log.e("Click", "onOptionsItemSelected: " + "Current user : " + currentUser );
                Log.e("Click", "onOptionsItemSelected: " + "firebaseauth : " + firebaseAuth );

                if (currentUser != null && firebaseAuth != null){
                    startActivity(new Intent(JournalListActivity.this , DashboardActivity.class));
                    this.finish();
                }
                break;
            case (R.id.sign_out):
                if (currentUser != null && firebaseAuth != null){
                    firebaseAuth.signOut();

                    startActivity(new Intent(JournalListActivity.this , GetStartedActivity.class));
                    this.finish();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();

        collectionReference.whereEqualTo("userId" , JournalAPI.getInstance().getUserId()).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        progressBar.setVisibility(View.INVISIBLE);

                        if (! queryDocumentSnapshots.isEmpty()){
                            for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots){
                                Journal journal = snapshot.toObject(Journal.class);
                                journalList.add(journal);
                            }

                            adapterClass = new JournalListAdapterClass(JournalListActivity.this , journalList);
                            recyclerView.setAdapter(adapterClass);
                            adapterClass.notifyDataSetChanged();
                        }
                        else {
                            Toast.makeText(JournalListActivity.this, "Could not fetch documents!", Toast.LENGTH_SHORT).show();
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.INVISIBLE);
                Log.e("JournalListActivity", "onFailure: " + e.toString() );
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}