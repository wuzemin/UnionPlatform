package com.min.smalltalk.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.min.smalltalk.R;

public class SubConversationListActivtiy extends FragmentActivity {
    private TextView mTitle;
    private RelativeLayout mBack;
    /**
     * 聚合类型
     */
    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_conversation_list_activtiy);
        mTitle = (TextView) findViewById(R.id.txt1);
        mBack = (RelativeLayout) findViewById(R.id.back);

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        /*SubConversationListFragment fragment = new SubConversationListFragment();
        fragment.setAdapter(new SubConversationListAdapterEx(RongContext.getInstance()));
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.subconversationlist, fragment);
        transaction.commit();*/

        getActionBarTitle();

    }

    /**
     * 通过 intent 中的数据，得到当前的 targetId 和 type
     */
    private void getActionBarTitle() {

        Intent intent = getIntent();
        if (intent.getData() == null) {
            return;
        }
        //聚合会话参数
        String type = intent.getData().getQueryParameter("type");

        if (type == null)
            return;

        if (type.equals("group")) {
            mTitle.setText("群组");
        } else if (type.equals("private")) {
            mTitle.setText("单聊");
        } else if (type.equals("discussion")) {
            mTitle.setText("讨论组");
        } else if (type.equals("system")) {
            mTitle.setText("系统会话");
        } else {
            mTitle.setText("聊天");
        }

    }
}
