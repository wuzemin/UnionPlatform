package com.min.smalltalk.bean;

/**
 * Created by Min on 2016/11/24.
 */

public class ConversationUser {
    private String userid;
    private String userName;
    private String portraitUri;

    public ConversationUser(String userid, String userName, String portraitUri) {
        this.userid = userid;
        this.userName = userName;
        this.portraitUri = portraitUri;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPortraitUri() {
        return portraitUri;
    }

    public void setPortraitUri(String portraitUri) {
        this.portraitUri = portraitUri;
    }
}
