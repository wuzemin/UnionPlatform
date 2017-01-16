package com.min.smalltalk.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.min.smalltalk.R;
import com.min.smalltalk.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MyPhoneActivity extends BaseActivity {

    @BindView(R.id.iv_title_back)
    ImageView ivTitleBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_title_right)
    TextView tvTitleRight;
    @BindView(R.id.et_phone)
    EditText etPhone;
    @BindView(R.id.et_add_phone)
    EditText etAddPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_phone);
        ButterKnife.bind(this);
        tvTitle.setText("电话号码");
        tvTitleRight.setVisibility(View.VISIBLE);
        tvTitleRight.setText("确定");
        etPhone.setText(getIntent().getStringExtra("phone"));
    }



    @OnClick({R.id.iv_title_back, R.id.tv_title_right})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_title_back:
                Intent intent=new Intent();
                intent.putExtra("phone1",etPhone.getText().toString());
                setResult(1,intent);
                finish();
                break;
            case R.id.tv_title_right:
                Intent intent1=new Intent();
                intent1.putExtra("phone1",etPhone.getText().toString());
                intent1.putExtra("phone2",etAddPhone.getText().toString());
                setResult(1,intent1);
                finish();
                break;
        }
    }
}
