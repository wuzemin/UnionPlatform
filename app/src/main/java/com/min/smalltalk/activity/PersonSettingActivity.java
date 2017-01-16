package com.min.smalltalk.activity;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.rong.imageloader.core.ImageLoader;
import okhttp3.Call;

public class PersonSettingActivity extends BaseActivity {

    @BindView(R.id.iv_title_back)
    ImageView ivTitleBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_title_right)
    TextView tvTitleRight;
    @BindView(R.id.iv_myHead)
    ImageView ivMyHead;
    @BindView(R.id.tv_nickname)
    TextView tvNickname;
    @BindView(R.id.rl_nickname)
    RelativeLayout rlNickname;
    @BindView(R.id.tv_sex)
    TextView tvSex;
    @BindView(R.id.rl_sex)
    RelativeLayout rlSex;
    @BindView(R.id.tv_birthday)
    TextView tvBirthday;
    @BindView(R.id.rl_birthday)
    RelativeLayout rlBirthday;
    @BindView(R.id.rl_QR_code)
    RelativeLayout rlQRCode;
    @BindView(R.id.tv_email)
    TextView tvEmail;
    @BindView(R.id.rl_email)
    RelativeLayout rlEmail;
    @BindView(R.id.activity_person_setting)
    LinearLayout activityPersonSetting;
    @BindView(R.id.tv_address)
    TextView tvAddress;
    @BindView(R.id.rl_address)
    RelativeLayout rlAddress;
    @BindView(R.id.rl_phone)
    RelativeLayout rlPhone;
    @BindView(R.id.tv_phone)
    TextView tvPhone;
    @BindView(R.id.tv_age)
    TextView tvAge;



    private SharedPreferences sp;
    private String userId;
    private String nickName;
    private String phone;
    private String email;
    private String sex1;
    private int sex;
    private String birthday;
    private String address;
    private int age;

    private PhotoUtils photoUtils;
    private Uri selectUri;
    private File imageFile;
    private String imageUrl;
    private BottomMenuDialog dialog;
    private WheelMain wheelMainDate;
    private String beginTime;
    private boolean flag=false;
    private EditText editText;
    private int str;
    private SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_setting);
        ButterKnife.bind(this);
        editor=getSharedPreferences("config",MODE_PRIVATE).edit();
        setPortraitChangListener();
        initView();

    }

    private void initView() {
        sp = getSharedPreferences("config", MODE_PRIVATE);
        userId = sp.getString(Const.LOGIN_ID, "");
        String iamge=sp.getString(Const.LOGIN_PORTRAIT,"");
        String name = sp.getString(Const.LOGIN_NICKNAME, "");
        phone = sp.getString(Const.LOGIN_PHONE, "");
        tvTitle.setText("个人信息设置");
        tvNickname.setText(name);
        tvPhone.setText(phone);

        ImageLoader.getInstance().displayImage(iamge,ivMyHead);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        str = Integer.parseInt(formatter.format(curDate));
    }

    private void setPortraitChangListener() {
        photoUtils = new PhotoUtils(new PhotoUtils.OnPhotoResultListener() {
            @Override
            public void onPhotoResult(Uri uri) {
                if (uri != null && !TextUtils.isEmpty(uri.getPath())) {
                    selectUri = uri;
                    LoadDialog.show(mContext);
                    imageFile = new File(selectUri.getPath());
                    imageUrl = selectUri.toString();
                    ImageLoader.getInstance().displayImage(imageUrl, ivMyHead);
                    changePerson(0);
                    LoadDialog.dismiss(mContext);
                }
            }

            @Override
            public void onPhotoCancel() {

            }
        });
    }

    @OnClick({R.id.iv_title_back, R.id.iv_myHead, R.id.rl_nickname, R.id.rl_sex, R.id.rl_birthday,
            R.id.rl_QR_code, R.id.rl_email, R.id.rl_phone, R.id.rl_address})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_title_back:
//                BroadcastManager.getInstance(mContext).sendBroadcast(Const.CHANGEINFO);
                finish();
                break;
            case R.id.iv_myHead:
                ShowPhotoDialog();
                break;
            case R.id.rl_nickname:
                editText=new EditText(mContext);
                new AlertDialog.Builder(mContext)
                        .setTitle("修改昵称")
                        .setView(editText)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                nickName=editText.getText().toString();
                                tvNickname.setText(nickName);
                                changePerson(1);
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .show();
                break;
            case R.id.rl_sex:
                final String[] test = new String[]{"男", "女","保密"};
                AlertDialog.Builder dialog_sex = new AlertDialog.Builder(mContext);
                dialog_sex.setTitle("选择性别");
                dialog_sex.setSingleChoiceItems(test, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String string=test[i];
                        if("男".equals(string)){
                            sex1=string;
                            sex=1;
                        }else if("女".equals(string)){
                            sex1=string;
                            sex=2;
                        }else {
                            sex1=string;
                            sex=0;
                        }
                        dialogInterface.dismiss();
                        tvSex.setText(string);
                        changePerson(2);
                    }
                });
                dialog_sex.create().show();
                break;
            case R.id.rl_birthday:
                showSTimePopupWindow();
                break;
            case R.id.rl_QR_code:
                Intent intent1=new Intent(mContext,ZxingActivity.class);
                intent1.putExtra("Id",userId);
                startActivity(intent1);
                break;
            case R.id.rl_email:
                editText=new EditText(mContext);
                new AlertDialog.Builder(mContext)
                        .setTitle("修改邮箱")
                        .setView(editText)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                email=editText.getText().toString();
                                tvEmail.setText(email);
                                changePerson(3);
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .show();
                break;
            case R.id.rl_address:
                editText=new EditText(mContext);
                new AlertDialog.Builder(mContext)
                        .setTitle("修改地址")
                        .setView(editText)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                address=editText.getText().toString();
                                tvAddress.setText(address);
                                changePerson(4);
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .show();
                break;
            case R.id.rl_phone:
                Intent intent=new Intent(mContext,MyPhoneActivity.class);
                intent.putExtra("phone",phone);
                startActivityForResult(intent,1);
                break;
            default:
                break;
        }
    }

    /**
     * 修改个人资料
     */
    private void changePerson(final int index) {
        JSONArray jsonArray=new JSONArray();
        JSONObject row=new JSONObject();
        try {
            row.put("userId",userId);
            row.put("nickname",nickName);
            row.put("sex",sex);
            row.put("email",email);
            row.put("phone",phone);
            row.put("address",address);
            row.put("birth_date",birthday);
            row.put("age",age);
            jsonArray.put(row);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String string=jsonArray.toString();
        if(index!=0){
            HttpUtils.postChangePerson("/editUserInfo", string, new StringCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {
                    T.showShort(mContext,"/editUserInfo----"+e);
                    return;
                }

                @Override
                public void onResponse(String response, int id) {
                    Gson gson=new Gson();
                    Type type=new TypeToken<Code<Integer>>(){}.getType();
                    Code<Integer> code = gson.fromJson(response,type);
                    if(code.getCode()==200){
                        switch (index){
                            case 1:   //昵称
                                editor.putString(Const.LOGIN_NICKNAME, nickName);
                                editor.commit();
                                T.showShort(mContext,"修改昵称成功");
                                break;
                            case 2:   //性别
                                editor.putString(Const.LOGIN_SEX, sex1);
                                editor.commit();
                                T.showShort(mContext,"修改性别成功");
                                break;
                            case 3:   //邮箱
                                editor.putString(Const.LOGIN_EMAIL, email);
                                editor.commit();
                                T.showShort(mContext,"修改邮箱成功");
                                break;
                            case 4:   //地址
                                editor.putString(Const.LOGIN_ADDRESS, address);
                                editor.commit();
                                T.showShort(mContext,"修改地址成功");
                                break;
                            case 5:   //生日
                                editor.putString(Const.LOGIN_BIRTHDAY, birthday);
                                editor.commit();
                                T.showShort(mContext,"修改生日成功");
                                break;
                            case 6:   //电话
                                editor.putString(Const.LOGIN_PHONE, phone);
                                editor.commit();
                                T.showShort(mContext,"修改电话成功");
                                break;
                            default:
                                break;

                        }
                    }else {
                        T.showShort(mContext,"修改失败");
                    }
                }
            });
        }else {
            HttpUtils.postChangePerson("/editUserInfo", string, imageFile, new StringCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {
                    T.showShort(mContext, "/editUserInfo----" + e);
                    return;
                }

                @Override
                public void onResponse(String response, int id) {
                    Gson gson = new Gson();
                    Type type = new TypeToken<Code<Integer>>() {
                    }.getType();
                    Code<Integer> code = gson.fromJson(response, type);
                    if (code.getCode() == 200) {
                        editor.putString(Const.LOGIN_PORTRAIT, imageUrl);
                        editor.commit();
                        T.showShort(mContext, "修改成功");
                    } else {
                        T.showShort(mContext, "修改失败");
                    }
                }
            });
        }
    }

    /**
     * 时间
     */
    private void showSTimePopupWindow() {
        WindowManager manager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display defaultDisplay = manager.getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        defaultDisplay.getMetrics(outMetrics);
        int width = outMetrics.widthPixels;
        View menuView = LayoutInflater.from(this).inflate(R.layout.popupwindow_select_time, null);
        final PopupWindow mPopupWindow = new PopupWindow(menuView, (int) (width * 0.8),
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
        wheelMainDate.DateTimePicker(year, month, day);
        final String currentTime = wheelMainDate.getTime().toString();
        mPopupWindow.setAnimationStyle(R.style.AnimationPreview);
        mPopupWindow.setTouchable(true);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        mPopupWindow.showAtLocation(rlBirthday, Gravity.CENTER, 0, 0);
        mPopupWindow.setOnDismissListener(new poponDismissListener());
        backgroundAlpha(0.6f);
        TextView tv_cancle = (TextView) menuView.findViewById(R.id.tv_cancle);
        TextView tv_ensure = (TextView) menuView.findViewById(R.id.tv_ensure);
        TextView tv_pop_title = (TextView) menuView.findViewById(R.id.tv_pop_title);
        tv_pop_title.setText("选择时间");
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
                birthday=DateUtils.formateStringH(beginTime, DateUtils.yyyyMMddHHmm);
                tvBirthday.setText(birthday);
                int birth= Integer.parseInt(birthday.substring(0,4));
                age= str-birth;
                tvAge.setText(age+"");
                changePerson(5);
                mPopupWindow.dismiss();
                backgroundAlpha(1f);
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

    /**
     * 图片
     */
    private void ShowPhotoDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        dialog = new BottomMenuDialog(mContext);
        dialog.setPhotographListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                photoUtils.takePicture(PersonSettingActivity.this);
            }
        });
        dialog.setLocalphotoListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                photoUtils.selectPicture(PersonSettingActivity.this);
            }
        });
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PhotoUtils.INTENT_CROP:
            case PhotoUtils.INTENT_TAKE:
            case PhotoUtils.INTENT_SELECT:
                photoUtils.onActivityResult(PersonSettingActivity.this, requestCode, resultCode, data);
                break;
            case 1:
                if(resultCode==1){
                    String phone1=data.getStringExtra("phone1");
                    String phone2=data.getStringExtra("phone2");
                    if("".equals(phone2)){
                        tvPhone.setText(phone1);
                    }else {
                        tvPhone.setText(phone1 + "/" + phone2);
                    }
                    changePerson(6);
                }
                break;
        }
    }

}
