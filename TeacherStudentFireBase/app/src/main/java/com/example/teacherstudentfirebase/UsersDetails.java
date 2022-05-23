package com.example.teacherstudentfirebase;

import static com.example.teacherstudentfirebase.MainActivity.MY_TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class UsersDetails extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    FirebaseFirestore fStore;
    ListView listView;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_details);
        firebaseAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        listView = findViewById(R.id.user_detail_listview);
        progressBar = findViewById(R.id.progressBarUD);

        fStore.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    //below statement also apply to this arraylist
                    ArrayList<String> arrayList = new ArrayList<>();
                    for(QueryDocumentSnapshot document : task.getResult()){
                        Map<String, Object> MyDATA;
                        MyDATA = document.getData();
                        String userData = "Name : " + MyDATA.get("fName") + "\nEmail : " + MyDATA.get("fEmail");
                        Log.d(MY_TAG, userData);
                        arrayList.add(userData);
                    }
                    //Note due to asynchronous behaviour, we cant make this adaptor and listview outside onComplete method, else we will get no result
                    ListAdaptor arrayAdapter = new ListAdaptor(arrayList, UsersDetails.this);
                    listView.setAdapter(arrayAdapter);
                }
                else {
                    Log.d(MY_TAG, "get failed with ", task.getException());
                    Toast.makeText(UsersDetails.this, "Failed to fetch Data", Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.INVISIBLE);
            }
        });

//        int a = arrayList.size();
//        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
//        String s = String.valueOf(a);
    }
}