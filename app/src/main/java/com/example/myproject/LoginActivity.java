package com.example.myproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApiNotAvailableException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

public class LoginActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    TextView Signup;
    Button Login;
    EditText Email,Password;
    String EmailStr,PasswordStr;

    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Signup=findViewById(R.id.Signup1);
        Login=findViewById(R.id.Login);
        Email=findViewById(R.id.Email);
        Password=findViewById(R.id.Password);

        firebaseAuth=FirebaseAuth.getInstance();
        progressDialog=new ProgressDialog(this);
        progressDialog.setCancelable(false);


        sharedPreferences=getSharedPreferences("Main", Context.MODE_PRIVATE);
        editor=sharedPreferences.edit();

        Signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,SignUpActivity.class));
            }
        });

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loginMethod();
            }
        });
    }

    private void loginMethod() {

        progressDialog.show();
        progressDialog.setMessage("Loading.....");

        EmailStr=Email.getText().toString();
        PasswordStr=Password.getText().toString();

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
        }
        else
        {
            firebaseAuth.signInWithEmailAndPassword(EmailStr,PasswordStr).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    progressDialog.dismiss();
                    if(!task.isSuccessful())
                    {

                        if (task.getException() instanceof FirebaseAuthInvalidUserException)
                        {
                            Toast.makeText(LoginActivity.this, "User with this email Doestn't exist.", Toast.LENGTH_SHORT).show();
                        }


                    }

                    else
                    {
                        editor.putString("Email",EmailStr);
                        editor.putInt("Key",1);
                        editor.commit();
                        editor.apply();

                        Intent i = new Intent(LoginActivity.this, HomeActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                        // Add new Flag to start new Activity
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);

                        finish();

                        Toast.makeText(getApplicationContext(),"Login Success",Toast.LENGTH_SHORT).show();

                    }



                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(),""+e.getMessage(),Toast.LENGTH_SHORT).show();


                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(sharedPreferences.getInt("Key",0)==1)
        {
            Intent i = new Intent(LoginActivity.this, HomeActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);

            finish();
        }
    }
}
