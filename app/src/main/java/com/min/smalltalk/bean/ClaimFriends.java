package com.min.smalltalk.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Min on 2017/1/5.
 */

public class ClaimFriends implements Parcelable {
    private String id;
    private String full_name;
    private int check_claim;

    protected ClaimFriends(Parcel in) {
        id = in.readString();
        full_name = in.readString();
        check_claim = in.readInt();
    }

    public static final Creator<ClaimFriends> CREATOR = new Creator<ClaimFriends>() {
        @Override
        public ClaimFriends createFromParcel(Parcel in) {
            return new ClaimFriends(in);
        }

        @Override
        public ClaimFriends[] newArray(int size) {
            return new ClaimFriends[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public int getCheck_claim() {
        return check_claim;
    }

    public void setCheck_claim(int check_claim) {
        this.check_claim = check_claim;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(full_name);
        parcel.writeInt(check_claim);
    }
}
