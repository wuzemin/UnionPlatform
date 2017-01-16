package com.min.smalltalk.bean;

/**
 * Created by Min on 2016/12/1.
 */

public class GroupInfo {
    private String groupId;
    private String groupName;
    private String groupPortraitUri;
    private int groupMemberConnt;
    private String groupCreatorId;

    public GroupInfo() {
    }

    public GroupInfo(String groupId, String groupName, String groupPortraitUri, int groupMemberConnt, String groupCreatorId) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.groupPortraitUri = groupPortraitUri;
        this.groupMemberConnt = groupMemberConnt;
        this.groupCreatorId = groupCreatorId;
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

    public String getGroupPortraitUri() {
        return groupPortraitUri;
    }

    public void setGroupPortraitUri(String groupPortraitUri) {
        this.groupPortraitUri = groupPortraitUri;
    }

    public int getGroupMemberConnt() {
        return groupMemberConnt;
    }

    public void setGroupMemberConnt(int groupMemberConnt) {
        this.groupMemberConnt = groupMemberConnt;
    }

    public String getGroupCreatorId() {
        return groupCreatorId;
    }

    public void setGroupCreatorId(String groupCreatorId) {
        this.groupCreatorId = groupCreatorId;
    }
}
