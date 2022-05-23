package com.example.teacherstudentfirebase;

import static com.example.teacherstudentfirebase.MainActivity.MY_TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class ViewTestUser extends AppCompatActivity {

    ListView queView;
    EditText TestId, QueNo;
    Button viewQA;
    ProgressBar progressBar;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_test_user);

        firebaseAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        queView = findViewById(R.id.ListViewVQU);
        TestId = findViewById(R.id.testIDVQU);
        QueNo = findViewById(R.id.QueNoVQU);
        viewQA = findViewById(R.id.viewVQU);
        progressBar = findViewById(R.id.progressBarVTU);

        TextView emptyQ = findViewById(R.id.EmptyTVVQU);
        queView.setEmptyView(emptyQ);

        viewQA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String testID = TestId.getText().toString();
                String QNumber = QueNo.getText().toString();
                if(TextUtils.isEmpty(testID)){
                    TestId.setError("Test ID is required ");
                    return;
                }
                if(testID.length() < 3){
                    TestId.setError("Test ID should be >= 3 Characters");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                DocumentReference documentReference = fStore.collection("tests").document(testID);
                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Map<String, Object> MyDATA;
                                MyDATA = document.getData();

                                if(TextUtils.isEmpty(QNumber)) {
                                    ArrayList<String> arrayListQ = new ArrayList<>();

                                    assert MyDATA != null;
                                    for (Map.Entry<String, Object> entry : MyDATA.entrySet()) {
                                        String my_que = entry.getKey() + "\n";
                                        Map<String, String> QA = (Map<String, String>) entry.getValue();
                                        my_que += QA.get("Question");
                                        arrayListQ.add(my_que);
                                    }

                                    ListAdaptor arrayAdapterQ = new ListAdaptor(arrayListQ, ViewTestUser.this);
                                    queView.setAdapter(arrayAdapterQ);
                                }
                                else{
                                    ArrayList<String> arrayListQ = new ArrayList<>();

                                    assert MyDATA != null;
                                    boolean flag = false;
                                    for (Map.Entry<String, Object> entry : MyDATA.entrySet()) {
                                        if(KeyCheck(QNumber, entry.getKey())) {
                                            String my_que = entry.getKey() + "\n";
                                            Map<String, String> QA = (Map<String, String>) entry.getValue();
                                            my_que += QA.get("Question");
                                            arrayListQ.add(my_que);
                                            flag = true;
                                            break;
                                        }
                                    }
                                    if(!flag) {
                                        Toast.makeText(ViewTestUser.this, "Question Number doesn't exist", Toast.LENGTH_SHORT).show();
                                        return;
                                    }

                                    ListAdaptor arrayAdapterQ = new ListAdaptor(arrayListQ, ViewTestUser.this);
                                    queView.setAdapter(arrayAdapterQ);
                                }
                            }
                            else {
                                Toast.makeText(ViewTestUser.this, "TEST ID : " + testID + " Doesn't Exist", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.d(MY_TAG, "get failed with ", task.getException());
                            Toast.makeText(ViewTestUser.this, "Failed to fetch Data", Toast.LENGTH_SHORT).show();
                        }
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });
            }
        });
    }

    boolean KeyCheck(String QNumber, String key){
        String s = key.substring(9);
        return s.equals(QNumber);
    }
}