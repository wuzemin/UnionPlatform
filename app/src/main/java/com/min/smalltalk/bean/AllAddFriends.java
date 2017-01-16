package com.min.smalltalk.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Min on 2016/12/9.
 */

public class AllAddFriends implements Parcelable{
    private String userid;
    private String nickname;
    private String addFriendMessage;
    private String portraitUri;
    private int status;
    private String addtime;

    public AllAddFriends(String userid, String nickname,String portraitUri, String addFriendMessage, int status, String addtime) {
        this.userid = userid;
        this.nickname = nickname;
        this.portraitUri=portraitUri;
        this.addFriendMessage = addFriendMessage;
        this.status = status;
        this.addtime = addtime;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPortraitUri() {
        return portraitUri;
    }

    public void setPortraitUri(String portraitUri) {
        this.portraitUri = portraitUri;
    }

    public String getAddFriendMessage() {
        return addFriendMessage;
    }

    public void setAddFriendMessage(String addFriendMessage) {
        this.addFriendMessage = addFriendMessage;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getAddtime() {
        return addtime;
    }

    public void setAddtime(String addtime) {
        this.addtime = addtime;
    }

    public static Creator<AllAddFriends> getCREATOR() {
        return CREATOR;
    }

    protected AllAddFriends(Parcel in) {
        userid = in.readString();
        nickname = in.readString();
        portraitUri=in.readString();
        addFriendMessage = in.readString();
        status = in.readInt();
        addtime = in.readString();
    }

    public static final Creator<AllAddFriends> CREATOR = new Creator<AllAddFriends>() {
        @Override
        public AllAddFriends createFromParcel(Parcel in) {
            return new AllAddFriends(in);
        }

        @Override
        public AllAddFriends[] newArray(int size) {
            return new AllAddFriends[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(userid);
        parcel.writeString(nickname);
        parcel.writeString(portraitUri);
        parcel.writeString(addFriendMessage);
        parcel.writeInt(status);
        parcel.writeString(addtime);
    }
}
