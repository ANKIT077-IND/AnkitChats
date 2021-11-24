package com.codewithankit.ankitchats;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.theartofdev.edmodo.cropper.CropImage;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class homepage extends AppCompatActivity {
   ActionBarDrawerToggle toggle;
   DrawerLayout drawerLayout;
   NavigationView navigationView;
    BottomNavigationView bottomnavigation;
   Toolbar hometoollbar;
    FrameLayout Framelayout;
    FirebaseDatabase database,db;
    DatabaseReference myref,reference;
    Uri filepath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        getSupportFragmentManager().beginTransaction().replace(R.id.Framelayout,new HomeFragment()).commit();

        hometoollbar=findViewById(R.id.hometoollbar);
        setSupportActionBar(hometoollbar);
        getSupportActionBar().setTitle("AnkitChats");
     drawerLayout=findViewById(R.id.drawerlayout);
     navigationView=findViewById(R.id.Navigationview);
     bottomnavigation=findViewById(R.id.bottomnavigation);
     Framelayout=findViewById(R.id.Framelayout);
    toggle=new ActionBarDrawerToggle(this,drawerLayout,hometoollbar,R.string.start,R.string.close);
    drawerLayout.addDrawerListener(toggle);
    toggle.syncState();

    navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
          switch (menuItem.getItemId()){
              case R.id.setting:
                  Toasty.normal(getApplicationContext(),"setting",Toasty.LENGTH_LONG).show();
                 break;

              case R.id.logout:
                  AlertDialog.Builder builder=new AlertDialog.Builder(homepage.this);
                  builder.setCancelable(false);
                  builder.setIcon(R.mipmap.logout_foreground);
                  builder.setTitle("Logout");
                  builder.setMessage("Are you sure want to Logout");
                  builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                      @Override
                      public void onClick(DialogInterface dialog, int which) {
                          FirebaseAuth.getInstance().signOut();
                          Toasty.success(getApplicationContext(),"Logout Successfully",Toasty.LENGTH_LONG,true).show();
                          Intent intent=new Intent(homepage.this,MainActivity.class);
                          startActivity(intent);
                          finish();
                      }
                  }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                      @Override
                      public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                      }
                  });

                  AlertDialog dialog=builder.create();
                  dialog.show();

                 break;
          }

          drawerLayout.closeDrawer(GravityCompat.START);

            return true;
        }
    });

    bottomnavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id=menuItem.getItemId();
            Fragment temp=null;
        switch (id){
            case R.id.home:temp=new HomeFragment();
            break;
            case R.id.Chats:temp=new ChatsFragment();
                break;
            case R.id.sortvideo:temp=new SortVideoFragment();
                break;
            case R.id.profile:temp=new ProfileFragment();
                break;

        }

      getSupportFragmentManager().beginTransaction().replace(R.id.Framelayout,temp).commit();

            return true;
        }
    });

        View header=navigationView.getHeaderView(0);

        CircleImageView profile=header.findViewById(R.id.profile_image);
        ImageView camera=header.findViewById(R.id.camera);
        TextView name =header.findViewById(R.id.name);
        TextView mobile=header.findViewById(R.id.mobile);

        database=FirebaseDatabase.getInstance();
        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        String uid=user.getUid();
        myref=database.getReference().child("Users").child(uid);
        myref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
           if (snapshot.getValue()!=null){

               String Name=snapshot.child("Name").getValue(String.class);
               String image=snapshot.child("image").getValue(String.class);
               String Mobile=snapshot.child("Mobile_Number").getValue(String.class);
               name.setText(Name);
               mobile.setText(Mobile);
               Glide.with(getApplicationContext()).load(image).placeholder(R.drawable.profile).into(profile);


           }else{
               Toasty.error(getApplicationContext(),"Data not found",Toasty.LENGTH_LONG,true).show();
           }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            Toasty.error(getApplicationContext(),error.toString(),Toasty.LENGTH_LONG,true).show();

            }
        });

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dexter.withContext(getApplicationContext())
                        .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                                Intent intent=new Intent(Intent.ACTION_PICK);
                                intent.setType("image/*");
                                startActivityForResult(Intent.createChooser(intent,"Please select Image"),101);
                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                                permissionToken.continuePermissionRequest();
                            }
                        }).check();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==101 && resultCode==RESULT_OK){

            filepath=data.getData();

            CropImage.activity(filepath)
                    .setAspectRatio(1,1)
                    .start(this);


        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();

                ProgressDialog dialog=new ProgressDialog(this);
                dialog.setCanceledOnTouchOutside(false);
                dialog.setTitle("Image Uploading");
                dialog.show();

                FirebaseStorage storage=FirebaseStorage.getInstance();
                StorageReference storageReference=storage.getReference().child("profile_image").child(getfilename(filepath));
                storageReference.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                db=FirebaseDatabase.getInstance();
                                FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
                                String id=firebaseUser.getUid();
                               reference=db.getReference().child("Users").child(id);

                                reference.child("image").setValue(uri.toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            dialog.hide();
                                            Toasty.success(getApplicationContext(),"Image Uploading Successfully",Toasty.LENGTH_LONG,true).show();
                                        }
                                        else {
                                            dialog.dismiss();
                                            Toasty.error(getApplicationContext(),"Image not Uploading ",Toasty.LENGTH_LONG,true).show();
                                        }
                                    }
                                });
                            }
                        });
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        float percent=(100*snapshot.getBytesTransferred())/snapshot.getTotalByteCount();
                        dialog.setMessage("Uploading:"+(int)percent+"%");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toasty.error(getApplicationContext(),e.toString(),Toasty.LENGTH_LONG,true).show();

                    }
                });

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }
    public String getfilename(Uri filepath) {
        String result = null;
        if (filepath.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(filepath, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = filepath.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }
}
