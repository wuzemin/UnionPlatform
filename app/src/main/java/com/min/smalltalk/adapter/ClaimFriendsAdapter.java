package com.min.smalltalk.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.min.smalltalk.R;
import com.min.smalltalk.bean.ClaimFriends;
import com.min.smalltalk.network.HttpUtils;
import com.min.smalltalk.wedget.image.CircleImageView;

import java.util.List;

import io.rong.imageloader.core.ImageLoader;

/**
 * Created by Min on 2017/1/5.
 */

public class ClaimFriendsAdapter extends BaseAdapter {
    private Context context;
    private List<ClaimFriends> list;


    public ClaimFriendsAdapter(Context context,List<ClaimFriends> list) {
        this.context=context;
        this.list=list;
    }

    @Override
    public int getCount() {
        return list.size();
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
            holder=new ViewHolder();
            convertView= LayoutInflater.from(context).inflate(R.layout.item_new_friends,viewGroup,false);
            holder.mHead = (CircleImageView) convertView.findViewById(R.id.civ_icon);
            holder.nickName= (TextView) convertView.findViewById(R.id.tv_nickname);
            holder.phone= (TextView) convertView.findViewById(R.id.tv_message);
            holder.btnClaim= (TextView) convertView.findViewById(R.id.tv_agree);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        final ClaimFriends bean=list.get(position);
        ImageLoader.getInstance().displayImage(HttpUtils.IMAGE_RUL+bean.getAvatar_image(),holder.mHead);
        holder.nickName.setText(bean.getNickname());
        holder.phone.setText(bean.getMobile());
        if(bean.getCheck_claim()==1){
            holder.btnClaim.setClickable(false);
            holder.btnClaim.setVisibility(View.VISIBLE);
            holder.btnClaim.setText("已认领");
            holder.btnClaim.setTextColor(Color.GRAY);
        }else {
            holder.btnClaim.setVisibility(View.VISIBLE);
            holder.btnClaim.setText("我要认领");
        }
        //同意
        holder.btnClaim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemButtonClick != null) {
                    mOnItemButtonClick.onButtonClaimClick(position, v, bean.getCheck_claim());
                }
            }
        });

        return convertView;
    }

    class ViewHolder {
        CircleImageView mHead;
        TextView nickName;
        TextView phone;
        TextView btnClaim;
    }

    ClaimFriendsAdapter.OnItemButtonClick mOnItemButtonClick;


    public void setOnItemButtonClick(ClaimFriendsAdapter.OnItemButtonClick onItemButtonClick) {
        this.mOnItemButtonClick = onItemButtonClick;
    }

    public interface OnItemButtonClick {
        boolean onButtonClaimClick(int position, View view, int status);

    }
}
