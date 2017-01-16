package com.min.smalltalk;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;

import com.min.mylibrary.util.L;
import com.min.smalltalk.bean.FriendInfo;
import com.min.smalltalk.bean.GroupMember;
import com.min.smalltalk.constant.Const;
import com.min.smalltalk.db.FriendInfoDAOImpl;
import com.min.smalltalk.db.GroupMemberDAOImpl;
import com.min.smalltalk.wedget.Generate;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import io.rong.imkit.RongIM;
import io.rong.imlib.model.UserInfo;

/**
 * Created by Min on 2016/12/28.
 */

public class UserInfoManager {

    private final static String TAG = "UserInfoManager";
    private static UserInfoManager sInstance;
    private final Context mContext;
    private SharedPreferences sp;
    private List<FriendInfo> list;
    private FriendInfoDAOImpl friendInfoDAO;
    private GroupMemberDAOImpl groupMemberDAO;
    private LinkedHashMap<String, UserInfo> mUserInfoCache;
    private Handler handler;

    public static UserInfoManager getInstance() {
        return sInstance;
    }

    public UserInfoManager(Context context) {
        mContext = context;
        sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        friendInfoDAO= new FriendInfoDAOImpl(mContext);
        groupMemberDAO = new GroupMemberDAOImpl(mContext);
        initPortrait();
        handler = new Handler();
        setUserInfoEngineListener();
    }
    public static void init(Context context) {
        L.e(TAG, "UserInfoManager init");
        sInstance = new UserInfoManager(context);
    }

    /**
     * 需要 rongcloud connect 成功后设置的 listener
     */
    public void setUserInfoEngineListener() {
        UserInfoEngine.getInstance(mContext).setListener(new UserInfoEngine.UserInfoListener() {
            @Override
            public void onResult(UserInfo info) {
                if (info != null && RongIM.getInstance() != null) {
                    if (TextUtils.isEmpty(String.valueOf(info.getPortraitUri()))) {
                        info.setPortraitUri(Uri.parse(Generate.generateDefaultAvatar(info.getName(), info.getUserId())));
                    }
                    L.e(TAG, "SealUserInfoManager getUserInfo from network " + info.getUserId() + " " + info.getName() + " " + info.getPortraitUri());
                    RongIM.getInstance().refreshUserInfoCache(info);
                }
            }
        });
        /*GroupInfoEngine.getInstance(mContext).setmListener(new GroupInfoEngine.GroupInfoListeners() {
            @Override
            public void onResult(Group info) {
                if (info != null && RongIM.getInstance() != null) {
                    NLog.d(TAG, "SealUserInfoManager getGroupInfo from network " + info.getId() + " " + info.getName() + " " + info.getPortraitUri());
                    if (TextUtils.isEmpty(String.valueOf(info.getPortraitUri()))) {
                        info.setPortraitUri(Uri.parse(RongGenerate.generateDefaultAvatar(info.getName(), info.getId())));
                    }
                    RongIM.getInstance().refreshGroupInfoCache(info);
                }
            }
        });*/
    }

    private void initPortrait() {
        //用户头像
        list = new ArrayList<>();
        mUserInfoCache=new LinkedHashMap<>();
        String userId=sp.getString(Const.LOGIN_ID,"");
        String name=sp.getString(Const.LOGIN_NICKNAME,"");
        String portrait = sp.getString(Const.LOGIN_PORTRAIT, "");
        /*if(TextUtils.isEmpty(sp.getString(Const.LOGIN_PORTRAIT,""))){
            portrait="http://192.168.0.209/public/effect/assets/avatars/avatar.jpg";
        }else {*/
//            portrait
//            L.e("-----------",portrait);
//        }
        String displayName="";
        String phone=sp.getString(Const.LOGIN_PHONE,"");
        String email=sp.getString(Const.LOGIN_EMAIL,"");

        list.clear();
        mUserInfoCache.clear();
        list=friendInfoDAO.findAll(userId);
//        list.add(new FriendInfo(userId,name,portrait,displayName,phone,email));
        if(list.size()>1){
            for(int i=0;i<list.size();i++){
                String userid=list.get(i).getUserId();
                String username=list.get(i).getName();
                String port=list.get(i).getPortraitUri();
                UserInfo userInfo=new UserInfo(userid,username,Uri.parse(port));
                mUserInfoCache.put(list.get(i).getUserId(),userInfo);
            }
        }

//        RongIM.setUserInfoProvider(this, true);
    }

    /**
     * 用户信息
     * @param userId
     */
    public void getUserInfo(final String userId) {
        if (TextUtils.isEmpty(userId)) {
            return;
        }
        if (mUserInfoCache != null) {
            UserInfo userInfo = mUserInfoCache.get(userId);
            if (userInfo != null) {
                RongIM.getInstance().refreshUserInfoCache(userInfo);
                L.e(TAG, "SealUserInfoManager getUserInfo from cache " + userId + " "
                        + userInfo.getName() + " " + userInfo.getPortraitUri());
                return;
            }
        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                UserInfo userInfo;
//                FriendInfo friend = getFriendByID(userId);
                FriendInfo friendInfo=friendInfoDAO.find(userId);
                if (friendInfo != null) {
                    String name = friendInfo.getName();
                    if (friendInfo.isExitsDisplayName()) {
                        name = friendInfo.getDisplayName();
                    }
                    userInfo = new UserInfo(friendInfo.getUserId(), name,
                            Uri.parse(friendInfo.getPortraitUri()));
                    L.e(TAG, "SealUserInfoManager getUserInfo from Friend db " + userId + " "
                            + userInfo.getName() + " " + userInfo.getPortraitUri());
                    RongIM.getInstance().refreshUserInfoCache(userInfo);
                    return;
                }
                List<GroupMember> groupMemberList = groupMemberDAO.findAll(userId);
                if (groupMemberList != null && groupMemberList.size() > 0) {
                    GroupMember groupMember = groupMemberList.get(0);
                    userInfo = new UserInfo(groupMember.getUserId(), groupMember.getUserName(),
                            Uri.parse(groupMember.getUserPortraitUri()));
                    L.e(TAG, "SealUserInfoManager getUserInfo from GroupMember db " + userId + " "
                            + userInfo.getName() + " " + userInfo.getPortraitUri());
                    RongIM.getInstance().refreshUserInfoCache(userInfo);
                    return;
                }
                UserInfoEngine.getInstance(mContext).startEngine(userId);
            }
        });


    }
}
