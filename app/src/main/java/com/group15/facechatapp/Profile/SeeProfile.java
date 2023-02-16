package com.group15.facechatapp.Profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.group15.facechatapp.Chat.FirebaseDataCallback;
import com.group15.facechatapp.FaceRecognisitionHelper.SimilarityClassifier;
import com.group15.facechatapp.R;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class SeeProfile extends AppCompatActivity {

    ImageView imageView;
    EditText editText;
    Button update_face;
    ImageButton imageButton;
    androidx.appcompat.widget.Toolbar tl;

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_profile);


        firebaseAuth = FirebaseAuth.getInstance();
        imageView = findViewById(R.id.view_img);
        editText = findViewById(R.id.view_name);
        update_face = findViewById(R.id.view_addfacedata);

        tl = findViewById(R.id.view_toolbar);
        imageButton = findViewById(R.id.view_back);
        setSupportActionBar(tl);



        getdata(new DataCallaback() {
            @Override
            public void onCallback(String name, String uri) {
                Picasso.get().load(uri).into(imageView);
                editText.setText(name);
            }
        });
        update_face.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SeeProfile.this, UpdateFaceActivity.class));
            }
        });


        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }


    void getdata(DataCallaback myCallback) {
        // Read from the database
        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        CollectionReference yourCollRef = rootRef.collection("Users");
        Query query = yourCollRef.whereEqualTo("uid", firebaseAuth.getUid());
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        myCallback.onCallback(document.get("name").toString(),document.get("image").toString());

                    }
                } else {
                    Log.w("--data--", "Error getting documents.", task.getException());
                }
            }
        });
    }
}

interface DataCallaback {
    void onCallback(String name , String uri);
}