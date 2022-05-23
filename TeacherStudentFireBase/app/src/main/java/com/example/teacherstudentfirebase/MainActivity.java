package com.example.teacherstudentfirebase;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;
    private FirebaseAuth firebaseAuth;
    private ProgressBar progressBar;
    private TextView forgotPass;
    private final String AdminEmail = "admin@gmail.com";
    private final String AdminPassword = "admin123";
    public static final String MY_TAG = "MyTag";
    boolean passwordVisible = false;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        email = findViewById(R.id.roll);
        password = findViewById(R.id.pass);
        progressBar = findViewById(R.id.progressBarMain);
        forgotPass = findViewById(R.id.forgotPassword);
        firebaseAuth = FirebaseAuth.getInstance();

        Button loginbtn = (Button) findViewById(R.id.button2);
        TextView Registerbtn = (TextView) findViewById(R.id.UserSignUp);

        password.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                final int Right = 2;
                if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                    if(motionEvent.getRawX() >= password.getRight()-password.getCompoundDrawables()[Right].getBounds().width()){
                        int  selection = password.getSelectionEnd();
                        if(passwordVisible){
                            password.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.ic_baseline_visibility_on,0);
                            password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            passwordVisible = false;
                        }
                        else{
                            password.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.ic_baseline_visibility_off,0);
                            password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            passwordVisible = true;
                        }
                        password.setSelection(selection);
                        return true;
                    }
                }
                return false;
            }
        });

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputEmail = email.getText().toString().trim();
                String inputPassword = password.getText().toString().trim();

                if(ValidateUser(inputEmail, inputPassword)){
                    progressBar.setVisibility(View.VISIBLE);
                    InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    mgr.hideSoftInputFromWindow(loginbtn.getWindowToken(), 0);

                    firebaseAuth.signInWithEmailAndPassword(inputEmail, inputPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                progressBar.setVisibility(View.INVISIBLE);
                                if(inputEmail.equals(AdminEmail) && inputPassword.equals(AdminPassword)){
                                    Toast.makeText(MainActivity.this, "ADMIN LOGIN SUCCESSFUL", Toast.LENGTH_SHORT).show();
                                    openActivity(Admin.class);
                                }else {
                                    Toast.makeText(MainActivity.this, "User Login Successful", Toast.LENGTH_SHORT).show();
                                    openActivity(User.class);
                                }
                            }
                            else{
                                Toast.makeText(MainActivity.this,"LOGIN FAILED, Enter Valid Email or Password!",Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.INVISIBLE);
                            }
                        }
                    });
                }
            }
        });

        Registerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openActivity(Registration.class);
            }
        });

        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final EditText resetMail = new EditText(view.getContext());
                final AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(view.getContext());
                passwordResetDialog.setTitle("Reset Password?");
                passwordResetDialog.setMessage("Enter your Email to Receive Reset Link");
                passwordResetDialog.setView(resetMail);

                passwordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String mail = resetMail.getText().toString().trim();
                        if(TextUtils.isEmpty(mail)){
                            Toast.makeText(MainActivity.this, "Enter an Email", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        progressBar.setVisibility(View.VISIBLE);
                        firebaseAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(MainActivity.this, "Reset Link sent to your Email", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.INVISIBLE);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this, "Error! Reset Link is Not Sent\n" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.INVISIBLE);
                            }
                        });
                    }
                });

                passwordResetDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //close the dialog Box
                    }
                });

                passwordResetDialog.create().show();
            }
        });

    }

    public Boolean ValidateUser(String inputEmail, String inputPassword){
        if(TextUtils.isEmpty(inputEmail)){
            email.setError("Email is required");
            return false;
        }
        if(TextUtils.isEmpty(inputPassword)){
            password.setError("Password is required");
            return false;
        }
        if(password.length() < 6){
            password.setError("Password must be >= 6 Characters");
            return false;
        }
        return true;
    }

    public void openActivity(Class activity) {
        Intent intent = new Intent(this, activity);
        startActivity(intent);
    }

}
