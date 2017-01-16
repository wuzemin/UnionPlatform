package com.min.smalltalk.bean;

import java.io.Serializable;

/**
 * Created by Min on 2016/11/23.
 */

public class GetUserInfoByIdResponse implements Serializable {
    /**
     * id : 10YVscJI3
     * nickname : 阿明
     * portraitUri :
     */

    private String id;
    private String nickname;
    private String portraitUri;

    public void setId(String id) {
        this.id = id;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setPortraitUri(String portraitUri) {
        this.portraitUri = portraitUri;
    }

    public String getId() {
        return id;
    }

    public String getNickname() {
        return nickname;
    }

    public String getPortraitUri() {
        return portraitUri;
    }

    public GetUserInfoByIdResponse(String id, String nickname, String portraitUri) {
        this.id = id;
        this.nickname = nickname;
        this.portraitUri = portraitUri;
    }
}