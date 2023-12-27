package com.techverse.chatverse;

import static com.techverse.chatverse.utils.FirebaseUtil.allChatroomCollectionReference;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.techverse.chatverse.adapter.AddMembersRecyclerAdapter;
import com.techverse.chatverse.adapter.RecentChatRecyclerAdapter;
import com.techverse.chatverse.model.ChatroomModel;
import com.techverse.chatverse.model.GroupModel;
import com.techverse.chatverse.model.UserModel;
import com.techverse.chatverse.utils.AndroidUtil;
import com.techverse.chatverse.utils.FirebaseUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupActivity extends AppCompatActivity {

    CircleImageView groupProfile;
    EditText groupName;
    UserModel otherUsers;
    RecyclerView addMembersRecyclerView;
    Button nextBtn;
    ImageButton backBtn;
    String groupChatroomId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        otherUsers = AndroidUtil.getUserModelFromIntent(getIntent());

        groupProfile = findViewById(R.id.group_profile);
        groupName = findViewById(R.id.group_name);
        addMembersRecyclerView = findViewById(R.id.add_member_recycler_view);
        nextBtn = findViewById(R.id.next_button);
        backBtn = findViewById(R.id.back_arrow);

        getOrCreateGroupChatroomModel();

        nextBtn.setOnClickListener(view -> {
            AndroidUtil.showToast(this, "Sorry \uD83D\uDE13 this feature will be available soon.");
        });

        backBtn.setOnClickListener(view -> {
            onBackPressed();
        });

    }

    void getOrCreateGroupChatroomModel(){

    }

}
