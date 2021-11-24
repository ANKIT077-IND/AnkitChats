package com.codewithankit.ankitchats;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import es.dmoral.toasty.Toasty;

public class ResetPassword extends AppCompatActivity {
     AppCompatButton sendmail;
     TextInputEditText email;
     Toolbar Resettoolbar;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        mAuth = FirebaseAuth.getInstance();
         sendmail=findViewById(R.id.sendmail);
         email=findViewById(R.id.email);

         Resettoolbar=findViewById(R.id.resettoolbar);
         setSupportActionBar(Resettoolbar);
         getSupportActionBar().setTitle("Reset Password");
         getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    sendmail.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String etmail=email.getText().toString().trim();
            if (etmail.isEmpty()){
                Toasty.error(getApplicationContext(),"Please enter the email",Toasty.LENGTH_LONG,true).show();
            }
            else if (etmail.indexOf('@') <= 0) {
                Toasty.error(getApplicationContext(), "@ invalid  position", Toasty.LENGTH_LONG, true).show();
            }
            else if (etmail.charAt(etmail.length() - 4) != '.' && etmail.charAt(etmail.length() - 3) != '.') {
                Toasty.error(getApplicationContext(), ". invalid position", Toasty.LENGTH_LONG, true).show();
            }
            else{
                ProgressDialog dialog=new ProgressDialog(ResetPassword.this);
                dialog.setCanceledOnTouchOutside(false);
                dialog.setMessage("Please wait...");
                dialog.show();
                mAuth.sendPasswordResetEmail(etmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            dialog.dismiss();
                            email.setText("");
                            Toasty.success(getApplicationContext(),"Reset link send to email",Toasty.LENGTH_LONG,true).show();
                        }else{
                            dialog.hide();
                            Toasty.error(getApplicationContext(),"Reset Password failed",Toasty.LENGTH_LONG,true).show();
                        }

                    }
                });

            }



        }
    });
    }
}
