package com.min.smalltalk.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.min.mylibrary.util.L;
import com.min.mylibrary.util.T;
import com.min.smalltalk.App;
import com.min.smalltalk.AppContext;
import com.min.smalltalk.R;
import com.min.smalltalk.activity.ClaimFriendsActivity;
import com.min.smalltalk.activity.GroupListActivity;
import com.min.smalltalk.activity.NewFriendListActivity;
import com.min.smalltalk.activity.UserDetailActivity;
import com.min.smalltalk.adapter.FriendListAdapter;
import com.min.smalltalk.bean.Code;
import com.min.smalltalk.bean.FriendInfo;
import com.min.smalltalk.constant.Const;
import com.min.smalltalk.db.FriendInfoDAOImpl;
import com.min.smalltalk.network.HttpUtils;
import com.min.smalltalk.server.broadcast.BroadcastManager;
import com.min.smalltalk.wedget.CharacterParser;
import com.min.smalltalk.wedget.Generate;
import com.min.smalltalk.wedget.PinyinComparator;
import com.min.smalltalk.wedget.SideBar;
import com.min.smalltalk.wedget.image.SelectableRoundedImageView;
import com.zhy.http.okhttp.callback.StringCallback;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.rong.imageloader.core.ImageLoader;
import okhttp3.Call;

public class FriendFragment extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.et_search)
    EditText etSearch;
    @BindView(R.id.lv_friends)
    ListView mListView;
    @BindView(R.id.tv_group_dialog)
    TextView tvGroupDialog;  //中部展示的字母提示
    @BindView(R.id.sb)
    SideBar sb;
    @BindView(R.id.tv_show_no_friend)
    TextView tvShowNoFriend;
    @BindView(R.id.swipeRefresh)
    SwipeRefreshLayout mSwipeRefresh;


    private PinyinComparator mPinyinComparator;


    private List<FriendInfo> mSourceFriendList;
    private List<FriendInfo> mFriendList = new ArrayList<>(0);
    private List<FriendInfo> mFilteredFriendList;
    /**
     * 好友列表的 mFriendListAdapter
     */
    private FriendListAdapter mFriendListAdapter;
    /**
     * 汉字转换成拼音的类
     */
    private CharacterParser mCharacterParser;
    /**
     * 根据拼音来排列ListView里面的数据类
     */

    private String mId;
    private String mCacheName;
    private String header;
    private SharedPreferences sp;

    private View mHeadView;

    private TextView tvUnread, tvMe;
    private RelativeLayout rlNewfriends, rlGroup, rlPublicservice, rlMeItem;
    private SelectableRoundedImageView sivMe;

    private static final int CLICK_CONTACT_FRAGMENT_FRIEND = 2;
    private static final int REFRESH_COMPLETE=0;

    private FriendInfoDAOImpl friendInfoDAO;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friend, container, false);
        ButterKnife.bind(this, view);
        friendInfoDAO = new FriendInfoDAOImpl(getActivity());
        mSourceFriendList = new ArrayList<>();
        mFriendList = new ArrayList<>();
        mFilteredFriendList = new ArrayList<>();
        initView();
        initText();
        friendInfoDAO.delete(mId);
        initData();
        refreshUIListener();
        return view;
    }

    private void initText(){
        sb.setTextView(tvGroupDialog);
        sp = getActivity().getSharedPreferences("config", Context.MODE_PRIVATE);
        mId = sp.getString(Const.LOGIN_ID, "");
        mCacheName = sp.getString(Const.LOGIN_NICKNAME, "");
        header = sp.getString(Const.LOGIN_PORTRAIT, "");

        tvMe.setText(mCacheName);
        if (!TextUtils.isEmpty(header)) {
            ImageLoader.getInstance().displayImage(header, sivMe);
        } else {
            sivMe.setImageResource(R.mipmap.default_portrait);
        }
    }


    private void initView() {
        //刷新
        mSwipeRefresh.setOnRefreshListener(this);

        //自己信息
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        mHeadView = inflater.inflate(R.layout.item_friend_list_header, null);
        tvUnread = (TextView) mHeadView.findViewById(R.id.tv_unread);
        rlNewfriends = (RelativeLayout) mHeadView.findViewById(R.id.rl_newfriends);
        rlGroup = (RelativeLayout) mHeadView.findViewById(R.id.rl_group);
        rlPublicservice = (RelativeLayout) mHeadView.findViewById(R.id.rl_publicservice);
        rlMeItem = (RelativeLayout) mHeadView.findViewById(R.id.rl_me_item);
        sivMe = (SelectableRoundedImageView) mHeadView.findViewById(R.id.siv_me);
        tvMe = (TextView) mHeadView.findViewById(R.id.tv_me);

        mListView.addHeaderView(mHeadView);

        rlMeItem.setOnClickListener(this);  //me
        rlNewfriends.setOnClickListener(this);
        rlGroup.setOnClickListener(this);
        rlPublicservice.setOnClickListener(this);

        //设置右侧触摸监听
        sb.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                //该字母首次出现的位置
                int position = mFriendListAdapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    mListView.setSelection(position);
                }

            }
        });
    }

    private void initData() {
        /**
         * 好友列表
         */
        HttpUtils.postRequest("/friends", mId, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                T.showShort(getActivity(), "friends-----" + e);
                return;
            }

            @Override
            public void onResponse(String response, int id) {
                Gson gson = new Gson();
                Type type = new TypeToken<Code<List<FriendInfo>>>() {
                }.getType();
                Code<List<FriendInfo>> code = gson.fromJson(response, type);
                if (code.getCode() == 200) {
                    List<FriendInfo> list = code.getMsg();
                    for (FriendInfo friend : list) {
                        String userId = friend.getUserId();
                        String name = friend.getName();
                        String portrait = HttpUtils.IMAGE_RUL + friend.getPortraitUri();
                        String displayName = friend.getDisplayName();
                        String phone = friend.getPhone();
                        String email = friend.getEmail();
                        FriendInfo friendInfo = new FriendInfo();
                        friendInfo.setMyId(mId);
                        friendInfo.setUserId(userId);
                        friendInfo.setName(name);
                        friendInfo.setPortraitUri(portrait);
                        friendInfo.setDisplayName(displayName);
                        friendInfo.setPhone(phone);
                        friendInfo.setEmail(email);
                        mSourceFriendList.add(friendInfo);
                        //存进Sqlite
                        friendInfoDAO.save(friendInfo);
                        L.e("---------===", "插入成功");
                    }

                    //实例化汉字转拼音类
                    mCharacterParser = CharacterParser
                            .getInstance();
                    mPinyinComparator = PinyinComparator.getInstance();
                    initList();

                } else {
                    mFriendListAdapter = new FriendListAdapter(getActivity(), mFriendList);
                    mListView.setAdapter(mFriendListAdapter);
                    tvShowNoFriend.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        initData2();
    }

    private void initData2() {
        mSourceFriendList.clear();
        mFriendList.clear();
        mSourceFriendList = friendInfoDAO.findAll(mId);
//        mFriendListAdapter.notifyDataSetChanged();
//        if(mSourceFriendList.size()>0){
        //实例化汉字转拼音类
        mCharacterParser = CharacterParser
                .getInstance();
        mPinyinComparator = PinyinComparator.getInstance();
        initList();

    }

    private void initList() {
        if (mSourceFriendList != null && mSourceFriendList.size() > 0) {
            mFriendList = labelSourceFriendList(mSourceFriendList); //过滤数据为有字母的字段  现在有字母 别的数据没有
            tvShowNoFriend.setVisibility(View.GONE);
        } else {
            tvShowNoFriend.setVisibility(View.VISIBLE);
        }
        //还原除了带字母字段的其他数据
        for (int i = 0; i < mSourceFriendList.size(); i++) {
            mFriendList.get(i).setName(mSourceFriendList.get(i).getName());
            mFriendList.get(i).setUserId(mSourceFriendList.get(i).getUserId());
            mFriendList.get(i).setPortraitUri(mSourceFriendList.get(i).getPortraitUri());
            mFriendList.get(i).setDisplayName(mSourceFriendList.get(i).getDisplayName());
        }
        // 根据a-z进行排序源数据
        Collections.sort(mFriendList, mPinyinComparator);

        mFriendListAdapter = new FriendListAdapter(getActivity(), mFriendList);
        mListView.setAdapter(mFriendListAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mListView.getHeaderViewsCount() > 0) {
                    startFriendDetailsPage(mFriendList.get(position - 1));
                } else {
                    startFriendDetailsPage(mFilteredFriendList.get(position));
                }
            }
        });

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (tvGroupDialog != null) {
            tvGroupDialog.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 为ListView填充数据
     */
    private List<FriendInfo> labelSourceFriendList(List<FriendInfo> list) {
        List<FriendInfo> mFriendInfoList = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            FriendInfo friendInfoModel = new FriendInfo();
            friendInfoModel.setName(list.get(i).getName());
            //汉字转换成拼音
            String pinyin = mCharacterParser.getSpelling(list.get(i).getName());
            String sortString = pinyin.substring(0, 1).toUpperCase();

            // 正则表达式，判断首字母是否是英文字母
            if (sortString.matches("[A-Z]")) {
                friendInfoModel.setLetters(sortString.toUpperCase());
            } else {
                friendInfoModel.setLetters("#");
            }

            mFriendInfoList.add(friendInfoModel);
        }
        return mFriendInfoList;

    }

    /**
     * 根据输入框中的值来过滤数据并更新ListView
     *
     * @param filterStr 需要过滤的 String
     */
    private void filterData(String filterStr) {
        List<FriendInfo> filterDateList = new ArrayList<>();


        if (TextUtils.isEmpty(filterStr)) {
            filterDateList = mFriendList;
        } else {
            filterDateList.clear();
            for (FriendInfo friendInfoModel : mFriendList) {
                String name = friendInfoModel.getName();
                String displayName = friendInfoModel.getDisplayName();
                if (!TextUtils.isEmpty(displayName)) {
                    if (name.contains(filterStr) || mCharacterParser.getSpelling(name).startsWith(filterStr) || displayName.contains(filterStr) || mCharacterParser.getSpelling(displayName).startsWith(filterStr)) {
                        filterDateList.add(friendInfoModel);
                    }
                } else {
                    if (name.contains(filterStr) || mCharacterParser.getSpelling(name).startsWith(filterStr)) {
                        filterDateList.add(friendInfoModel);
                    }
                }
            }
        }

        // 根据a-z进行排序
        Collections.sort(filterDateList, mPinyinComparator);
        mFilteredFriendList = filterDateList;
        mFriendListAdapter.updateListView(filterDateList);
    }

    /**
     * 用户信息
     *
     * @param friend
     */
    private void startFriendDetailsPage(FriendInfo friend) {
        Intent intent = new Intent(getActivity(), UserDetailActivity.class);
        intent.putExtra("type", CLICK_CONTACT_FRAGMENT_FRIEND);
        intent.putExtra("friends", friend);
        startActivity(intent);
    }

    private void refreshUIListener() {
        BroadcastManager.getInstance(getActivity()).addAction(AppContext.UPDATE_FRIEND, new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String command = intent.getAction();
                if (!TextUtils.isEmpty(command)) {
//                    updateUI();
                }
            }
        });
        BroadcastManager.getInstance(getActivity()).addAction(AppContext.UPDATE_RED_DOT, new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String command = intent.getAction();
                if (!TextUtils.isEmpty(command)) {
                    tvUnread.setVisibility(View.INVISIBLE);
                }
            }
        });
        BroadcastManager.getInstance(getActivity()).addAction(Const.CHANGEINFO, new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
                mId = sp.getString(Const.LOGIN_ID, "");
                mCacheName = sp.getString(Const.LOGIN_NICKNAME, "");
                String header = sp.getString(Const.LOGIN_PORTRAIT, "");
                tvMe.setText(mCacheName);
                ImageLoader.getInstance().displayImage(TextUtils.isEmpty(header) ?
                        Generate.generateDefaultAvatar(mCacheName, mId) : header, sivMe, App.getOptions());
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            BroadcastManager.getInstance(getActivity()).destroy(AppContext.UPDATE_FRIEND);
            BroadcastManager.getInstance(getActivity()).destroy(AppContext.UPDATE_RED_DOT);
            BroadcastManager.getInstance(getActivity()).destroy(Const.CHANGEINFO);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_newfriends:
                tvUnread.setVisibility(View.GONE);
                Intent intent = new Intent(getActivity(), NewFriendListActivity.class);
                startActivityForResult(intent, 20);
                break;
            case R.id.rl_group:
                startActivity(new Intent(getActivity(), GroupListActivity.class));
                break;
            case R.id.rl_publicservice:   //好友认领
                Intent intent1 = new Intent(getActivity(), ClaimFriendsActivity.class);
                startActivity(intent1);
                break;
            case R.id.rl_me_item:
                T.showShort(getActivity(),"不能和自己聊天喔！");
//                startActivity(new Intent(getActivity(), PersonSettingActivity.class));
//                RongIM.getInstance().startPrivateChat(getActivity(), mId, mCacheName);
                break;
        }
    }

    @Override
    public void onRefresh() {
        mHandler.sendEmptyMessageDelayed(REFRESH_COMPLETE, 1000);
    }

    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case REFRESH_COMPLETE:
                    mSourceFriendList.clear();
                    mFriendList.clear();
//                    initView();
                    initText();
                    friendInfoDAO.delete(mId);
                    initData();
                    /*mSourceFriendList=friendInfoDAO.findAll(mId);
                    initList();
                    mFriendListAdapter.notifyDataSetChanged();*/
                    mSwipeRefresh.setRefreshing(false);
            }
        }
    };
}
