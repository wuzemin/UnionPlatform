package com.min.smalltalk.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.min.smalltalk.R;
import com.min.smalltalk.bean.Recommend;
import com.min.smalltalk.wedget.image.SelectableRoundedImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Min on 2017/1/16.
 */

public class RecommendAdapter extends RecyclerView.Adapter<RecommendAdapter.MyViewHolder> implements View.OnClickListener {
    private Context context;
    private List<Recommend> list = new ArrayList<>();


    public RecommendAdapter(Context context,List<Recommend> list) {
        this.context=context;
        this.list=list;
    }

    @Override
    public RecommendAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_group,parent,false);
        RecommendAdapter.MyViewHolder holder = new RecommendAdapter.MyViewHolder(view);
        view.setOnClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecommendAdapter.MyViewHolder holder, int position) {
        holder.siv_Claim_head.setImageResource(R.mipmap.ic_launcher);
        holder.tv_Claim_name.setText(list.get(position).getFull_name());
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

    private RecommendAdapter.OnRecyclerViewItemClickListener mOnItemClickListener = null;

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view , Recommend recommend);
    }
    public void setOnItemClickListener(RecommendAdapter.OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    @Override
    public void onClick(View view) {
        if (mOnItemClickListener != null) {
            //注意这里使用getTag方法获取数据
            mOnItemClickListener.onItemClick(view,(Recommend) view.getTag());
        }
    }
}