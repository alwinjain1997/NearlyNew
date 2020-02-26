package com.example.nearlynew;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class SellerHome extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_home);
    }
    public void signOut(View view) {
        FirebaseAuth.getInstance().signOut();

        Toast.makeText(this, "LogedOut", Toast.LENGTH_SHORT).show();


    }
}
