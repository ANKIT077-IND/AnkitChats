package com.codewithankit.ankitchats;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;

public class postviewholder extends RecyclerView.ViewHolder {
    TextView Username,Time,Status;
    CircleImageView Profile;
    ImageView image;


    public postviewholder(@NonNull View itemView) {
        super(itemView);

     Username=itemView.findViewById(R.id.username);
     Time=itemView.findViewById(R.id.time);
     Status=itemView.findViewById(R.id.status);
     Profile=itemView.findViewById(R.id.profile_image);
     image=itemView.findViewById(R.id.image);

    }
}
