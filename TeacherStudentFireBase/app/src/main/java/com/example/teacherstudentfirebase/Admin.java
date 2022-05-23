package com.example.teacherstudentfirebase;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class Admin extends AppCompatActivity {
    Button addTest, viewTest, viewPdf, userDetails;
    Button openFireBase;
    public static final int ADMIN_ID = 420;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        addTest = findViewById(R.id.addTest);
        viewPdf = findViewById(R.id.viewPdf);
        viewTest = findViewById(R.id.viewTest);
        userDetails = findViewById(R.id.userDetails);
        openFireBase = findViewById(R.id.openFireBase);

        addTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openActivity(AddTest.class);
            }
        });

        viewTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openActivity(ViewTest.class);
            }
        });

        userDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openActivity(UsersDetails.class);
            }
        });

        viewPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openActivity(GetPdf.class);
            }
        });

        openFireBase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("https://console.firebase.google.com/u/1/project/studentteacher-e6d83/authentication/users"); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

    }

    public void openActivity(Class activity) {
        Intent intent = new Intent(this, activity);
        startActivity(intent);
    }
}