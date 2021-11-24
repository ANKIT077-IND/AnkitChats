package com.codewithankit.ankitchats;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.FirebaseDatabase;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    FloatingActionButton post;
    RecyclerView recyclerview;
    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_home, container, false);

    post=view.findViewById(R.id.post);
    recyclerview=view.findViewById(R.id.recyclerview);
    recyclerview.setLayoutManager(new LinearLayoutManager(getContext()));


        FirebaseRecyclerOptions<postmodel> options =
                new FirebaseRecyclerOptions.Builder<postmodel>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Post"),postmodel.class)
                        .build();


        FirebaseRecyclerAdapter<postmodel,postviewholder>adapter=new FirebaseRecyclerAdapter<postmodel, postviewholder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull postviewholder holder, int position, @NonNull postmodel model) {
                holder.Username.setText(model.getUsername());
                holder.Time.setText(model.getTime());
                holder.Status.setText(model.getStatus());
                Glide.with(holder.Profile.getContext()).load(model.getProfile_image()).into(holder.Profile);
                Glide.with(holder.image.getContext()).load(model.getPost_image()).into(holder.image);
            }

            @NonNull
            @Override
            public postviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view1=LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item,parent,false);
             return new postviewholder(view1);
            }
        };
        adapter.startListening();
        recyclerview.setAdapter(adapter);

    post.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent=new Intent(getContext(),Post.class);
            startActivity(intent);

        }
    });


    return view;
    }
}
