package com.example.teacherstudentfirebase;

import static com.example.teacherstudentfirebase.MainActivity.MY_TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Registration extends AppCompatActivity {

    private EditText userName, userEmail, userPassword;
    private Button userButton;
    private TextView userLogin;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore fStore;
    private String UserID;
    ProgressBar progressBar;
    boolean passwordVisible = false;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        setupUIViews();

        firebaseAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        userButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = userName.getText().toString().trim();
                String password = userPassword.getText().toString().trim();
                String email = userEmail.getText().toString().trim();

                if(validate(name, password, email)){
                    progressBar.setVisibility(View.VISIBLE);
                    InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    mgr.hideSoftInputFromWindow(userButton.getWindowToken(), 0);

                    firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()) {
                                Toast.makeText(Registration.this, "Registration Successful!", Toast.LENGTH_SHORT).show();

                                UserID = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
                                DocumentReference documentReference = fStore.collection("users").document(UserID);
                                Map<String, Object> user = new HashMap<>();
                                user.put("fName", name);
                                user.put("fEmail", email);

                                documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Log.d(MY_TAG, "OnSuccess : user profile is created for " + UserID);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(MY_TAG, "OnFailure\n" + e + UserID);
                                    }
                                });

                                progressBar.setVisibility(View.INVISIBLE);
                                startActivity(new Intent(Registration.this, MainActivity.class));
                            }
                            else{
                                Toast.makeText(Registration.this, "Registration Failed, Provided Email might be Invalid", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.INVISIBLE);
                            }
                        }
                    });
                }
            }
        });

        userLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Registration.this, MainActivity.class));
            }
        });

        userPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                final int Right = 2;
                if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                    if(motionEvent.getRawX() >= userPassword.getRight()-userPassword.getCompoundDrawables()[Right].getBounds().width()){
                        int  selection = userPassword.getSelectionEnd();
                        if(passwordVisible){
                            userPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.ic_baseline_visibility_on,0);
                            userPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            passwordVisible = false;
                        }
                        else{
                            userPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.ic_baseline_visibility_off,0);
                            userPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            passwordVisible = true;
                        }
                        userPassword.setSelection(selection);
                        return true;
                    }
                }
                return false;
            }
        });
    }

    private void setupUIViews(){
        userName = findViewById(R.id.RegisterName);
        userEmail = findViewById(R.id.RegisterEmail);
        userPassword = findViewById(R.id.RegisterPassword);
        userLogin = findViewById(R.id.TVRegister);
        userButton = findViewById(R.id.RegisterButton);
        progressBar = findViewById(R.id.progressBarReg);
    }

    private Boolean validate(String name, String password, String email) {
        if(TextUtils.isEmpty(email)){
            userEmail.setError("Email is required");
            return false;
        }
        if(TextUtils.isEmpty(name)){
            userName.setError("Name is required");
            return false;
        }
        if(TextUtils.isEmpty(password)){
            userPassword.setError("Password is required");
            return false;
        }
        if(password.length() < 6){
            userPassword.setError("Password must be >= 6 Characters");
            return false;
        }

        return true;
    }
}