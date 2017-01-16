package com.min.smalltalk.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.min.mylibrary.util.L;
import com.min.mylibrary.util.T;
import com.min.smalltalk.R;
import com.min.smalltalk.base.BaseActivity;
import com.min.smalltalk.base.BaseRecyclerAdapter;
import com.min.smalltalk.base.BaseRecyclerHolder;
import com.min.smalltalk.bean.Code;
import com.min.smalltalk.bean.FriendInfo;
import com.min.smalltalk.bean.GroupMember;
import com.min.smalltalk.constant.Const;
import com.min.smalltalk.network.HttpUtils;
import com.min.smalltalk.wedget.ItemDivider;
import com.zhy.http.okhttp.callback.StringCallback;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * 群成员
 */
public class GroupMemberActivity extends BaseActivity {

    @BindView(R.id.iv_title_back)
    ImageView ivTitleBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.rv_group_member)
    RecyclerView rvGroupMember;

    private String groupId;
    private String userId,userName,userPort,userPhone,userEmail,userDisplayName;
    private BaseRecyclerAdapter<GroupMember> adapter;
    private List<GroupMember> list=new ArrayList<>();
    private List<FriendInfo> friendInfoList=new ArrayList<>();
    private GroupMember groupMembers;
    private String fromConversationId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_member);
        ButterKnife.bind(this);
        groupId = getIntent().getStringExtra("groupId");
        initList();
    }

    private void initList() {
        fromConversationId = getSharedPreferences("config",MODE_PRIVATE).getString(Const.LOGIN_ID,"");
        HttpUtils.postGroupsRequest("/group_member", groupId, fromConversationId, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                T.showShort(mContext,"/group_member----------连接失败");
                return;
            }

            @Override
            public void onResponse(String response, int id) {
                Gson gson=new Gson();
                Type type=new TypeToken<Code<List<GroupMember>>>(){}.getType();
                Code<List<GroupMember>> code = gson.fromJson(response,type);
                if(code.getCode()==200){
                    List<GroupMember> groupMember=code.getMsg();
                    for(GroupMember member:groupMember) {
                        userId = member.getUserId();
                        userName = member.getUserName();
                        userPort = HttpUtils.IMAGE_RUL+member.getUserPortraitUri();
                        userDisplayName=member.getDisplayName();
                        userPhone=member.getPhone();
                        userEmail=member.getEmail();
                        list.add(new GroupMember(userId, userName, userPort,userDisplayName));
                        friendInfoList.add(new FriendInfo(userId,userName,userPort,userPhone,userEmail));
                        groupMembers=new GroupMember(userId,userName,userPort,userPhone,userEmail);
                    }
                    initAdapter();
                    initAdapterListener();

                }else {
                    T.showShort(mContext,"/group/group_member---------获取数据失败");
                }
            }
        });
    }

    private void initAdapter() {
        adapter=new BaseRecyclerAdapter<GroupMember>(mContext,list,R.layout.item_group) {
            @Override
            public void convert(BaseRecyclerHolder holder, GroupMember item, int position, boolean isScrolling) {
                String siv=item.getUserPortraitUri();
                if(!TextUtils.isEmpty(siv)){
                    holder.setImageByUrl(R.id.siv_group_head,siv);
                }else {
                    holder.setImageResource(R.id.siv_group_head,R.mipmap.default_portrait);
                }
                String name=item.getDisplayName();
                if(TextUtils.isEmpty(name)){
                    holder.setText(R.id.tv_group_name,item.getUserName());
                }else {
                    holder.setText(R.id.tv_group_name, name);
                }
                if("1".equals(item.getRole())){
                    holder.setText(R.id.tv_role,"群主");
                }else {
                    holder.setText(R.id.tv_role,"");
                }
            }
        };
        rvGroupMember.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(mContext,LinearLayoutManager.VERTICAL,false);
        rvGroupMember.setLayoutManager(linearLayoutManager);
        rvGroupMember.addItemDecoration(new ItemDivider(mContext,ItemDivider.VERTICAL_LIST));
    }

    private void initAdapterListener() {
        adapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, View view, int position) {

                L.e("-------------",list.get(position).getUserId()+list.get(position).getUserName()+list.get(position).getUserPortraitUri());
                FriendInfo friend=friendInfoList.get(position);
                String mId=getSharedPreferences("config",MODE_PRIVATE).getString(Const.LOGIN_ID,"");
                if(friend.getUserId().equals(mId)){
                    T.showShort(mContext,"这是自己");
                    return;
                }
                Intent intent=new Intent(mContext,UserDetailActivity.class);
                intent.putExtra("friends",friend);

                /*String userid=list.get(position).getUserId();
                String username=list.get(position).getName();
                String userport=list.get(position).getPortraitUri();
                String userPhone=list.get(position).getPhone();
                String userEmail=list.get(position).getEmail();

//                intent.putExtra("friends",groupMember);
                intent.putExtra("type",1);
                intent.putExtra("userId",userid);
                intent.putExtra("userName",username);
                intent.putExtra("userPort",userport);
                intent.putExtra("userPhone",userPhone);
                intent.putExtra("userEmail",userEmail);*/
                startActivity(intent);
            }
        });
    }

    @OnClick(R.id.iv_title_back)
    public void onClick() {
        finish();
    }
}
