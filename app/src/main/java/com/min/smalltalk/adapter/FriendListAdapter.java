package com.min.smalltalk.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.min.smalltalk.App;
import com.min.smalltalk.R;
import com.min.smalltalk.bean.FriendInfo;
import com.min.smalltalk.utils.file.image.MyBitmapUtils;
import com.min.smalltalk.wedget.Generate;
import com.min.smalltalk.wedget.image.SelectableRoundedImageView;

import java.util.List;

import io.rong.imageloader.core.ImageLoader;

/**
 * Created by Min on 2016/11/25.
 */

public class FriendListAdapter extends BaseAdapter implements SectionIndexer {

    private Context context;

    private List<FriendInfo> list;
    private MyBitmapUtils myBitmapUtils;

    public FriendListAdapter(Context context, List<FriendInfo> list) {
        this.context = context;
        this.list = list;
        myBitmapUtils = new MyBitmapUtils();
    }


    /**
     * 传入新的数据 刷新UI的方法
     */
    public void updateListView(List<FriendInfo> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        final FriendInfo mContent = list.get(position);
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.friend_item, null);
            viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.friendname);
            viewHolder.tvLetter = (TextView) convertView.findViewById(R.id.catalog);
            viewHolder.mImageView = (SelectableRoundedImageView) convertView.findViewById(R.id.frienduri);
            viewHolder.tvUserId = (TextView) convertView.findViewById(R.id.friend_id);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //根据position获取分类的首字母的Char ascii值
        int section = getSectionForPosition(position);
        //如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
        if (position == getPositionForSection(section)) {
            viewHolder.tvLetter.setVisibility(View.VISIBLE);
            viewHolder.tvLetter.setText(mContent.getLetters());
        } else {
            viewHolder.tvLetter.setVisibility(View.GONE);
        }
        if(TextUtils.isEmpty(list.get(position).getDisplayName())){
            viewHolder.tvTitle.setText(this.list.get(position).getName());
        }else {
            viewHolder.tvTitle.setText(this.list.get(position).getDisplayName());
        }
        String url=list.get(position).getPortraitUri();
        if (TextUtils.isEmpty(url)) {
            String s = Generate.generateDefaultAvatar(list.get(position).getName(), list.get(position).getUserId());
            ImageLoader.getInstance().displayImage(s, viewHolder.mImageView, App.getOptions());
        } else {
            myBitmapUtils.disPlay(viewHolder.mImageView, url);
        }
        if (context.getSharedPreferences("config", Context.MODE_PRIVATE).getBoolean("isDebug", false)) {
            viewHolder.tvUserId.setVisibility(View.VISIBLE);
            viewHolder.tvUserId.setText(list.get(position).getUserId());
        }
        return convertView;
    }

    @Override
    public Object[] getSections() {
        return new Object[0];
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        for (int i = 0; i < getCount(); i++) {
            String sortStr = list.get(i).getLetters();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == sectionIndex) {
                return i;
            }
        }

        return -1;
    }

    @Override
    public int getSectionForPosition(int position) {
        return list.get(position).getLetters().charAt(0);
    }


    final static class ViewHolder {
        /**
         * 首字母
         */
        TextView tvLetter;
        /**
         * 昵称
         */
        TextView tvTitle;
        /**
         * 头像
         */
        SelectableRoundedImageView mImageView;
//        CircleImageView mImageView;
        /**
         * userid
         */
        TextView tvUserId;
    }
}