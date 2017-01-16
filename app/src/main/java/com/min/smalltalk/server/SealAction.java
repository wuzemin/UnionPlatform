package com.min.smalltalk.server;

import android.content.Context;
import android.content.SharedPreferences;

import com.min.smalltalk.Exception.HttpException;
import com.min.smalltalk.bean.GetUserInfoByIdResponse;
import com.min.smalltalk.constant.Const;

/**
 * Created by Min on 2017/1/15.
 */

public class SealAction extends BaseAction{

    private SharedPreferences sp;
    /**
     * 构造方法
     *
     * @param context 上下文
     */
    public SealAction(Context context) {
        super(context);
        sp=context.getSharedPreferences("config",Context.MODE_PRIVATE);
    }


    /**
     * 根据 id 去服务端查询用户信息
     *
     * @param userid 用户ID
     * @throws HttpException
     */
    public GetUserInfoByIdResponse getUserInfoById(String userid) throws HttpException {
        String Userid=sp.getString(Const.LOGIN_ID,"");
        String nickname=sp.getString(Const.LOGIN_NICKNAME,"");
        String portraitUri=sp.getString(Const.LOGIN_PORTRAIT,"");
        GetUserInfoByIdResponse response = new GetUserInfoByIdResponse(Userid,nickname,portraitUri);
        return response;
    }
}
