package com.min.smalltalk.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.min.mylibrary.util.T;
import com.min.smalltalk.R;
import com.min.smalltalk.base.BaseActivity;
import com.min.smalltalk.bean.Code;
import com.min.smalltalk.constant.Const;
import com.min.smalltalk.network.HttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.lang.reflect.Type;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import okhttp3.Call;

public class RecommendActivity extends BaseActivity {

    @BindView(R.id.iv_title_back)
    ImageView ivTitleBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_title_right)
    TextView tvTitleRight;
    @BindView(R.id.et_recommend_phone)
    EditText etRecommendPhone;
    @BindView(R.id.et_recommend_name)
    EditText etRecommendName;

    private SharedPreferences sp;
    private String userId;
    private String full_name;
    private String mobile;
    private String reCommendCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend);
        ButterKnife.bind(this);
        sp = getSharedPreferences("config", MODE_PRIVATE);
        initView();
    }

    private void initView() {
        tvTitle.setText("分享");
        tvTitleRight.setVisibility(View.VISIBLE);
        tvTitleRight.setText("分享");
        userId = sp.getString(Const.LOGIN_ID, "");
    }


    @OnClick({R.id.iv_title_back, R.id.tv_title_right})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_title_back:
                finish();
                break;
            case R.id.tv_title_right:
                full_name = etRecommendPhone.getText().toString();
                mobile = etRecommendPhone.getText().toString();
                if (TextUtils.isEmpty(full_name) || TextUtils.isEmpty(mobile)) {
                    T.showShort(mContext, "姓名或手机号不能为空");
                    return;
                }
                HttpUtils.postRecommend("/frends_recommend", userId, full_name, mobile, new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        T.showShort(mContext,"onError----"+e);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Gson gson = new Gson();
                        Type type=new TypeToken<Code<String>>(){}.getType();
                        Code<String> code = gson.fromJson(response,type);
                        switch (code.getCode()){
                            case 200:
                                reCommendCode = code.getMsg();
                                showShare();
                                break;
                            case 0:
                                T.showShort(mContext,"推荐失败");
                                break;
                            case 100:
                                T.showShort(mContext,"账号已存在");
                                break;
                        }
                    }
                });
                showShare();
                break;
        }
    }

    private void showShare() {
        ShareSDK.initSDK(this);
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间等使用
        oks.setTitle("社群联盟");
        // titleUrl是标题的网络链接，QQ和QQ空间等使用
        oks.setTitleUrl("https://www.bjike.com");
        // text是分享文本，所有平台都需要这个字段
        oks.setText("推荐id："+reCommendCode);
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
}
