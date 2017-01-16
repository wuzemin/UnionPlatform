package com.min.smalltalk.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
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
import com.min.smalltalk.adapter.RecommendAdapter;
import com.min.smalltalk.base.BaseActivity;
import com.min.smalltalk.bean.Code;
import com.min.smalltalk.bean.Recommend;
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
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import okhttp3.Call;

public class RecommendListActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.iv_title_back)
    ImageView ivTitleBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.swipeRefresh)
    SwipeRefreshLayout swipeRefresh;

    private String userId;
    private String friends_userId;
    private String problem_title;
    private List<Recommend> list = new ArrayList<Recommend>();;
    private RecommendAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend_list);
        ButterKnife.bind(this);
        swipeRefresh.setOnRefreshListener(this);
        LoadDialog.show(mContext);
        initData();
    }

    private void initData() {
        userId = getSharedPreferences("config", MODE_PRIVATE).getString(Const.LOGIN_ID, "");
        HttpUtils.postRecommendFriendsList("/all_recommends_users", userId, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                T.showShort(mContext, "onError---" + e);
                LoadDialog.dismiss(mContext);
                return;
            }

            @Override
            public void onResponse(String response, int id) {
                Gson gson = new Gson();
                Type type = new TypeToken<Code<List<Recommend>>>() {}.getType();
                Code<List<Recommend>> code = gson.fromJson(response, type);
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
        adapter = new RecommendAdapter(mContext, list);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager lm = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(lm);
        recyclerView.addItemDecoration(new ItemDivider(this, ItemDivider.VERTICAL_LIST));
        adapter.setOnItemClickListener(new RecommendAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, final Recommend recommend) {
                T.showShort(mContext,recommend+"");
                new AlertDialog.Builder(mContext)
                        .setTitle("再次推荐")
                        .setMessage(recommend.getFull_name())
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                showShare(recommend.getUserid());
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .show();
            }
        });
    }

    /**
     * 推荐
     */
    private void showShare(String reCommendCode) {
        ShareSDK.initSDK(this);
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间等使用
        oks.setTitle("社群联盟");
        // titleUrl是标题的网络链接，QQ和QQ空间等使用
        oks.setTitleUrl("https://www.bjike.com");
        // text是分享文本，所有平台都需要这个字段
        oks.setText("推荐id：" + reCommendCode);
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("https://www.bjike.com");


        /*// comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("http://sharesdk.cn");*/
// 启动分享GUI
        oks.show(this);
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
}
