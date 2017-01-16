package com.min.smalltalk.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
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
import com.min.smalltalk.bean.FlexibleMember;
import com.min.smalltalk.bean.GroupFlexible;
import com.min.smalltalk.constant.Const;
import com.min.smalltalk.network.HttpUtils;
import com.min.smalltalk.wedget.ItemDivider;
import com.min.smalltalk.wedget.image.SelectableRoundedImageView;
import com.zhy.http.okhttp.callback.StringCallback;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.rong.imageloader.core.ImageLoader;
import okhttp3.Call;

/**
 * 群活动的详细信息
 */
public class FlexibleDetailActivity extends BaseActivity {

    @BindView(R.id.iv_title_back)
    ImageView ivTitleBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_group_activity_head)
    SelectableRoundedImageView ivGroupActivityHead;
    @BindView(R.id.activity_name)
    TextView tvActivityName;
    @BindView(R.id.tv_activity_start_time)
    TextView tvActivityStartTime;
    @BindView(R.id.tv_activity_end_time)
    TextView tvActivityEndTime;
    @BindView(R.id.tv_activity_place)
    TextView tvActivityPlace;
    @BindView(R.id.tv_activity_content)
    TextView tvActivityContent;
    @BindView(R.id.btn_join_activity)
    Button btnJoinActivity;
    @BindView(R.id.rv_activity_user)
    RecyclerView rvActivityUser;

    private GroupFlexible groupFlexible;
    private String userId;
    private String flexibleId,flexibleName,flexiblePort,flexibleStartTime,flexibleEndTime,flexiblePlace,flexibleContent;
    private List<GroupFlexible> list;
    private BaseRecyclerAdapter<FlexibleMember> adapter;
    private List<FlexibleMember> flexibleMemberList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flexible_detail);
        ButterKnife.bind(this);
        list=new ArrayList<>();
        flexibleMemberList=new ArrayList<>();
        Intent intent=getIntent();
        groupFlexible=intent.getParcelableExtra("flexible");
        flexibleId=groupFlexible.getActives_id();
        flexibleName=groupFlexible.getActives_title();
        flexibleStartTime=groupFlexible.getActives_start();
        flexibleEndTime=groupFlexible.getActives_end();
        flexiblePlace=groupFlexible.getActives_address();
        flexiblePort=HttpUtils.IMAGE_RUL+groupFlexible.getActives_image();
        flexibleContent=groupFlexible.getActives_content();
        initView();
        initData();  //群活动成员
    }

    /**
     * 群活动信息
     */
    private void initView() {
        if(!TextUtils.isEmpty(flexiblePort)){
            ImageLoader.getInstance().displayImage(flexiblePort,ivGroupActivityHead);
        }else {
            ivGroupActivityHead.setImageResource(R.mipmap.load_image_2);
        }
        tvTitle.setText("群活动");
        tvActivityName.setText(flexibleName);
        tvActivityStartTime.setText(flexibleStartTime);
        tvActivityEndTime.setText(flexibleEndTime);
        tvActivityPlace.setText(flexiblePlace);
        tvActivityContent.setText(flexibleContent);
    }


    /**
     * 群活动成员
     */
    private void initData() {
        HttpUtils.postAddGroupMember("/infoActives", flexibleId, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                T.showShort(mContext,"/infoActives------"+e);
                return;
            }

            @Override
            public void onResponse(String response, int id) {
                Gson gson=new Gson();
                Type type=new TypeToken<Code<List<FlexibleMember>>>(){}.getType();
                Code<List<FlexibleMember>> beanCode=gson.fromJson(response,type);
                if(beanCode.getCode()==200){
//                    List<FlexibleMember> bean=beanCode.getMsg();
                    flexibleMemberList=beanCode.getMsg();
                    initAdapter();
                }
            }
        });
    }

    private void initAdapter() {
        adapter=new BaseRecyclerAdapter<FlexibleMember>(mContext,flexibleMemberList,R.layout.item_group) {
            @Override
            public void convert(BaseRecyclerHolder holder, FlexibleMember item, int position, boolean isScrolling) {
                holder.setImageByUrl(R.id.siv_group_head,HttpUtils.IMAGE_RUL+item.getAvatar_image());
                holder.setText(R.id.tv_group_name,item.getNickname());
            }
        };
        rvActivityUser.setAdapter(adapter);
        LinearLayoutManager lm=new LinearLayoutManager(mContext,LinearLayoutManager.VERTICAL,false);
        rvActivityUser.setLayoutManager(lm);
        rvActivityUser.addItemDecoration(new ItemDivider(mContext,ItemDivider.VERTICAL_LIST));
    }


    @OnClick({R.id.iv_title_back, R.id.btn_join_activity})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_title_back:
                finish();
                break;
            case R.id.btn_join_activity:
                joinActivity();
                break;
        }
    }

    //参加活动
    private void joinActivity() {
        userId=getSharedPreferences("config",MODE_PRIVATE).getString(Const.LOGIN_ID,"");
        HttpUtils.postAddFlexible("/joinActives", userId, flexibleId, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                T.showShort(mContext,"/joinActives------"+e);
                return;
            }

            @Override
            public void onResponse(String response, int id) {
                Gson gson=new Gson();
                Type type=new TypeToken<Code<Integer>>(){}.getType();
                Code<Integer> code = gson.fromJson(response,type);
                switch (code.getCode()){
                    case 200:
                        T.showShort(mContext,"加入成功");
                        break;
                    case 0:
                        T.showShort(mContext,"加入失败");
                        break;
                    case 100:
                        T.showShort(mContext,"非群内人员无法加入该活动");
                        break;
                    case 101:
                        T.showShort(mContext,"活动还未开始");
                        break;
                    case 102:
                        T.showShort(mContext,"活动结束");
                        break;
                    case 103:
                        T.showShort(mContext,"活动人数已满");
                        break;
                    default:
                        break;
                }
            }
        });
    }
}
