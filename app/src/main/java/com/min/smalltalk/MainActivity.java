package com.min.smalltalk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.min.smalltalk.activity.ChatPopupWindow;
import com.min.smalltalk.activity.LoginActivity;
import com.min.smalltalk.activity.NewFriendListActivity;
import com.min.smalltalk.activity.RecommendActivity;
import com.min.smalltalk.base.BaseActivity;
import com.min.smalltalk.constant.Const;
import com.min.smalltalk.fragment.FriendFragment;
import com.min.smalltalk.fragment.PersonalFragment;
import com.min.smalltalk.server.broadcast.BroadcastManager;

import java.util.ArrayList;
import java.util.List;

import io.rong.imkit.RongIM;
import io.rong.imkit.fragment.ConversationListFragment;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.message.ContactNotificationMessage;

import static com.min.smalltalk.R.id.chat_more;

public class MainActivity extends BaseActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {

    private ViewPager mViewPager;
    private RelativeLayout rlChat,rlFriend,rlDynamic;
    private List<Fragment> mFragment = new ArrayList<>();
    private ImageView chatMore;
    private ImageView mImageInfo, mImageFriend, mImageDynamic;
    private TextView mTextInfo, mTextFriend, mTextDynamic;
    private TextView tv_recommend;

    /**
     * 会话列表的fragment
     */
    private Fragment mConversationListFragment = null;
    private boolean isDebug;
    private Context mContext;
    public static final String EXIT = "EXIT";
    private Conversation.ConversationType[] mConversationsTypes = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        isDebug = getSharedPreferences("config", MODE_PRIVATE).getBoolean("isDebug", false);
        if (RongIM.getInstance() != null &&
                RongIM.getInstance().getCurrentConnectionStatus()
                        .equals(RongIMClient.ConnectionStatusListener.ConnectionStatus.DISCONNECTED)) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    initViews();
                    initMainViewPager();
                    changeTextViewColor();
                    changeSelectedTabState(0);
                    if (RongIM.getInstance() != null && RongIM.getInstance().getCurrentConnectionStatus().equals(RongIMClient.ConnectionStatusListener.ConnectionStatus.DISCONNECTED)) {
                        reconnect();
                    }
                }
            }, 300);
        } else {
            initViews();
            initMainViewPager();
            changeTextViewColor();
            changeSelectedTabState(0);
        }
    }

    private void initViews() {
        rlChat= (RelativeLayout) findViewById(R.id.rl_chat);
        rlFriend= (RelativeLayout) findViewById(R.id.rl_friend);
        rlDynamic= (RelativeLayout) findViewById(R.id.rl_dynamic);
        chatMore= (ImageView) findViewById(R.id.chat_more);
        mImageInfo= (ImageView) findViewById(R.id.iv_chat);
        mImageFriend= (ImageView) findViewById(R.id.iv_friend);
        mImageDynamic= (ImageView) findViewById(R.id.iv_dynamic);
        mViewPager= (ViewPager) findViewById(R.id.main_viewpager);
        mTextInfo=(TextView) findViewById(R.id.tv_chat);
        mTextFriend= (TextView) findViewById(R.id.tv_friend);
        mTextDynamic= (TextView) findViewById(R.id.tv_dynamic);
        rlChat.setOnClickListener(this);
        rlFriend.setOnClickListener(this);
        rlDynamic.setOnClickListener(this);
        chatMore.setOnClickListener(this);

        tv_recommend = (TextView) findViewById(R.id.tv_recommend);
        tv_recommend.setOnClickListener(this);
    }
    private void initMainViewPager() {
        Fragment conversationList = initConversationList();
        mFragment.add(conversationList);
        mFragment.add(new FriendFragment());
        mFragment.add(new PersonalFragment());
        FragmentPagerAdapter fragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mFragment.get(position);
            }

            @Override
            public int getCount() {
                return mFragment.size();
            }
        };
        mViewPager.setAdapter(fragmentPagerAdapter);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.addOnPageChangeListener(this);
        initData();
    }
    protected void initData() {

        final Conversation.ConversationType[] conversationTypes = {
                Conversation.ConversationType.PRIVATE,
                Conversation.ConversationType.GROUP, Conversation.ConversationType.SYSTEM,
                Conversation.ConversationType.PUBLIC_SERVICE, Conversation.ConversationType.APP_PUBLIC_SERVICE
        };

//        RongIM.getInstance().addUnReadMessageCountChangedObserver(this, conversationTypes);
        getConversationPush();// 获取 push 的 id 和 target
        getPushMessage();
        BroadcastManager.getInstance(mContext).addAction(EXIT, new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                SharedPreferences.Editor editor = getSharedPreferences("config", MODE_PRIVATE).edit();
                editor.putBoolean("exit", true);
                editor.putString("loginToken", "");
                editor.putString("loginid", "");
                editor.apply();

                RongIM.getInstance().logout();
                context.unregisterReceiver(this);
                MainActivity.this.finish();
                try {
                    Thread.sleep(500);
                    android.os.Process.killProcess(android.os.Process.myPid());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private Fragment initConversationList() {
        if (mConversationListFragment == null) {
            ConversationListFragment listFragment=new ConversationListFragment();
            Uri uri = Uri.parse("rong://" + getApplicationInfo().packageName).buildUpon()
                    .appendPath("conversationlist")
                    .appendQueryParameter(Conversation.ConversationType.PRIVATE.getName(), "false") //设置私聊会话非聚合显示
                    .appendQueryParameter(Conversation.ConversationType.GROUP.getName(), "false")//设置群组会话聚合显示
                    .appendQueryParameter(Conversation.ConversationType.DISCUSSION.getName(), "false")//设置讨论组会话非聚合显示
//                    .appendQueryParameter(Conversation.ConversationType.SYSTEM.getName(), "false")//设置系统会话非聚合显示
                    .appendQueryParameter(Conversation.ConversationType.SYSTEM.getName(), "true")//系统
                    .build();
            mConversationsTypes = new Conversation.ConversationType[] {Conversation.ConversationType.PRIVATE,
                    Conversation.ConversationType.GROUP,
                    Conversation.ConversationType.PUBLIC_SERVICE,
                    Conversation.ConversationType.APP_PUBLIC_SERVICE,
                    Conversation.ConversationType.SYSTEM};
            listFragment.setUri(uri);
            return listFragment;
        } else {
            return mConversationListFragment;
        }
    }

    /**
     * 得到不落地 push 消息
     */
    private void getPushMessage() {
        Intent intent = getIntent();
        if (intent != null && intent.getData() != null && intent.getData().getScheme().equals("rong")) {
            String path = intent.getData().getPath();
            if (path.contains("push_message")) {
                SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
                String cacheToken = sharedPreferences.getString("loginToken", "");
                if (TextUtils.isEmpty(cacheToken)) {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                } else {
                    if (!RongIM.getInstance().getCurrentConnectionStatus().equals(RongIMClient.ConnectionStatusListener.ConnectionStatus.CONNECTED)) {
//                        LoadingDialog.show(mContext);
                        RongIM.connect(cacheToken, new RongIMClient.ConnectCallback() {
                            @Override
                            public void onTokenIncorrect() {

                            }

                            @Override
                            public void onSuccess(String s) {
//                                LoadingDialog.dismiss(mContext);
                            }

                            @Override
                            public void onError(RongIMClient.ErrorCode e) {

                            }
                        });
                    }
                }
            }
        }
    }

    private void reconnect() {
        SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
        String token = sp.getString(Const.LOGIN_TOKEN, "");
        RongIM.connect(token, new RongIMClient.ConnectCallback() {
            @Override
            public void onTokenIncorrect() {

            }

            @Override
            public void onSuccess(String s) {
                initViews();
                initMainViewPager();
                changeTextViewColor();
                changeSelectedTabState(0);
            }

            @Override
            public void onError(RongIMClient.ErrorCode e) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_chat:
                mViewPager.setCurrentItem(0, false);
                break;
            case R.id.rl_friend:
                mViewPager.setCurrentItem(1, false);
                break;
            case R.id.rl_dynamic:
                mViewPager.setCurrentItem(2,false);
                break;
            case chat_more:  //
                ChatPopupWindow chatPopupWindow =new ChatPopupWindow(mContext);
                chatPopupWindow.showPopupWindow(chatMore);
                break;
            case R.id.tv_recommend:
                startActivity(new Intent(mContext, RecommendActivity.class));
                break;
            default:
                break;
        }
    }
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        changeTextViewColor();
        changeSelectedTabState(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private void changeTextViewColor() {
        mImageInfo.setBackground(ContextCompat.getDrawable(mContext,R.mipmap.info_normal));
        mImageFriend.setBackground(ContextCompat.getDrawable(mContext,R.mipmap.affairs_normal));
        mImageDynamic.setBackground(ContextCompat.getDrawable(mContext,R.mipmap.personal_normal));
        mTextInfo.setTextColor(Color.parseColor("#abadbb"));
        mTextFriend.setTextColor(Color.parseColor("#abadbb"));
        mTextDynamic.setTextColor(Color.parseColor("#abadbb"));
    }

    private void changeSelectedTabState(int position) {
        switch (position) {
            case 0:
                mTextInfo.setTextColor(Color.parseColor("#0099ff"));
                mImageInfo.setBackground(ContextCompat.getDrawable(mContext,R.mipmap.info_pressed));
                break;
            case 1:
                mTextFriend.setTextColor(Color.parseColor("#0099ff"));
                mImageFriend.setBackground(ContextCompat.getDrawable(mContext,R.mipmap.affairs_pressed));
                break;
            case 2:
                mTextDynamic.setTextColor(Color.parseColor("#0099ff"));
                mImageDynamic.setBackground(ContextCompat.getDrawable(mContext,R.mipmap.personal_pressed));
                break;
            default:
                break;
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getBooleanExtra("systemconversation", false)) {
            mViewPager.setCurrentItem(0, false);
        }
    }

    //好友消息验证
    private void getConversationPush() {
        if (getIntent() != null && getIntent().hasExtra("PUSH_CONVERSATIONTYPE") && getIntent().hasExtra("PUSH_TARGETID")) {

            final String conversationType = getIntent().getStringExtra("PUSH_CONVERSATIONTYPE");
            final String targetId = getIntent().getStringExtra("PUSH_TARGETID");


            RongIM.getInstance().getConversation(Conversation.ConversationType.valueOf(conversationType), targetId, new RongIMClient.ResultCallback<Conversation>() {
                @Override
                public void onSuccess(Conversation conversation) {

                    if (conversation != null) {

                        if (conversation.getLatestMessage() instanceof ContactNotificationMessage) { //好友消息的push
                            startActivity(new Intent(MainActivity.this, NewFriendListActivity.class));
                        } else {
                            Uri uri = Uri.parse("rong://" + getApplicationInfo().packageName).buildUpon().appendPath("conversation")
                                    .appendPath(conversationType).appendQueryParameter("targetId", targetId).build();
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(uri);
                            startActivity(intent);
                        }
                    }
                }

                @Override
                public void onError(RongIMClient.ErrorCode e) {

                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
//        Fragment fragment=getSupportFragmentManager().getFragments().get(requestCode);
//        fragment.onActivityResult(requestCode,resultCode,data);
    }

}
