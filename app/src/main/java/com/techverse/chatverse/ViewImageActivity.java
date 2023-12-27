package com.techverse.chatverse;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;
import com.techverse.chatverse.model.ChatMessageModel;
import com.techverse.chatverse.model.UserModel;
import com.techverse.chatverse.utils.AndroidUtil;
import com.techverse.chatverse.utils.FirebaseUtil;

public class ViewImageActivity extends AppCompatActivity {

    TextView username;
    ImageView backBtn, sendImage, blueTick;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);


        username = findViewById(R.id.user_name_view_image_activity);
        backBtn = findViewById(R.id.back_arrow);
        sendImage = findViewById(R.id.send_image);
        blueTick = findViewById(R.id.bluetick);

        backBtn.setOnClickListener(view -> onBackPressed());

        Intent intent = getIntent();
        if (intent != null) {
            String senderUserId = intent.getStringExtra("userid");
            String imageUrl = intent.getStringExtra("imageUrl");

            fetchUserDetails(senderUserId);

            Glide.with(this).load(imageUrl).into(sendImage);
        } else {
            // Handle the case when there is no intent data
        }
    }

    private void fetchUserDetails(String userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0);
                            UserModel userModel = documentSnapshot.toObject(UserModel.class);
                            if (userModel != null) {
                                username.setText(userModel.getUsername());
                            }
                            if (userModel.isHasBlueTick()){
                                blueTick.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                });
    }
}