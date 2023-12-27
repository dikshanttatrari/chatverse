package com.techverse.chatverse.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.techverse.chatverse.model.UserModel;

public class AndroidUtil {

    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public static void passUserModelAsIntent(Intent intent, UserModel model) {
        intent.putExtra("accountUserId", model.getAccountUserId());
        intent.putExtra("hasBlueTick", model.isHasBlueTick());
        intent.putExtra("username", model.getUsername());
        intent.putExtra("about",model.getAbout());
        intent.putExtra("phone", model.getPhone());
        intent.putExtra("userId", model.getUserId());
        intent.putExtra("fcmToken", model.getFcmToken());
    }

    public static UserModel getUserModelFromIntent(Intent intent) {
        UserModel userModel = new UserModel();
        userModel.setAccountUserId(intent.getStringExtra("accountUserId"));
        userModel.setHasBlueTick(intent.getBooleanExtra("hasBlueTick", false));
        userModel.setUsername(intent.getStringExtra("username"));
        userModel.setPhone(intent.getStringExtra("phone"));
        userModel.setUserId(intent.getStringExtra("userId"));
        userModel.setFcmToken(intent.getStringExtra("fcmToken"));
        userModel.setAbout(intent.getStringExtra("about"));
        return userModel;
    }

    public static void setProfilePic(Context context, Uri imageUri, ImageView imageView) {
        Glide.with(context)
                .asDrawable()
                .load(imageUri)
                .apply(RequestOptions.circleCropTransform())
                .into(imageView);
    }

    public static void showProfilePic(Context context, Uri imageUri, ImageView imageView) {
        Glide.with(context).load(imageUri).into(imageView);
    }

}
