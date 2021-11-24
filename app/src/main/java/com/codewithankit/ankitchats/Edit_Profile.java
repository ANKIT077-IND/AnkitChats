package com.codewithankit.ankitchats;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import es.dmoral.toasty.Toasty;

public class Edit_Profile extends AppCompatActivity {
    Spinner gender;
    String Gender;
    String GEnder[]={"Male","Female","Other"};
    EditText email,password,name,mobile;
    Toolbar edittoolbaar;
   DatabaseReference reference,myref;
   FirebaseDatabase database,db;
   Button save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit__profile);


        edittoolbaar=findViewById(R.id.edittoolbaar);
        setSupportActionBar(edittoolbaar);
        getSupportActionBar().setTitle("Edit Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        gender=findViewById(R.id.gender);
        email=findViewById(R.id.email);
        password=findViewById(R.id.password);
        name=findViewById(R.id.name);
        mobile=findViewById(R.id.mobile);
        save=findViewById(R.id.save);
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

      database=FirebaseDatabase.getInstance();
        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        String id=user.getUid();
        reference=database.getReference().child("Users").child(id);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
            if (snapshot.getValue()!=null){
                String Name=snapshot.child("Name").getValue(String.class);
                String Mobile=snapshot.child("Mobile_Number").getValue(String.class);
                String Email=snapshot.child("Email").getValue(String.class);
                String Password=snapshot.child("Password").getValue(String.class);

                name.setText(Name);
                mobile.setText(Mobile);
                email.setText(Email);
                password.setText(Password);


            }
            else{
                Toasty.error(getApplicationContext(),"Data not found",Toasty.LENGTH_LONG,true).show();

            }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toasty.error(getApplicationContext(),error.toString(),Toasty.LENGTH_LONG,true).show();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressDialog dialog=new ProgressDialog(Edit_Profile.this);
                dialog.setCanceledOnTouchOutside(false);
                dialog.setMessage("Please wait...");
                dialog.show();

                String Email=email.getText().toString().trim();
                String Pass=password.getText().toString().trim();
                String Mobile=mobile.getText().toString().trim();
                String Name=name.getText().toString().trim();

                db=FirebaseDatabase.getInstance();
                FirebaseUser user1=FirebaseAuth.getInstance().getCurrentUser();
                String uid=user1.getUid();
                myref=db.getReference().child("Users").child(uid);

                HashMap<String,Object> map=new HashMap<>();

                map.put("Email",Email);
                map.put("Password",Pass);
                map.put("Name",Name);
                map.put("Mobile_Number",Mobile);
                map.put("Gender",Gender);

                myref.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            dialog.hide();
                            Toasty.success(getApplicationContext(),"Profile Updated Successfully",Toasty.LENGTH_LONG,true).show();
                        }
                        else{
                            dialog.dismiss();
                            Toasty.error(getApplicationContext(),"Profile Updated failed",Toasty.LENGTH_LONG,true).show();

                        }

                    }
                });







            }
        });
    }
}
