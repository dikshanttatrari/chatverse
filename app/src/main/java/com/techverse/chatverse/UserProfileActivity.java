package com.techverse.chatverse;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.techverse.chatverse.model.UserModel;
import com.techverse.chatverse.utils.AndroidUtil;
import com.techverse.chatverse.utils.FirebaseUtil;
import com.vanniktech.emoji.EmojiTextView;
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationConfig;
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationService;
import com.zegocloud.uikit.prebuilt.call.invite.widget.ZegoSendCallInvitationButton;
import com.zegocloud.uikit.service.defines.ZegoUIKitUser;

import java.util.Collections;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileActivity extends AppCompatActivity {
    ImageView backBtn, blueTick;
    TextView userNameText, phoneNumber, accountUserId;
    CircleImageView profilePic;
    EmojiTextView userName, about;
    UserModel model;
    ZegoSendCallInvitationButton voiceCallBtn, videoCallBtn;


    @SuppressLint({"MissingInflatedId", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        model = AndroidUtil.getUserModelFromIntent(getIntent());
        startService();

        blueTick = findViewById(R.id.bluetick);
        backBtn = findViewById(R.id.back_arrow);
        userNameText = findViewById(R.id.user_name_user_profile);
        phoneNumber = findViewById(R.id.user_profile_mobile_number);
        profilePic = findViewById(R.id.user_profile_pic);
        userName = findViewById(R.id.user_profile_username);
        about = findViewById(R.id.user_profile_about);
        voiceCallBtn = findViewById(R.id.user_profile_call_btn);
        videoCallBtn = findViewById(R.id.user_profile_video_call_btn);
        accountUserId = findViewById(R.id.user_profile_user_id);

        backBtn.setOnClickListener(view -> onBackPressed());

        userNameText.setText(model.getUsername());
        accountUserId.setText(model.getAccountUserId());
        userName.setText(model.getUsername());
        phoneNumber.setText(model.getPhone());
        about.setText(model.getAbout());


        String targetUserID = model.getUserId();
        String targetUserName = model.getUsername();
        setVoiceCall(targetUserID, targetUserName);
        setVideoCall(targetUserID, targetUserName);


        if (model.getUserId() != null && !model.getUserId().isEmpty()) {
            FirebaseUtil.getOtherProfilePicStorageRef(model.getUserId()).getDownloadUrl()
                    .addOnCompleteListener(t -> {
                        if (t.isSuccessful()) {
                            Uri uri = t.getResult();
                            AndroidUtil.setProfilePic(this, uri, profilePic);
                        }
                    });
        }

        if (model.getAbout() == null || model.getAbout().length() == 0){
            about.setText("(no about)");
        }

        if (model.isHasBlueTick()){
            blueTick.setVisibility(View.VISIBLE);
        }

        profilePic.setOnClickListener(view -> {
            Intent intent = new Intent(UserProfileActivity.this, UserProfilePicActivity.class);
            AndroidUtil.passUserModelAsIntent(intent,model);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });

    }

    void startService(){

        Application application = getApplication();
        long appID = 1742026727;
        String appSign = "e50bd3f49ce2d052afa16b129e6a3a639fbdd6fa0efc100d24579793e58e3640";
        String userID = FirebaseUtil.currentUserId();
        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                UserModel currentUser = task.getResult().toObject(UserModel.class);
                if (currentUser != null) {
                    String userName = currentUser.getUsername();

                    ZegoUIKitPrebuiltCallInvitationConfig callInvitationConfig = new ZegoUIKitPrebuiltCallInvitationConfig();
                    ZegoUIKitPrebuiltCallInvitationService.init(application, appID, appSign, userID, userName, callInvitationConfig);
                }
            }
        });
    }

    void setVoiceCall(String targetUserID, String targetUserName){
        voiceCallBtn.setIsVideoCall(false);
        voiceCallBtn.setResourceID("zego_uikit_call");
        voiceCallBtn.setInvitees(Collections.singletonList(new ZegoUIKitUser(targetUserID,targetUserName)));
    }

    void setVideoCall(String targetUserID, String targetUserName){
        videoCallBtn.setIsVideoCall(true);
        videoCallBtn.setResourceID("zego_uikit_call");
        videoCallBtn.setInvitees(Collections.singletonList(new ZegoUIKitUser(targetUserID,targetUserName)));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ZegoUIKitPrebuiltCallInvitationService.unInit();
    }
}