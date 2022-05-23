package com.example.teacherstudentfirebase;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.Objects;

public class User extends AppCompatActivity {
    
    Button uploadPdf, viewTest, logOut;
    TextView name, email;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore fStore;
    String UserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        uploadPdf = findViewById(R.id.upload);
        viewTest = findViewById(R.id.view);
        logOut = findViewById(R.id.Logout);
        name = findViewById(R.id.nameUser);
        email = findViewById(R.id.emailUser);
        firebaseAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        UserID = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();

        DocumentReference documentReference = fStore.collection("users").document(UserID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error == null) {
                    name.setText(Objects.requireNonNull(value).getString("fName"));
                    email.setText(value.getString("fEmail"));
                }
            }
        });

        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                Toast.makeText(User.this, "Signed Out", Toast.LENGTH_SHORT).show();
                openActivity(MainActivity.class);
            }
        });

        viewTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openActivity(ViewTestUser.class);
            }
        });

        uploadPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openActivity(UploadPdf.class);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser != null){
            //someone is logged in
        }else{
            openActivity(MainActivity.class);
            finish();
        }
    }

    public void openActivity(Class activity) {
        Intent intent = new Intent(this, activity);
        startActivity(intent);
    }
}