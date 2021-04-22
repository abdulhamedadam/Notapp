package com.example.noteapp.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.noteapp.MainActivity;
import com.example.noteapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class Login extends AppCompatActivity {
    EditText lemail,lpassword;
    Button loginnow;
    TextView forgetpassword,createaccount;
    ProgressBar progressBar;
    FirebaseAuth fAuth;
    FirebaseFirestore fstore;
    FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Login to Note App");
        lemail=findViewById(R.id.email);
        lpassword=findViewById(R.id.password);
        loginnow=findViewById(R.id.nlogin);
        forgetpassword=findViewById(R.id.forgetpassword);
        createaccount=findViewById(R.id.creatnewaccount);
        progressBar=findViewById(R.id.progressBar);
        fAuth=FirebaseAuth.getInstance();
        fstore=FirebaseFirestore.getInstance();


          showwarrning();

        loginnow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nemail=lemail.getText().toString();
                String npassword=lpassword.getText().toString();
                if (nemail.isEmpty() || npassword.isEmpty()){
                    Toast.makeText(Login.this, "Fields Are Required ", Toast.LENGTH_SHORT).show();
                    return;
                }

                //delete note frist

                progressBar.setVisibility(View.VISIBLE);

                if (fAuth.getCurrentUser().isAnonymous()){
                    user=fAuth.getCurrentUser();

                    fstore.collection("notes").document(user.getUid()).delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(Login.this, "All Temp Notes Are Deleted", Toast.LENGTH_SHORT).show();
                                }
                            });
                    //delet temp user
                    user.delete().addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Login.this, "Temp user Deleted.", Toast.LENGTH_SHORT).show();
                        }
                    });

                }

                fAuth.createUserWithEmailAndPassword(nemail,npassword).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Toast.makeText(Login.this, "Success", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Login.this, "Login failed"+e.getMessage(), Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }
        });


    }

    private void showwarrning() {

        AlertDialog.Builder warning=new AlertDialog.Builder(this)
                .setTitle("Are you Sure ?")
                .setMessage("Linking Existing Account will Delete All The Temp Notes.Create New Account To Save them")
                .setPositiveButton("Save Note", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(new Intent(getApplicationContext(), Register.class));
                        finish();
                    }
                }).setNegativeButton("It’s Ok ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {



                    }
                });

        warning.show();
    }
}