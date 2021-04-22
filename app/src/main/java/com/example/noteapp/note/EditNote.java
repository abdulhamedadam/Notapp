package com.example.noteapp.note;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.example.noteapp.MainActivity;
import com.example.noteapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import static android.widget.Toast.makeText;

public class EditNote extends AppCompatActivity {

    Intent data;
    EditText editeNotetitle,editNotecontent;
    FirebaseFirestore fstore;
    ProgressBar progressBaredite;
    FirebaseUser user;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        progressBaredite=findViewById(R.id.progressBar);


        fstore=fstore.getInstance();
        user=FirebaseAuth.getInstance().getCurrentUser();

        data=getIntent();

        editeNotetitle=findViewById(R.id.editnoteTitle);
        editNotecontent=findViewById(R.id.editNoteContent);


        String noteTitle=data.getStringExtra("title");
        String noteContent=data.getStringExtra("content");

        editeNotetitle.setText(noteTitle);
        editNotecontent.setText(noteContent);

        FloatingActionButton fab = findViewById(R.id.saveeditenote);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nTitle=editeNotetitle.getText().toString();
                String nContent=editNotecontent.getText().toString();
                if(nTitle.isEmpty() || nContent.isEmpty()){
                    makeText(EditNote.this, "Can not Save Note With Empty Field ", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBaredite.setVisibility(View.VISIBLE);

                DocumentReference docref=fstore.collection("notes").document(user.getUid()).collection("mynotes").document(data.getStringExtra("noteId"));
                Map<String, Object> note = new HashMap<>();
                note.put("title", nTitle);
                note.put("content", nContent);
                docref.update(note).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        makeText(EditNote.this, "Note Edited", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));

                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                makeText(EditNote.this, "Error Try again", Toast.LENGTH_SHORT).show();;
                                progressBaredite.setVisibility(View.VISIBLE);

                            }
                        });

            }
        });




    }
}