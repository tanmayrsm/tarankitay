package com.example.rishikesh.insta;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.rishikesh.insta.Adapter.CommentsAdapter;
import com.example.rishikesh.insta.Model.Comment;
import com.example.rishikesh.insta.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CommentsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private CommentsAdapter commentsAdapter;
    private List<Comment> commentList;

    EditText addcomment;
    ImageView image_profile;
    TextView post;
    String postid;
    String publisherid;

    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Comments");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        addcomment  = findViewById(R.id.add_comment);
        image_profile = findViewById(R.id.image_profile);
        post = findViewById(R.id.post);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        commentList = new ArrayList<>();
        commentsAdapter = new CommentsAdapter(this , commentList);
        recyclerView.setAdapter(commentsAdapter);

        Intent intent = getIntent();
        postid = intent.getStringExtra("postid");
        publisherid = intent.getStringExtra("publisherid");

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(addcomment.getText().toString().equals("")){
                    Toast.makeText(CommentsActivity.this, "Enter the comment re", Toast.LENGTH_SHORT).show();
                }else{
                    addComment();
                }
            }
        });
        getImage();
        readComments();
    }

    private void addComment() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Comments").child(postid);

        HashMap<String  ,Object> hashMap = new HashMap<>();
        hashMap.put("comment",addcomment.getText().toString());
        hashMap.put("publisher" , firebaseUser.getUid());

        reference.push().setValue(hashMap);
        addNotifications();
        addcomment.setText("");
    }

    private void addNotifications(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(publisherid);

        HashMap<String , Object> hashMap = new HashMap<>();
        hashMap.put("userid" , firebaseUser.getUid());
        hashMap.put("text" , "commented " + addcomment.getText().toString());
        hashMap.put("postid" , postid);
        hashMap.put("ispost" , true);

        reference.push().setValue(hashMap);
    }

    private void getImage(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users")
                .child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Glide.with(getApplicationContext()).load(user.getImageurl()).load(image_profile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readComments(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Comments").child(postid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                commentList.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Comment comment = snapshot.getValue(Comment.class);
                    commentList.add(comment);
                }
                commentsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
