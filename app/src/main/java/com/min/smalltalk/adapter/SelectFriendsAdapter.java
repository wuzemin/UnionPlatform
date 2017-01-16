package com.min.smalltalk.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.min.smalltalk.wedget.image.SelectableRoundedImageView;
import com.min.smalltalk.R;
import com.min.smalltalk.bean.FriendInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Min on 2016/11/26.
 */

public class SelectFriendsAdapter extends BaseAdapter implements SectionIndexer {
    private Context context;
    private List<CheckBox> checkBoxList=new ArrayList<>();

    private List<FriendInfo> list=new ArrayList<>();
    private static HashMap<Integer,Boolean> mCBFlag;


    public SelectFriendsAdapter(Context context, List<FriendInfo> list) {
        this.context = context;
        this.list=list;
        mCBFlag=new HashMap<>();
        init();
    }

    public void setData(List<FriendInfo> friends){
        list=friends;
        init();
    }

    void init(){
        for(int i=0;i<list.size();i++){
            mCBFlag.put(i,false);
        }
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
    public View getView(final int position, View convertView, ViewGroup viewGroup) {
        final ViewHolder viewHolder;
        final FriendInfo friendInfo=list.get(position);
        if(convertView==null){
            viewHolder=new ViewHolder();
            convertView= LayoutInflater.from(context).inflate(R.layout.item_selected_freinds,viewGroup,false);
            viewHolder.tvTitle= (TextView) convertView.findViewById(R.id.tv_friendname);
            viewHolder.tvLetter= (TextView) convertView.findViewById(R.id.tv_catalog);
            viewHolder.mImageView= (SelectableRoundedImageView) convertView.findViewById(R.id.siv_frienduri);
            viewHolder.isSelect= (CheckBox) convertView.findViewById(R.id.sb_select);
            convertView.setTag(viewHolder);
        }else {
            viewHolder= (ViewHolder) convertView.getTag();
        }
        //根据position获取分类的首字母的Char ascii值
        int section=getSectionForPosition(position);
        //如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
        if(position==getPositionForSection(section)){
            viewHolder.tvLetter.setVisibility(View.VISIBLE);
            viewHolder.tvLetter.setText(friendInfo.getLetters());
            viewHolder.isSelect.setChecked(getIsSelected().get(position));
        }else {
            viewHolder.tvLetter.setVisibility(View.GONE);
        }

        return null;
    }

    private void updateSelectedSizeView(Map<Integer, Boolean> mCBFlag){

    }

    public static HashMap<Integer,Boolean> getIsSelected(){
        return mCBFlag;
    }

    public static void setIsSelected(HashMap<Integer,Boolean> isSelected){
        mCBFlag=isSelected;
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
    public int getSectionForPosition(int i) {
        return list.get(i).getLetters().charAt(0);
    }

    final class ViewHolder{
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
        /**
         * userid
         */
//            TextView tvUserId;
        /**
         * 是否被选中的checkbox
         */
        CheckBox isSelect;
    }
}
