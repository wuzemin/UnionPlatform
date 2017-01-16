package com.min.smalltalk.activity;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.min.mylibrary.util.T;
import com.min.mylibrary.widget.ClearWriteEditText;
import com.min.mylibrary.widget.dialog.LoadDialog;
import com.min.smalltalk.R;
import com.min.smalltalk.base.BaseActivity;
import com.min.smalltalk.base.BaseRecyclerAdapter;
import com.min.smalltalk.base.BaseRecyclerHolder;
import com.min.smalltalk.bean.Code;
import com.min.smalltalk.constant.Const;
import com.min.smalltalk.network.HttpUtils;
import com.min.smalltalk.utils.DateUtils;
import com.min.smalltalk.wedget.ItemDivider;
import com.min.smalltalk.wedget.SwitchButton;
import com.min.smalltalk.wedget.Wheel.JudgeDate;
import com.min.smalltalk.wedget.Wheel.ScreenInfo;
import com.min.smalltalk.wedget.Wheel.WheelMain;
import com.zhy.http.okhttp.callback.StringCallback;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.MentionedInfo;
import io.rong.imlib.model.Message;
import io.rong.message.TextMessage;
import okhttp3.Call;

/**
 * 创建投票
 */
public class AddVoteActivity extends BaseActivity {

    @BindView(R.id.iv_title_back)
    ImageView ivTitleBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.et_vote_title)
    ClearWriteEditText etVoteTitle;
    @BindView(R.id.rv_vote)
    RecyclerView rvVote;
    @BindView(R.id.sw_vote_multiselect)
    SwitchButton swVoteMultiselect;
    @BindView(R.id.tv_vote_period)
    TextView tvVotePeriod;
    @BindView(R.id.ll_vote_period)
    LinearLayout llVotePeriod;
    @BindView(R.id.btn_create_vote)
    Button btnCreateVote;
    @BindView(R.id.ll_add_new)
    LinearLayout llAddNew;
    @BindView(R.id.et_vote_content_1)
    ClearWriteEditText etVoteContent1;
    @BindView(R.id.et_vote_content_2)
    ClearWriteEditText etVoteContent2;
    @BindView(R.id.rl_add_content)
    RelativeLayout rlAddContent;
    @BindView(R.id.et_add_content)
    EditText etAddContent;

    private BaseRecyclerAdapter<String> adapter;
    private SharedPreferences sp;
    private String userId;
    private String groupId;
    private List<String> contentList = new ArrayList<>(0);
    private List<String> data;
    private WheelMain wheelMainDate;
    private String beginTime;
    private String period;
    private boolean select = false;
    private View view;
    private EditText editText;
    private String addContent;
    private Conversation.ConversationType mConversationType;
    private String targetId;
    private String title;
    private SimpleDateFormat format;
    private Date date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vote);
        ButterKnife.bind(this);
        format=new SimpleDateFormat("yyyy-MM-dd HH:mm");
        date=new Date(System.currentTimeMillis());
        initView();
        initAdapter();
    }



    private void initView() {
        sp = getSharedPreferences("config", MODE_PRIVATE);
        userId = sp.getString(Const.LOGIN_ID, "");
        groupId = getIntent().getStringExtra("group_id");
        view = LayoutInflater.from(mContext).inflate(R.layout.item_vote, null);
//        editText = (EditText) view.findViewById(R.id.et_vote_content);
        Intent intent=getIntent();
        targetId=intent.getStringExtra("targetId");
        mConversationType= Conversation.ConversationType.setValue(intent.getIntExtra("conversationType",0));

    }



    private void initAdapter() {
        adapter = new BaseRecyclerAdapter<String>(mContext, contentList, R.layout.item_vote) {
            @Override
            public void convert(BaseRecyclerHolder holder, String item, int position, boolean isScrolling) {
//                String ss=item;
//                L.e("----------++",ss);
                holder.setText(R.id.tv_content, contentList.get(position));
            }
        };
        rvVote.setAdapter(adapter);
        LinearLayoutManager lm = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        rvVote.setLayoutManager(lm);
        rvVote.addItemDecoration(new ItemDivider(mContext, ItemDivider.VERTICAL_LIST));
        rvVote.setItemAnimator(new DefaultItemAnimator());

        adapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, View view, int position) {
                removeData(position);
//                L.e("----------++", contentList.get(position));
            }
        });
    }

    @OnClick({R.id.iv_title_back, R.id.sw_vote_multiselect, R.id.ll_vote_period, R.id.btn_create_vote,
            R.id.rl_add_content})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_title_back:
                finish();
                break;
            case R.id.rl_add_content:
                addContent=etAddContent.getText().toString();
                if(contentList.size()>13){
                    return;
                }
                if(TextUtils.isEmpty(addContent)){
                    T.showShort(mContext,"新增的内容不能为空");
                    return;
                }
                addData(contentList.size());
                break;
            case R.id.sw_vote_multiselect:    //多选
                if (select) {
                    select = false;
                } else {
                    select = true;
                }
                break;
            case R.id.ll_vote_period:   //投票期限
                showSTimePopupWindow();
                break;
            case R.id.btn_create_vote:   //投票
                title = etVoteTitle.getText().toString();
                if (TextUtils.isEmpty(title)) {
                    T.showShort(mContext, "标题不能为空");
                    return;
                }
                if(TextUtils.isEmpty(period)){
                    T.showShort(mContext,"期限不能为空");
                    return;
                }
                LoadDialog.show(mContext);
                addPeriod();
                break;
            default:
                break;
        }
    }

    /**
     * 创建投票
     */
    private void addPeriod() {
        int mode;
        if (!select) {
            mode = 0;
        } else {
            mode = 1;
        }
        String title = etVoteTitle.getText().toString();
        String content1 = etVoteContent1.getText().toString();
        String content2 = etVoteContent2.getText().toString();
        contentList.add(content1);
        contentList.add(content2);
        final Gson gson=new Gson();
        String sss=gson.toJson(contentList);
        HttpUtils.postAddVote("/found_vote", userId, groupId, title, sss, mode, period, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                T.showShort(mContext, "/found_vote------" + e);
            }

            @Override
            public void onResponse(String response, int id) {
                Type type = new TypeToken<Code<Integer>>() {}.getType();
                Code<Integer> code = gson.fromJson(response, type);
                if (code.getCode() == 200) {
                    T.showShort(mContext, "创建投票成功");
                    LoadDialog.dismiss(mContext);
                    sendMessage();
                    Intent intent=new Intent();
                    setResult(0,intent);
                    finish();
                } else {
                    T.showShort(mContext, "创建投票失败");
                    LoadDialog.dismiss(mContext);
                }
            }
        });
    }

    private void sendMessage() {
        /*RichContentMessage richContentMessage=RichContentMessage.obtain("投票活动",title,"http://rongcloud.cn/images/logo.png");
        Message myMessage=Message.obtain(groupId,mConversationType,richContentMessage);
        MentionedInfo mentionedInfo=new MentionedInfo(MentionedInfo.MentionedType.ALL,null,null);
        richContentMessage.setMentionedInfo(mentionedInfo);
        RongIM.getInstance().sendMessage(myMessage, null, null, new IRongCallback.ISendMessageCallback() {
            @Override
            public void onAttached(Message message) {

            }

            @Override
            public void onSuccess(Message message) {

            }

            @Override
            public void onError(Message message, RongIMClient.AudioErrorCode errorCode) {

            }
        });*/
        String string="有人发起了投票活动，请点击右上角进入该活动进行投票";
        if(!TextUtils.isEmpty(string)){
            TextMessage textMessage=TextMessage.obtain(RongContext.getInstance().getString(
                    R.string.group_notice_prefix)+string);
            MentionedInfo mentionedInfo=new MentionedInfo(MentionedInfo.MentionedType.ALL,null,null);
            textMessage.setMentionedInfo(mentionedInfo);
            RongIM.getInstance().sendMessage(Message.obtain(groupId, mConversationType, textMessage),
                    null, null, new IRongCallback.ISendMessageCallback() {
                        @Override
                        public void onAttached(Message message) {
//                            T.showLong(mContext,"onAttached--"+message);
                        }

                        @Override
                        public void onSuccess(Message message) {
//                          T.showLong(mContext,"onSuccess--"+message);
                        }

                        @Override
                        public void onError(Message message, RongIMClient.ErrorCode errorCode) {
//                            T.showLong(mContext,"onError--"+message+"---"+errorCode);
                        }
                    });
            finish();

        }else {
            T.showShort(mContext,"内容不能为空");
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
        View menuView = LayoutInflater.from(mContext).inflate(R.layout.popupwindow_select_time, null);
        final PopupWindow mPopupWindow = new PopupWindow(menuView, (int) (width * 0.8),
                ActionBar.LayoutParams.WRAP_CONTENT);
        ScreenInfo screenInfoDate = new ScreenInfo(AddVoteActivity.this);
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
        wheelMainDate.initDateTimePicker(year, month, day, hours, minute);
        final String currentTime = wheelMainDate.getTime().toString();
        mPopupWindow.setAnimationStyle(R.style.AnimationPreview);
        mPopupWindow.setTouchable(true);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        mPopupWindow.showAtLocation(llVotePeriod, Gravity.CENTER, 0, 0);
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
                period = DateUtils.formateStringH(beginTime, DateUtils.yyyyMMddHHmm);
                Date dateBir=stringToDate(period);
                if(dateBir.before(date)){
                    T.showLong(mContext,"不能小于当前时间");
                    return;
                }
                tvVotePeriod.setText(period);
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
     * 判断时间
     * @return
     */
    private Date stringToDate(String string) {
        String updatedAtDateStr = string.substring(0, 10) + " " + string.substring(11, 16);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date updateAtDate = null;
        try {
            updateAtDate = simpleDateFormat.parse(updatedAtDateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return updateAtDate;
    }


    /**
     * 添加--删除
     */
    public void addData(int position) {
        contentList.add(position, addContent);
        adapter.notifyItemChanged(position);
        adapter.notifyItemRangeChanged(position, contentList.size());
        etAddContent.setText("");
    }

    public void removeData(int position) {
        contentList.remove(position);
        adapter.notifyItemChanged(position);
        adapter.notifyItemRangeChanged(position, contentList.size());
    }
}
