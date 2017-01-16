package com.min.smalltalk.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.min.mylibrary.util.T;
import com.min.smalltalk.R;
import com.min.smalltalk.base.BaseActivity;
import com.min.smalltalk.base.BaseRecyclerAdapter;
import com.min.smalltalk.base.BaseRecyclerHolder;
import com.min.smalltalk.bean.Code;
import com.min.smalltalk.bean.GroupVote;
import com.min.smalltalk.constant.Const;
import com.min.smalltalk.network.HttpUtils;
import com.min.smalltalk.wedget.ItemDivider;
import com.zhy.http.okhttp.callback.StringCallback;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.rong.imlib.model.Conversation;
import okhttp3.Call;

/**
 * 投票活动
 */
public class GroupVoteActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.iv_title_back)
    ImageView ivTitleBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_title_right)
    TextView tvTitleRight;
    @BindView(R.id.rv_group_vote)
    RecyclerView rvGroupVote;
    @BindView(R.id.swipeRefresh)
    SwipeRefreshLayout swipeRefresh;

    private BaseRecyclerAdapter<GroupVote> adapter;
    private List<GroupVote> list = new ArrayList<>();

    private String groupId;
    private String voteId;
    private static final int REFRESH_COMPLETE=0;
    private int status;
    private Conversation.ConversationType conversationType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_vote);
        ButterKnife.bind(this);
        initView();
        initListView();

    }

    private void initView() {
        tvTitle.setText("投票活动");
        tvTitleRight.setVisibility(View.VISIBLE);
        tvTitleRight.setText("添加");
        Intent intent=getIntent();
        groupId = intent.getStringExtra("group_id");
        conversationType=Conversation.ConversationType.setValue(intent.getIntExtra("conversationType",0));
        swipeRefresh.setOnRefreshListener(this);

    }

    private void initListView() {
        String userid = getSharedPreferences("config", MODE_PRIVATE).getString(Const.LOGIN_ID, "");
        HttpUtils.postVoteList("/vote_list", groupId, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                T.showShort(mContext, "/vote_list------" + e);
                return;
            }

            @Override
            public void onResponse(String response, int id) {
                Gson gson = new Gson();
                Type type = new TypeToken<Code<List<GroupVote>>>() {
                }.getType();
                Code<List<GroupVote>> code = gson.fromJson(response, type);
                List<GroupVote> voteList = code.getMsg();
                if (code.getCode() == 200) {
                    for (GroupVote groupVote : voteList) {
                        voteId = groupVote.getVote_id();
                        String voteTitle = groupVote.getVote_title();
                        String voteCreate = groupVote.getAdd_time();
                        String voteEndTime = groupVote.getEnd_time();
                        status=groupVote.getStatus();
                        GroupVote groupVotes = new GroupVote(voteId, voteTitle, voteCreate, voteEndTime,groupId,status);
                        list.add(groupVotes);
                    }
                    //
                    Collections.sort(list, new Comparator<GroupVote>() {
                        @Override
                        public int compare(GroupVote groupVote, GroupVote t1) {
                            Date date1 = stringToDate(groupVote);
                            Date date2 = stringToDate(t1);
                            if (date1.before(date2)) {
                                return 1;
                            }
                            return -1;
                        }
                    });

                    initAdapter();
                } else {
                    T.showShort(mContext, "没有活动");
                }
            }
        });
    }

    private Date stringToDate(GroupVote groupVote) {
        String updatedAt = groupVote.getAdd_time();
        String updatedAtDateStr = updatedAt.substring(0, 10) + " " + updatedAt.substring(11, 16);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date updateAtDate = null;
        try {
            updateAtDate = simpleDateFormat.parse(updatedAtDateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return updateAtDate;
    }

    private void initAdapter() {
        adapter = new BaseRecyclerAdapter<GroupVote>(mContext, list, R.layout.item_group_vote) {
            @Override
            public void convert(BaseRecyclerHolder holder, GroupVote item, int position, boolean isScrolling) {
                holder.setText(R.id.tv_vote_title, item.getVote_title());
                holder.setText(R.id.tv_vote_time, item.getAdd_time());
                if(item.getStatus()==0){
                    holder.setText(R.id.tv_vote_status,"已结束");
                }else {
                    holder.setText(R.id.tv_vote_status,"进行中");
                }
            }
        };
        rvGroupVote.setAdapter(adapter);
        LinearLayoutManager lm = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        rvGroupVote.setLayoutManager(lm);
        rvGroupVote.addItemDecoration(new ItemDivider(mContext, ItemDivider.VERTICAL_LIST));

        //点击事件
        adapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, View view, int position) {
                Intent intent = new Intent(mContext, VoteDetailActivity.class);
                intent.putExtra("group_id", list.get(position).getGroupId());
                intent.putExtra("vote_id", list.get(position).getVote_id());
                startActivity(intent);
            }
        });
    }

    @OnClick({R.id.iv_title_back, R.id.tv_title_right})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_title_back:
                finish();
                break;
            case R.id.tv_title_right:
                Intent intent = new Intent(mContext, AddVoteActivity.class);
                intent.putExtra("group_id", groupId);
                intent.putExtra("conversationType", Conversation.ConversationType.GROUP.getValue());
                startActivityForResult(intent, 0);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            list.clear();
            initListView();
        }
    }

    @Override
    public void onRefresh() {
        mHandler.sendEmptyMessageDelayed(REFRESH_COMPLETE, 2000);
    }

    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case REFRESH_COMPLETE:
                    list.clear();
                    initListView();
                    swipeRefresh.setRefreshing(false);
            }
        }
    };
}
