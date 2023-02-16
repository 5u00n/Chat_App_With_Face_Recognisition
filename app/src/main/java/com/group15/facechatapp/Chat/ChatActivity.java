package com.group15.facechatapp.Chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.group15.facechatapp.Firebase.FirebaseModel;
import com.group15.facechatapp.Profile.SeeProfile;
import com.group15.facechatapp.Profile.UpdateFaceActivity;
import com.group15.facechatapp.R;
import com.squareup.picasso.Picasso;


public class ChatActivity extends AppCompatActivity {


    private FirebaseFirestore firebaseFirestore;
    LinearLayoutManager linearLayoutManager;
    private FirebaseAuth firebaseAuth;

    ImageView mimageviewofuser;

    FirestoreRecyclerAdapter<FirebaseModel, NoteViewHolder> chatAdapter;

    RecyclerView mrecyclerview;
    Toolbar toolbar;
    AppBarLayout appBarLayout;


    private String userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        appBarLayout = findViewById(R.id.app_bar);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        Drawable drawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_baseline_more_vert_24);

        toolbar.setOverflowIcon(drawable);


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        mrecyclerview = findViewById(R.id.recyclerview);


        userid = firebaseAuth.getUid();
        //userid = "j3Uyz6mMfobDFGeB2GlGjDYwrpx1";


        // Query query=firebaseFirestore.collection("Users"); xo9pBUCmanceqAHkNZBPcL1ghw22
        // Query query = firebaseFirestore.collection("Users").whereNotEqualTo("uid", firebaseAuth.getUid());
        Query query = firebaseFirestore.collection("Users").whereNotEqualTo("uid", userid);
        FirestoreRecyclerOptions<FirebaseModel> allusername = new FirestoreRecyclerOptions.Builder<FirebaseModel>().setQuery(query, FirebaseModel.class).build();

        chatAdapter = new FirestoreRecyclerAdapter<FirebaseModel, NoteViewHolder>(allusername) {
            @Override
            protected void onBindViewHolder(@NonNull NoteViewHolder noteViewHolder, int i, @NonNull FirebaseModel firebasemodel) {

                noteViewHolder.particularusername.setText(firebasemodel.getName());
                String uri = firebasemodel.getImage();

                Picasso.get().load(uri).into(mimageviewofuser);
                if (firebasemodel.getStatus().equals("Online")) {
                    noteViewHolder.statusofuser.setText(firebasemodel.getStatus());
                    noteViewHolder.statusofuser.setTextColor(Color.GREEN);
                } else {
                    noteViewHolder.statusofuser.setText(firebasemodel.getStatus());
                }

                noteViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(ChatActivity.this, SpecificChatActivity.class);
                        intent.putExtra("name", firebasemodel.getName());
                        intent.putExtra("receiveruid", firebasemodel.getUid());
                        intent.putExtra("imageuri", firebasemodel.getImage());
                        startActivity(intent);
                    }
                });


            }


            @NonNull
            @Override
            public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_view_layout, parent, false);
                return new NoteViewHolder(view);
            }
        };


        mrecyclerview.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(getBaseContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        mrecyclerview.setLayoutManager(linearLayoutManager);
        mrecyclerview.setAdapter(chatAdapter);


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.profile:
                startActivity(new Intent(ChatActivity.this, SeeProfile.class));
                break;

            case R.id.settings:
                Toast.makeText(getApplicationContext(), "Setting is clicked", Toast.LENGTH_SHORT).show();
                break;

            case R.id.update_face:
                startActivity(new Intent(ChatActivity.this, UpdateFaceActivity.class));
                break;
        }


        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);


        return true;
    }


    @Override
    public void onStart() {
        super.onStart();
        chatAdapter.startListening();
        mrecyclerview.getRecycledViewPool().clear();
        chatAdapter.notifyDataSetChanged();
        DocumentReference documentReference = firebaseFirestore.collection("Users").document(userid);
        documentReference.update("status", "Online").addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(), "Now User is Online", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        if (chatAdapter != null) {
            chatAdapter.stopListening();
        }
        DocumentReference documentReference = firebaseFirestore.collection("Users").document(userid);
        documentReference.update("status", "Offline").addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(), "Now User is Offline", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (chatAdapter != null) {
            chatAdapter.stopListening();
        }
        DocumentReference documentReference = firebaseFirestore.collection("Users").document(userid);
        documentReference.update("status", "Offline").addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(), "Now User is Offline", Toast.LENGTH_SHORT).show();
            }
        });
    }

    class NoteViewHolder extends RecyclerView.ViewHolder {

        private TextView particularusername;
        private TextView statusofuser;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            particularusername = itemView.findViewById(R.id.nameofuser);
            statusofuser = itemView.findViewById(R.id.statusofuser);
            mimageviewofuser = itemView.findViewById(R.id.imageviewofuser);


        }
    }


}


