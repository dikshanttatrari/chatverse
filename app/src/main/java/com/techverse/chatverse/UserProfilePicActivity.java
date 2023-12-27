package com.techverse.chatverse;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.techverse.chatverse.model.UserModel;
import com.techverse.chatverse.utils.AndroidUtil;
import com.techverse.chatverse.utils.FirebaseUtil;

public class UserProfilePicActivity extends AppCompatActivity {

    ImageView backBtn, profilePic, blueTick;
    TextView userName, noProfilePhoto;
    UserModel model;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_pic);

        model = AndroidUtil.getUserModelFromIntent(getIntent());
        backBtn = findViewById(R.id.back_arrow);
        profilePic = findViewById(R.id.user_profile_profile_pic);
        userName = findViewById(R.id.user_name_user_profile_pic);
        noProfilePhoto = findViewById(R.id.no_profile_pic);
        blueTick = findViewById(R.id.bluetick);

        backBtn.setOnClickListener(view -> {
            onBackPressed();
        });

        if (model.isHasBlueTick()){
            blueTick.setVisibility(View.VISIBLE);
        }

        userName.setText(model.getUsername());
        if (model.getUserId() != null && !model.getUserId().isEmpty()) {
            FirebaseUtil.getOtherProfilePicStorageRef(model.getUserId()).getDownloadUrl()
                    .addOnCompleteListener(t -> {
                        if (t.isSuccessful()) {
                            Uri uri = t.getResult();
                            if (uri != null) {
                                AndroidUtil.showProfilePic(this, uri, profilePic);
                                noProfilePhoto.setVisibility(View.GONE);
                            } else {

                                profilePic.setVisibility(View.GONE);
                                noProfilePhoto.setVisibility(View.VISIBLE);
                            }
                        } else {

                            profilePic.setVisibility(View.GONE);
                            noProfilePhoto.setVisibility(View.VISIBLE);
                        }
                    });
        } else {

            profilePic.setVisibility(View.GONE);
            noProfilePhoto.setVisibility(View.VISIBLE);
        }

    }
}
