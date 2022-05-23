package com.example.teacherstudentfirebase;

import static com.example.teacherstudentfirebase.MainActivity.MY_TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class AddTest extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    FirebaseFirestore fStore;
    EditText TestID;
    EditText QueNo;
    EditText question;
    EditText answer;
    Button submit;
    ProgressBar progressBar;

    Map<String, Object> test = new HashMap<>();

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_test);

        firebaseAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        question = findViewById(R.id.QuestionETAT);
        answer = findViewById(R.id.AnswerETAT);
        TestID = findViewById(R.id.testIDAT);
        submit = findViewById(R.id.submitAT);
        QueNo = findViewById(R.id.QueNoAT);
        progressBar = findViewById(R.id.progressBarAD);

        question.setOnTouchListener((v, event) -> {
            if (question.hasFocus()) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                switch (event.getAction() & MotionEvent.ACTION_MASK){
                    case MotionEvent.ACTION_UP:
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }
            }
            return false;
        });
        answer.setOnTouchListener((v, event) -> {
            if (question.hasFocus()) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                switch (event.getAction() & MotionEvent.ACTION_MASK){
                    case MotionEvent.ACTION_UP:
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }
            }
            return false;
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String TEST_ID = TestID.getText().toString();
                String ans = answer.getText().toString();
                String que = question.getText().toString();
                String queNo = QueNo.getText().toString();

                if(TextUtils.isEmpty(TEST_ID)){
                    TestID.setError("Test ID is required ");
                    return;
                }
                if(TextUtils.isEmpty(queNo)){
                    QueNo.setError("Que No is required ");
                    return;
                }
                if(TEST_ID.length() < 3){
                    TestID.setError("Test ID should be >= 3 Characters");
                    return;
                }
                if(TextUtils.isEmpty(que)){
                    question.setError("Question cannot be Empty");
                    return;
                }
                if(TextUtils.isEmpty(ans)){
                    answer.setError("Answer cannot be Empty");
                    return;
                }

                DocumentReference documentReference = fStore.collection("tests").document(TEST_ID);
                Map<String, String> MyQue = new HashMap<>();
                MyQue.put("Question", que);
                MyQue.put("Answer", ans);

                test.put("Question "+queNo, MyQue);

                progressBar.setVisibility(View.VISIBLE);
                documentReference.set(test, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(MY_TAG, "OnSuccess : Que added for " + TEST_ID + " Question No. " + queNo);
                        Toast.makeText(AddTest.this, "Test ID : " + TEST_ID + "\nQuestion " + queNo + " added", Toast.LENGTH_SHORT).show();
                        question.setText(null);
                        answer.setText(null);
                        QueNo.setText(null);
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(MY_TAG, "OnFailure\n" + e + TEST_ID + " " + queNo);
                        Toast.makeText(AddTest.this, "Failed to add data", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });
            }
        });


    }
}