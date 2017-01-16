package com.min.smalltalk.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.min.smalltalk.R;
import com.min.smalltalk.bean.ClaimFriends;
import com.min.smalltalk.wedget.image.SelectableRoundedImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Min on 2017/1/5.
 */

public class ClaimFriendsAdapter extends RecyclerView.Adapter<ClaimFriendsAdapter.MyViewHolder> implements View.OnClickListener {
    private Context context;
    private List<ClaimFriends> list = new ArrayList<>();


    public ClaimFriendsAdapter(Context context,List<ClaimFriends> list) {
        this.context=context;
        this.list=list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_group,parent,false);
        MyViewHolder holder = new MyViewHolder(view);
        view.setOnClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.siv_Claim_head.setImageResource(R.mipmap.ic_launcher);
        holder.tv_Claim_name.setText(list.get(position).getFull_name());
        int status=list.get(position).getCheck_claim();
        if(status==1){
            holder.tv_Claim_status.setTextColor(Color.GRAY);
            holder.tv_Claim_status.setText("已认领");
        }else {
            holder.tv_Claim_status.setTextColor(Color.BLUE);
            holder.tv_Claim_status.setText("未认领");
        }
        holder.itemView.setTag(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder{
        SelectableRoundedImageView siv_Claim_head;
        TextView tv_Claim_name;
        TextView tv_Claim_status;


        public MyViewHolder(View itemView) {
            super(itemView);
            siv_Claim_head = (SelectableRoundedImageView) itemView.findViewById(R.id.siv_group_head);
            tv_Claim_name = (TextView) itemView.findViewById(R.id.tv_group_name);
            tv_Claim_status = (TextView) itemView.findViewById(R.id.tv_role);
        }
    }

    private ClaimFriendsAdapter.OnRecyclerViewItemClickListener mOnItemClickListener = null;

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view , ClaimFriends claimFriends);
    }
    public void setOnItemClickListener(ClaimFriendsAdapter.OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    @Override
    public void onClick(View view) {
        if (mOnItemClickListener != null) {
            //注意这里使用getTag方法获取数据
            mOnItemClickListener.onItemClick(view,(ClaimFriends) view.getTag());
        }
    }

    /*@Override
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

    }*/
}
