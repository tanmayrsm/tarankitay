package com.example.rishikesh.insta.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.rishikesh.insta.Main2Activity;
import com.example.rishikesh.insta.Model.Comment;
import com.example.rishikesh.insta.Model.Post;
import com.example.rishikesh.insta.Model.User;
import com.example.rishikesh.insta.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder>{

    private Context mContext ;
    private List<Comment> mComment;
    private FirebaseUser firebaseUser;

    public CommentsAdapter(Context mContext, List<Comment> mComment) {
        this.mContext = mContext;
        this.mComment = mComment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view  =LayoutInflater.from(mContext).inflate(R.layout.comment_item , parent , false);

        return new CommentsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        firebaseUser  =FirebaseAuth.getInstance().getCurrentUser();
        final Comment comment = mComment.get(position);
        holder.comment.setText(comment.getComment());
        getuserInfo(holder.image_profile , holder.username , comment.getPublisher());

        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext , Main2Activity.class);
                intent.putExtra("publisherid" , comment.getPublisher());
                mContext.startActivity(intent);
            }
        });

        holder.image_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext , Main2Activity.class);
                intent.putExtra("publisherid" , comment.getPublisher());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mComment.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView image_profile;
        public TextView username , comment;

        public ViewHolder(View itemView) {
            super(itemView);
            image_profile = itemView.findViewById(R.id.image_profile);
            username = itemView.findViewById(R.id.username);
            comment = itemView.findViewById(R.id.comment);
        }
    }
    private void getuserInfo(final ImageView imageView , final TextView username , String publisherid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(publisherid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Glide.with(mContext).load(user.getImageurl()).into(imageView);
                username.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
