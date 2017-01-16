package com.min.smalltalk.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.min.mylibrary.util.AMUtils;
import com.min.mylibrary.util.T;
import com.min.mylibrary.widget.dialog.LoadDialog;
import com.min.smalltalk.wedget.image.SelectableRoundedImageView;
import com.min.smalltalk.R;
import com.min.smalltalk.base.BaseActivity;
import com.min.smalltalk.bean.AddFriend;
import com.min.smalltalk.bean.Code;
import com.min.smalltalk.bean.LoginBean;
import com.min.smalltalk.constant.Const;
import com.min.smalltalk.network.HttpUtils;
import com.xys.libzxing.zxing.activity.CaptureActivity;
import com.zhy.http.okhttp.callback.StringCallback;

import java.lang.reflect.Type;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.rong.imageloader.core.ImageLoader;
import okhttp3.Call;

/**
 * 搜索好友----添加好友
 */
public class SearchFriendActivity extends BaseActivity {

    @BindView(R.id.et_friend)
    EditText etFriend;
    @BindView(R.id.siv_friend)
    SelectableRoundedImageView sivFriend;
    @BindView(R.id.tv_friend)
    TextView tvFriend;
    @BindView(R.id.ll_friend)
    LinearLayout llFriend;
    @BindView(R.id.iv_title_back)
    ImageView ivTitleBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_title_right)
    ImageView ivTitleRight;

    private String phone;
    private String headUri;
    private String userName;
    private String f_userid;

    private SharedPreferences sp;
    private String myUserId,myNickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_friend);
        ButterKnife.bind(this);
        sp=getSharedPreferences("config",MODE_PRIVATE);
        myUserId=sp.getString(Const.LOGIN_ID,"");
        myNickname=sp.getString(Const.LOGIN_NICKNAME,"");
        tvTitle.setText("添加好友");
        ivTitleRight.setVisibility(View.VISIBLE);
        ivTitleRight.setImageResource(R.mipmap.scan_white);
        etFriend.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 11) {
                    phone = charSequence.toString().trim();
                    if (!AMUtils.isMobile(phone)) {
                        T.showShort(mContext, "非手机号码");
                        return;
                    }
                    LoadDialog.show(mContext);
                    searchFriends();
                } else {
                    llFriend.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    private void searchFriends() {
        HttpUtils.PostSearchFriendRequest("/addfriends", phone, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LoadDialog.dismiss(mContext);
                T.showShort(mContext, "addfriends-----"+e);
                return;
            }

            @Override
            public void onResponse(String response, int id) {
                Gson gson = new Gson();
                Type type=new TypeToken<Code<LoginBean>>(){}.getType();
                Code<LoginBean> beanCode = gson.fromJson(response,type);
                int code=beanCode.getCode();
                if (code == 200) {
                    f_userid=beanCode.getMsg().getUserid();
                    headUri = HttpUtils.IMAGE_RUL+beanCode.getMsg().getPortrait();
                    userName = beanCode.getMsg().getNickname();
                    LoadDialog.dismiss(mContext);
                    llFriend.setVisibility(View.VISIBLE);
                    String image=beanCode.getMsg().getPortrait();
                    if(!TextUtils.isEmpty(image)) {
                        ImageLoader.getInstance().displayImage(headUri, sivFriend);
                    }else {
                        sivFriend.setImageResource(R.mipmap.default_portrait);
                    }
                    tvFriend.setText(userName);
                    llFriend.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (phone.equals(getSharedPreferences("config", MODE_PRIVATE).getString("loginphone", ""))) {
                                T.showShort(mContext, "不能添加自己为好友");
                                return;
                            }
                            final EditText editText = new EditText(mContext);
                            new AlertDialog.Builder(mContext)
                                    .setTitle("验证信息")
                                    .setView(editText)
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            String provingMessage = editText.getText().toString();
                                            LoadDialog.show(mContext);
                                            toProving(provingMessage);
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

                } else {
                    T.showShort(mContext, "用户不存在");
                    LoadDialog.dismiss(mContext);
                }
            }
        });
    }

    private void toProving(String message) {
        HttpUtils.sendPostRequest("/addfriend_request", myUserId, myNickname, f_userid, message, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                T.showShort(mContext, "/addfriend_request-----"+e);
                return;
            }

            @Override
            public void onResponse(String response, int id) {
                Gson gson = new Gson();
                Type type = new TypeToken<AddFriend>() {
                }.getType();
                AddFriend addFriend = gson.fromJson(response, type);
                int code = addFriend.getCode();
                switch (code) {
                    case 200:
                        T.showShort(mContext, "请求成功，请耐心等待对方审核");
                        LoadDialog.dismiss(mContext);
                        break;
                    case 11:
                        T.showShort(mContext, "你们已是好友");
                        LoadDialog.dismiss(mContext);
                        break;
                    case 0:
                        T.showShort(mContext,"好友添加失败");
                        LoadDialog.dismiss(mContext);
                        break;
                    default:
                        T.showShort(mContext, "error");
                        LoadDialog.dismiss(mContext);
                        break;
                }
            }
        });
    }

    @OnClick({R.id.iv_title_back, R.id.iv_title_right})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_title_back:
                finish();
                break;
            case R.id.iv_title_right:
                startActivityForResult(new Intent(mContext, CaptureActivity.class),0);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            Bundle bundle = data.getExtras();
            if(bundle!=null){
                String result=bundle.getString("result");
                etFriend.setText(result);
            }
        }
    }
}
