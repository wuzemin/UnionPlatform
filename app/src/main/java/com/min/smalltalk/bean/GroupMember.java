package com.min.smalltalk.bean;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * Created by Min on 2016/11/25.
 */
public class GroupMember implements Parcelable{
    /** Not-null value. */
    private String groupId;
    /** Not-null value. */
    private String userId;
    private String userName;
    private String userPortraitUri;
    private String displayName;
    private String nameSpelling;
    private String displayNameSpelling;
    private String groupName;
    private String groupNameSpelling;
    private String groupPortraitUri;
    private String phone;
    private String email;
    private String role;

    public GroupMember(String userId, String userName, String userPortraitUri) {
        this.userId = userId;
        this.userName = userName;
        this.userPortraitUri = userPortraitUri;
    }

    public GroupMember(String userId, String userName, String userPortraitUri, String displayName) {
        this.userId = userId;
        this.userName = userName;
        this.userPortraitUri = userPortraitUri;
        this.displayName = displayName;
    }

    public GroupMember(String userId, String userName, String userPortraitUri, String phone, String email) {
        this.userId = userId;
        this.userName = userName;
        this.userPortraitUri = userPortraitUri;
        this.phone = phone;
        this.email = email;
    }

    protected GroupMember(Parcel in) {
        groupId = in.readString();
        userId = in.readString();
        userName = in.readString();
        userPortraitUri = in.readString();
        displayName = in.readString();
        nameSpelling = in.readString();
        displayNameSpelling = in.readString();
        groupName = in.readString();
        groupNameSpelling = in.readString();
        groupPortraitUri = in.readString();
        phone = in.readString();
        email = in.readString();
        role = in.readString();
    }

    public GroupMember(String groupId, String userId, String userName, String userPortraitUri, String displayName,
            String nameSpelling, String displayNameSpelling, String groupName, String groupNameSpelling,
            String groupPortraitUri, String phone, String email, String role) {
        this.groupId = groupId;
        this.userId = userId;
        this.userName = userName;
        this.userPortraitUri = userPortraitUri;
        this.displayName = displayName;
        this.nameSpelling = nameSpelling;
        this.displayNameSpelling = displayNameSpelling;
        this.groupName = groupName;
        this.groupNameSpelling = groupNameSpelling;
        this.groupPortraitUri = groupPortraitUri;
        this.phone = phone;
        this.email = email;
        this.role = role;
    }

    public GroupMember() {
    }

    public static final Creator<GroupMember> CREATOR = new Creator<GroupMember>() {
        @Override
        public GroupMember createFromParcel(Parcel in) {
            return new GroupMember(in);
        }

        @Override
        public GroupMember[] newArray(int size) {
            return new GroupMember[size];
        }
    };

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPortraitUri() {
        return userPortraitUri;
    }

    public void setUserPortraitUri(String userPortraitUri) {
        this.userPortraitUri = userPortraitUri;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getNameSpelling() {
        return nameSpelling;
    }

    public void setNameSpelling(String nameSpelling) {
        this.nameSpelling = nameSpelling;
    }

    public String getDisplayNameSpelling() {
        return displayNameSpelling;
    }

    public void setDisplayNameSpelling(String displayNameSpelling) {
        this.displayNameSpelling = displayNameSpelling;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupNameSpelling() {
        return groupNameSpelling;
    }

    public void setGroupNameSpelling(String groupNameSpelling) {
        this.groupNameSpelling = groupNameSpelling;
    }

    public String getGroupPortraitUri() {
        return groupPortraitUri;
    }

    public void setGroupPortraitUri(String groupPortraitUri) {
        this.groupPortraitUri = groupPortraitUri;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(groupId);
        parcel.writeString(userId);
        parcel.writeString(userName);
        parcel.writeString(userPortraitUri);
        parcel.writeString(displayName);
        parcel.writeString(nameSpelling);
        parcel.writeString(displayNameSpelling);
        parcel.writeString(groupName);
        parcel.writeString(groupNameSpelling);
        parcel.writeString(groupPortraitUri);
        parcel.writeString(phone);
        parcel.writeString(email);
        parcel.writeString(role);
    }
}
