package com.min.smalltalk.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.min.smalltalk.R;
import com.min.smalltalk.base.BaseActivity;
import com.min.smalltalk.constant.Const;
import com.xys.libzxing.zxing.activity.CaptureActivity;
import com.xys.libzxing.zxing.encoding.EncodingUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 二维码
 */
public class ZxingActivity extends BaseActivity {

    @BindView(R.id.iv_title_back)
    ImageView ivTitleBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_zxing)
    ImageView ivZxing;
    @BindView(R.id.btn)
    Button btn;
    @BindView(R.id.textView2)
    TextView textView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zxing);
        ButterKnife.bind(this);
        Intent intent = getIntent();
//        String input=intent.getStringExtra("Id");
        String input = "http://www.baidu.com";
        String port = getSharedPreferences("config", MODE_PRIVATE).getString(Const.LOGIN_PORTRAIT, "");
        Bitmap bitmap = null;
        bitmap = EncodingUtils.createQRCode(input, 500, 500,
                BitmapFactory.decodeResource(getResources(), 0));
        ivZxing.setImageBitmap(bitmap);
    }

    @OnClick({R.id.iv_title_back, R.id.btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_title_back:
                ZxingActivity.this.finish();
                break;
            case R.id.btn:
                startActivityForResult(new Intent(mContext, CaptureActivity.class), 0);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            if (bundle != null) {
                String result = bundle.getString("result");
                textView2.setText(result);
            }
        }
    }

}
