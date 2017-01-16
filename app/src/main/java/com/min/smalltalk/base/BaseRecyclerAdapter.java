package com.min.smalltalk.base;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.min.smalltalk.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Min on 2016/11/30.
 * RecyclerView的万能适配
 */
public abstract class BaseRecyclerAdapter<T> extends RecyclerView.Adapter<BaseRecyclerHolder> {
    private Context context;//上下文
    private List<T> list;//数据源
    private LayoutInflater inflater;//布局器
    private int itemLayoutId;//布局id
    private boolean isScrolling;//是否在滚动
    private OnItemClickListener listener;//点击事件监听器
    private OnItemLongClickListener longClickListener;//长按监听器
    private RecyclerView recyclerView;
    private SparseBooleanArray selectedItems;//记录选择的item
    private CheckBox checkBox;

    //在RecyclerView提供数据的时候调用
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        this.recyclerView = null;
    }

    /**
     * 定义一个点击事件接口回调
     */
    public interface OnItemClickListener {
        void onItemClick(RecyclerView parent, View view, int position);
    }

    public interface OnItemLongClickListener {
        boolean onItemLongClick(RecyclerView parent, View view, int position);
    }

    /**
     * 插入一项
     *
     * @param item
     * @param position
     */
    public void insert(T item, int position) {
        list.add(position, item);
        notifyItemInserted(position);
    }

    /**
     * 删除一项
     *
     * @param position 删除位置
     */
    public void delete(int position) {
        list.remove(position);
        notifyItemRemoved(position);
    }

    /**
     * 判读是否选中
     */
    public boolean isSelected(int position){
        return getSelectedItems().contains(position);
    }

    /**
     * 切换选中或取消选中
     */
    public void switchSelectedState(int position){
        if(selectedItems.get(position, false)){
            selectedItems.delete(position);
        }else{
            selectedItems.put(position,true);
        }
        notifyItemChanged(position);
    }

    /**
     * 清除所有选中item的标记
     */
    public void clearSelectedState(){
        List<Integer> selection = getSelectedItems();
        selectedItems.clear();
        for(Integer i : selection){
            notifyItemChanged(i);
        }
    }

    /**
     * 全选
     */
    public void selectAllItems(){
        clearSelectedState();
        for(int i = 0; i < getItemCount(); i++){
            selectedItems.put(i,true);
            notifyItemChanged(i);
        }
    }

    /**
     * 获取item数目
     */
    public int getSelectedItemCount(){
        return selectedItems.size();
    }

    /**
     * 获取选中的items
     */
    public List<Integer> getSelectedItems(){
        List<Integer> items = new ArrayList<>(selectedItems.size());
        for(int i = 0; i < selectedItems.size(); i++){
            items.add(selectedItems.keyAt(i));
        }
        return items;
    }

    public BaseRecyclerAdapter(Context context, List<T> list, int itemLayoutId) {
        this.context = context;
        this.list = list;
        this.itemLayoutId = itemLayoutId;
        inflater = LayoutInflater.from(context);

        //        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
        //            @Override
        //            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        //                super.onScrollStateChanged(recyclerView, newState);
        //                isScrolling = !(newState == RecyclerView.SCROLL_STATE_IDLE);
        //                if (!isScrolling) {
        //                    notifyDataSetChanged();
        //                }
        //            }
        //        });
    }

    @Override
    public BaseRecyclerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(itemLayoutId, parent, false);
        return BaseRecyclerHolder.getRecyclerHolder(context, view);
    }

    @Override
    public void onBindViewHolder(final BaseRecyclerHolder holder, int position) {

        if (listener != null){
            holder.itemView.setBackgroundResource(R.color.white);//设置背景
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null && view != null && recyclerView != null) {
                    int position = recyclerView.getChildAdapterPosition(view);
                    listener.onItemClick(recyclerView, view, position);
                }
            }
        });


        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (longClickListener != null && view != null && recyclerView != null) {
                    int position = recyclerView.getChildAdapterPosition(view);
                    longClickListener.onItemLongClick(recyclerView, view, position);
                    return true;
                }
                return false;
            }
        });

        convert(holder, list.get(position), position, isScrolling);

    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener longClickListener) {
        this.longClickListener = longClickListener;
    }

    /**
     * 填充RecyclerView适配器的方法，子类需要重写
     *
     * @param holder      ViewHolder
     * @param item        子项
     * @param position    位置
     * @param isScrolling 是否在滑动
     */
    public abstract void convert(BaseRecyclerHolder holder, T item, int position, boolean isScrolling);
}
