package com.min.smalltalk;

import android.content.Context;
import android.net.Uri;

import com.min.smalltalk.Exception.HttpException;
import com.min.smalltalk.bean.GetUserInfoByIdResponse;
import com.min.smalltalk.listener.OnDataListener;
import com.min.smalltalk.network.async.AsyncTaskManager;
import com.min.smalltalk.server.SealAction;

import io.rong.imlib.model.UserInfo;

/**
 * Created by Min on 2017/1/15.
 * 用户信息提供者的异步请求类
 */

public class UserInfoEngine  implements OnDataListener {


    private static UserInfoEngine instance;
    private UserInfoListener mListener;
    private Context context;

    public static UserInfoEngine getInstance(Context context) {
        if (instance == null) {
            instance = new UserInfoEngine(context);
        }
        return instance;
    }

    private UserInfoEngine(Context context) {
        this.context = context;
    }


    private String userid;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    private static final int REQUSERINFO = 4234;

    public void startEngine(String userid) {
        setUserid(userid);
        AsyncTaskManager.getInstance(context).request(userid, REQUSERINFO, this);
    }

    @Override
    public Object doInBackground(int requestCode, String id) throws HttpException {
        return new SealAction(context).getUserInfoById(id);
    }

    @Override
    public void onSuccess(int requestCode, Object result) {
        if (result != null) {
            GetUserInfoByIdResponse res = (GetUserInfoByIdResponse) result;
            UserInfo userInfo = new UserInfo(res.getId(), res.getNickname(), Uri.parse(res.getPortraitUri()));
            if (mListener != null) {
                mListener.onResult(userInfo);
            }
        }
    }

    @Override
    public void onFailure(int requestCode, int state, Object result) {
        if (mListener != null) {
            mListener.onResult(null);
        }
    }

    public void setListener(UserInfoListener listener) {
        this.mListener = listener;
    }

    public interface UserInfoListener {
        void onResult(UserInfo info);
    }
}
