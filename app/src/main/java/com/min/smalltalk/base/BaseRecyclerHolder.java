package com.min.smalltalk.base;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import io.rong.imageloader.core.ImageLoader;

/**
 * Created by Min on 2016/8/11.
 * RecyclerView万能适配
 */
public class BaseRecyclerHolder extends RecyclerView.ViewHolder {
    private Context context;
    private SparseArray<View> viewSparseArray;

    public BaseRecyclerHolder(Context context,View itemView) {
        super(itemView);
        this.context=context;
        viewSparseArray=new SparseArray<>(8); //指定一个初始值为10
    }

    public static BaseRecyclerHolder getRecyclerHolder(Context context,View itemView){
        return new BaseRecyclerHolder(context,itemView);
    }

    public SparseArray<View> getViews(){
        return this.viewSparseArray;
    }

    /**
     * 通过view的id获取对应的控件，如果没有则加入views中
     * @param viewId 控件的id
     * @return 返回一个控件
     */
    @SuppressWarnings("unchecked")
    public <T extends View> T getView(int viewId){
        View view = viewSparseArray.get(viewId);
        if (view == null ){
            view = itemView.findViewById(viewId);
            viewSparseArray.put(viewId,view);
        }
        return (T) view;
    }

    /**
     * 设置字符串
     */
    public BaseRecyclerHolder setText(int viewId,String text){
        TextView tv = getView(viewId);
        tv.setText(text);
        return this;
    }

    /**
     * 设置图片
     */
    public BaseRecyclerHolder setImageResource(int viewId,int drawableId){
        ImageView iv = getView(viewId);
        iv.setImageResource(drawableId);
        return this;
    }

    /**
     * 设置图片
     */
    public BaseRecyclerHolder setImageBitmap(int viewId, Bitmap bitmap){
        ImageView iv = getView(viewId);
        iv.setImageBitmap(bitmap);
        return this;
    }

    /**
     * 设置图片
     */
    public BaseRecyclerHolder setImageByUrl(int viewId,String url){
//        Picasso.with(context).load(url).into((ImageView) getView(viewId));
        ImageLoader.getInstance().displayImage(url, (ImageView) getView(viewId));
//        ImageView imageView = getView(viewId);
//        new MyBitmapUtils().disPlay(imageView,url);
        //        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(context));
        //        ImageLoader.getInstance().displayImage(url, (ImageView) getView(viewId));
        return this;
    }
    /**
     * 实现多选
     */
    public BaseRecyclerHolder setSelectListener(int viewId,View.OnClickListener listener){
        View view=getView(viewId);
        view.setOnClickListener(listener);
        return this;
    }
}
