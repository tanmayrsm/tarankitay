package com.example.rishikesh.insta;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    Button login , register;

    FirebaseUser firebaseUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        login = findViewById(R.id.login);
        register = findViewById(R.id.register);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this , LoginActivity.class));
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this , RegisterActivity.class));
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseUser  =FirebaseAuth.getInstance().getCurrentUser();

        if(firebaseUser != null){
            startActivity(new Intent(MainActivity.this , Main2Activity.class));
            finish();
        }
        else{
            Toast.makeText(this, "No user", Toast.LENGTH_SHORT).show();
        }
    }

}
