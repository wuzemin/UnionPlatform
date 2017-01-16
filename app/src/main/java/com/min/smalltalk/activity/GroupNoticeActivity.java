package com.min.smalltalk.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.min.mylibrary.util.T;
import com.min.smalltalk.R;
import com.min.smalltalk.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.MentionedInfo;
import io.rong.imlib.model.Message;
import io.rong.message.TextMessage;

/**
 * 群公告
 */
public class GroupNoticeActivity extends BaseActivity {

    @BindView(R.id.iv_title_back)
    ImageView ivTitleBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_title_right)
    TextView tvTitleRight;
    @BindView(R.id.et_group_notice)
    EditText etGroupNotice;

    private String string;
    private Conversation.ConversationType mConversationType;
    private String targetId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_notice);
        ButterKnife.bind(this);
        Intent intent=getIntent();
        targetId=intent.getStringExtra("targetId");
        mConversationType= Conversation.ConversationType.setValue(intent.getIntExtra("conversationType",0));
        tvTitle.setText("群公告");
        tvTitleRight.setVisibility(View.VISIBLE);
        tvTitleRight.setText("确定");
    }

    @OnClick({R.id.iv_title_back, R.id.tv_title_right})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_title_back:
                finish();
                break;
            case R.id.tv_title_right:
                string=etGroupNotice.getText().toString();
                if(!TextUtils.isEmpty(string)){
                    TextMessage textMessage=TextMessage.obtain(RongContext.getInstance().getString(
                            R.string.group_notice_prefix)+string);
                    MentionedInfo mentionedInfo=new MentionedInfo(MentionedInfo.MentionedType.ALL,null,null);
                    textMessage.setMentionedInfo(mentionedInfo);
                    RongIM.getInstance().sendMessage(Message.obtain(targetId, mConversationType, textMessage),
                            null, null, new IRongCallback.ISendMessageCallback() {
                                @Override
                                public void onAttached(Message message) {

                                }

                                @Override
                                public void onSuccess(Message message) {

                                }

                                @Override
                                public void onError(Message message, RongIMClient.ErrorCode errorCode) {

                                }
                            });
                    finish();

                }else {
                    T.showShort(mContext,"内容不能为空");
                }
                break;
        }
    }
}
