package com.example.rishikesh.insta.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.rishikesh.insta.Adapter.PostAdapter;
import com.example.rishikesh.insta.Adapter.StoryAdapter;
import com.example.rishikesh.insta.Model.Post;
import com.example.rishikesh.insta.Model.Story;
import com.example.rishikesh.insta.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<Post> postLists;

    private RecyclerView recyclerView_story;
    private StoryAdapter storyAdapter;
    private List<Story> storyList;

    private List<String> followingLists;

    ProgressBar progressBar;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager  =new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        progressBar = view.findViewById(R.id.progress_circular);

        postLists = new ArrayList<>();
        postAdapter = new PostAdapter(getContext() , postLists);
        recyclerView.setAdapter(postAdapter);

        recyclerView_story = view.findViewById(R.id.recycler_view_story);
        recyclerView_story.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(getContext() ,
                LinearLayoutManager.HORIZONTAL , false);
        recyclerView_story.setLayoutManager(linearLayoutManager1);

        storyList = new ArrayList<>();
        storyAdapter = new StoryAdapter(getContext() , storyList);
        recyclerView_story.setAdapter(storyAdapter);


        checkFollowing();
        return view;
    }

    private void checkFollowing(){
        followingLists = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Follow")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("following");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                followingLists.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    followingLists.add(snapshot.getKey());
                }
                readPosts();
                readStory();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readPosts(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postLists.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Post post = snapshot.getValue(Post.class);

                    for(String id : followingLists){
                        if(post.getPublisher().equals(id)){
                            postLists.add(post);
                        }
                    }
                }
                postAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private  void  readStory(){
        DatabaseReference reference  =FirebaseDatabase.getInstance().getReference("Story");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long timecurrent = System.currentTimeMillis();
                storyList.clear();
                storyList.add(new Story("" , 0 , 0,"" , FirebaseAuth.getInstance().
                        getCurrentUser().getUid()));

                for(String id : followingLists){
                    int countStory = 0;
                    Story story = null;
                    for(DataSnapshot snapshot : dataSnapshot.child(id).getChildren()){
                        story = snapshot.getValue(Story.class);
                        if(timecurrent  >story.getTimestart() && timecurrent < story.getTimeend()){
                            countStory++;
                        }
                    }
                    if(countStory == 0){
                        storyList.add(story);
                    }
                }
                storyAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
