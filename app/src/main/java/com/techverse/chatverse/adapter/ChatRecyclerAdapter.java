package com.techverse.chatverse.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.auth.User;
import com.techverse.chatverse.R;
import com.techverse.chatverse.ViewImageActivity;
import com.techverse.chatverse.model.ChatMessageModel;
import com.techverse.chatverse.model.UserModel;
import com.techverse.chatverse.utils.AndroidUtil;
import com.techverse.chatverse.utils.FirebaseUtil;
import com.vanniktech.emoji.EmojiTextView;

public class ChatRecyclerAdapter extends FirestoreRecyclerAdapter<ChatMessageModel, ChatRecyclerAdapter.ChatModelViewHolder> {

    Context context;

    public ChatRecyclerAdapter(@NonNull FirestoreRecyclerOptions<ChatMessageModel> options,Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatModelViewHolder holder, int position, @NonNull ChatMessageModel model) {


        if (model != null && model.getSenderId() != null) {
            // Check if it's an image message and the senderId matches the current user
            // For the sender (current user)
            if ("image".equals(model.getMessageType()) && model.getSenderId().equals(FirebaseUtil.currentUserId())) {
                String imageUrl = model.getImageUrl();
                UserModel userModel = new UserModel();
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    // Load and display the image using Glide
                    Glide.with(context).load(imageUrl).into(holder.rightImage);
                }
                holder.rightImage.setVisibility(View.VISIBLE);
                holder.rightChatTextview.setVisibility(View.GONE);
                holder.rightImage.setOnClickListener(view -> {
                    Intent rightImageIntent = new Intent(context, ViewImageActivity.class);
                    rightImageIntent.putExtra("imageUrl", model.getImageUrl());
                    rightImageIntent.putExtra("userid", model.getSenderId());
                    rightImageIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(rightImageIntent);
                });
            } else {
                // Display the text message
                holder.rightImage.setVisibility(View.GONE);
                holder.rightChatTextview.setVisibility(View.VISIBLE);
                holder.rightChatTextview.setText(model.getMessage());
            }

            if ("image".equals(model.getMessageType()) && !model.getSenderId().equals(FirebaseUtil.currentUserId())) {
                String imageUrl = model.getImageUrl();
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    // Load and display the image using Glide
                    Glide.with(context).load(imageUrl).into(holder.leftImage);
                }
                holder.leftImage.setVisibility(View.VISIBLE);
                holder.leftChatTextview.setVisibility(View.GONE);
                holder.leftImage.setOnClickListener(view -> {
                    Intent leftImageIntent = new Intent(context, ViewImageActivity.class);
                    leftImageIntent.putExtra("userid", model.getSenderId());
                    leftImageIntent.putExtra("imageUrl", model.getImageUrl());
                    leftImageIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(leftImageIntent);
                });

            } else {
                // Display the text message
                holder.leftImage.setVisibility(View.GONE);
                holder.leftChatTextview.setVisibility(View.VISIBLE);
                holder.leftChatTextview.setText(model.getMessage());
            }


            // Check if the senderId matches the current user
            if (model.getSenderId().equals(FirebaseUtil.currentUserId())) {
                holder.leftChatLayout.setVisibility(View.GONE);
                if (holder.rightChatTextview.length() > 0) {
                    holder.rightChatTextview.setVisibility(View.VISIBLE);
                }
                if (model.getMessage() != null) {
                    holder.rightChatTextview.setText(model.getMessage());
                }
                holder.rightChatTime.setText(FirebaseUtil.timestampToStringFormatted(model.getTimestamp()));
            } else {
                holder.rightChatLayout.setVisibility(View.GONE);
                if (model.getMessage() != null) {
                    holder.leftChatTextview.setText(model.getMessage());
                }
                if (model.getMessage() != null) {
                    holder.leftChatTextview.setText(model.getMessage());
                }
                holder.leftChatTime.setText(FirebaseUtil.timestampToStringFormatted(model.getTimestamp()));
            }
        }
    }

    @NonNull
    @Override
    public ChatModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_message_recycler_row,parent,false);
        return new ChatModelViewHolder(view);
    }

    static class ChatModelViewHolder extends RecyclerView.ViewHolder{

        RelativeLayout leftChatLayout, rightChatLayout;
        TextView leftChatTime, rightChatTime, dayStampText;
        EmojiTextView leftChatTextview, rightChatTextview;
        ImageView leftImage, rightImage;

        public ChatModelViewHolder(@NonNull View itemView) {
            super(itemView);

            leftChatLayout = itemView.findViewById(R.id.left_chat_layout);
            rightChatLayout = itemView.findViewById(R.id.right_chat_layout);
            leftChatTextview = itemView.findViewById(R.id.left_chat_textview);
            rightChatTextview = itemView.findViewById(R.id.right_chat_textview);
            leftChatTime = itemView.findViewById(R.id.left_chat_time);
            rightChatTime = itemView.findViewById(R.id.right_chat_time);
            dayStampText = itemView.findViewById(R.id.day_stamp_text);
            leftImage = itemView.findViewById(R.id.left_image_view);
            rightImage = itemView.findViewById(R.id.right_image_view);
           
        }
    }
}