package com.example.noteapp.note;

import android.os.Bundle;

import com.example.noteapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class AddNote extends AppCompatActivity {

    FirebaseFirestore fstore;
    EditText notecontent1,notetitle1;
    ProgressBar progressBarsave;
    FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fstore=FirebaseFirestore.getInstance();
        notetitle1=findViewById(R.id.addnoteTitle);
        notecontent1=findViewById(R.id.addnoteContent);
        progressBarsave=findViewById(R.id.progressBar);
        user= FirebaseAuth.getInstance().getCurrentUser();







        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               String nTitle=notetitle1.getText().toString();
               String nContent=notecontent1.getText().toString();
            if(nTitle.isEmpty() || nContent.isEmpty()){
                   Toast.makeText(AddNote.this, "Can not Save Note With Empty Field ", Toast.LENGTH_SHORT).show();
                    return;
               }

                progressBarsave.setVisibility(View.VISIBLE);


                Map<String, Object> note = new HashMap<>();
                note.put("title", nTitle);
                note.put("content", nContent);
                fstore.collection("notes").document(user.getUid()).collection("mynotes")
                        .add(note)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Toast.makeText(AddNote.this, "Note Added", Toast.LENGTH_SHORT).show();

                                onBackPressed();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(AddNote.this, "Error Try again", Toast.LENGTH_SHORT).show();;
                                progressBarsave.setVisibility(View.VISIBLE);

                            }
                        });

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.close_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==R.id.close){

            onBackPressed();
            Toast.makeText(this, "Not Saved", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }
}