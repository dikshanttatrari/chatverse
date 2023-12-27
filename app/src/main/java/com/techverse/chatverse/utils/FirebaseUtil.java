package com.techverse.chatverse.utils;

import android.net.Uri;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FirebaseUtil {

    public static String currentUserId(){
        return FirebaseAuth.getInstance().getUid();
    }

    public static String currentUserName(){
        return FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
    }


    public static boolean isLoggedIn(){
        if(currentUserId()!=null){
            return true;
        }
        return false;
    }

    public static DocumentReference currentUserDetails(){
        return FirebaseFirestore.getInstance().collection("users").document(currentUserId());
    }

    public static CollectionReference allUserCollectionReference(){
        return FirebaseFirestore.getInstance().collection("users");
    }

    public static DocumentReference getChatroomReference(String chatroomId){
        return FirebaseFirestore.getInstance().collection("chatrooms").document(chatroomId);
    }

    public static CollectionReference getChatroomMessageReference(String chatroomId){
        return getChatroomReference(chatroomId).collection("chats");
    }

    public static String getChatroomId(String userId1,String userId2){
        if(userId1.hashCode()<userId2.hashCode()){
            return userId1+"_"+userId2;
        }else{
            return userId2+"_"+userId1;
        }
    }

    public static CollectionReference allChatroomCollectionReference(){
        return FirebaseFirestore.getInstance().collection("chatrooms");
    }

    public static DocumentReference getOtherUserFromChatroom(List<String> userIds){
        if(userIds.get(0).equals(FirebaseUtil.currentUserId())){
            return allUserCollectionReference().document(userIds.get(1));
        }else{
            return allUserCollectionReference().document(userIds.get(0));
        }
    }

    public static String timestampToStringFormatted(Timestamp timestamp){
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        return sdf.format(timestamp.toDate());
    }
    public static long getCurrentTimestamp() {
        return System.currentTimeMillis();
    }

    public static boolean isMessageSeen(Timestamp messageTimestamp) {
        long currentTimestamp = getCurrentTimestamp();
        long messageTimeMillis = messageTimestamp.toDate().getTime();
        return messageTimeMillis <= currentTimestamp;
    }


    public static String timestampToString(Timestamp timestamp) {
        long currentTime = System.currentTimeMillis();
        long timestampMillis = timestamp.toDate().getTime();
        long differenceMillis = currentTime - timestampMillis;

        if (differenceMillis < 24 * 60 * 60 * 1000) {
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            return sdf.format(timestamp.toDate());
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MMM/yy", Locale.getDefault());
            return sdf.format(timestamp.toDate());
        }
    }

    public static StorageReference getCurrentProfilePicStorageRef(){
        return FirebaseStorage.getInstance().getReference().child("profile_pic")
                .child(FirebaseUtil.currentUserId());
    }

    public static StorageReference getOtherProfilePicStorageRef(String otherUserId){
        return FirebaseStorage.getInstance().getReference().child("profile_pic")
                .child(otherUserId);
    }
}
