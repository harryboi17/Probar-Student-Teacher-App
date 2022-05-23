package com.example.teacherstudentfirebase;

import static com.example.teacherstudentfirebase.MainActivity.MY_TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class GetPdf extends AppCompatActivity {

    ListView listView;
    EditText testID;
    Button getPdfs;
    ProgressBar progressBar;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore fStore;
    List<PdfDetails> PdfsList;
    ArrayList<String> uploads;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_pdf);

        listView = findViewById(R.id.listviewVP);
        testID = findViewById(R.id.testIDVP);
        getPdfs = findViewById(R.id.buttonVP);
        progressBar = findViewById(R.id.progressBarVP);
        PdfsList = new ArrayList<>();

        getPdfs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            progressBar.setVisibility(View.VISIBLE);
            viewALlFiles();

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    PdfDetails pdfDetails = PdfsList.get(i);

                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                    Uri fileuri =  Uri.parse(pdfDetails.getUrl()) ;
                    intent.setDataAndType(fileuri,"application/pdf");
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    Intent in = Intent.createChooser(intent,"open file");
                    in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(in);
                }
            });
            }
        });
    }

    private void viewALlFiles() {
        firebaseAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        String Test_ID = testID.getText().toString();

        DocumentReference documentReference = fStore.collection("StudentPDFs").document(Test_ID);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map<String, Object> MyDATA;
                        MyDATA = document.getData();

                        uploads = new ArrayList<>();

                        assert MyDATA != null;
                        for (Map.Entry<String, Object> entry : MyDATA.entrySet()) {
                            String Student_ID = entry.getKey();
                            Map<String, String> details = (Map<String, String>) entry.getValue();
                            PdfDetails pdfDetails = new PdfDetails(details.get("Student name") ,details.get("PDF name"), details.get("URL"));
                            PdfsList.add(pdfDetails);
                            uploads.add(pdfDetails.getStudentName() + "\n" + pdfDetails.getName());
                        }

                        ListAdaptor arrayAdapter = new ListAdaptor(uploads, GetPdf.this);
                        listView.setAdapter(arrayAdapter);
                    }
                    else {
                        Toast.makeText(GetPdf.this, "TEST ID : " + Test_ID + " Doesn't Exist", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.d(MY_TAG, "get failed with ", task.getException());
                    Toast.makeText(GetPdf.this, "Failed to fetch Data", Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }
}