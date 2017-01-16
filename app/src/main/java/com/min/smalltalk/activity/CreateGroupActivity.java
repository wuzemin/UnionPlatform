package com.min.smalltalk.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.min.smalltalk.bean.FriendInfo;
import com.min.smalltalk.bean.GroupId;
import com.min.smalltalk.bean.Groups;
import com.min.smalltalk.constant.Const;
import com.min.smalltalk.db.DBOpenHelper;
import com.min.smalltalk.db.GroupsDAOImpl;
import com.min.smalltalk.network.HttpUtils;
import com.min.smalltalk.utils.file.image.MyBitmapUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.rong.imageloader.core.ImageLoader;
import io.rong.imkit.RongIM;
import io.rong.imkit.widget.AsyncImageView;
import io.rong.imlib.model.Conversation;
import okhttp3.Call;

/**
 * 创建群组
 */
public class CreateGroupActivity extends BaseActivity {
    @BindView(R.id.iv_title_back)
    ImageView ivTitleBack;
    @BindView(R.id.tv_title_right)
    TextView tvTitleRight;
    @BindView(R.id.img_Group_portrait)
    AsyncImageView imgGroupPortrait;
    @BindView(R.id.et_group_name)
    EditText etGroupName;
    @BindView(R.id.btn_create_group)
    Button btnCreateGroup;
    @BindView(R.id.tv_title)
    TextView tvTitle;

    private List<String> groupIds=new ArrayList<>();
    private BottomMenuDialog dialog;
    private PhotoUtils photoUtils;
    private Uri selectUri;
    private String imageUrl;
    private File imageFile;
    private String groupId;

    private DBOpenHelper dbOpenHelper;  //SQLite
    private GroupsDAOImpl sqLiteDAO;

    private MyBitmapUtils myBitmapUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        ButterKnife.bind(this);

        dbOpenHelper = new DBOpenHelper(mContext, "talk.db", null, 2);// 创建数据库文件
        dbOpenHelper.getWritableDatabase();
        sqLiteDAO= new GroupsDAOImpl(mContext);

        tvTitle.setText("创建群组");
        List<FriendInfo> memberList= (List<FriendInfo>) getIntent().getSerializableExtra("GroupMember");

        setPortraitChangListener();

        groupIds.add(getSharedPreferences("config",MODE_PRIVATE).getString(Const.LOGIN_ID,""));
        if(null!=memberList && memberList.size()>0) {
            for (FriendInfo friendInfo : memberList) {
                groupIds.add(friendInfo.getUserId());

            }
        }
        myBitmapUtils  = new MyBitmapUtils();
    }

    private void setPortraitChangListener() {
        photoUtils=new PhotoUtils(new PhotoUtils.OnPhotoResultListener() {
            @Override
            public void onPhotoResult(Uri uri) {
                if(uri!=null && !TextUtils.isEmpty(uri.getPath())){
                    selectUri=uri;
                    LoadDialog.show(mContext);
                    imageFile=new File(selectUri.getPath());
                    imageUrl=selectUri.toString();
                    ImageLoader.getInstance().clearDiskCache();
                    ImageLoader.getInstance().displayImage(imageUrl,imgGroupPortrait);
//                    myBitmapUtils.disPlay(imgGroupPortrait,imageUrl);
                    LoadDialog.dismiss(mContext);
                }
            }

            @Override
            public void onPhotoCancel() {

            }
        });
    }

    /**
     * 群组
     */
    private void createGroup() {
        final String groupName=etGroupName.getText().toString().trim();
        if(TextUtils.isEmpty(groupName) && imgGroupPortrait!=null){
            T.showShort(mContext,"群组图片和群组名称不能为空");
            LoadDialog.dismiss(mContext);
            return;
        }

        final String userid=getSharedPreferences("config",MODE_PRIVATE).getString(Const.LOGIN_ID,"");
        if(groupIds.size() >= 1){
            Gson gson=new Gson();
            String sss=gson.toJson(groupIds);
            HttpUtils.sendPostListRequest("/create_group", userid,groupName, sss,imageFile, new StringCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {
                    T.showShort(mContext,"/create_group-----"+e);
                    return;
                }

                @Override
                public void onResponse(String response, int id) {
                    Gson gson=new Gson();
                    Type type=new TypeToken<Code<GroupId>>(){}.getType();
                    Code<GroupId> code = gson.fromJson(response,type);
                    if(code.getCode()==200){
                        groupId=code.getMsg().getGroupId();
                        Groups groups = new Groups();
                        groups.setUserId(userid);
                        groups.setGroupId(groupId);  //groupId
                        groups.setGroupName(groupName);  //groupName
                        imageUrl = imageUrl.substring(imageUrl.lastIndexOf("/")+1,imageUrl.length());
                        groups.setGroupPortraitUri(imageUrl);
                        groups.setRole("1");
                        sqLiteDAO.save(groups);
                        Log.i("-------------==-=-", "插入成功");// 用日志记录一个我们自定义的输出。可以在LogCat窗口中查看，
                        LoadDialog.dismiss(mContext);
                        RongIM.getInstance().startConversation(mContext, Conversation.ConversationType.GROUP,groupId,groupName);
                        T.showShort(mContext,"创建成功");
                        finish();
                    }else {
                        LoadDialog.dismiss(mContext);
                        T.showShort(mContext,"创建失败,请检查网络是否完好或重新创建一个群");
                    }
                }
            });
        }
    }

    @OnClick({R.id.iv_title_back, R.id.img_Group_portrait, R.id.btn_create_group})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_title_back:
                CreateGroupActivity.this.finish();
                break;
            case R.id.img_Group_portrait:
                ShowPhotoDialog();
                break;
            case R.id.btn_create_group:
                LoadDialog.show(mContext);
                createGroup();
                break;
        }
    }

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
                photoUtils.takePicture(CreateGroupActivity.this);
            }
        });
        dialog.setLocalphotoListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dialog!=null && dialog.isShowing()){
                    dialog.dismiss();
                }
                photoUtils.selectPicture(CreateGroupActivity.this);
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
                photoUtils.onActivityResult(CreateGroupActivity.this, requestCode, resultCode, data);
        }
    }
}