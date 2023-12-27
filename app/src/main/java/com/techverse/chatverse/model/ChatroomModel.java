package com.techverse.chatverse.model;

import com.google.firebase.Timestamp;

import java.util.List;

public class ChatroomModel {
    String chatroomId;
    List<String> userIds;
    Timestamp lastMessageTimestamp;
    String lastMessageSenderid;
    String lastMessage;
    boolean isMessageRead;
    private String lastMessageType;

    public ChatroomModel() {
    }

    public ChatroomModel(String chatroomId, List<String> userIds, Timestamp lastMessageTimestamp, String lastMessageSenderid) {
        this.chatroomId = chatroomId;
        this.userIds = userIds;
        this.lastMessageTimestamp = lastMessageTimestamp;
        this.lastMessageSenderid = lastMessageSenderid;
    }



    public boolean isMessageRead() {
        return isMessageRead;
    }

    public void setMessageRead(boolean messageRead) {
        isMessageRead = messageRead;
    }

    public String getChatroomId() {
        return chatroomId;
    }

    public void setChatroomId(String chatroomId) {
        this.chatroomId = chatroomId;
    }

    public List<String> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<String> userIds) {
        this.userIds = userIds;
    }

    public Timestamp getLastMessageTimestamp() {
        return lastMessageTimestamp;
    }

    public void setLastMessageTimestamp(Timestamp lastMessageTimestamp) {
        this.lastMessageTimestamp = lastMessageTimestamp;
    }

    public String getLastMessageSenderid() {
        return lastMessageSenderid;
    }

    public void setLastMessageSenderid(String lastMessageSenderid) {
        this.lastMessageSenderid = lastMessageSenderid;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getMessageType() {
        return lastMessageType;
    }

    public void setMessageType(String lastMessageType) {
        this.lastMessageType = lastMessageType;
    }
}
