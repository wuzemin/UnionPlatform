package com.min.smalltalk.activity;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.min.mylibrary.util.PhotoUtils;
import com.min.mylibrary.util.T;
import com.min.mylibrary.widget.dialog.BottomMenuDialog;
import com.min.mylibrary.widget.dialog.LoadDialog;
import com.min.smalltalk.R;
import com.min.smalltalk.base.BaseActivity;
import com.min.smalltalk.bean.Code;
import com.min.smalltalk.constant.Const;
import com.min.smalltalk.network.HttpUtils;
import com.min.smalltalk.utils.DateUtils;
import com.min.smalltalk.wedget.Wheel.JudgeDate;
import com.min.smalltalk.wedget.Wheel.ScreenInfo;
import com.min.smalltalk.wedget.Wheel.WheelMain;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.rong.imageloader.core.ImageLoader;
import okhttp3.Call;

/**
 * 添加群活动
 */
public class GroupAddFlexibleActivity extends BaseActivity {

    @BindView(R.id.iv_title_back)
    ImageView ivTitleBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_title_right)
    TextView tvTitleRight;
    @BindView(R.id.iv_group_activity_head)
    ImageView ivGroupActivityHead;
    @BindView(R.id.et_activity_name)
    EditText etActivityName;
    @BindView(R.id.tv_activity_start_time)
    TextView tvActivityStartTime;
    @BindView(R.id.tv_activity_end_time)
    TextView tvActivityEndTime;
    @BindView(R.id.et_activity_place)
    EditText etActivityPlace;
    @BindView(R.id.et_activity_content)
    EditText etdActivityContent;
    @BindView(R.id.tv_select_time)
    TextView tvSelectTime;

    private PhotoUtils photoUtils;
    private String imageUri;
    private Uri selectUri;
    private File imageFile;
    private BottomMenuDialog dialog;
    private WheelMain wheelMainDate;
    private String beginTime;
    private int number=0;

    private String userId,group_id,actives_title,actives_content,actives_start,actives_end,actives_address;
    private int actives_limit=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_add_flexible);
        ButterKnife.bind(this);
        setPortraitChangListener();
        initView();
    }


    private void initView() {
        tvTitle.setText("添加群活动");
        tvTitleRight.setVisibility(View.VISIBLE);
        tvTitleRight.setText("添加");
        userId=getSharedPreferences("config",MODE_PRIVATE).getString(Const.LOGIN_ID,"");
        group_id=getIntent().getStringExtra("group_id");
    }

    @OnClick({R.id.iv_title_back, R.id.tv_title_right, R.id.iv_group_activity_head, R.id.tv_activity_start_time, R.id.tv_activity_end_time})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_title_back:
                GroupAddFlexibleActivity.this.finish();
                break;
            case R.id.iv_group_activity_head:
                ShowPhotoDialog();
                break;
            case R.id.tv_activity_start_time:
                number=1;
                showSTimePopupWindow(number);
                break;
            case R.id.tv_activity_end_time:
                number=2;
                showSTimePopupWindow(number);
                break;
            case R.id.tv_title_right:
                actives_title=etActivityName.getText().toString();
                actives_content=etdActivityContent.getText().toString();
                actives_address=etActivityPlace.getText().toString();
                if(imageFile==null){
                    T.showShort(mContext,"群活动图标不能为空");
                    return;
                }
                if(TextUtils.isEmpty(actives_title)){
                    T.showShort(mContext,"群活动标题不能为空");
                    return;
                }
                if(TextUtils.isEmpty(actives_content)){
                    T.showShort(mContext,"群活动内容不能为空");
                    return;
                }
                if(TextUtils.isEmpty(actives_start)){
                    T.showShort(mContext,"群活动开始时间不能为空");
                    return;
                }
                if(TextUtils.isEmpty(actives_end)){
                    T.showShort(mContext,"群活动结束时间不能为空");
                    return;
                }
                if(TextUtils.isEmpty(actives_address)){
                    T.showShort(mContext,"群活动地址不能为空");
                    return;
                }
                LoadDialog.show(mContext);
                addFlexible();

                break;
        }
    }

    private void addFlexible() {
        JSONArray jsonArray=new JSONArray();
        JSONObject row=new JSONObject();
        try {
            row.put("userId",userId);
            row.put("group_id",group_id);
            row.put("actives_title",actives_title);
            row.put("actives_content",actives_content);
            row.put("actives_limit",actives_limit);
            row.put("actives_start",actives_start);
            row.put("actives_end",actives_end);
            row.put("actives_address",actives_address);
            jsonArray.put(row);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String string = jsonArray.toString();
        HttpUtils.postAddGroupFlexible("/foundActives", string, imageFile, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                T.showShort(mContext,"/foundActives---------"+e);
                return;
            }

            @Override
            public void onResponse(String response, int id) {
                Gson gson=new Gson();
                Type type=new TypeToken<Code<Integer>>(){}.getType();
                Code<Integer> code = gson.fromJson(response,type);
                if(code.getCode()==200){
                    T.showShort(mContext,"创建成功");
                    Intent intent=new Intent();
                    setResult(0,intent);
                    finish();
                    LoadDialog.dismiss(mContext);
                }else {
                    T.showShort(mContext,"创建失败");
                    LoadDialog.dismiss(mContext);
                }
            }
        });
    }

    private java.text.DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private void showSTimePopupWindow(final int number1) {
        WindowManager manager = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
        Display defaultDisplay = manager.getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        defaultDisplay.getMetrics(outMetrics);
        int width = outMetrics.widthPixels;
        View menuView = LayoutInflater.from(this).inflate(R.layout.popupwindow_select_time,null);
        final PopupWindow mPopupWindow = new PopupWindow(menuView, (int)(width*0.8),
                ActionBar.LayoutParams.WRAP_CONTENT);
        ScreenInfo screenInfoDate = new ScreenInfo(this);
        wheelMainDate = new WheelMain(menuView, true);
        wheelMainDate.screenheight = screenInfoDate.getHeight();
        String time = DateUtils.currentMonth().toString();
        Calendar calendar = Calendar.getInstance();
        if (JudgeDate.isDate(time, "yyyy-MM-DD")) {
            try {
                calendar.setTime(new Date(time));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        wheelMainDate.initDateTimePicker(year, month, day, hours,minute);
        final String currentTime = wheelMainDate.getTime().toString();
        mPopupWindow.setAnimationStyle(R.style.AnimationPreview);
        mPopupWindow.setTouchable(true);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        mPopupWindow.showAtLocation(tvSelectTime, Gravity.CENTER, 0, 0);
        mPopupWindow.setOnDismissListener(new poponDismissListener());
        backgroundAlpha(0.6f);
        TextView tv_cancle = (TextView) menuView.findViewById(R.id.tv_cancle);
        TextView tv_ensure = (TextView) menuView.findViewById(R.id.tv_ensure);
        TextView tv_pop_title = (TextView) menuView.findViewById(R.id.tv_pop_title);
        tv_pop_title.setText("选择起始时间");
        tv_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                mPopupWindow.dismiss();
                backgroundAlpha(1f);
            }
        });
        tv_ensure.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                beginTime = wheelMainDate.getTime().toString();
                try {
                    Date begin = dateFormat.parse(currentTime);
                    Date end = dateFormat.parse(beginTime);
                    if(number1==1){
                        actives_start=DateUtils.formateStringH(beginTime,DateUtils.yyyyMMddHHmm);
                        tvActivityStartTime.setText(actives_start);
                    }else {
                        actives_end=DateUtils.formateStringH(beginTime,DateUtils.yyyyMMddHHmm);
                        tvActivityEndTime.setText(actives_end);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                mPopupWindow.dismiss();
                backgroundAlpha(1f);
            }
        });
    }

    /**
     * 图片选择
     */
    private void ShowPhotoDialog() {
        if(dialog!=null && dialog.isShowing()){
            dialog.dismiss();
        }
        dialog=new BottomMenuDialog(mContext);
        dialog.setPhotographListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dialog!=null && dialog.isShowing()){
                    dialog.dismiss();
                }
                photoUtils.takePicture(GroupAddFlexibleActivity.this);
            }
        });
        dialog.setLocalphotoListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dialog!=null && dialog.isShowing()){
                    dialog.dismiss();
                }
                photoUtils.selectPicture(GroupAddFlexibleActivity.this);
            }
        });
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case PhotoUtils.INTENT_CROP:
            case PhotoUtils.INTENT_TAKE:
            case PhotoUtils.INTENT_SELECT:
                photoUtils.onActivityResult(GroupAddFlexibleActivity.this, requestCode, resultCode, data);
        }
    }
    private void setPortraitChangListener() {
        photoUtils  = new PhotoUtils(new PhotoUtils.OnPhotoResultListener() {
            @Override
            public void onPhotoResult(Uri uri) {
                if(uri!=null && uri.getPath()!=null){
                    selectUri=uri;
                    imageFile=new File(selectUri.getPath());
                    imageUri=selectUri.toString();
                    ImageLoader.getInstance().clearDiskCache();
                    ImageLoader.getInstance().displayImage(imageUri,ivGroupActivityHead);
                }
            }

            @Override
            public void onPhotoCancel() {

            }
        });
    }


    class poponDismissListener implements PopupWindow.OnDismissListener {
        @Override
        public void onDismiss() {
            backgroundAlpha(1f);
        }
    }
    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha;
        getWindow().setAttributes(lp);
    }
}
