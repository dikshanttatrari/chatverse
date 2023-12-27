package com.techverse.chatverse.model;

import java.util.List;

public class GroupModel {
    private String groupId;
    private String groupName;
    private String groupProfile;
    private List<String> members;
    private List<String> memberIds;

    public GroupModel(String groupId, String groupName, String groupProfile, List<String> members) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.groupProfile = groupProfile;
        this.members = members;
    }

    public GroupModel(List<String> memberIds) {
        this.memberIds = memberIds;
    }

    public List<String> getMemberIds() {
        return memberIds;
    }

    public void setMemberIds(List<String> memberIds) {
        this.memberIds = memberIds;
    }

    public GroupModel() {

    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupProfile() {
        return groupProfile;
    }

    public void setGroupProfile(String groupProfile) {
        this.groupProfile = groupProfile;
    }

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }
}


