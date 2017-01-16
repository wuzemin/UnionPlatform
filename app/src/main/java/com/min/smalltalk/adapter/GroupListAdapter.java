package com.min.smalltalk.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.min.smalltalk.R;
import com.min.smalltalk.bean.Groups;
import com.min.smalltalk.utils.file.image.MyBitmapUtils;
import com.min.smalltalk.wedget.image.SelectableRoundedImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Min on 2017/1/14.
 */

public class GroupListAdapter extends RecyclerView.Adapter<GroupListAdapter.MyViewHolder> implements View.OnClickListener {

    private Context context;
    private List<Groups> list = new ArrayList<>();
    private MyBitmapUtils myBitmapUtils;
    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view , Groups groups);
    }
    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public GroupListAdapter(Context context,List<Groups> list) {
        this.context = context;
        this.list = list;
        myBitmapUtils = new MyBitmapUtils();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_group, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        view.setOnClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        String url = list.get(position).getGroupPortraitUri();
        holder.tv_group_name.setText(list.get(position).getGroupName());
        myBitmapUtils.disPlay(holder.siv_group_head, url);
        holder.itemView.setTag(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_group_name;
        SelectableRoundedImageView siv_group_head;

        public MyViewHolder(View view)
        {
            super(view);
            tv_group_name = (TextView) view.findViewById(R.id.tv_group_name);
            siv_group_head = (SelectableRoundedImageView) view.findViewById(R.id.siv_group_head);
        }
    }

    @Override
    public void onClick(View view) {
        if (mOnItemClickListener != null) {
            //注意这里使用getTag方法获取数据
            mOnItemClickListener.onItemClick(view,(Groups) view.getTag());
        }
    }
}
