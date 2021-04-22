package com.example.noteapp.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
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
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;

public class Register extends AppCompatActivity {
    Button syncAccount ;
    EditText rusername,ruserEmail,ruserpassword,ruserconfirmpass;
    TextView login;
    ProgressBar progressBar;
    FirebaseAuth fAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        getSupportActionBar().setTitle("Create New Account");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        rusername=findViewById(R.id.name);
        ruserEmail=findViewById(R.id.emailadrress);
        ruserpassword=findViewById(R.id.rpassword);
        ruserconfirmpass=findViewById(R.id.comfirmpassword);
        syncAccount=findViewById(R.id.login);
        login=findViewById(R.id.loginhere);
        progressBar=findViewById(R.id.progressBar);
        fAuth=FirebaseAuth.getInstance();




        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),Login.class));

            }
        });


        syncAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username=rusername.getText().toString();
                String useremail=ruserEmail.getText().toString();
                String userpass=ruserpassword.getText().toString();
                String userconpass=ruserconfirmpass.getText().toString();

                if (username.isEmpty() ||  useremail.isEmpty() || userpass.isEmpty() || userconpass.isEmpty()){

                    Toast.makeText(Register.this, "All Fields Are required.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!userpass.equals(userconpass)){
                    ruserconfirmpass.setError("Password Do not Match.");
                }

                progressBar.setVisibility(View.VISIBLE);

                AuthCredential credential= EmailAuthProvider.getCredential(useremail,userpass);
                fAuth.getCurrentUser().linkWithCredential(credential).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Toast.makeText(Register.this, "Notes are synced", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        overridePendingTransition(R.anim.slide_up,R.anim.slide_down);
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Register.this, "failed to connect ,try again.", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                });

            }
        });



    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        startActivity(new Intent(this, MainActivity.class));
        finish();
        return super.onOptionsItemSelected(item);
    }
}