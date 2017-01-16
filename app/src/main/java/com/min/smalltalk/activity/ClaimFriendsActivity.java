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
import com.min.mylibrary.util.L;
import com.min.mylibrary.util.T;
import com.min.mylibrary.widget.dialog.LoadDialog;
import com.min.smalltalk.R;
import com.min.smalltalk.adapter.ClaimFriendsAdapter;
import com.min.smalltalk.base.BaseActivity;
import com.min.smalltalk.bean.ClaimFriends;
import com.min.smalltalk.bean.Code;
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
 * 认领好友
 */
public class ClaimFriendsActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener{

    @BindView(R.id.iv_title_back)
    ImageView ivTitleBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.swipeRefresh)
    SwipeRefreshLayout swipeRefresh;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private List<ClaimFriends> list = new ArrayList<ClaimFriends>();;
    private ClaimFriendsAdapter adapter;

    private String userId;
    private String friends_userId;
    private String problem_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_claim_friends);
        ButterKnife.bind(this);
        swipeRefresh.setOnRefreshListener(this);
        LoadDialog.show(mContext);
        initData();
    }

    private void initData() {
        userId = getSharedPreferences("config", MODE_PRIVATE).getString(Const.LOGIN_ID, "");
        HttpUtils.postClaimFriendsList("/all_friends_claim", userId, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                T.showShort(mContext, "onError---" + e);
                LoadDialog.dismiss(mContext);
                return;
            }

            @Override
            public void onResponse(String response, int id) {
                Gson gson = new Gson();
                Type type = new TypeToken<Code<List<ClaimFriends>>>() {
                }.getType();
                Code<List<ClaimFriends>> code = gson.fromJson(response, type);
                L.e("---------end", "");
                if (code.getCode() == 200) {
                    list = code.getMsg();
                    LoadDialog.dismiss(mContext);
                } else {
                    T.showShort(mContext, "没有注册用户");
                    LoadDialog.dismiss(mContext);
                }
                initAdapter();
            }
        });
    }

    private void initAdapter() {
        adapter = new ClaimFriendsAdapter(mContext, list);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager lm = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(lm);
        recyclerView.addItemDecoration(new ItemDivider(this, ItemDivider.VERTICAL_LIST));
        adapter.setOnItemClickListener(new ClaimFriendsAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, ClaimFriends claimFriends) {
                int status = claimFriends.getCheck_claim();
                if (status==1){
                    T.showShort(mContext,"已认领");
                    return;
                }
                Intent intent = new Intent(mContext,ClaimQuestionActivity.class);
                intent.putExtra("friends_userId",claimFriends.getId());
                startActivity(intent);
            }
        });
    }

    @OnClick(R.id.iv_title_back)
    public void onClick() {
        finish();
    }

    @Override
    public void onRefresh() {
        initData();
        if (list != null && list.size() != 0) {
            adapter.notifyDataSetChanged();
        }
        swipeRefresh.setRefreshing(false);
    }

    /*//认领点击事件
    @Override
    public boolean onButtonClaimClick(final int position, View view, int status) {
        final EditText editText = new EditText(mContext);
        new AlertDialog.Builder(mContext)
                .setTitle("验证问题")
                .setMessage(list.get(position).getProblem_title())
                .setView(editText)
                .setPositiveButton("验证", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String answer = editText.getText().toString();
                        friends_userId = list.get(position).getTu_id();
                        LoadDialog.show(mContext);
                        initClaim(answer);

                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .show();
        return false;
    }
*/
    /**
     * 认领
     *
     * @param answer
     */
    private void initClaim(String answer) {
        HttpUtils.postClaimFriends("/claim_user", userId, friends_userId, answer, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                T.showShort(mContext, "OnError---" + e);
                LoadDialog.dismiss(mContext);
                return;
            }

            @Override
            public void onResponse(String response, int id) {
                Gson gson = new Gson();
                Type type = new TypeToken<Code<Integer>>() {
                }.getType();
                Code<Integer> code = gson.fromJson(response, type);
                int code1 = code.getCode();
                switch (code1) {
                    case 200:
                        T.showShort(mContext, "认领成功");
                        initData();
                        LoadDialog.dismiss(mContext);
                        break;
                    case 0:
                        T.showShort(mContext, "认领失败");
                        LoadDialog.dismiss(mContext);
                        break;
                    case 100:
                        T.showShort(mContext, "已认领");
                        LoadDialog.dismiss(mContext);
                        break;
                    default:
                        LoadDialog.dismiss(mContext);
                        break;
                }
            }
        });
    }
}
