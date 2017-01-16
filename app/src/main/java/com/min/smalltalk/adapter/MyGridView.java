package com.min.smalltalk.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.min.mylibrary.util.T;
import com.min.smalltalk.App;
import com.min.smalltalk.R;
import com.min.smalltalk.activity.SelectFriendsActivity;
import com.min.smalltalk.activity.UserDetailActivity;
import com.min.smalltalk.bean.FriendInfo;
import com.min.smalltalk.bean.GroupMember;
import com.min.smalltalk.bean.Groups;
import com.min.smalltalk.constant.Const;
import com.min.smalltalk.network.HttpUtils;
import com.min.smalltalk.utils.file.image.MyBitmapUtils;
import com.min.smalltalk.wedget.CharacterParser;
import com.min.smalltalk.wedget.Generate;
import com.min.smalltalk.wedget.image.SelectableRoundedImageView;

import java.util.ArrayList;
import java.util.List;

import io.rong.imageloader.core.ImageLoader;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.UserInfo;

/**
 * Created by Min on 2016/12/1.
 */

public class MyGridView extends BaseAdapter {
    private Context context;
    private List<GroupMember> list=new ArrayList<>();
//    private String isCreator;
    private boolean isCreated;
    private LayoutInflater inflater;
    private String groupId;
    private String groupName;
    private String groupPortraitUri;
    private Groups groups;
    private MyBitmapUtils myBitmapUtils;

    public MyGridView(Context context, List<GroupMember> list, boolean isCreated, Groups groups) {
        this.context = context;
        if (list.size() >= 20) {
            this.list = list.subList(0, 19);
        } else {
            this.list = list;
        }
        this.isCreated=isCreated;
        this.groups=groups;
        this.inflater=inflater.from(context);
        myBitmapUtils = new MyBitmapUtils();

    }

    @Override
    public int getCount() {
        if (isCreated) {
            return list.size() + 2;
        } else {
            return list.size() + 1;
        }
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup) {
        ViewHolder holder;
        if(convertView==null){
            convertView=inflater.inflate(R.layout.item_groups_list,null);
            holder=new ViewHolder();
            holder.sivGroupDetails= (SelectableRoundedImageView) convertView.findViewById(R.id.siv_group_details_head);
            holder.tvGroupDetailsName= (TextView) convertView.findViewById(R.id.tv_group_details_name);
            convertView.setTag(holder);
        }else {
            holder= (ViewHolder) convertView.getTag();
        }

        //
        if(position==getCount()-1 && isCreated){
            holder.tvGroupDetailsName.setText("");
            holder.sivGroupDetails.setImageResource(R.mipmap.icon_btn_deleteperson);
            holder.sivGroupDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, SelectFriendsActivity.class);
                    intent.putExtra("isDeleteGroupMember", true);
                    intent.putExtra("GroupId", groups.getGroupId());
                    context.startActivity(intent);
                }
            });
        }else if ((isCreated && position == getCount() - 2) || (!isCreated && position == getCount() - 1)) {
            holder.tvGroupDetailsName.setText("");
            holder.sivGroupDetails.setImageResource(R.mipmap.jy_drltsz_btn_addperson);

            holder.sivGroupDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, SelectFriendsActivity.class);
                    intent.putExtra("isAddGroupMember", true);
                    intent.putExtra("GroupId", groups.getGroupId());
                    context.startActivity(intent);
                }
            });
        } else { // 普通成员
            final GroupMember bean = list.get(position);
            String name=bean.getDisplayName();
            if(TextUtils.isEmpty(name)){
                holder.tvGroupDetailsName.setText(bean.getUserName());
            }else {
                holder.tvGroupDetailsName.setText(name);
            }
            if (TextUtils.isEmpty(bean.getUserPortraitUri())) {
                ImageLoader.getInstance().displayImage(Generate.generateDefaultAvatar(bean.getUserName(), bean.getUserId()), holder.sivGroupDetails, App.getOptions());
            } else {
                myBitmapUtils.disPlay(holder.sivGroupDetails,HttpUtils.IMAGE_RUL+bean.getUserPortraitUri());
//                ImageLoader.getInstance().displayImage(HttpUtils.IMAGE_RUL+bean.getUserPortraitUri(), holder.sivGroupDetails, App.getOptions());
            }
            holder.sivGroupDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String mId=context.getSharedPreferences("config",Context.MODE_PRIVATE).getString(Const.LOGIN_ID,"");
                    if(bean.getUserId().equals(mId)){
                        T.showShort(context,"这是自己");
                        return;
                    }
                    UserInfo userInfo = new UserInfo(bean.getUserId(), bean.getUserName(),
                            Uri.parse(TextUtils.isEmpty(bean.getUserPortraitUri()) ? Generate.generateDefaultAvatar(bean.getUserName(), bean.getUserId()) : bean.getUserPortraitUri()));
                    Intent intent = new Intent(context, UserDetailActivity.class);
                    FriendInfo friend = CharacterParser.getInstance().generateFriendFromUserInfo(userInfo);
                    intent.putExtra("friends", friend);
                    intent.putExtra("conversationType", Conversation.ConversationType.GROUP.getValue());
                    //Groups not Serializable,just need group name
                    intent.putExtra("groupName", list.get(position).getGroupName());
                    intent.putExtra("type", 1);
                    context.startActivity(intent);
                }

            });

        }
        return convertView;
    }

    static class ViewHolder{
        SelectableRoundedImageView sivGroupDetails;
        TextView tvGroupDetailsName;
    }
}
