package com.techverse.chatverse.model;

import com.google.firebase.Timestamp;

import java.sql.Time;

public class ChatMessageModel {
    private String message;
    private String senderId;
    private Timestamp timestamp;
    private String imageUrl;
    private String messageType;

    public ChatMessageModel() {
    }

    public ChatMessageModel(String message, String senderId, Timestamp timestamp,String messageType) {
        this.message = message;
        this.senderId = senderId;
        this.timestamp = timestamp;
        this.messageType = messageType;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public ChatMessageModel(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
