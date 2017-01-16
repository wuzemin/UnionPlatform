package com.min.smalltalk.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.min.mylibrary.util.T;
import com.min.mylibrary.widget.dialog.LoadingDialog;
import com.min.smalltalk.App;
import com.min.smalltalk.MainActivity;
import com.min.smalltalk.R;
import com.min.smalltalk.constant.Const;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

/**
 * 会话列表
 * push 重连，收到 push 消息的时候，做一下 connect 操作
 */
public class ConversationListActivity extends FragmentActivity {

    private static final String TAG=ConversationListActivity.class.getSimpleName();
    private LoadingDialog mDialog;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation_list);
        sp=getSharedPreferences("config",MODE_PRIVATE);
        mDialog=new LoadingDialog(this);

        initPush();
//        setActionBarTitle();
//        isReconnect();
    }
    public void initPush(){
        Intent intent=getIntent();
        if(intent.getData().getScheme().equals("rong") && intent.getData().getQueryParameter("push")!=null){
            //通过intent.getData().getQueryParameter("push") 为true，判断是否是push消息
            if(intent.getData().getQueryParameter("push").equals("true")){
                enterActivity();
            }
        }else {//通知过来
            //程序切到后台，收到消息后点击进入,会执行这里
            if(RongIM.getInstance().getCurrentConnectionStatus().equals(RongIMClient.ConnectionStatusListener.ConnectionStatus.CONNECTED)){
                enterActivity();
            }else{
                startActivity(new Intent(ConversationListActivity.this,MainActivity.class));
                finish();
            }
        }
    }
    /**
     * 收到 push 消息后，选择进入哪个 Activity
     * 如果程序缓存未被清理，进入 MainActivity
     * 程序缓存被清理，进入 LoginActivity，重新获取token
     * <p/>
     * 作用：由于在 manifest 中 intent-filter 是配置在 ConversationListActivity 下面，所以收到消息后点击notifacition 会跳转到 DemoActivity。
     * 以跳到 MainActivity 为例：
     * 在 ConversationListActivity 收到消息后，选择进入 MainActivity，这样就把 MainActivity 激活了，当你读完收到的消息点击 返回键 时，程序会退到
     * MainActivity 页面，而不是直接退回到 桌面。
     */
    private void enterActivity() {
        String token=sp.getString(Const.LOGIN_TOKEN,"");
        if(token.equals("default")){
            startActivity(new Intent(ConversationListActivity.this, LoginActivity.class));
            finish();
        }else {
            if(mDialog!=null && !mDialog.isShowing()){
                mDialog.show();
            }
            reconnect(token);
        }
    }
    /**
     * 重连
     *
     * @param token
     */
    private void reconnect(String token) {

        if (getApplicationInfo().packageName.equals(App.getCurProcessName(getApplicationContext()))) {

            RongIM.connect(token, new RongIMClient.ConnectCallback() {
                @Override
                public void onTokenIncorrect() {
                    T.showShort(ConversationListActivity.this,"----onTokenIncorrect----");
                }

                @Override
                public void onSuccess(String s) {
                    if(mDialog!=null)
                        mDialog.dismiss();
                    startActivity(new Intent(ConversationListActivity.this,MainActivity.class));
                    finish();
                }

                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {
                    T.showShort(ConversationListActivity.this,errorCode.getValue());
                }
            });
        }
    }


    /**
     * 设置 actionbar 事件
     */
    /*private void setActionBarTitle() {

        mTitle = (TextView) findViewById(R.id.txt1);
        mBack = (RelativeLayout) findViewById(R.id.back);

        mTitle.setText("会话列表");

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }*/
}

