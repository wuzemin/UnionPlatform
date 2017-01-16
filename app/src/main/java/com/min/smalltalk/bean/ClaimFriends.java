package com.min.smalltalk.bean;

import android.os.Parcel;
import android.os.Parcelable;

import static io.rong.imlib.statistics.UserData.phone;

/**
 * Created by Min on 2017/1/5.
 */

public class ClaimFriends implements Parcelable {
    private String tu_id;
    private String nickname;
    private String mobile;
    private String avatar_image;
    private String problem_title;
    private int check_claim;
    private String gen_time;

    public String getTu_id() {
        return tu_id;
    }

    public void setTu_id(String tu_id) {
        this.tu_id = tu_id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAvatar_image() {
        return avatar_image;
    }

    public void setAvatar_image(String avatar_image) {
        this.avatar_image = avatar_image;
    }

    public String getProblem_title() {
        return problem_title;
    }

    public void setProblem_title(String problem_title) {
        this.problem_title = problem_title;
    }

    public int getCheck_claim() {
        return check_claim;
    }

    public void setCheck_claim(int check_claim) {
        this.check_claim = check_claim;
    }

    public String getGen_time() {
        return gen_time;
    }

    public void setGen_time(String gen_time) {
        this.gen_time = gen_time;
    }

    protected ClaimFriends(Parcel in) {
        tu_id = in.readString();
        nickname = in.readString();
        mobile = in.readString();
        avatar_image = in.readString();
        problem_title = in.readString();
        check_claim = in.readInt();
        gen_time = in.readString();
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(tu_id);
        parcel.writeString(nickname);
        parcel.writeString(mobile);
        parcel.writeString(avatar_image);
        parcel.writeString(problem_title);
        parcel.writeInt(check_claim);
        parcel.writeString(gen_time);
    }
}
