package com.example.rishikesh.insta;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    EditText username , fullname ,email ,password;
    Button register;
    TextView text_login;

    FirebaseAuth auth;
    DatabaseReference reference;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        username = findViewById(R.id.username);
        fullname = findViewById(R.id.fullname);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);

        register = findViewById(R.id.register);
        text_login = findViewById(R.id.text_login);

        auth = FirebaseAuth.getInstance();
        text_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this , LoginActivity.class));
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd = new ProgressDialog(RegisterActivity.this);
                pd.setMessage("Please Wait...");
                pd.show();

                String str_username = username.getText().toString();
                String str_fullname = fullname.getText().toString();
                String str_email = email.getText().toString();
                String str_password = password.getText().toString();

                if(TextUtils.isEmpty(str_username) || TextUtils.isEmpty(str_fullname)
                        || TextUtils.isEmpty(str_email) || TextUtils.isEmpty(str_password)){
                    Toast.makeText(RegisterActivity.this, "Fill all details", Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }else if(str_password.length() < 6){
                    Toast.makeText(RegisterActivity.this, "Password shpould have min 6 characters", Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }else{
                    register(str_username , str_fullname , str_email , str_password);
                }

            }
        });
    }

    private void register(final String username , final String fullname , String email , String password){
        auth.createUserWithEmailAndPassword(email , password)
                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            String userid = firebaseUser.getUid();

                            reference = FirebaseDatabase.getInstance().getReference().child("Users").child(userid);

                            HashMap<String , Object> hashmap = new HashMap<>();
                            hashmap.put("id" , userid);
                            hashmap.put("username"  ,username.toLowerCase());
                            hashmap.put("fullname" , fullname);
                            hashmap.put("bio" , "");
                            hashmap.put("imageurl" , "https://firebasestorage.googleapis.com/v0/b/insta-399d7.appspot.com/o/prof.jpg?alt=media&token=e044a283-6f56-47a9-9d71-3bdbef2dabd5");

                            reference.setValue(hashmap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        pd.dismiss();
                                        Intent intent  =new Intent(RegisterActivity.this , Main2Activity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                        Toast.makeText(RegisterActivity.this, "Hogya regsiter", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                        else {
                            pd.dismiss();
                            Toast.makeText(RegisterActivity.this, "You cant register with this email and password", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
