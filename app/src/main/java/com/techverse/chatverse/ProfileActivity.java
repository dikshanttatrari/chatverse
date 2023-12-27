package com.techverse.chatverse;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.techverse.chatverse.model.UserModel;
import com.techverse.chatverse.utils.AndroidUtil;
import com.techverse.chatverse.utils.FirebaseUtil;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class ProfileActivity extends AppCompatActivity {
    EditText username;
    EditText accountUserId;
    Button finishBtn;
    String phone;
    UserModel userModel;

    public static boolean isLoggedIn(){
        return currentUserId() != null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        phone = getIntent().getExtras().getString("phone");
        accountUserId = findViewById(R.id.userid);
        username = findViewById(R.id.username);
        finishBtn = findViewById(R.id.finish_btn);

        getDetails();
        finishBtn.setOnClickListener((view -> {
            setDetails();
        }));
    }

    void setDetails() {
        String userName = username.getText().toString();
        String accountUserId = this.accountUserId.getText().toString().toLowerCase(); // Convert to lowercase

        if (userName.isEmpty() || userName.length() < 2) {
            AndroidUtil.showToast(this, "Name should be at least 2 characters.");
            return;
        }

        if (!accountUserId.matches("^@[a-zA-Z0-9_.]+$")) {
            AndroidUtil.showToast(this, "User ID must start with @ and only contain . and _");
            return;
        }

        if (userModel != null) {
            // User has logged in before, update the details without checking AccountUserId uniqueness
            userModel.setUsername(userName);
            userModel.setAccountUserId(accountUserId);
            updateProfileAndNavigateToMain();
        } else {
            // New user, check if AccountUserId is unique using a query
            FirebaseFirestore.getInstance().collection("users")
                    .whereEqualTo("accountUserId", accountUserId.toLowerCase())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            boolean isAvailable = task.getResult().isEmpty();

                            if (isAvailable) {
                                // AccountUserId is available, proceed with updating user details
                                userModel = new UserModel(phone, userName, Timestamp.now(), FirebaseUtil.currentUserId());
                                userModel.setAccountUserId(accountUserId);
                                updateProfileAndNavigateToMain();
                            } else {
                                // AccountUserId is not available, show error
                                AndroidUtil.showToast(this, "User ID " + accountUserId + " is already taken.");
                            }
                        } else {
                            Log.e("ProfileActivity", "Error checking user ID availability", task.getException());
                        }
                    });
        }
    }





    void updateProfileAndNavigateToMain() {
        FirebaseUtil.currentUserDetails().set(userModel).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }



    void getDetails() {
        currentUserDetails().get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                userModel = task.getResult().toObject(UserModel.class);
                if (userModel != null) {
                    username.setText(userModel.getUsername());
                    accountUserId.setText(userModel.getAccountUserId());

                    accountUserId.setEnabled(userModel.getAccountUserId() == null);
                }
            }
        });
    }

    public static String currentUserId() {
        return FirebaseAuth.getInstance().getUid();
    }

    DocumentReference currentUserDetails() {
        return FirebaseFirestore.getInstance().collection("users").document(currentUserId());
    }
}
