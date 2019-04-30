package com.example.rishikesh.insta;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.rishikesh.insta.Fragment.HomeFragment;
import com.example.rishikesh.insta.Fragment.NotificationFragment;
import com.example.rishikesh.insta.Fragment.ProfileFragment;
import com.example.rishikesh.insta.Fragment.SearchFragment;
import com.google.firebase.auth.FirebaseAuth;

public class Main2Activity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    Fragment selectFragment = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        Bundle intent = getIntent().getExtras();
        if(intent!=null){
            String publisher = intent.getString("publisherid");

            SharedPreferences.Editor editor = getSharedPreferences("PREFS" , MODE_PRIVATE).edit();
            editor.putString("profileid" , publisher);
            editor.apply();

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container ,
                    new ProfileFragment()).commit();
        }else{
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container ,
                    new HomeFragment()).commit();
        }


    }
    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()){
                case R.id.nav_home :
                    selectFragment = new HomeFragment();
                    break;

                case R.id.nav_search :
                    selectFragment = new SearchFragment();
                    break;
                case R.id.nav_add :
                    selectFragment = null;
                    startActivity(new Intent(Main2Activity.this , PostActivity.class));
                    break;
                case R.id.nav_heart :
                    selectFragment = new NotificationFragment();
                    break;
                case R.id.nav_profile :
                    SharedPreferences.Editor editor = getSharedPreferences("PREFS" , MODE_PRIVATE).edit();
                    editor.putString("profileid" , FirebaseAuth.getInstance().getCurrentUser().getUid());
                    editor.apply();
                    selectFragment = new ProfileFragment();
                    break;
            }
            if(selectFragment != null){
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container , selectFragment).commit();
            }
            return true;
        }
    };
}
