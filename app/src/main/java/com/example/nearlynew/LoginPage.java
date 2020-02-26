package com.example.nearlynew;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginPage extends AppCompatActivity {

    private Spinner spinner;
    private EditText editTextPhoneNumber;

    private SignInButton googleSignInBtn;
    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;
    FirebaseDatabase database;
    String personName = "", personEmail = "";
    String userId = "";

    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
        googleSignInBtn=findViewById(R.id.sign_in_button);
        mAuth = FirebaseAuth.getInstance();
        database =  FirebaseDatabase.getInstance();

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);



        googleSignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        } );

        spinner = findViewById(R.id.spinnerCountries);
        spinner.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item, CountryData.countryNames));
        editTextPhoneNumber = findViewById(R.id.number);
        findViewById(R.id.loginBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = CountryData.countryAreaCodes[spinner.getSelectedItemPosition()];
                String number = editTextPhoneNumber.getText().toString().trim();
                if (number.isEmpty() || number.length() < 10){
                    editTextPhoneNumber.setError("Number is required");
                    editTextPhoneNumber.requestFocus();
                    return;
                }
                String phoneNumber = "+" + code + number;
                Intent intent = new Intent(LoginPage.this,VerifyPhoneActivity.class);
                intent.putExtra("phoneNumber",phoneNumber);
                startActivity(intent);
                finish();

            }
        });


    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!=null)
        {
            updateUI(currentUser);
        }
    }


    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            if(user != null){
                                personName = user.getDisplayName();
                                personEmail = user.getEmail();

                            }
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            updateUI(null);
                        }

                        // ...
                    }
                });
    }

    private void updateUI(FirebaseUser fUser) {

        RadioButton r1,r2;
        r1=findViewById(R.id.seller_radio_btn);
        r2=findViewById(R.id.buyer_radio_btn);

        if(r1.isChecked()) {
            Log.d("r1 checked","r1 checked");
            FirebaseUser user =  mAuth.getCurrentUser();
            if(user != null){
                userId = fUser.getUid();
            }
            DatabaseReference mRef =  database.getReference().child("users").child("seller").child(userId);
            mRef.child("name").setValue(personName);
            mRef.child("email").setValue(personEmail);
            startActivity(new Intent(this, SellerHome.class));
        }
        else if (r2.isChecked()){
            FirebaseUser user =  mAuth.getCurrentUser();
            if(user != null){
                userId = fUser.getUid();
            }
            DatabaseReference mRef =  database.getReference().child("users").child("buyer").child(userId);
            mRef.child("name").setValue(personName);
            mRef.child("email").setValue(personEmail);
            startActivity(new Intent(this, BuyerHome.class));
        }
    }


    public void register(View view) {
        startActivity(new Intent(this, Register.class));

    }
}
