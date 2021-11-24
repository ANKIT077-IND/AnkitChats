package com.codewithankit.ankitchats;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity {
  Button login;
  Toolbar logintoolbar;
  TextInputEditText email,password;
    private FirebaseAuth mAuth;
    TextView register_now,forgotpassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();

        logintoolbar=findViewById(R.id.logintoolbar);
        setSupportActionBar(logintoolbar);
        getSupportActionBar().setTitle("Login");


    login=findViewById(R.id.login);
    email=findViewById(R.id.email);
    password=findViewById(R.id.etpassword);
    register_now=findViewById(R.id.register_now);
    forgotpassword=findViewById(R.id.forgotpassword);
    login.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            String etemail=email.getText().toString().trim();
            String etpassword=password.getText().toString().trim();

            if (etemail.isEmpty()){
                Toasty.error(getApplicationContext(),"Please enter the Email",Toasty.LENGTH_LONG,true).show();
            }
            else if (etemail.indexOf('@') <= 0) {
                Toasty.error(getApplicationContext(), "@ invalid  position", Toasty.LENGTH_LONG, true).show();
            }
            else if (etemail.charAt(etemail.length() - 4) != '.' && etemail.charAt(etemail.length() - 3) != '.') {
                Toasty.error(getApplicationContext(), ". invalid position", Toasty.LENGTH_LONG, true).show();
            }
            else if (etpassword.isEmpty()){
                Toasty.error(getApplicationContext(),"Please enter the password",Toasty.LENGTH_LONG,true).show();
            }
            else if (etpassword.length()<5 && etpassword.length()<15){
                Toasty.error(getApplicationContext(),"Password must we length 5 to 15 characters",Toasty.LENGTH_LONG,true).show();
            }
            else {
                ProgressDialog dialog=new ProgressDialog(MainActivity.this);
                dialog.setCanceledOnTouchOutside(false);
                dialog.setMessage("Please wait...");
                dialog.show();
                mAuth.signInWithEmailAndPassword(etemail,etpassword)
                        .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    dialog.dismiss();
                                    email.setText("");
                                    password.setText("");
                                Toasty.success(getApplicationContext(),"Login Successfully",Toasty.LENGTH_LONG,true).show();
                                  Intent intent=new Intent(MainActivity.this,homepage.class);
                                  startActivity(intent);
                                  finish();
                                } else {
                                    dialog.hide();
                                    email.setText("");
                                    password.setText("");
                                    Toasty.error(getApplicationContext(),"Login failed,try again",Toasty.LENGTH_LONG,true).show();

                                }
                            }
                        });

            }


        }
    });

    register_now.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent=new Intent(MainActivity.this,Signup.class);
            startActivity(intent);
        }
    });

    forgotpassword.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent=new Intent(MainActivity.this,ResetPassword.class);
            startActivity(intent);

        }
    });

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user=mAuth.getCurrentUser();
        if(user!=null){
          Intent intent=new Intent(MainActivity.this,homepage.class);
          startActivity(intent);
          finish();
        }
    }
}
