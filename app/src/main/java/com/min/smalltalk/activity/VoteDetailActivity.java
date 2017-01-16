package com.min.smalltalk.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.min.mylibrary.util.L;
import com.min.mylibrary.util.T;
import com.min.mylibrary.widget.dialog.LoadDialog;
import com.min.smalltalk.R;
import com.min.smalltalk.base.BaseActivity;
import com.min.smalltalk.bean.Code;
import com.min.smalltalk.bean.GroupVote;
import com.min.smalltalk.constant.Const;
import com.min.smalltalk.network.HttpUtils;
import com.min.smalltalk.wedget.CheckableLinearLayout;
import com.zhy.http.okhttp.callback.StringCallback;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * 投票详情
 */
public class VoteDetailActivity extends BaseActivity {

    @BindView(R.id.listView)
    ListView listView;
    @BindView(R.id.iv_title_back)
    ImageView ivTitleBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_vote_name)
    TextView tvVoteName;
    @BindView(R.id.tv_select)
    TextView tvSelect;
    @BindView(R.id.btn_vote)
    Button btnVote;
    @BindView(R.id.activity_vote_detail)
    LinearLayout activityVoteDetail;

    private String userId;
    private String group_id;
    private String vote_id;
    private String vote_title;
    private int mode;
    private String add_time;
    private String end_time;
    private List<Map<String, String>> data = new ArrayList<Map<String, String>>();
    private Map<String, String> option;
    private List<String> list=new ArrayList<>();
    private HashSet hashSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote_detail);
        ButterKnife.bind(this);
        initView();
        LoadDialog.show(mContext);
        initData();
    }

    private void initView() {
        Intent intent = getIntent();
        group_id = intent.getStringExtra("group_id");
        vote_id = intent.getStringExtra("vote_id");
        tvTitle.setText("投票详情");
        userId=getSharedPreferences("config",MODE_PRIVATE).getString(Const.LOGIN_ID,"");

    }

    private void initData() {
        HttpUtils.postVoteDetails("/vote_details", group_id, vote_id, new StringCallback() {


            @Override
            public void onError(Call call, Exception e, int id) {
                T.showShort(mContext, "/vote_details------" + e);
                LoadDialog.dismiss(mContext);
                return;
            }

            @Override
            public void onResponse(String response, int id) {
                Gson gson = new Gson();
                Type type = new TypeToken<Code<GroupVote>>() {
                }.getType();
                Code<GroupVote> code = gson.fromJson(response, type);
                if (code.getCode() == 200) {
                    GroupVote groupVote = code.getMsg();
                    vote_id = groupVote.getVote_id();
                    vote_title = groupVote.getVote_title();
                    mode = groupVote.getMode();
                    add_time = groupVote.getAdd_time();
                    end_time = groupVote.getEnd_time();
                    option = groupVote.getOption();
                    LoadDialog.dismiss(mContext);
                } else {
                    T.showShort(mContext, "空");
                    LoadDialog.dismiss(mContext);
                }
                ini();
                initListView();
            }
        });
    }

    private void initListView() {
        tvVoteName.setText(vote_title);
        if(mode==0) {
            tvSelect.setText("以下选项为单选");
            listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        }else{
            tvSelect.setText("以下选项为多选");
            listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        }
        hashSet = new HashSet();
        listView.setAdapter(new MyAdapter());
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //获取条目
                CheckableLinearLayout linearLayout = (CheckableLinearLayout) view.findViewById(R.id.ll_contain);
                if (linearLayout.isChecked()) {
                    if(mode==0) {
                        hashSet.clear();
                        hashSet.add(data.get(position).get("id"));
                    }else {
                        hashSet.add(data.get(position).get("id"));
                    }
                } else {
                    hashSet.remove(data.get(position).get("id"));
                }
                /**对于多选，建议创建集合，用于封装用户选中的条目position，存入时判定                     用户来回切换的状态*/
            }
        });
    }

    private void ini() {
        Iterator<Map.Entry<String, String>> iterator = option.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            Map<String, String> d = new HashMap<>();
            L.e("--------", entry.getKey() + "---" + entry.getValue());
            d.put("id", entry.getKey());
            d.put("content", entry.getValue());
            data.add(d);
        }
    }

    @OnClick({R.id.iv_title_back, R.id.btn_vote})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_title_back:
                finish();
                break;
            case R.id.btn_vote:
                LoadDialog.show(mContext);
                postPeriod();
                break;
        }
    }

    private void postPeriod() {
        final Gson gson=new Gson();
        String vote_option=gson.toJson(hashSet);
        HttpUtils.postVote("/vote_collect", userId, group_id, vote_id, vote_option, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                T.showShort(mContext,"/vote_collect------"+e);
                LoadDialog.dismiss(mContext);
                return;
            }

            @Override
            public void onResponse(String response, int id) {
                Type type=new TypeToken<Code<Integer>>(){}.getType();
                Code<Integer> code = gson.fromJson(response,type);
                switch (code.getCode()){
                    case 200:
                        T.showShort(mContext,"投票成功");
                        LoadDialog.dismiss(mContext);
                        finish();
                        break;
                    case 0:
                        T.showShort(mContext,"未知失败");
                        LoadDialog.dismiss(mContext);
                        break;
                    case 101:
                        T.showShort(mContext,"投票时间已结束");
                        LoadDialog.dismiss(mContext);
                        break;
                    case 102:
                        T.showShort(mContext,"已投票，请勿重复提交");
                        LoadDialog.dismiss(mContext);
                        break;
                    case 103:
                        T.showShort(mContext,"投票已关闭或已投票已失效(创建者或群主删除投票)");
                        LoadDialog.dismiss(mContext);
                        break;
                    default:
                        break;
                }
            }
        });
    }


    private class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public String getItem(int position) {
            return data.get(position).get("content");
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup container) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.item_vote_details, container, false);
            }
            ((TextView) convertView.findViewById(android.R.id.text1))
                    .setText(getItem(position));
            return convertView;
        }
    }

}
