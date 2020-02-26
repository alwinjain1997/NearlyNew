package com.example.nearlynew;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {

    FirebaseUser firebaseUser;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    EditText editTTextname;
    Button buttonContinue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        database =  FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        editTTextname = findViewById(R.id.editTextName);
        buttonContinue = findViewById(R.id.buttonNameContinue);
        final String uid = firebaseUser.getUid();
        final String phoneNumber = firebaseUser.getPhoneNumber();
        buttonContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editTTextname.getText().toString().trim().isEmpty()){
                    editTTextname.setError("This Field Cannot Be Empty");
                }else{
                    DatabaseReference mRef =  database.getReference().child("users").child(uid);
                    mRef.child("contact").setValue(phoneNumber);
                    String nameEntered = editTTextname.getText().toString().trim();
                    mRef.child("name").setValue(nameEntered);
                    Log.v("name entered",nameEntered);
                    startActivity(new Intent(Register.this,BuyerHome.class));
                    finish();
                }

            }
        });

    }
}
