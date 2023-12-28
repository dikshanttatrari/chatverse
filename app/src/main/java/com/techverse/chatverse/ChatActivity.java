package com.techverse.chatverse;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.techverse.chatverse.adapter.ChatRecyclerAdapter;
import com.techverse.chatverse.adapter.RecentChatRecyclerAdapter;
import com.techverse.chatverse.model.ChatMessageModel;
import com.techverse.chatverse.model.ChatroomModel;
import com.techverse.chatverse.model.UserModel;
import com.techverse.chatverse.utils.AndroidUtil;
import com.techverse.chatverse.utils.FirebaseUtil;
import com.vanniktech.emoji.EmojiPopup;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.ios.IosEmojiProvider;


import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;



public class ChatActivity extends AppCompatActivity {

    UserModel otherUser;
    EditText messageInput;
    ImageButton sendMessageBtn;
    ImageButton backBtn;
    ImageView emojiBtn, cameraBtn, blueTick;
    TextView otherUsername;
    RecyclerView recyclerView;
    public static String chatroomId;
    ChatroomModel chatroomModel;
    ChatRecyclerAdapter adapter;
    ImageView imageView;
    EmojiPopup emojiPopup;
    RelativeLayout rootView;
    Uri imageUri;
    

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EmojiManager.install(new IosEmojiProvider());
        setContentView(R.layout.activity_chat);


        otherUser = AndroidUtil.getUserModelFromIntent(getIntent());
        chatroomId = FirebaseUtil.getChatroomId(FirebaseUtil.currentUserId(),otherUser.getUserId());

        messageInput = findViewById(R.id.message);
        sendMessageBtn = findViewById(R.id.send_btn);
        backBtn = findViewById(R.id.back_arrow);
        otherUsername = findViewById(R.id.user_name_text_chat_activity);
        recyclerView = findViewById(R.id.chatroom_recycler_view);
        imageView = findViewById(R.id.profile_image);
        emojiBtn = findViewById(R.id.emoji_btn);
        rootView = findViewById(R.id.root_view);
        cameraBtn = findViewById(R.id.camera_btn);
        blueTick = findViewById(R.id.bluetick);

        otherUsername.setOnClickListener(view -> {
            Intent intent = new Intent(ChatActivity.this, UserProfileActivity.class);
            AndroidUtil.passUserModelAsIntent(intent,otherUser);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });



        if (otherUser.isHasBlueTick()){
            blueTick.setVisibility(View.VISIBLE);
        }

        cameraBtn.setOnClickListener(v -> {
            ImagePicker.Companion.with(this)
                    .compress(600)
                    .crop()
                    .start();
        });



        emojiPopup = EmojiPopup.Builder.fromRootView(rootView)
                .setOnEmojiPopupShownListener(() -> {
                    emojiBtn.setImageResource(R.drawable.keyboard);
                    smoothScrollToBottom();
                })
                .setOnEmojiPopupDismissListener(() -> {
                    emojiBtn.setImageResource(R.drawable.emoji);
                    smoothScrollToBottom();
                })
                .build(messageInput);

        emojiBtn.setOnClickListener(v -> {
            emojiPopup.toggle();
        });

        FirebaseUtil.getOtherProfilePicStorageRef(otherUser.getUserId()).getDownloadUrl()
                .addOnCompleteListener(t -> {
                    if (t.isSuccessful()){
                        Uri uri = t.getResult();
                        AndroidUtil.setProfilePic(this,uri,imageView);
                    }
                });


        backBtn.setOnClickListener((v -> {
            onBackPressed();
        }));
        otherUsername.setText(otherUser.getUsername());
        if (otherUser.getUserId().equals(FirebaseUtil.currentUserId())){
            otherUsername.setText(otherUser.getUsername()+" (You)");
        }

        sendMessageBtn.setOnClickListener((v -> {
            String message = messageInput.getText().toString().trim();
            if(message.isEmpty())
                return;
            sendMessageToUser(message, "text", imageUri);
        }));

        getOrCreateChatroomModel();
        loadChatDataAndInitializeAdapter();
        markChatAsRead();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == ImagePicker.REQUEST_CODE) {
            Uri imageUri = data.getData();
            if (imageUri != null) {
                sendMessageToUser("", "image",imageUri);
            }
        }
    }
    void loadChatDataAndInitializeAdapter() {
        FirebaseUtil.getChatroomReference(chatroomId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                chatroomModel = task.getResult().toObject(ChatroomModel.class);
                if (chatroomModel == null) {
                    chatroomModel = new ChatroomModel(
                            chatroomId,
                            Arrays.asList(FirebaseUtil.currentUserId(), otherUser.getUserId()),
                            Timestamp.now(),
                            ""
                    );
                    FirebaseUtil.getChatroomReference(chatroomId).set(chatroomModel);
                }

                // Chat Data fetching
                Query query = FirebaseUtil.getChatroomMessageReference(chatroomId)
                        .orderBy("timestamp", Query.Direction.DESCENDING);
                FirestoreRecyclerOptions<ChatMessageModel> options = new FirestoreRecyclerOptions.Builder<ChatMessageModel>()
                        .setQuery(query, ChatMessageModel.class).build();

                adapter = new ChatRecyclerAdapter(options, getApplicationContext());
                LinearLayoutManager manager = new LinearLayoutManager(this);
                manager.setReverseLayout(true);
                recyclerView.setLayoutManager(manager);
                recyclerView.setAdapter(adapter);
                adapter.startListening();

                adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                    @Override
                    public void onItemRangeInserted(int positionStart, int itemCount) {
                        super.onItemRangeInserted(positionStart, itemCount);
                        recyclerView.smoothScrollToPosition(0);
                    }
                });
            }
        });
    }

    void sendMessageToUser(String message, String messageType, Uri imageUri) {
        chatroomModel.setLastMessageTimestamp(Timestamp.now());
        chatroomModel.setLastMessageSenderid(FirebaseUtil.currentUserId());
        chatroomModel.setLastMessage(message);
        FirebaseUtil.getChatroomReference(chatroomId).set(chatroomModel);

        ChatMessageModel chatMessageModel = new ChatMessageModel();
        chatMessageModel.setMessage(message);
        chatMessageModel.setSenderId(FirebaseUtil.currentUserId());
        chatMessageModel.setTimestamp(Timestamp.now());
        chatMessageModel.setMessageType(messageType);

        if (messageType.equals("image") && imageUri != null) {
            // Pass an OnSuccessListener to set the image URL in the ChatMessageModel
            uploadImageToStorage(imageUri, uri -> {
                chatMessageModel.setImageUrl(uri.toString());
                // Send the message after setting the image URL
                sendMessageWithModel(chatMessageModel);
            });
        } else {
            // For text messages, send the message directly
            sendMessageWithModel(chatMessageModel);
        }
    }



    private void sendMessageWithModel(ChatMessageModel chatMessageModel) {
        FirebaseUtil.getChatroomMessageReference(chatroomId).add(chatMessageModel)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        messageInput.setText("");
                        sendNotification(chatMessageModel.getMessage());
                    }
                });
        FirebaseUtil.getChatroomReference(chatroomId).set(chatroomModel);
    }


    private void uploadImageToStorage(Uri imageUri, OnSuccessListener<Uri> onSuccessListener) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference imagesRef = storageRef.child("images/" + System.currentTimeMillis() + ".jpg");

        imagesRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    imagesRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        // Set the actual download URL in the ChatMessageModel
                        onSuccessListener.onSuccess(uri);
                    });
                })
                .addOnFailureListener(e -> {
                    // Handle image upload failure
                });
    }



    void getOrCreateChatroomModel(){
        FirebaseUtil.getChatroomReference(chatroomId).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                chatroomModel = task.getResult().toObject(ChatroomModel.class);
                if(chatroomModel==null){
                    chatroomModel = new ChatroomModel(
                            chatroomId,
                            Arrays.asList(FirebaseUtil.currentUserId(),otherUser.getUserId()),
                            Timestamp.now(),
                            ""
                    );
                    FirebaseUtil.getChatroomReference(chatroomId).set(chatroomModel);
                }
            }
        });
    }

    private void markChatAsRead() {
        FirebaseUtil.getChatroomReference(chatroomId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ChatroomModel chatroomModel = task.getResult().toObject(ChatroomModel.class);
                if (chatroomModel != null) {
                    chatroomModel.setMessageRead(true);
                    FirebaseUtil.getChatroomReference(chatroomId).set(chatroomModel);
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }

   void sendNotification(String message){

        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                UserModel currentUser = task.getResult().toObject(UserModel.class);
                try {
                    JSONObject jsonObject = new JSONObject();

                    JSONObject notificationObject = new JSONObject();
                    notificationObject.put("title", currentUser.getUsername());
                    notificationObject.put("body", message);

                    JSONObject dataObject = new JSONObject();
                    dataObject.put("userId", currentUser.getUserId());

                    jsonObject.put("notification", notificationObject);
                    jsonObject.put("data", dataObject);
                    jsonObject.put("to", otherUser.getFcmToken());

                    callApi(jsonObject);


                }catch (Exception e){

                }
            }
        });

   }


   void callApi(JSONObject jsonObject){
        MediaType JSON = MediaType.get("application/json");
        OkHttpClient client = new OkHttpClient();
        String url = "https://fcm.googleapis.com/fcm/send";
        RequestBody body = RequestBody.create(jsonObject.toString(), JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .header("Authorization", "Bearer AAAABxFSCgY:APA91bFRspE6ifrV33bPg8-6lZTdL_pxJCbBmwZtPT469OoSLhJc3bRyVT_AxDA2_9E0ZCkn57iAb-i9tLBlC1BnT4anR2fe5I1CZc4MFy2r8w5SD3yJLXr8OTDTRMPcvq17NmRejc56")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

            }
        });
   }

    void smoothScrollToBottom() {
        if (adapter.getItemCount() > 0) {
            recyclerView.smoothScrollToPosition(0);
        }
    }

}