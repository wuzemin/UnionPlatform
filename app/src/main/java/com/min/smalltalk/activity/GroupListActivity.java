package com.min.smalltalk.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.min.mylibrary.util.T;
import com.min.mylibrary.widget.dialog.LoadDialog;
import com.min.smalltalk.R;
import com.min.smalltalk.adapter.GroupListAdapter;
import com.min.smalltalk.base.BaseActivity;
import com.min.smalltalk.bean.Code;
import com.min.smalltalk.bean.GroupMember;
import com.min.smalltalk.bean.Groups;
import com.min.smalltalk.constant.Const;
import com.min.smalltalk.db.GroupMemberDAOImpl;
import com.min.smalltalk.db.GroupsDAOImpl;
import com.min.smalltalk.network.HttpUtils;
import com.min.smalltalk.wedget.ItemDivider;
import com.zhy.http.okhttp.callback.StringCallback;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.rong.imkit.RongIM;
import okhttp3.Call;

/**
 * 群列表
 */
public class GroupListActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.iv_title_back)
    ImageView ivTitleBack;
    @BindView(R.id.iv_title_right)
    ImageView ivTitleRight;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.rv_group_list)
    RecyclerView rvGroupList;
    @BindView(R.id.tv_no_group)
    TextView tvNoGroup;
    @BindView(R.id.swipeRefresh)
    SwipeRefreshLayout swipeRefresh;

//    private BaseRecyclerAdapter<Groups> adapter;
    private GroupListAdapter adapter;
    private List<Groups> list = new ArrayList<>();
    private List<GroupMember> mGroupMember=new ArrayList<>();
    private String groupName;
    private String groupId;
    private String groupPortraitUri;

    private GroupsDAOImpl sqLiteDAO;
    private GroupMemberDAOImpl groupMemberDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_list);
        ButterKnife.bind(this);

        sqLiteDAO = new GroupsDAOImpl(mContext);
        groupMemberDAO = new GroupMemberDAOImpl(mContext);

        tvTitle.setText("我的群组");
        ivTitleRight.setVisibility(View.VISIBLE);
        ivTitleRight.setImageResource(R.mipmap.add_more);
        swipeRefresh.setOnRefreshListener(this);
        LoadDialog.show(mContext);
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    private void initData() {
        String userId = getSharedPreferences("config", MODE_PRIVATE).getString(Const.LOGIN_ID, "");
        list = sqLiteDAO.findAll(userId);
        if (list.size() > 0) {
            initAdapter();
        } else {
            rvGroupList.setVisibility(View.GONE);
            tvNoGroup.setVisibility(View.VISIBLE);
            tvNoGroup.setText("你暂时未加入任何一个群组");
            LoadDialog.dismiss(mContext);
        }
    }

    private void initAdapter() {
        adapter = new GroupListAdapter(mContext,list);
        rvGroupList.setAdapter(adapter);
        LinearLayoutManager lm = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        rvGroupList.setLayoutManager(lm);
        rvGroupList.addItemDecoration(new ItemDivider(this, ItemDivider.VERTICAL_LIST));
        adapter.notifyDataSetChanged();
        initListItemClick();
        LoadDialog.dismiss(mContext);
    }

    private void initListItemClick() {
        adapter.setOnItemClickListener(new GroupListAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, Groups groups) {
                String groupId=groups.getGroupId();
                String groupName=groups.getGroupName();
                initList(groupId,groupName);
                RongIM.getInstance().startGroupChat(mContext,groupId,groupName);
            }
        });
    }

    private void initList(String groupId,String groupName) {
        String userId=getSharedPreferences("config",MODE_PRIVATE).getString(Const.LOGIN_ID,"");
        HttpUtils.postGroupsRequest("/group_member", groupId, userId, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                T.showShort(mContext, "group_member------" + e);
                return;
            }

            @Override
            public void onResponse(String response, int id) {
                Gson gson = new Gson();
                Type type = new TypeToken<Code<List<GroupMember>>>() {
                }.getType();
                Code<List<GroupMember>> code = gson.fromJson(response, type);
                if (code.getCode() == 200) {
                    mGroupMember = code.getMsg();
                    for(GroupMember groupMember:mGroupMember){
                        groupMemberDAO.save(groupMember);
                    }
                }
            }
        });
    }


    @OnClick({R.id.iv_title_back, R.id.iv_title_right})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_title_back:
                GroupListActivity.this.finish();
                break;
            case R.id.iv_title_right:
                Intent intent=new Intent(mContext,SelectFriendsActivity.class);
                intent.putExtra("createGroup",true);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onRefresh() {
        initData();
        swipeRefresh.setRefreshing(false);
    }
}
