package com.example.lahiru.trackapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.style.UpdateAppearance;
import android.text.style.UpdateLayout;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SignupActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText mEmailField;
    private EditText mPwdField;
    private EditText mTpField;
    private EditText mRePwdField;
    private ProgressBar mProgressBar;
    private Button mSubmit;
    private DatabaseReference mUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        mAuth=FirebaseAuth.getInstance();
        mEmailField=(EditText)findViewById(R.id.Email);
        mPwdField=(EditText)findViewById(R.id.Pwd);
        mRePwdField=(EditText)findViewById(R.id.RePwd);
        mTpField=(EditText)findViewById(R.id.telephone);
        mProgressBar=(ProgressBar)findViewById(R.id.progressBar);
        mSubmit=(Button)findViewById(R.id.submit_btn);
        mUser=FirebaseDatabase.getInstance().getReference("Vehicle");

        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email=mEmailField.getText().toString().trim();
                String pwd=mPwdField.getText().toString().trim();
                String repwd=mRePwdField.getText().toString().trim();
                String tp=mTpField.getText().toString().trim();

                if(TextUtils.isEmpty(email))
                {
                    mEmailField.setError("Invalid Email Address");
                }
                if(TextUtils.isEmpty(pwd))
                {
                    mPwdField.setError("Invalid Password");
                }
                if(TextUtils.isEmpty(repwd))
                {
                    mRePwdField.setError("Invalid Password");
                }
                if(TextUtils.isEmpty(tp))
                {
                    mTpField.setError("Invalid Telephone");
                }
                if(!pwd.equals(repwd))
                {
                    mRePwdField.setError("Password does not match");
                }

                try{
                    if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pwd) && !TextUtils.isEmpty(repwd) && !TextUtils.isEmpty(tp) && pwd.equals(repwd)){
                        mProgressBar.setVisibility(View.VISIBLE);
                        mAuth.createUserWithEmailAndPassword(email,pwd).addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Toast.makeText(SignupActivity.this,"createUserWithEmail:onComplete:"+task.isSuccessful(),Toast.LENGTH_SHORT).show();
                                mProgressBar.setVisibility(View.GONE);

                                if(!task.isSuccessful()){
                                    Toast.makeText(SignupActivity.this,"Authentication Failed."+task.getException(),Toast.LENGTH_LONG).show();
                                }
                                else{
                                    String hashEmail=convertToSha(email);
                                    startActivity(new Intent(SignupActivity.this,LoginActivity.class));
                                    finish();
                                }

                            }
                        });
                    }

                }catch (Exception e)
                {

                }




                    /*final Snackbar mySnackbar=Snackbar.make(findViewById(R.id.signupAc),"Fields can not be Empty..!",Snackbar.LENGTH_SHORT);
                    mySnackbar.setAction("Dismiss", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                mySnackbar.dismiss();
                        }
                    });
                    mySnackbar.show();*/

            }
        });


    }



    @Override
    protected void onResume() {
        super.onResume();
        mProgressBar.setVisibility(View.GONE);
    }
    private String convertToSha(String email){
        MessageDigest messageDigest=null;
        try {
            messageDigest=MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        messageDigest.update(email.getBytes(),0,email.length());

        String Email=new BigInteger(1,messageDigest.digest()).toString(16);
        return Email;


    }
}
