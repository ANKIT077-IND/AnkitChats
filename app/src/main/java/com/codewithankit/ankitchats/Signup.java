package com.codewithankit.ankitchats;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import es.dmoral.toasty.Toasty;

public class Signup extends AppCompatActivity {
   Toolbar signuptoolbaar;
   Spinner gender;
   String Gender;
   String GEnder[]={"Male","Female","Other"};
    private FirebaseAuth mAuth;
 Button Register;
 EditText email,password,name,mobile;
 FirebaseDatabase db;
 DatabaseReference myref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        signuptoolbaar=findViewById(R.id.signuptoolbaar);
        setSupportActionBar(signuptoolbaar);
        getSupportActionBar().setTitle("Register");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mAuth = FirebaseAuth.getInstance();

     gender=findViewById(R.id.gender);
     email=findViewById(R.id.email);
     password=findViewById(R.id.password);
     name=findViewById(R.id.name);
     mobile=findViewById(R.id.mobile);
     Register=findViewById(R.id.register);
     ArrayAdapter<String> mysex=new ArrayAdapter<>(this,R.layout.support_simple_spinner_dropdown_item,GEnder);
        gender.setAdapter(mysex);

      gender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
          @Override
          public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
              Gender=gender.getSelectedItem().toString();
          }

          @Override
          public void onNothingSelected(AdapterView<?> parent) {

          }
      });

   Register.setOnClickListener(new View.OnClickListener() {
       @Override
       public void onClick(View v) {

           String etemail=email.getText().toString();
           String etpass=password.getText().toString();
           String etmobile=mobile.getText().toString();
           String etname=name.getText().toString();


           db=FirebaseDatabase.getInstance();
           myref=db.getReference().child("Users");
           myref.orderByChild("Mobile_Number").equalTo(etmobile).addListenerForSingleValueEvent(new ValueEventListener() {
               @Override
               public void onDataChange(@NonNull DataSnapshot snapshot) {
                 if (snapshot.getValue()!=null){
                   Toasty.error(getApplicationContext(),"Mobile number already register",Toasty.LENGTH_LONG,true).show();
                 }else{
                     if (etemail.isEmpty()){
                         Toasty.error(getApplicationContext(),"Please enter the Email",Toasty.LENGTH_LONG,true).show();
                     }
                     else if (etemail.indexOf('@') <= 0) {
                         Toasty.error(getApplicationContext(), "@ invalid  position", Toasty.LENGTH_LONG, true).show();
                     }
                     else if (etemail.charAt(etemail.length() - 4) != '.' && etemail.charAt(etemail.length() - 3) != '.') {
                         Toasty.error(getApplicationContext(), ". invalid position", Toasty.LENGTH_LONG, true).show();
                     }
                     else if (etpass.isEmpty()){
                         Toasty.error(getApplicationContext(),"Please enter the password",Toasty.LENGTH_LONG,true).show();
                     }
                     else if (etpass.length()<5 && etpass.length()<15){
                         Toasty.error(getApplicationContext(),"Password must we length 5 to 15 characters",Toasty.LENGTH_LONG,true).show();
                     }
                     else if (etname.isEmpty()){
                         Toasty.error(getApplicationContext(),"Please enter the name",Toasty.LENGTH_LONG,true).show();
                     }
                     else if (etname.length()<3 && etname.length()>15){
                         Toasty.error(getApplicationContext(),"Name must we 3 to 15 characters",Toasty.LENGTH_LONG,true).show();
                     }
                     else if (etmobile.isEmpty()){
                         Toasty.error(getApplicationContext(),"Please Enter the mobile number",Toasty.LENGTH_LONG,true).show();
                     }
                     else if (etmobile.length()!=10){
                         Toasty.error(getApplicationContext(),"mobile number must we 10 digit",Toasty.LENGTH_LONG,true).show();
                     }
                     else {
                         ProgressDialog dialog=new ProgressDialog(Signup.this);
                         dialog.setMessage("Please wait...");
                         dialog.setCanceledOnTouchOutside(false);
                         dialog.show();

                         mAuth.createUserWithEmailAndPassword(etemail,etpass)
                                 .addOnCompleteListener(Signup.this, new OnCompleteListener<AuthResult>() {
                                     @Override
                                     public void onComplete(@NonNull Task<AuthResult> task) {
                                         if (task.isSuccessful()) {


                                             db=FirebaseDatabase.getInstance();
                                             String id=task.getResult().getUser().getUid();

                                             myref=db.getReference().child("Users").child(id);

                                             HashMap<String,Object> map=new HashMap<>();

                                             map.put("Email",etemail);
                                             map.put("Password",etpass);
                                             map.put("Name",etname);
                                             map.put("Mobile_Number",etmobile);
                                             map.put("Gender",Gender);

                                             myref.setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                 @Override
                                                 public void onSuccess(Void aVoid) {
                                                     dialog.dismiss();
                                                     email.setText("");
                                                     mobile.setText("");
                                                     password.setText("");
                                                     name.setText("");
                                                     Toasty.success(getApplicationContext(),"Account Creating Successfully",Toasty.LENGTH_LONG,true).show();

                                                 }
                                             });



                                         } else {
                                             dialog.hide();
                                             email.setText("");
                                             mobile.setText("");
                                             password.setText("");
                                             name.setText("");
                                             Toasty.error(getApplicationContext(),"Account Creating failed",Toasty.LENGTH_LONG,true).show();

                                         }
                                     }
                                 });
                     }


                 }

               }

               @Override
               public void onCancelled(@NonNull DatabaseError error) {
                  Toasty.error(getApplicationContext(),error.toString(),Toasty.LENGTH_LONG,true).show();
               }
           });



       }
   });


    }
}
