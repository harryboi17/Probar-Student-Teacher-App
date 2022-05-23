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

public class ViewTest extends AppCompatActivity {

    ListView queView, ansView;
    EditText TestId, QueNo;
    Button viewQA;
    ProgressBar progressBar;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_test);

        firebaseAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        queView = findViewById(R.id.ListView1);
        ansView = findViewById(R.id.ListView2);
        TestId = findViewById(R.id.testIDVT);
        QueNo = findViewById(R.id.QueNoVT);
        viewQA = findViewById(R.id.viewVT);
        progressBar = findViewById(R.id.progressBarVT);

        TextView emptyQ = findViewById(R.id.EmptyTVQ);
        TextView emptyA = findViewById(R.id.EmptyTVA);
        queView.setEmptyView(emptyQ);
        ansView.setEmptyView(emptyA);

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
                                    ArrayList<String> arrayListA = new ArrayList<>();

                                    assert MyDATA != null;
                                    for (Map.Entry<String, Object> entry : MyDATA.entrySet()) {
                                        String my_que = entry.getKey() + "\n";
                                        String my_ans = entry.getKey() + "\nAns : ";
                                        Map<String, String> QA = (Map<String, String>) entry.getValue();
                                        my_que += QA.get("Question");
                                        my_ans += QA.get("Answer");
                                        arrayListQ.add(my_que);
                                        arrayListA.add(my_ans);
                                    }

                                    ListAdaptor arrayAdapterQ = new ListAdaptor(arrayListQ, ViewTest.this);
                                    ListAdaptor arrayAdapterA = new ListAdaptor(arrayListA, ViewTest.this);
                                    queView.setAdapter(arrayAdapterQ);
                                    ansView.setAdapter(arrayAdapterA);
                                }
                                else{
                                    ArrayList<String> arrayListQ = new ArrayList<>();
                                    ArrayList<String> arrayListA = new ArrayList<>();

                                    assert MyDATA != null;
                                    boolean flag = false;
                                    for (Map.Entry<String, Object> entry : MyDATA.entrySet()) {
                                        if(KeyCheck(QNumber, entry.getKey())) {
                                            String my_que = entry.getKey() + "\n";
                                            String my_ans = entry.getKey() + "\nAns : ";
                                            Map<String, String> QA = (Map<String, String>) entry.getValue();
                                            my_que += QA.get("Question");
                                            my_ans += QA.get("Answer");
                                            arrayListQ.add(my_que);
                                            arrayListA.add(my_ans);
                                            flag = true;
                                            break;
                                        }
                                    }
                                    if(!flag) {
                                        Toast.makeText(ViewTest.this, "Question Number doesn't exist", Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.INVISIBLE);
                                        return;
                                    }

                                    ListAdaptor arrayAdapterQ = new ListAdaptor(arrayListQ, ViewTest.this);
                                    ListAdaptor arrayAdapterA = new ListAdaptor(arrayListA, ViewTest.this);
                                    queView.setAdapter(arrayAdapterQ);
                                    ansView.setAdapter(arrayAdapterA);
                                }
                            }
                            else {
                                Toast.makeText(ViewTest.this, "TEST ID : " + testID + " Doesn't Exist", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.d(MY_TAG, "get failed with ", task.getException());
                            Toast.makeText(ViewTest.this, "Failed to fetch Data", Toast.LENGTH_SHORT).show();
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