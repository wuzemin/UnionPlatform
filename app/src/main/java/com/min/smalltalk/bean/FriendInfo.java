package com.min.smalltalk.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

/**
 * Created by Min on 2016/11/26.
 */
public class FriendInfo implements Parcelable{
    private String myId;
    private String userId;
    private String name;
    private String portraitUri;
    private String displayName;
    private String status;
    private Long timestamp;
    private String letters;
    private String phone;
    private String email;

    protected FriendInfo(Parcel in) {
        myId=in.readString();
        userId = in.readString();
        name = in.readString();
        portraitUri = in.readString();
        displayName = in.readString();
        status = in.readString();
        letters = in.readString();
        phone=in.readString();
        email=in.readString();
    }



    public static final Creator<FriendInfo> CREATOR = new Creator<FriendInfo>() {
        @Override
        public FriendInfo createFromParcel(Parcel in) {
            return new FriendInfo(in);
        }

        @Override
        public FriendInfo[] newArray(int size) {
            return new FriendInfo[size];
        }
    };

    public String getLetters() {
        return letters;
    }

    public void setLetters(String letters) {
        this.letters = letters;
    }

    public FriendInfo() {
    }

    public FriendInfo(String userId, String name, String portraitUri, String displayName, String phone, String email) {
        this.userId = userId;
        this.name = name;
        this.portraitUri = portraitUri;
        this.displayName = displayName;
        this.phone = phone;
        this.email = email;
    }

    public FriendInfo(String userId) {
        this.userId = userId;
    }

    public FriendInfo(String userId, String name, String portraitUri) {
        this.userId = userId;
        this.name = name;
        this.portraitUri = portraitUri;
    }

    public FriendInfo(String userId, String name, String portraitUri, String displayName) {
        this.userId = userId;
        this.name = name;
        this.portraitUri = portraitUri;
        this.displayName = displayName;
    }



    public FriendInfo(String userId, String name, String portraitUri, String displayName, String letters) {
        this.userId = userId;
        this.name = name;
        this.portraitUri = portraitUri;
        this.displayName = displayName;
        this.letters = letters;
    }

    public FriendInfo(String userId, String name, String portraitUri, String displayName, String status, Long timestamp) {
        this.userId = userId;
        this.name = name;
        this.portraitUri = portraitUri;
        this.displayName = displayName;
        this.status = status;
        this.timestamp = timestamp;
    }

    public FriendInfo(String myId, String userId, String name, String portraitUri, String displayName, String status,
            Long timestamp, String letters, String phone, String email) {
        this.myId = myId;
        this.userId = userId;
        this.name = name;
        this.portraitUri = portraitUri;
        this.displayName = displayName;
        this.status = status;
        this.timestamp = timestamp;
        this.letters = letters;
        this.phone = phone;
        this.email = email;
    }

    public String getMyId() {
        return myId;
    }

    public void setMyId(String myId) {
        this.myId = myId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPortraitUri() {
        return portraitUri;
    }

    public void setPortraitUri(String portraitUri) {
        this.portraitUri = portraitUri;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
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

    public boolean isExitsDisplayName() {
        if (TextUtils.isEmpty(getDisplayName())) {
            return false;
        }
        return true;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(myId);
        parcel.writeString(userId);
        parcel.writeString(name);
        parcel.writeString(portraitUri);
        parcel.writeString(displayName);
        parcel.writeString(status);
        parcel.writeString(letters);
        parcel.writeString(phone);
        parcel.writeString(email);
    }
}
