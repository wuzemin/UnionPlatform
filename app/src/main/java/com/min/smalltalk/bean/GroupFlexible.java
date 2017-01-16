package com.min.smalltalk.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by Min on 2016/12/6.
 */

public class GroupFlexible implements Parcelable{
    private String actives_id;
    private String actives_title;
    private String actives_image;
    private String actives_limit;
    private String actives_start;
    private String actives_end;
    private String actives_address;
    private String actives_content;
    private List<GroupMember> flexibleList;

    public String getActives_id() {
        return actives_id;
    }

    public void setActives_id(String actives_id) {
        this.actives_id = actives_id;
    }

    public String getActives_title() {
        return actives_title;
    }

    public void setActives_title(String actives_title) {
        this.actives_title = actives_title;
    }

    public String getActives_image() {
        return actives_image;
    }

    public void setActives_image(String actives_image) {
        this.actives_image = actives_image;
    }

    public String getActives_limit() {
        return actives_limit;
    }

    public void setActives_limit(String actives_limit) {
        this.actives_limit = actives_limit;
    }

    public String getActives_start() {
        return actives_start;
    }

    public void setActives_start(String actives_start) {
        this.actives_start = actives_start;
    }

    public String getActives_end() {
        return actives_end;
    }

    public void setActives_end(String actives_end) {
        this.actives_end = actives_end;
    }

    public String getActives_address() {
        return actives_address;
    }

    public void setActives_address(String actives_address) {
        this.actives_address = actives_address;
    }

    public String getActives_content() {
        return actives_content;
    }

    public void setActives_content(String actives_content) {
        this.actives_content = actives_content;
    }

    public List<GroupMember> getFlexibleList() {
        return flexibleList;
    }

    public void setFlexibleList(List<GroupMember> flexibleList) {
        this.flexibleList = flexibleList;
    }

    public static Creator<GroupFlexible> getCREATOR() {
        return CREATOR;
    }

    public GroupFlexible(String actives_id, String actives_title, String actives_image, String actives_start, String actives_end, String actives_address, String actives_content) {
        this.actives_id = actives_id;
        this.actives_title = actives_title;
        this.actives_image = actives_image;
        this.actives_start = actives_start;
        this.actives_end = actives_end;
        this.actives_address = actives_address;
        this.actives_content = actives_content;
    }

    public GroupFlexible(String actives_id, String actives_title, String actives_image, String actives_limit, String actives_start, String actives_end, String actives_address, String actives_content) {
        this.actives_id = actives_id;
        this.actives_title = actives_title;
        this.actives_image = actives_image;
        this.actives_limit = actives_limit;
        this.actives_start = actives_start;
        this.actives_end = actives_end;
        this.actives_address = actives_address;
        this.actives_content = actives_content;
    }

    public GroupFlexible(String actives_id, String actives_title, String actives_image, String actives_limit, String actives_start, String actives_end, String actives_address, String actives_content, List<GroupMember> flexibleList) {
        this.actives_id = actives_id;
        this.actives_title = actives_title;
        this.actives_image = actives_image;
        this.actives_limit = actives_limit;
        this.actives_start = actives_start;
        this.actives_end = actives_end;
        this.actives_address = actives_address;
        this.actives_content = actives_content;
        this.flexibleList = flexibleList;
    }

    protected GroupFlexible(Parcel in) {
        actives_id = in.readString();
        actives_title = in.readString();
        actives_image = in.readString();
        actives_limit = in.readString();
        actives_start = in.readString();
        actives_end = in.readString();
        actives_address = in.readString();
        actives_content = in.readString();
        flexibleList = in.createTypedArrayList(GroupMember.CREATOR);
    }

    public static final Creator<GroupFlexible> CREATOR = new Creator<GroupFlexible>() {
        @Override
        public GroupFlexible createFromParcel(Parcel in) {
            return new GroupFlexible(in);
        }

        @Override
        public GroupFlexible[] newArray(int size) {
            return new GroupFlexible[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(actives_id);
        parcel.writeString(actives_title);
        parcel.writeString(actives_image);
        parcel.writeString(actives_limit);
        parcel.writeString(actives_start);
        parcel.writeString(actives_end);
        parcel.writeString(actives_address);
        parcel.writeString(actives_content);
        parcel.writeTypedList(flexibleList);
    }
}
