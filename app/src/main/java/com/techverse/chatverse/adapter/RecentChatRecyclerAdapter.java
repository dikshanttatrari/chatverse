package com.techverse.chatverse.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.techverse.chatverse.ChatActivity;
import com.techverse.chatverse.R;
import com.techverse.chatverse.UserProfilePicActivity;
import com.techverse.chatverse.model.ChatMessageModel;
import com.techverse.chatverse.model.ChatroomModel;
import com.techverse.chatverse.model.UserModel;
import com.techverse.chatverse.utils.AndroidUtil;
import com.techverse.chatverse.utils.FirebaseUtil;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.EmojiTextView;
import com.vanniktech.emoji.ios.IosEmojiProvider;

public class RecentChatRecyclerAdapter extends FirestoreRecyclerAdapter<ChatroomModel, RecentChatRecyclerAdapter.ChatroomModelViewHolder> {

    Context context;

    public RecentChatRecyclerAdapter(@NonNull FirestoreRecyclerOptions<ChatroomModel> options,Context context) {
        super(options);
        EmojiManager.install(new IosEmojiProvider());
        this.context = context;
    }
    @Override
    protected void onBindViewHolder(@NonNull ChatroomModelViewHolder holder, int position, @NonNull ChatroomModel model) {
        FirebaseUtil.getOtherUserFromChatroom(model.getUserIds())
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        boolean lastMessageSentByMe = model.getLastMessageSenderid().equals(FirebaseUtil.currentUserId());
                        UserModel otherUserModel = task.getResult().toObject(UserModel.class);

                        FirebaseUtil.getOtherProfilePicStorageRef(otherUserModel.getUserId())
                                .getDownloadUrl()
                                .addOnCompleteListener(t -> {
                                    if (t.isSuccessful()) {
                                        Uri uri = t.getResult();
                                        if (!isActivityDestroyed(context)) {
                                            AndroidUtil.setProfilePic(context, uri, holder.profilePic);
                                        }
                                    }
                                });

                        if (lastMessageSentByMe) {
                            if ("image".equals(model.getMessageType())) {
                                holder.lastMessageText.setText("(image)");
                            } else {
                                holder.lastMessageText.setText("You : " + model.getLastMessage());
                            }
                        } else {
                            if ("image".equals(model.getMessageType())) {
                                holder.lastMessageText.setText("(image)");
                            } else {
                                holder.lastMessageText.setText(model.getLastMessage());
                            }
                        }

                        if (!model.isMessageRead() && !lastMessageSentByMe){
                            holder.dot.setVisibility(View.VISIBLE);
                        }else{
                            holder.dot.setVisibility(View.GONE);
                        }

                        if (otherUserModel.isHasBlueTick()) {
                            holder.blueTick.setVisibility(View.VISIBLE);
                        }

                        holder.usernameText.setText(otherUserModel.getUsername());
                        if (otherUserModel.getUserId().equals(FirebaseUtil.currentUserId())) {
                            holder.usernameText.setText(otherUserModel.getUsername() + " (You)");
                        }

                        if (lastMessageSentByMe)
                            holder.lastMessageText.setText("You : " + model.getLastMessage());
                        else
                            holder.lastMessageText.setText(model.getLastMessage());
                        holder.lastMessageTime.setText(FirebaseUtil.timestampToString(model.getLastMessageTimestamp()));

                        holder.usernameText.setOnClickListener(v -> {
                            Intent intent = new Intent(context, ChatActivity.class);
                            AndroidUtil.passUserModelAsIntent(intent, otherUserModel);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);

                        });

                        holder.lastMessageTime.setOnClickListener(v -> {
                            Intent intent = new Intent(context, ChatActivity.class);
                            AndroidUtil.passUserModelAsIntent(intent, otherUserModel);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        });

                        holder.blueTick.setOnClickListener(v -> {
                            Intent intent = new Intent(context, ChatActivity.class);
                            AndroidUtil.passUserModelAsIntent(intent, otherUserModel);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        });

                        holder.lastMessageText.setOnClickListener(v -> {
                            Intent intent = new Intent(context, ChatActivity.class);
                            AndroidUtil.passUserModelAsIntent(intent, otherUserModel);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        });

                        holder.profilePic.setOnClickListener(view -> {
                            Intent intent = new Intent(context, UserProfilePicActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            AndroidUtil.passUserModelAsIntent(intent, otherUserModel);
                            context.startActivity(intent);
                        });
                    }
                });
    }

    private boolean isActivityDestroyed(Context context) {
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            return activity.isDestroyed();
        }
        return false;
    }

    @NonNull
    @Override
    public ChatroomModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.sample_show_user,parent,false);
        return new ChatroomModelViewHolder(view);
    }

    static class ChatroomModelViewHolder extends RecyclerView.ViewHolder{
        TextView usernameText;
        EmojiTextView lastMessageText;
        TextView lastMessageTime, messageCount;
        ImageView profilePic, dot, blueTick;

        public ChatroomModelViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameText = itemView.findViewById(R.id.username_text);
            messageCount = itemView.findViewById(R.id.message_count);
            lastMessageText = itemView.findViewById(R.id.last_message_text);
            lastMessageTime = itemView.findViewById(R.id.last_message_time_text);
            profilePic = itemView.findViewById(R.id.profile_image);
            dot = itemView.findViewById(R.id.dot);
            blueTick = itemView.findViewById(R.id.bluetick);
        }

    }

}