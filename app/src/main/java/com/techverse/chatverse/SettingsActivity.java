package com.techverse.chatverse;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.techverse.chatverse.model.UserModel;
import com.techverse.chatverse.utils.AndroidUtil;
import com.techverse.chatverse.utils.FirebaseUtil;

import de.hdodenhof.circleimageview.CircleImageView;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class SettingsActivity extends AppCompatActivity {
    CircleImageView profilePic;
    ImageView blueTick;
    TextView username, mobileNumber, accountUserId;
    EditText changeUsername, about, accountUserIdInput;
    Button saveBtn;
    TextView privacyPolicy, invite, notifications, help;
    UserModel currentUserModel;
    ImageButton backBtn;
    ActivityResultLauncher<Intent> imagePickLauncher;
    Uri selectedImageUri;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        currentUserModel = AndroidUtil.getUserModelFromIntent(getIntent());


        profilePic = findViewById(R.id.account_profile_pic);
        username = findViewById(R.id.account_user_name);
        mobileNumber = findViewById(R.id.account_mobile_number);
        changeUsername = findViewById(R.id.username_edit_text);
        about = findViewById(R.id.about);
        saveBtn = findViewById(R.id.account_save_btn);
        privacyPolicy = findViewById(R.id.privacy_policy);
        invite = findViewById(R.id.invite);
        notifications = findViewById(R.id.notification);
        help = findViewById(R.id.help);
        backBtn = findViewById(R.id.back_arrow);
        blueTick = findViewById(R.id.bluetick);
        accountUserId = findViewById(R.id.account_user_id);
        accountUserIdInput = findViewById(R.id.user_id);

        if (currentUserModel.isHasBlueTick()){
            blueTick.setVisibility(View.VISIBLE);
        }

        imagePickLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();
                        if (data!= null && data.getData()!= null){
                            selectedImageUri = data.getData();
                            AndroidUtil.setProfilePic(this, selectedImageUri, profilePic);
                        }
                    }
                }
                );

        backBtn.setOnClickListener(view -> onBackPressed());

        getUserData();

        saveBtn.setOnClickListener(view -> {
            updateBtnClick();
        });

        privacyPolicy.setOnClickListener(view -> {
            openWebsite("https://dikshanttatrari.github.io/chatverse-website/privacy.html");
        });

        help.setOnClickListener(view -> {
           openWebsite("https://wa.me/918439199567");
        });

        invite.setOnClickListener(view -> {
            sendWhatsAppMessage("Hola amigo! Let's chat on this brand new app *ChatVerse*. It's completely secure, fast and reliable. Download the app now: https://dikshanttatrari.github.io/chatverse-website");
        });

        notifications.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");

            intent.putExtra("app_package", getPackageName());
            intent.putExtra("app_uid", getApplicationInfo().uid);

            intent.putExtra("android.provider.extra.APP_PACKAGE", getPackageName());

            startActivity(intent);
        });


        profilePic.setOnClickListener(view -> {
            ImagePicker.with(this).cropSquare().compress(512).maxResultSize(512,512)
                    .createIntent(new Function1<Intent, Unit>() {
                        @Override
                        public Unit invoke(Intent intent) {
                            imagePickLauncher.launch(intent);
                            return null;
                        }
                    });
        });

    }

    void updateBtnClick() {
        String newAbout = about.getText().toString();
        String newAccountUserId = accountUserIdInput.getText().toString();
        String newUsername = changeUsername.getText().toString();

        if (!isValidUserId(newAccountUserId) && !newAccountUserId.equals(currentUserModel.getAccountUserId())) {
            Toast.makeText(this, "User ID must start with @ and only contain . and _", Toast.LENGTH_SHORT).show();
            return;
        }

        if (newUsername.isEmpty() || newUsername.length() < 3) {
            username.setError("Username length should be at least 3 characters.");
            return;
        }
        if (!newAccountUserId.equals(currentUserModel.getAccountUserId())) {
            Query query = FirebaseUtil.allUserCollectionReference().whereEqualTo("accountUserId", newAccountUserId);
            query.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        Toast.makeText(this, "user id " + newAccountUserId + " already taken", Toast.LENGTH_SHORT).show();
                    } else {
                        // Continue with the update
                        updateUserData(newAbout, newUsername, newAccountUserId);
                    }
                } else {
                    Toast.makeText(this, "Error checking accountUserId: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            updateUserData(newAbout, newUsername, newAccountUserId);
        }
    }

    void updateUserData(String newAbout, String newUsername, String newAccountUserId) {
        currentUserModel.setAbout(newAbout);
        currentUserModel.setUsername(newUsername);
        currentUserModel.setAccountUserId(newAccountUserId);
        username.setText(newUsername);
        accountUserId.setText(newAccountUserId);

        if (selectedImageUri != null) {
            FirebaseUtil.getCurrentProfilePicStorageRef().putFile(selectedImageUri)
                    .addOnCompleteListener(uploadTask -> {
                        if (uploadTask.isSuccessful()) {
                            updateToFirestore();
                        } else {
                            Toast.makeText(this, "Image upload failed!", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            updateToFirestore();
        }
    }

    void updateToFirestore(){
        FirebaseUtil.currentUserDetails().set(currentUserModel)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        Toast.makeText(this, "Updated successfully!", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(this, "Update failed!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    boolean isValidUserId(String userId) {
        return userId.matches("^@[a-zA-Z0-9_.]+$");
    }

    void getUserData(){
        FirebaseUtil.getCurrentProfilePicStorageRef().getDownloadUrl()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()){
                                Uri uri = task.getResult();
                                AndroidUtil.setProfilePic(this, uri, profilePic);
                            }
                        });
        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(task -> {
            currentUserModel = task.getResult().toObject(UserModel.class);
            username.setText(currentUserModel.getUsername());
            accountUserId.setText(currentUserModel.getAccountUserId());
            accountUserIdInput.setText(currentUserModel.getAccountUserId());
            about.setText(currentUserModel.getAbout());
            changeUsername.setText(currentUserModel.getUsername());
            mobileNumber.setText(currentUserModel.getPhone());
        });
    }

    private void openWebsite(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }

    private void sendWhatsAppMessage(String message) {
        Intent sendIntent = new Intent("android.intent.action.MAIN");
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, message);
        sendIntent.setType("text/plain");
        sendIntent.setPackage("com.whatsapp");

        try {
            startActivity(sendIntent);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "WhatsApp is not installed.", Toast.LENGTH_SHORT).show();
        }
    }


}