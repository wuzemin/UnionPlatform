package com.min.smalltalk.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Min on 2016/12/16.
 */

public class UserId implements Parcelable{
    private String userId;

    public UserId(String userId) {
        this.userId = userId;
    }

    protected UserId(Parcel in) {
        userId = in.readString();
    }

    public static final Creator<UserId> CREATOR = new Creator<UserId>() {
        @Override
        public UserId createFromParcel(Parcel in) {
            return new UserId(in);
        }

        @Override
        public UserId[] newArray(int size) {
            return new UserId[size];
        }
    };

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(userId);
    }
}
