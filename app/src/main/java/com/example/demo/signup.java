package com.example.demo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class signup extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    public static final String TAG = "TAG";

    EditText mName,mEmail,mPassword;
    Button mSignUpButton;
    Spinner mYear,mBranch;


    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;

    String userID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

         mYear = findViewById(R.id.sp1);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.cls,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mYear.setAdapter(adapter);
        mYear.setOnItemSelectedListener(this);

        mBranch = findViewById(R.id.sp2);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,R.array.branch,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mBranch.setAdapter(adapter2);
        mBranch.setOnItemSelectedListener(this);

        mName=findViewById(R.id.signUp_name);
        mEmail=findViewById(R.id.signUp_email);
        mPassword=findViewById(R.id.signUp_password);

        mSignUpButton=findViewById(R.id.signUp_button);

        TextView tvLogin = findViewById(R.id.tvLogin);
        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signup.this.finish();
            }
        });
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        // When user clicks on Sign Up button this method gets call
        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = mEmail.getText().toString();
                final String name = mName.getText().toString();

                String password = mPassword.getText().toString();
                final String year = mYear.getSelectedItem().toString();
                final String branch = mBranch.getSelectedItem().toString();

                // Checks whether name input is empty or not
                if(TextUtils.isEmpty(name))    {
                    mName.setError("Name is required");
                    return;
                }
                // Checks whether email input is empty or not
                if(TextUtils.isEmpty(email))    {
                    mEmail.setError("Email is required");
                    return;
                }

                // Checks whether password input is empty or not
                if(TextUtils.isEmpty(password))    {
                    mPassword.setError("Password is required");
                    return;
                }



                // Checks whether password input length is long enough or not
                if(password.length() < 6)    {
                    mPassword.setError("Password Must be >= 6 Characters");
                    return;
                }



                firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        // Checks whether task is successful or not
                        if(task.isSuccessful()) {
                            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                            // Sends verification email to user
                            firebaseUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(signup.this, "Verification Email Has been Sent.", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "onFailure: Email not sent " + e.getMessage());
                                }
                            });

                            userID = firebaseUser.getUid();
                            DocumentReference documentReference = firebaseFirestore.collection("Users").document(userID);
                            Map<String,Object> user = new HashMap<>();
                            user.put("Name",name);
                            user.put("Email",email);
                            user.put("Year",year);
                            user.put("Branch",branch);

                            // Stores user information in database using user Unique ID
                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "onSuccess: user Profile is created for "+ userID);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "onFailure: " + e.toString());
                                }
                            });

                            // Opens main page after signup process is completed
                            startActivity(new Intent(getApplicationContext(),loginActivity.class));
                        }
                        else {
                            Toast.makeText(signup.this, "Error ! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Failure : "+e.toString());
                    }
                });
            }
        });

              //      }
            //    });


          //  }
        //});
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String text = adapterView.getItemAtPosition(i).toString();
        //Toast.makeText(adapterView.getContext(),text,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}