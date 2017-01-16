package com.min.smalltalk.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
import okhttp3.Call;

public class ClaimQuestionActivity extends BaseActivity {

    @BindView(R.id.et_name)
    EditText etName;
    @BindView(R.id.et_phone)
    EditText etPhone;
    @BindView(R.id.iv_title_back)
    ImageView ivTitleBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.btn_claim_user)
    Button btnClaimUser;

    private SharedPreferences sp;
    private String userId;
    private String friends_userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_claim_friend);
        ButterKnife.bind(this);
        friends_userId = getIntent().getStringExtra("friends_userId");
        sp = getSharedPreferences("config", MODE_PRIVATE);
        userId = sp.getString(Const.LOGIN_ID, "");
    }

    @OnClick({R.id.iv_title_back, R.id.btn_claim_user})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_title_back:
                finish();
                break;
            case R.id.btn_claim_user:
                clainUser();
                break;
            default:
                break;
        }
    }

    /**
     * 认领用户
     */
    private void clainUser() {
        String full_name = etName.getText().toString();
        String mobile = etPhone.getText().toString();
        HttpUtils.postClaimUser("/claim_user", userId, friends_userId, full_name, mobile, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                T.showShort(mContext,"claim_user----"+e);
            }

            @Override
            public void onResponse(String response, int id) {
                Gson gson = new Gson();
                Type type = new TypeToken<Code<Integer>>(){}.getType();
                Code<Integer> code = gson.fromJson(response,type);
                int code1=code.getCode();
                switch (code1){
                    case 200:
                        T.showShort(mContext,"认领成功");
                        finish();
                        break;
                    case 0:
                        T.showShort(mContext,"认领失败");
                        break;
                    case 100:
                        T.showShort(mContext,"已认领");
                        break;
                    default:
                        break;
                }
            }
        });
    }
}
