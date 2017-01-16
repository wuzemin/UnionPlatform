package com.min.smalltalk.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.min.smalltalk.R;

/**
 * Created by Min on 2016/11/24.
 *  添加好友，创建群组下拉框
 */

public class ChatPopupWindow extends PopupWindow implements View.OnClickListener {
    private Context context;
    private LinearLayout ll_pop_chat,ll_pop_grout,ll_pop_addfriend;

    public ChatPopupWindow(Context context) {
        this.context = context;
        LayoutInflater inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view=inflater.inflate(R.layout.popupwindow_chat,null);
        //设置SelectPicPopupWindow的view
        this.setContentView(view);
        //
        this.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setFocusable(true);
        this.setBackgroundDrawable(new BitmapDrawable());
        this.setOutsideTouchable(true);  //点击外面去取消
        this.update(); //刷新

//        ll_pop_chat= (LinearLayout) view.findViewById(R.id.ll_pop_chat);
        ll_pop_grout= (LinearLayout) view.findViewById(R.id.ll_pop_group);
        ll_pop_addfriend= (LinearLayout) view.findViewById(R.id.ll_pop_add);
//        ll_pop_chat.setOnClickListener(this);
        ll_pop_grout.setOnClickListener(this);
        ll_pop_addfriend.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            /*case R.id.ll_pop_chat:
                context.startActivity(new Intent(context, MainActivity.class));
                ChatPopupWindow.this.dismiss();
                break;*/
            case R.id.ll_pop_group:    //创建群组
                Intent intent=new Intent(context,SelectFriendsActivity.class);
                intent.putExtra("createGroup",true);
                context.startActivity(intent);
                break;
            case R.id.ll_pop_add:   //添加好友
                context.startActivity(new Intent(context,SearchFriendActivity.class));
                ChatPopupWindow.this.dismiss();
                break;
            default:
                break;
        }
    }
    public void showPopupWindow(View view){
        if(!this.isShowing()){
            //以下拉的方式显示
            this.showAsDropDown(view,0,0);
        }else {
            this.dismiss();
        }
    }
}
