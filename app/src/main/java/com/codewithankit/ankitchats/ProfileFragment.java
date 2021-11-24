package com.codewithankit.ankitchats;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import es.dmoral.toasty.Toasty;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    TextView name,email,gender,mobile,password;
    DatabaseReference myref;
    FirebaseDatabase database;
    Button editprofile;
    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_profile, container, false);

        name=view.findViewById(R.id.name);
        email=view.findViewById(R.id.email);
        gender=view.findViewById(R.id.gender);
        mobile=view.findViewById(R.id.mobile);
        password=view.findViewById(R.id.password);
        editprofile=view.findViewById(R.id.editprofile);

        database=FirebaseDatabase.getInstance();
        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        String uid=user.getUid();
        myref=database.getReference().child("Users").child(uid);

        myref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue()!=null){
                    String Name=snapshot.child("Name").getValue(String.class);
                    String Mobile=snapshot.child("Mobile_Number").getValue(String.class);
                    String Gender=snapshot.child("Gender").getValue(String.class);
                    String Email=snapshot.child("Email").getValue(String.class);
                    String Password=snapshot.child("Password").getValue(String.class);
                    name.setText(Name);
                    mobile.setText(Mobile);
                    gender.setText(Gender);
                    email.setText(Email);
                    password.setText(Password);

                }
                else {
                    Toasty.error(getContext(),"Data not found",Toasty.LENGTH_LONG,true).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toasty.error(getContext(),error.toString(),Toasty.LENGTH_LONG,true).show();

            }
        });

        editprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(),Edit_Profile.class);
                startActivity(intent);

            }
        });

        return view;
    }
}
