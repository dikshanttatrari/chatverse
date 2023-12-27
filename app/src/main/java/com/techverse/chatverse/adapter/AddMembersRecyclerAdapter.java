package com.techverse.chatverse.adapter;

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

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.techverse.chatverse.ChatActivity;
import com.techverse.chatverse.R;
import com.techverse.chatverse.model.ChatroomModel;
import com.techverse.chatverse.model.UserModel;
import com.techverse.chatverse.utils.AndroidUtil;
import com.techverse.chatverse.utils.FirebaseUtil;

import java.util.ArrayList;
import java.util.List;

public class AddMembersRecyclerAdapter extends FirestoreRecyclerAdapter<ChatroomModel, AddMembersRecyclerAdapter.ChatroomModelViewHolder> {

    Context context;
    private List<ChatroomModel> selectedUsers = new ArrayList<>();

    public AddMembersRecyclerAdapter(@NonNull FirestoreRecyclerOptions<ChatroomModel> options,Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatroomModelViewHolder holder, int position, @NonNull ChatroomModel model) {
        FirebaseUtil.getOtherUserFromChatroom(model.getUserIds())
                .get().addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        UserModel otherUserModel = task.getResult().toObject(UserModel.class);
                        FirebaseUtil.getOtherProfilePicStorageRef(otherUserModel.getUserId()).getDownloadUrl()
                                .addOnCompleteListener(t -> {
                                    if (t.isSuccessful()){
                                        Uri uri = t.getResult();
                                        AndroidUtil.setProfilePic(context,uri,holder.profilePic);
                                    }
                                });



                        holder.usernameText.setText(otherUserModel.getUsername());
                        if (otherUserModel.getUserId().equals(FirebaseUtil.currentUserId())){
                            holder.usernameText.setText(otherUserModel.getUsername()+" (You)");
                        }

                    }
                });
    }


    @NonNull
    @Override
    public ChatroomModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.show_user,parent,false);
        return new ChatroomModelViewHolder(view);
    }

    public List<ChatroomModel> getSelectedUsers() {
        return selectedUsers;
    }

    static class ChatroomModelViewHolder extends RecyclerView.ViewHolder{
        TextView usernameText;
        ImageView profilePic, selected;

        public ChatroomModelViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameText = itemView.findViewById(R.id.username_text);
            profilePic = itemView.findViewById(R.id.profile_image);
            selected = itemView.findViewById(R.id.selected);
        }
    }
}