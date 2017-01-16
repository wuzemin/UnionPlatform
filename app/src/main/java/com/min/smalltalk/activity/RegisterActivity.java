package com.min.smalltalk.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.min.mylibrary.util.AMUtils;
import com.min.mylibrary.util.T;
import com.min.mylibrary.widget.ClearWriteEditText;
import com.min.mylibrary.widget.dialog.LoadDialog;
import com.min.smalltalk.R;
import com.min.smalltalk.base.BaseActivity;
import com.min.smalltalk.bean.Code;
import com.min.smalltalk.network.HttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.lang.reflect.Type;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import okhttp3.Call;

/**
 * 注册页面
 */
public class RegisterActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.textView)
    TextView textView;
    @BindView(R.id.et_nickname)
    ClearWriteEditText etNickname;
    @BindView(R.id.et_phone)
    ClearWriteEditText etPhone;
    @BindView(R.id.et_code)
    ClearWriteEditText etCode;
    @BindView(R.id.btn_get_cord)
    Button btnGetCord;
    @BindView(R.id.tv_message)
    TextView tvMessage;
    @BindView(R.id.et_password)
    ClearWriteEditText etPassword;
    @BindView(R.id.btn_register)
    Button btnRegister;
    @BindView(R.id.et_recommend_code)
    ClearWriteEditText etRecommendCode;
    /*@BindView(R.id.et_answer)
    ClearWriteEditText etAnswer;
    @BindView(R.id.et_question)
    ClearWriteEditText etQuestion;*/

    private String phone;
    private String iCord;
    private String nickname;
    private String password;
    private String recommendCode;
    private String question;
    private String answer;
    private int time = 60;
    private boolean flag = true;
    private static String APPKEY = "15cfe7a51e5c4";
    private static String APPSECRET = "f9b566453fc559487eb0f6419aa42030";

    private List<String> data_list;
    private ArrayAdapter<String> arr_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        initView();
        addEditTextListener();
        SMSSDK.initSDK(mContext, APPKEY, APPSECRET);
        EventHandler eventHandler = new EventHandler() {
            @Override
            public void afterEvent(int event, int result, Object data) {
                Message msg = new Message();
                msg.arg1 = event;
                msg.arg2 = result;
                msg.obj = data;
                handler.sendMessage(msg);
            }
        };
        SMSSDK.registerEventHandler(eventHandler);
    }

    private void addEditTextListener() {
        etPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 11) {
                    if (!AMUtils.isMobile(etPhone.getText().toString().trim())) {
                        T.showShort(mContext, "请输入正确的手机号码");
                        return;
                    }
                    btnGetCord.setClickable(true);
                    btnGetCord.setBackgroundColor(Color.argb(255, 0, 121, 255));
                } else {
                    btnGetCord.setClickable(false);
                    btnGetCord.setBackgroundColor(Color.argb(204, 199, 199, 199));
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void initView() {
        btnGetCord.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_get_cord:
                phone = etPhone.getText().toString().trim();
                if (!TextUtils.isEmpty(phone)) {
                    if (phone.length() == 11) {
                        LoadDialog.show(mContext, "正在请求服务器中...");
                        SMSSDK.getVerificationCode("86", phone);
                        etCode.requestFocus();
                        btnGetCord.setVisibility(View.GONE);
                    } else {
                        Toast.makeText(RegisterActivity.this, "请输入完整电话号码", Toast.LENGTH_LONG).show();
                        etPhone.requestFocus();
                    }
                } else {
                    Toast.makeText(RegisterActivity.this, "请输入您的电话号码", Toast.LENGTH_LONG).show();
                    etPhone.requestFocus();
                }
                break;
            case R.id.btn_register:
                nickname = etNickname.getText().toString();
                password = etPassword.getText().toString();
                phone = etPhone.getText().toString();
                iCord = etCode.getText().toString().trim();
                recommendCode = etRecommendCode.getText().toString();
                /*question = etQuestion.getText().toString();
                answer = etAnswer.getText().toString();*/
                if (TextUtils.isEmpty(nickname)) {
                    T.showShort(mContext, "昵称不能为空");
                    return;
                }
                if (TextUtils.isEmpty(phone)) {
                    T.showShort(mContext, "手机号不能为空");
                    return;
                }
                if (TextUtils.isEmpty(password) || password.length() < 4) {
                    T.showShort(mContext, "密码不能为空且长度不能小于4");
                    return;
                }
                if(TextUtils.isEmpty(recommendCode)){
                    T.showShort(mContext,"推荐id不能为空");
                    return;
                }
                if (!TextUtils.isEmpty(iCord)) {
                    if (iCord.length() == 4) {
                        SMSSDK.submitVerificationCode("86", phone, iCord);
                        flag = false;
                    } else {
                        T.showShort(mContext, "请输入完整验证码");
                        etCode.requestFocus();
                        return;
                    }
                } else {
                    T.showShort(mContext, "请输入验证码");
                    etCode.requestFocus();
                    return;
                }
                LoadDialog.show(mContext);
                initRegister();
                break;
            default:
                break;
        }
    }

    private void initRegister() {
        HttpUtils.postRegisterRequest("/register", nickname, phone, password, question, answer, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                T.showShort(mContext, "/register---" + e);
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
                        T.showShort(mContext, "注册成功");
                        Intent intent = new Intent(mContext, LoginActivity.class);
                        intent.putExtra("phone", phone);
                        intent.putExtra("password", password);
                        setResult(RESULT_OK, intent);
                        LoadDialog.dismiss(mContext);
                        RegisterActivity.this.finish();
                        break;
                    case 0:
                        LoadDialog.dismiss(mContext);
                        T.showShort(mContext, "一个号码只能注册一个用户哦");
                        break;
                    case 1000:
                        LoadDialog.dismiss(mContext);
                        T.showShort(mContext, "推荐信息不一致，请检查推荐信息");
                        break;
                    default:
                        break;

                }
            }
        });
    }

    //验证码送成功后提示文字
    private void reminderText() {
        LoadDialog.dismiss(mContext);
        tvMessage.setVisibility(View.VISIBLE);
        handlerText.sendEmptyMessageDelayed(1, 1000);
    }

    Handler handlerText = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                if (time > 0) {
                    tvMessage.setVisibility(View.VISIBLE);
                    tvMessage.setText("验证码已发送" + time + "秒");
                    time--;
                    handlerText.sendEmptyMessageDelayed(1, 1000);
                } else {
                    tvMessage.setText("提示信息");
                    time = 60;
                    tvMessage.setVisibility(View.GONE);
                    btnGetCord.setVisibility(View.VISIBLE);
                }
            } else {
                etCode.setText("");
                tvMessage.setText("提示信息");
                time = 60;
                tvMessage.setVisibility(View.GONE);
                btnGetCord.setVisibility(View.VISIBLE);
            }
        }
    };

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int event = msg.arg1;
            int result = msg.arg2;
            Object data = msg.obj;
            Log.e("event", "event=" + event);
            if (result == SMSSDK.RESULT_COMPLETE) {
                //短信注册成功后，返回MainActivity,然后提示新好友
                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {//提交验证码成功,验证通过
                    Toast.makeText(getApplicationContext(), "验证码校验成功", Toast.LENGTH_SHORT).show();
                    handlerText.sendEmptyMessage(2);
                    LoadDialog.dismiss(mContext);
                } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {//服务器验证码发送成功
                    reminderText();
                    Toast.makeText(getApplicationContext(), "验证码已经发送", Toast.LENGTH_SHORT).show();
                } else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {//返回支持发送验证码的国家列表
                    Toast.makeText(getApplicationContext(), "获取国家列表成功", Toast.LENGTH_SHORT).show();
                }
            } else {
                if (flag) {
                    btnGetCord.setVisibility(View.VISIBLE);
                    Toast.makeText(RegisterActivity.this, "验证码获取失败，请重新获取", Toast.LENGTH_SHORT).show();
                    etPhone.requestFocus();
                    LoadDialog.dismiss(mContext);
                    return;
                } else {
                    ((Throwable) data).printStackTrace();
//                    int resId = getStringRes(RegisterActivity.this, "smssdk_network_error");
                    Toast.makeText(RegisterActivity.this, "验证码错误", Toast.LENGTH_SHORT).show();
                    etCode.selectAll();
                    LoadDialog.dismiss(mContext);
//                    if (resId > 0) {
//                        Toast.makeText(RegisterActivity.this, resId + "", Toast.LENGTH_SHORT).show();
//                    }
                    return;
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterAllEventHandler();
    }
}
