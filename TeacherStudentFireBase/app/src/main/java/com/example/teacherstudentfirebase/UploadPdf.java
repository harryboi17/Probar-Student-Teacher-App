package com.example.teacherstudentfirebase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class UploadPdf extends AppCompatActivity {

    EditText testId;
    Button upload, selectPdf;
    EditText textView;
    ProgressBar progressBar;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore fStore;
    StorageReference storageReference;
    Map<String, Object> My_pdf = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_pdf);

        testId = findViewById(R.id.testIDUP);
        upload = findViewById(R.id.upload);
        selectPdf = findViewById(R.id.selectPDF);
        textView = findViewById(R.id.textViewUP);
        progressBar = findViewById(R.id.progressBarUP);

        storageReference = FirebaseStorage.getInstance().getReference();
        fStore = FirebaseFirestore.getInstance();

        upload.setEnabled(false);
        upload.setVisibility(View.INVISIBLE);

        selectPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                selectPDF();
            }
        });
    }

    private void selectPDF(){
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "PDF FILE SELECT"), 12);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        progressBar.setVisibility(View.INVISIBLE);

        if(requestCode == 12 && resultCode == RESULT_OK && data != null && data.getData() != null){
            upload.setEnabled(true);
            upload.setVisibility(View.VISIBLE);
            textView.setText(data.getDataString()
                    .substring(data.getDataString().lastIndexOf("/") + 1));

            upload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(TextUtils.isEmpty(testId.getText().toString())){
                        testId.setError("Test Id is required");
                        return;
                    }
                    if(testId.getText().toString().length() < 3){
                        testId.setError("Test Id should be >= 3 Character");
                        return;
                    }
                    uploadPDFFileFireBase(data.getData());
                }
            });
        }
    }

    private void uploadPDFFileFireBase(Uri data) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("File is loading...");
        progressDialog.show();

        StorageReference reference = storageReference.child("upload"+System.currentTimeMillis()+" .pdf");
        reference.putFile(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while(!uriTask.isComplete());
                Uri uri = uriTask.getResult();

                firebaseAuth = FirebaseAuth.getInstance(); //necessary when running through a function
                fStore = FirebaseFirestore.getInstance();

                String Uid = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
                String test_ID = testId.getText().toString();

                DocumentReference documentReferenceUser =  fStore.collection("users").document(Uid);
                documentReferenceUser.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();
                            Map<String, Object> MyData = document.getData();
                            assert MyData != null;
                            String user_Name = (String) MyData.get("fName");

                            DocumentReference documentReference =  fStore.collection("StudentPDFs").document(test_ID);

                            Map<String, String> UserPdfDetails = new HashMap<>();
                            UserPdfDetails.put("Student name", user_Name);
                            UserPdfDetails.put("PDF name", textView.getText().toString());
                            UserPdfDetails.put("URL", uri.toString());

                            My_pdf.put(Uid, UserPdfDetails);
                            documentReference.set(My_pdf, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(UploadPdf.this, "File Uploaded", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(UploadPdf.this, "Failed to Upload File", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                }
                            });
                        }
                        else{
                            Toast.makeText(UploadPdf.this, "Failed to fetch Data", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double progress = (100 * snapshot.getBytesTransferred())/snapshot.getTotalByteCount();
                progressDialog.setMessage("File Uploading..." + (snapshot.getBytesTransferred()/1024) + "/" + (snapshot.getTotalByteCount())/1024 +"Kb");
            }
        });
    }


}