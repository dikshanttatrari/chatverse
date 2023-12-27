package com.techverse.chatverse.model;

import com.google.firebase.Timestamp;

public class UserModel {
    private String phone;
    private String username;
    private Timestamp createdTimestamp;
    private String userId;
    private String accountUserId;
    private String fcmToken;
    private String about;
    private boolean hasBlueTick;

    public UserModel() {
    }

    public UserModel(String phone, String username, Timestamp createdTimestamp,String userId) {
        this.phone = phone;
        this.username = username;
        this.createdTimestamp = createdTimestamp;
        this.userId = userId;
    }

    public UserModel(String about, String accountUserId) {
        this.about = about;
        this.accountUserId = accountUserId;
    }


    public String getAccountUserId() {
        return accountUserId;
    }

    public void setAccountUserId(String accountUserId) {
        this.accountUserId = accountUserId;
    }


    public UserModel(boolean hasBlueTick) {
        this.hasBlueTick = hasBlueTick;
    }

    public boolean isHasBlueTick() {
        return hasBlueTick;
    }

    public void setHasBlueTick(boolean hasBlueTick) {
        this.hasBlueTick = hasBlueTick;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Timestamp getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(Timestamp createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }
}
