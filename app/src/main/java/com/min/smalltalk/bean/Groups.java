package com.min.smalltalk.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Min on 2016/11/30.
 */
public class Groups implements Parcelable{
    private String userId;
    private String groupId;
    private String groupName;
    private String groupPortraitUri;
    private String displayName;
    private String role;  //角色---群主或成员
    private String bulletin;  //公告
    private String timestamp;
    private String nameSpelling;

    public Groups() {
    }

    public Groups(String groupId, String groupName, String groupPortraitUri) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.groupPortraitUri = groupPortraitUri;
    }
    public Groups(String groupId, String groupName, String groupPortraitUri,String role) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.groupPortraitUri = groupPortraitUri;
        this.role=role;
    }

    public Groups(String userId, String groupId, String groupName, String groupPortraitUri,
            String displayName, String role, String bulletin, String timestamp,
            String nameSpelling) {
        this.userId = userId;
        this.groupId = groupId;
        this.groupName = groupName;
        this.groupPortraitUri = groupPortraitUri;
        this.displayName = displayName;
        this.role = role;
        this.bulletin = bulletin;
        this.timestamp = timestamp;
        this.nameSpelling = nameSpelling;
    }

    public Groups(Parcel in) {
        userId=in.readString();
        groupId = in.readString();
        groupName = in.readString();
        groupPortraitUri = in.readString();
        displayName = in.readString();
        role = in.readString();
        bulletin = in.readString();
        timestamp = in.readString();
        nameSpelling = in.readString();
    }

    public static final Creator<Groups> CREATOR = new Creator<Groups>() {
        @Override
        public Groups createFromParcel(Parcel in) {
            return new Groups(in);
        }

        @Override
        public Groups[] newArray(int size) {
            return new Groups[size];
        }
    };

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getBulletin() {
        return bulletin;
    }

    public void setBulletin(String bulletin) {
        this.bulletin = bulletin;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getNameSpelling() {
        return nameSpelling;
    }

    public void setNameSpelling(String nameSpelling) {
        this.nameSpelling = nameSpelling;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(userId);
        parcel.writeString(groupId);
        parcel.writeString(groupName);
        parcel.writeString(groupPortraitUri);
        parcel.writeString(displayName);
        parcel.writeString(role);
        parcel.writeString(bulletin);
        parcel.writeString(timestamp);
        parcel.writeString(nameSpelling);
    }
}
