package com.min.smalltalk.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.min.mylibrary.util.L;
import com.min.mylibrary.util.T;
import com.min.smalltalk.AppContext;
import com.min.smalltalk.MainActivity;
import com.min.smalltalk.R;
import com.min.smalltalk.base.BaseActivity;
import com.min.smalltalk.bean.Code;
import com.min.smalltalk.bean.FriendInfo;
import com.min.smalltalk.bean.GroupMember;
import com.min.smalltalk.bean.UserId;
import com.min.smalltalk.constant.Const;
import com.min.smalltalk.db.FriendInfoDAOImpl;
import com.min.smalltalk.db.GroupMemberDAOImpl;
import com.min.smalltalk.network.HttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.rong.eventbus.EventBus;
import io.rong.imkit.RongIM;
import io.rong.imkit.fragment.ConversationFragment;
import io.rong.imkit.fragment.UriFragment;
import io.rong.imkit.userInfoCache.RongUserInfoManager;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.UserInfo;
import okhttp3.Call;

/**
 * 会话页面
 */
public class ConversationActivity extends BaseActivity implements View.OnClickListener, RongIM.UserInfoProvider {

    /*@BindView(R.id.iv_title_back)
    ImageView ivTitleBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_title_right)
    ImageView ivTitleRight;*/
    private ImageView ivTitleBack, ivTitleRight;
    private TextView tvTitle;


    private List<FriendInfo> list;

    private String TAG = ConversationActivity.class.getSimpleName();
    //对方id
    private String mTargetId;
    //会话类型
    private Conversation.ConversationType mConversationType;
    //是否在讨论组内，如果不在讨论组内，则进入不到讨论组设置页面
    private boolean isFromPush = false;

    private String title;

    private SharedPreferences sp;

    private FriendInfoDAOImpl friendInfoDAO;
    private GroupMemberDAOImpl groupMemberDAO;

    private RongIM.IGroupMemberCallback mMentionMemberCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        friendInfoDAO = new FriendInfoDAOImpl(mContext);
        groupMemberDAO = new GroupMemberDAOImpl(mContext);

        ivTitleBack = (ImageView) findViewById(R.id.iv_title_back);
        ivTitleRight = (ImageView) findViewById(R.id.iv_title_right);
        tvTitle = (TextView) findViewById(R.id.tv_title);

        ivTitleBack.setOnClickListener(this);
        ivTitleRight.setOnClickListener(this);

        sp = getSharedPreferences("config", MODE_PRIVATE);

        Intent intent = getIntent();
        initPortrait();   //头像

        if (intent == null || intent.getData() == null)
            return;

        mTargetId = intent.getData().getQueryParameter("targetId");
        //10000 为 Demo Server 加好友的 id，若 targetId 为 10000，则为加好友消息，默认跳转到 NewFriendListActivity
        // Demo 逻辑
        newFriend();  //好友请求

        setActionBarTitle(mConversationType, mTargetId);
        //展示如何从 Intent 中得到 融云会话页面传递的 Uri
//        intent.getData().getLastPathSegment();//获得当前会话类型
        mConversationType = Conversation.ConversationType.valueOf(intent.getData()
                .getLastPathSegment().toUpperCase(Locale.getDefault()));

        title = intent.getData().getQueryParameter("title");

        //私聊---群聊
        /*if(mConversationType== Conversation.ConversationType.PRIVATE){
            ivTitleRight.setVisibility(View.VISIBLE);
            ivTitleRight.setImageResource(R.mipmap.icon1_menu);
        }*/
        if (mConversationType == Conversation.ConversationType.GROUP) {
            ivTitleRight.setVisibility(View.VISIBLE);
            ivTitleRight.setImageResource(R.mipmap.icon2_menu);
        }
        tvTitle.setText(title);

        isPushMessage(intent);
//        if("ConversationActivity".equals(this.getClass().getSimpleName())){
//            EventBus.getDefault().register(this);
//        }

        // android 6.0 以上版本，监听SDK权限请求，弹出对应请求框。
        initPermission();

        AppContext.getInstance().pushActivity(this);

        RongIM.getInstance().setGroupMembersProvider(new RongIM.IGroupMembersProvider() {
            @Override
            public void getGroupMembers(String groupId, RongIM.IGroupMemberCallback callback) {
                getGroupMembersForMention();
                mMentionMemberCallback = callback;
            }
        });

    }

    private void newFriend() {
        final String userid = sp.getString(Const.LOGIN_ID, "");
        HttpUtils.postAddFriender("/all_unread_friends", userid, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                T.showShort(mContext, "/all_unread_friends--------" + e);
                return;
            }

            @Override
            public void onResponse(String response, int id) {
                Gson gson = new Gson();
                Type type = new TypeToken<Code<List<UserId>>>() {
                }.getType();
                Code<List<UserId>> code = gson.fromJson(response, type);
                if (code.getCode() == 200) {
                    List<UserId> userIds = code.getMsg();
                    for (int i = 0; i < userIds.size(); i++) {
                        if (mTargetId != null && mTargetId.equals(userIds.get(i).getUserId())) {
                            startActivity(new Intent(ConversationActivity.this, NewFriendListActivity.class));
                            return;
                        }
                    }
                }
            }
        });
        /*if (mTargetId != null && mTargetId.equals("10000")) {
            startActivity(new Intent(ConversationActivity.this, NewFriendListActivity.class));
            return;
        }*/
    }

    /**
     * 用户头像
     */
    private void initPortrait() {
        String uid = sp.getString(Const.LOGIN_ID, "");
        String nickName = sp.getString(Const.LOGIN_NICKNAME, "");
        String portraitUri = sp.getString(Const.LOGIN_PORTRAIT, "");
        list = new ArrayList<>();
        list.add(new FriendInfo(uid,nickName,portraitUri))
        ;list.clear();
        list=friendInfoDAO.findAll(uid);

        RongIM.getInstance().refreshUserInfoCache(new UserInfo(uid, nickName, Uri.parse(portraitUri)));
        L.e("-------sss----",list.toString());
        RongIM.setUserInfoProvider(this, true);
    }

    /**
     * 群头像
     */
    private void getGroupMembersForMention() {
        List<GroupMember> groupMembers = groupMemberDAO.findAll(mTargetId);
                List < UserInfo > userInfos = new ArrayList<>();
        if (groupMembers != null) {
            for (GroupMember groupMember : groupMembers) {
                if (groupMember != null) {
                    UserInfo userInfo = new UserInfo(groupMember.getUserId(), groupMember.getUserName(),
                            Uri.parse(groupMember.getUserPortraitUri()));
                    userInfos.add(userInfo);
                }
            }
        }
        mMentionMemberCallback.onGetGroupMembersResult(userInfos);

    }

    /**
     * 判断是否是 Push 消息，判断是否需要做 connect 操作
     */
    private void isPushMessage(Intent intent) {

        if (intent == null || intent.getData() == null)
            return;

        //push
        if (intent.getData().getScheme().equals("rong") && intent.getData().getQueryParameter("isFromPush") != null) {

            //通过intent.getData().getQueryParameter("push") 为true，判断是否是push消息
            if (intent.getData().getQueryParameter("isFromPush").equals("true")) {
                //只有收到系统消息和不落地 push 消息的时候，pushId 不为 null。而且这两种消息只能通过 server 来发送，客户端发送不了。
//                RongIM.getInstance().getRongIMClient().recordNotificationEvent(id);
                isFromPush = true;
            } else if (RongIM.getInstance().getCurrentConnectionStatus().equals(
                    RongIMClient.ConnectionStatusListener.ConnectionStatus.DISCONNECTED)) {
                if (intent.getData().getPath().contains("conversation/system")) {
                    Intent intent1 = new Intent(mContext, MainActivity.class);
                    intent1.putExtra("systemconversation", true);
                    startActivity(intent1);
                    finish();
                    return;
                }
                enterActivity();
            } else {
                if (intent.getData().getPath().contains("conversation/system")) {
                    Intent intent1 = new Intent(mContext, MainActivity.class);
                    intent1.putExtra("systemconversation", true);
                    startActivity(intent1);
                    finish();
                    return;
                }
                enterFragment(mConversationType, mTargetId);
            }

        } else {
            if (RongIM.getInstance().getCurrentConnectionStatus().equals(
                    RongIMClient.ConnectionStatusListener.ConnectionStatus.DISCONNECTED)) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        enterActivity();
                    }
                }, 300);
            } else {
                enterFragment(mConversationType, mTargetId);
            }
        }
    }


    /**
     * 收到 push 消息后，选择进入哪个 Activity
     * 如果程序缓存未被清理，进入 MainActivity
     * 程序缓存被清理，进入 LoginActivity，重新获取token
     * <p/>
     * 作用：由于在 manifest 中 intent-filter 是配置在 ConversationActivity 下面，所以收到消息后点击notifacition 会跳转到 DemoActivity。
     * 以跳到 MainActivity 为例：
     * 在 ConversationActivity 收到消息后，选择进入 MainActivity，这样就把 MainActivity 激活了，当你读完收到的消息点击 返回键 时，程序会退到
     * MainActivity 页面，而不是直接退回到 桌面。
     */
    private void enterActivity() {

        String token = sp.getString(Const.LOGIN_TOKEN, "");//loginToken

        if (token.equals("default")) {
            L.e("ConversationActivity push", "push2");
            startActivity(new Intent(ConversationActivity.this, LoginActivity.class));
            finish();
        } else {
            L.e("ConversationActivity push", "push3");
            reconnect(token);
        }
    }

    private void reconnect(String token) {

        RongIM.connect(token, new RongIMClient.ConnectCallback() {
            @Override
            public void onTokenIncorrect() {
                Log.e(TAG, "---onTokenIncorrect--");
            }

            @Override
            public void onSuccess(String s) {
                Log.i(TAG, "---onSuccess--" + s);
                L.e("ConversationActivity push", "push4");

                Intent intent = new Intent();
                intent.setClass(ConversationActivity.this, MainActivity.class);
                intent.putExtra("PUSH_CONVERSATIONTYPE", mConversationType.toString());
                intent.putExtra("PUSH_TAR   GETID", mTargetId);
                startActivity(intent);
                finish();

                enterFragment(mConversationType, mTargetId);
            }

            @Override
            public void onError(RongIMClient.ErrorCode e) {
                Log.e(TAG, "---onError--" + e);
                enterFragment(mConversationType, mTargetId);
            }
        });

    }

    private ConversationFragment fragment;

    /**
     * 加载会话页面 ConversationFragment
     *
     * @param mConversationType 会话类型
     * @param mTargetId         会话 Id
     */


    private void enterFragment(Conversation.ConversationType mConversationType, String mTargetId) {

        fragment = new ConversationFragment();

        Uri uri = Uri.parse("rong://" + getApplicationInfo().packageName).buildUpon()
                .appendPath("conversation").appendPath(mConversationType.getName().toLowerCase())
                .appendQueryParameter("targetId", mTargetId).build();

        fragment.setUri(uri);
        /*fragment.setInputBoardListener(new InputView.IInputBoardListener() {
            @Override
            public void onBoardExpanded(int height) {
                L.e(TAG, "onBoardExpanded h : " + height);
            }

            @Override
            public void onBoardCollapsed() {
                L.e(TAG, "onBoardCollapsed");
            }
        });*/

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        //xxx 为你要加载的 id
        transaction.add(R.id.rong_content, fragment);
        transaction.commitAllowingStateLoss();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 501) {
            finish();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        if (isFromPush) {
            isFromPush = false;
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

    }

    @Override
    protected void onDestroy() {
        if ("ConversationActivity".equals(this.getClass().getSimpleName()))
            EventBus.getDefault().unregister(this);
        RongIM.getInstance().setGroupMembersProvider(null);
        RongIM.getInstance().setRequestPermissionListener(null);
        RongIMClient.setTypingStatusListener(null);
        super.onDestroy();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (KeyEvent.KEYCODE_BACK == event.getKeyCode()) {
            if (fragment != null && !fragment.onBackPressed()) {
                if (isFromPush) {
                    isFromPush = false;
                    startActivity(new Intent(this, MainActivity.class));
                }
                AppContext.getInstance().popAllActivity();
            }
        }
        return false;
    }

    private void initPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            RongIM.getInstance().setRequestPermissionListener(new RongIM.RequestPermissionsListener() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onPermissionRequest(String[] permissions, final int requestCode) {
                    for (final String permission : permissions) {
                        if (shouldShowRequestPermissionRationale(permission)) {
                            requestPermissions(new String[]{permission}, requestCode);
                        } else {
                            int isPermissionGranted = checkSelfPermission(permission);
                            if (isPermissionGranted != PackageManager.PERMISSION_GRANTED) {
                                new AlertDialog.Builder(ConversationActivity.this)
                                        .setMessage("你需要在设置里打开以下权限:" + permission)
                                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                requestPermissions(new String[]{permission}, requestCode);
                                            }
                                        })
                                        .setNegativeButton("取消", null)
                                        .create().show();
                            }
                            return;
                        }
                    }
                }
            });
        }
    }

    /**
     * 设置会话页面 Title
     *
     * @param conversationType 会话类型
     * @param targetId         目标 Id
     */
    private void setActionBarTitle(Conversation.ConversationType conversationType, String targetId) {

        if (conversationType == null)
            return;

        if (conversationType.equals(Conversation.ConversationType.PRIVATE)) {
            setPrivateActionBar(targetId);
        } else if (conversationType.equals(Conversation.ConversationType.GROUP)) {
            setGroupActionBar(targetId);
        } else if (conversationType.equals(Conversation.ConversationType.CHATROOM)) {
            setTitle(title);
        } else if (conversationType.equals(Conversation.ConversationType.SYSTEM)) {
            setTitle(R.string.de_actionbar_system);
        } else if (conversationType.equals(Conversation.ConversationType.CUSTOMER_SERVICE)) {
            setTitle(R.string.main_customer);
        } else {
            setTitle(R.string.de_actionbar_sub_defult);
        }
    }

    /**
     * 设置私聊界面 ActionBar
     */
    private void setPrivateActionBar(String targetId) {
        if (!TextUtils.isEmpty(title)) {
            if (title.equals("null")) {
                if (!TextUtils.isEmpty(targetId)) {
                    UserInfo userInfo = RongUserInfoManager.getInstance().getUserInfo(targetId);
                    if (userInfo != null) {
                        setTitle(userInfo.getName());
                    }
                }
            } else {
                setTitle(title);
            }

        } else {
            setTitle(targetId);
        }
    }

    /**
     * 设置群聊界面 ActionBar
     *
     * @param targetId 会话 Id
     */
    private void setGroupActionBar(String targetId) {
        if (!TextUtils.isEmpty(title)) {
            setTitle(title);
        } else {
            setTitle(targetId);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_title_back:
                ConversationActivity.this.finish();
                break;
            case R.id.iv_title_right:
                enterSettingActivity();
                break;
            default:
                break;

        }
    }

    private void enterSettingActivity() {
        if (mConversationType == Conversation.ConversationType.PUBLIC_SERVICE
                || mConversationType == Conversation.ConversationType.APP_PUBLIC_SERVICE) {

            RongIM.getInstance().startPublicServiceProfile(this, mConversationType, mTargetId);
        } else {
            UriFragment fragment = (UriFragment) getSupportFragmentManager().getFragments().get(0);
            //得到讨论组的 targetId
            mTargetId = fragment.getUri().getQueryParameter("targetId");

            if (TextUtils.isEmpty(mTargetId)) {
                T.showShort(mContext, "讨论组尚未创建成功");
            }

            Intent intent = null;
            if (mConversationType == Conversation.ConversationType.GROUP) {
                intent = new Intent(this, GroupDetailActivity.class);
                intent.putExtra("conversationType", Conversation.ConversationType.GROUP);
            } else if (mConversationType == Conversation.ConversationType.PRIVATE) {
                intent = new Intent(this, PrivateChatDetailActivity.class);
                intent.putExtra("conversationType", Conversation.ConversationType.PRIVATE);
            }
            intent.putExtra("TargetId", mTargetId);
            if (intent != null) {
                startActivityForResult(intent, 500);
            }

        }
    }

    @Override
    public UserInfo getUserInfo(String s) {
        for (FriendInfo i : list) {
            if (i.getUserId().equals(s)) {
                UserInfo userInfo = new UserInfo(i.getUserId(), i.getName(), Uri.parse(i.getPortraitUri()));
                return userInfo;
            }
        }
//        UserInfoManager.getInstance().getUserInfo(s);
        return null;
    }
}
