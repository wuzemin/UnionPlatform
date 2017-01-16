package com.min.smalltalk.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;

import com.min.mylibrary.util.CommonUtils;
import com.min.mylibrary.util.T;
import com.min.smalltalk.MainActivity;
import com.min.smalltalk.R;
import com.min.smalltalk.UserInfoManager;
import com.min.smalltalk.base.BaseActivity;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

/**
 * 加载页
 */
public class LogoActivity extends BaseActivity {
    private SharedPreferences sharedPreferences;
    private Handler handler=new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo);
        sharedPreferences=getSharedPreferences("config",MODE_PRIVATE);
        if(!CommonUtils.isNetConnect(mContext)){
            T.showShort(mContext,"网络不可用");
            toLogin();
            return;
        }
        String cacheToken=sharedPreferences.getString("loginToken","");
        if(!TextUtils.isEmpty(cacheToken)){
            if(RongIM.getInstance().getCurrentConnectionStatus()== RongIMClient.ConnectionStatusListener.ConnectionStatus.CONNECTED){
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        toMain();
                    }
                },800);
            }else {
                RongIM.connect(cacheToken, new RongIMClient.ConnectCallback() {
                    @Override
                    public void onTokenIncorrect() {
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                toLogin();
                            }
                        },300);
                    }

                    @Override
                    public void onSuccess(String s) {
                        getSharedPreferences("config",MODE_PRIVATE).edit().putString("loginid",s).apply();
                        UserInfoManager.getInstance().setUserInfoEngineListener();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                toMain();
                            }
                        },300);
                    }

                    @Override
                    public void onError(final RongIMClient.ErrorCode errorCode) {
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                T.showShort(mContext,"connect error value:"+errorCode.getValue());
                            }
                        },300);
                    }
                });
            }
        }else {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    toLogin();
                }
            },800);
        }
    }

    private void toLogin() {
        startActivity(new Intent(mContext,LoginActivity.class));
        finish();
    }
    private void toMain(){
        startActivity(new Intent(mContext,MainActivity.class));
        finish();
    }
}
