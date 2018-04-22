package com.example.lahiru.trackapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginActivity extends AppCompatActivity {
    private EditText mEmailField;
    private EditText mPasswordField;
    private Button mLogin;
    private TextView mSign;
    private String hashEmail = null;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        //hashEmail=getIntent().getExtras().getString("hashEmail");
        mEmailField = (EditText) findViewById(R.id.email_field);
        mPasswordField = (EditText) findViewById(R.id.pass_field);
        mSign = (TextView) findViewById(R.id.signuptext);
        mSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
                finish();
            }
        });
        mLogin = (Button) findViewById(R.id.login_btn);


        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSignIn();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    private void startSignIn() {
        final String email = mEmailField.getText().toString();
        String pwd = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(pwd)) {
            //Toast.makeText(LoginActivity.this, "SFields Are Empty..!!", Toast.LENGTH_SHORT).show();
            final Snackbar mySnackbar = Snackbar.make(findViewById(R.id.loginAc), "Fields Are Empty..!!", Snackbar.LENGTH_INDEFINITE);
            mySnackbar.setAction("Dismiss", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mySnackbar.dismiss();

                }
            });
            mySnackbar.show();
        } else {
            mAuth.signInWithEmailAndPassword(email, pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (!task.isSuccessful()) {
                        // Toast.makeText(LoginActivity.this, "Sign In Problem..!!", Toast.LENGTH_SHORT).show();
                        final Snackbar mySnackbar = Snackbar.make(findViewById(R.id.loginAc), "Sign In Problem..!!", Snackbar.LENGTH_INDEFINITE);
                        mySnackbar.setAction("Dismiss", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mySnackbar.dismiss();

                            }
                        });
                        mySnackbar.show();
                    } else {
                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        hashEmail = convertToSha(email);
                        intent.putExtra("hashEmail", hashEmail);
                        startActivity(intent);
                        overridePendingTransition(R.xml.fadein, R.xml.fadeout);
                    }
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.login_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.signup:
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
                return true;
            case R.id.about:
                Toast.makeText(getApplicationContext(), "Welcome", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.exit:
                System.exit(1);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private String convertToSha(String email) {
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        messageDigest.update(email.getBytes(), 0, email.length());

        String Email = new BigInteger(1, messageDigest.digest()).toString(16);
        return Email;
    }
}
