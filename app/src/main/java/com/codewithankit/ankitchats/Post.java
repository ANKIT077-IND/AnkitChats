package com.codewithankit.ankitchats;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class Post extends AppCompatActivity {

    CircleImageView profile;
    TextView Username, Photos;
    EditText Status;
    ImageView Postimage;
    Button Post;
    FirebaseDatabase  db;
    DatabaseReference myref;
    Toolbar posttoolbar;
    Uri filepath;
    String currentUser,image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);


        posttoolbar = findViewById(R.id.posttoolbar);
        setSupportActionBar(posttoolbar);
        getSupportActionBar().setTitle("Post Activity");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        profile = findViewById(R.id.profile_image);
        Username = findViewById(R.id.username);
        Photos = findViewById(R.id.photos);
        Status = findViewById(R.id.status);
        Postimage = findViewById(R.id.postimage);
        Post = findViewById(R.id.Post);


        db = FirebaseDatabase.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        myref = db.getReference().child("Users").child(uid);

        myref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {

                     currentUser = snapshot.child("Name").getValue(String.class);
                     image = snapshot.child("image").getValue(String.class);
                    Username.setText(currentUser);
                    Glide.with(getApplicationContext()).load(image).placeholder(R.drawable.profile).into(profile);

                } else {
                    Toasty.error(getApplicationContext(), "data not found", Toasty.LENGTH_LONG, true).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toasty.error(getApplicationContext(), error.toString(), Toasty.LENGTH_LONG, true).show();
            }
        });


        Photos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dexter.withContext(getApplicationContext())
                        .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                                Intent intent = new Intent(Intent.ACTION_PICK);
                                intent.setType("image/*");
                                startActivityForResult(Intent.createChooser(intent, "Please select Image"), 101);

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

        Post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String status = Status.getText().toString().trim();

                if (status.isEmpty()) {
                    Toasty.error(getApplicationContext(), "Please fill the status", Toasty.LENGTH_LONG, true).show();
                } else if (status.length() > 500) {
                    Toasty.error(getApplicationContext(), "status length more then 500 character", Toasty.LENGTH_LONG, true).show();
                } else {
                    ProgressDialog dialog = new ProgressDialog(Post.this);
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();

                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageReference = storage.getReference().child("PostImage").child(getfilename(filepath));
                    storageReference.putFile(filepath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                         storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                             @Override
                             public void onSuccess(Uri uri) {
                              FirebaseDatabase database=FirebaseDatabase.getInstance();
                              DatabaseReference reference=database.getReference().child("Post").push();

                                long now =System.currentTimeMillis();
                                 String timeAgo = TimeAgo.getTimeAgo(now);
                                 HashMap<String,Object>map=new HashMap<>();
                                 map.put("Username",currentUser);
                                 map.put("Profile_image",image);
                                 map.put("Status",status);
                                 map.put("Post_image",uri.toString());
                                 map.put("Time",timeAgo);

                                 reference.setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                     @Override
                                     public void onComplete(@NonNull Task<Void> task) {
                                         if (task.isSuccessful()){
                                             dialog.dismiss();
                                             Status.setText("");
                                             Toasty.success(getApplicationContext(),"Posted Successfully",Toasty.LENGTH_LONG,true).show();
                                         }
                                         else {
                                             dialog.hide();
                                             Toasty.error(getApplicationContext(),"Posted failed",Toasty.LENGTH_LONG,true).show();

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
                    });

                }

            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 101 && resultCode == RESULT_OK) {
            filepath = data.getData();

            try {
                InputStream inputStream = getContentResolver().openInputStream(filepath);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                Postimage.setImageBitmap(bitmap);
                Postimage.setVisibility(View.VISIBLE);
                CropImage.activity(filepath)
                        .start(this);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    Uri resultUri = result.getUri();


                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                }
            }


        }
        super.onActivityResult(requestCode, resultCode, data);
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
    public static class TimeAgo {
        private static final int SECOND_MILLIS = 1000;
        private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
        private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
        private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

        public static String getTimeAgo(long time) {
            if (time < 1000000000000L) {
                time *= 1000;
            }

            long now = System.currentTimeMillis();
            if (time > now || time <= 0) {
                return null;
            }


            final long diff = now - time;
            if (diff < MINUTE_MILLIS) {
                return "just now";
            } else if (diff < 2 * MINUTE_MILLIS) {
                return "a minute ago";
            } else if (diff < 50 * MINUTE_MILLIS) {
                return diff / MINUTE_MILLIS + " minutes ago";
            } else if (diff < 90 * MINUTE_MILLIS) {
                return "an hour ago";
            } else if (diff < 24 * HOUR_MILLIS) {
                return diff / HOUR_MILLIS + " hours ago";
            } else if (diff < 48 * HOUR_MILLIS) {
                return "yesterday";
            } else {
                return diff / DAY_MILLIS + " days ago";
            }
        }
    }
}

