package com.min.smalltalk.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Min on 2017/1/16.
 */

public class Recommend implements Parcelable {
    private String userid;
    private String full_name;
    private int check_claim;

    protected Recommend(Parcel in) {
        userid = in.readString();
        full_name = in.readString();
        check_claim = in.readInt();
    }

    public static final Creator<Recommend> CREATOR = new Creator<Recommend>() {
        @Override
        public Recommend createFromParcel(Parcel in) {
            return new Recommend(in);
        }

        @Override
        public Recommend[] newArray(int size) {
            return new Recommend[size];
        }
    };

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
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
        parcel.writeString(userid);
        parcel.writeString(full_name);
        parcel.writeInt(check_claim);
    }
}