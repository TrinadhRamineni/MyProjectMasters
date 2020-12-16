package com.example.myproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Button Signup;
    EditText Name,Email,Password,ConfrimPassword;
    String NameStr,EmailStr,PasswordStr,ConfirmPassStr;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    ProgressDialog progressDialog;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Signup=findViewById(R.id.Signup);
        Name=findViewById(R.id.Name);
        Email=findViewById(R.id.Email);
        Password=findViewById(R.id.Password);
        ConfrimPassword=findViewById(R.id.CPassword);

        firebaseAuth=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();
        progressDialog=new ProgressDialog(this);
        progressDialog.setCancelable(false);


        sharedPreferences=getSharedPreferences("Main", Context.MODE_PRIVATE);
        editor=sharedPreferences.edit();
        
        Signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
                signUpMethod();
            }
        });

    }

    private void signUpMethod() {

        progressDialog.show();
        progressDialog.setMessage("Loading....");

        NameStr=Name.getText().toString();
        EmailStr=Email.getText().toString();
        PasswordStr=Password.getText().toString();
        ConfirmPassStr=ConfrimPassword.getText().toString();

        if(TextUtils.isEmpty(NameStr))
        {
            Name.setError("Enter Name");
            progressDialog.dismiss();
            return;
        }
        if(TextUtils.isEmpty(EmailStr))
        {
            Email.setError("Enter Email");
            progressDialog.dismiss();

            return;
        }

        if(TextUtils.isEmpty(PasswordStr))
        {
            Password.setError("Enter Password");
            progressDialog.dismiss();

            return;
        }

        if(PasswordStr.length()<6)
        {
            Toast.makeText(getApplicationContext(),"Minimum 6 Characters",Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();

            return;
        }

        if(TextUtils.isEmpty(ConfirmPassStr))
        {
            ConfrimPassword.setError("Re Enter the Password");
            progressDialog.dismiss();

            return;
        }

        if(!ConfirmPassStr.equals(PasswordStr))
        {
            Toast.makeText(getApplicationContext(),"Passwords are not matching",Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();

        }

        else
        {
            dataupLoad();
        }
    }

    private void dataupLoad() {

        firebaseAuth.createUserWithEmailAndPassword( EmailStr,PasswordStr).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.dismiss();

                if(!task.isSuccessful())
                {

                    if(task.getException() instanceof FirebaseAuthUserCollisionException)
                    {
                        Toast.makeText(getApplicationContext(),"User Email Alredy Exisit",Toast.LENGTH_SHORT).show();
                    }


                }

                else
                {
                    userData();
                }


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(),"Fail"+e,Toast.LENGTH_SHORT).show();


            }
        });
    }

    private void userData() {
        progressDialog.show();
        progressDialog.setMessage("Please Wait....");
        HashMap<String,String> hashMap=new HashMap<>();
        hashMap.put("Name",NameStr);
        hashMap.put("Email",EmailStr);

        firebaseFirestore.collection("Users").add(hashMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {

                if(task.isSuccessful())
                {

                    editor.putString("Email",EmailStr);
                    editor.putInt("Key",1);
                    editor.commit();
                    editor.apply();

                    Intent i = new Intent(SignUpActivity.this, HomeActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                    // Add new Flag to start new Activity
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);

                    finish();

                    Toast.makeText(getApplicationContext(),"Sign Up Success",Toast.LENGTH_SHORT).show();

                }
                else
                {
                    Toast.makeText(getApplicationContext(),""+task.getException().getMessage(),Toast.LENGTH_SHORT).show();

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),"Fail"+e,Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(sharedPreferences.getInt("Key",0)==1)
        {
            Intent i = new Intent(SignUpActivity.this, HomeActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);

            finish();
        }
    }
}
