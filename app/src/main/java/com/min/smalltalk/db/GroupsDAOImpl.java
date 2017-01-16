package com.min.smalltalk.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.min.smalltalk.bean.Groups;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Min on 2016/12/15.
 */

public class GroupsDAOImpl {
    private DBOpenHelper dbOpenHelper;

    public GroupsDAOImpl(Context context) {
        this.dbOpenHelper = new DBOpenHelper(context, "talk.db", null, 2);
    }

    public void save(Groups groups) {// 插入记录
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();// 取得数据库操作
        db.execSQL("insert into t_groupInfo ( userId,groupId,groupName,groupPortraitUri,role) values(?,?,?,?,?)",
                new Object[] { groups.getUserId(),groups.getGroupId(), groups.getGroupName(), groups.getGroupPortraitUri(), groups.getRole() });
        db.close();  //关闭数据库操作
    }

    public void delete(String id) {// 删除纪录
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        db.execSQL("delete from t_groupInfo where userId=?", new Object[] { id.toString() });
        db.close();
    }
    public void deleteOne(String id) {// 删除纪录
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        db.execSQL("delete from t_groupInfo where groupId=?", new Object[] { id.toString() });
        db.close();
    }

    public void update(String groupName,String groupId) {// 修改群名称
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        db.execSQL("update t_groupInfo set groupName=? where" + " groupId=?",
                new Object[] { groupName, groupId });
        db.close();
    }

    public void updatePic(String file, String getGroupId) {// 修改图片
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        db.execSQL("update t_groupInfo set groupPortraitUri = ? where" + " groupId=?",
                new Object[] { file, getGroupId });
        db.close();
    }

    public Groups find(String id) {// 根据ID查找纪录
        Groups groups = null;
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        // 用游标Cursor接收从数据库检索到的数据
        Cursor cursor = db.rawQuery("select * from t_groupInfo where groupId=?", new String[] { id.toString() });
        if (cursor.moveToFirst()) {// 依次取出数据
            groups = new Groups();
            groups.setGroupId(cursor.getString(cursor.getColumnIndex("groupId")));
            groups.setGroupName(cursor.getString(cursor.getColumnIndex("groupName")));
            groups.setGroupPortraitUri(cursor.getString(cursor.getColumnIndex("groupPortraitUri")));
            groups.setRole(cursor.getString(cursor.getColumnIndex("role")));

        }
        db.close();
        return groups;
    }

    public List<Groups> findAll(String id) {// 查询所有记录
        List<Groups> lists = new ArrayList<Groups>();
        Groups groups = null;
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        // Cursor cursor=db.rawQuery("select * from t_users limit ?,?", new
        // String[]{offset.toString(),maxLength.toString()});
        // //这里支持类型MYSQL的limit分页操作

        Cursor cursor = db.rawQuery("select * from t_groupInfo where userId=? ", new String[] { id.toString()});
        while (cursor.moveToNext()) {
            groups = new Groups();
            groups.setGroupId(cursor.getString(cursor.getColumnIndex("groupId")));
            groups.setGroupName(cursor.getString(cursor.getColumnIndex("groupName")));
            groups.setGroupPortraitUri(cursor.getString(cursor.getColumnIndex("groupPortraitUri")));
            groups.setRole(cursor.getString(cursor.getColumnIndex("role")));
            lists.add(groups);
        }
        db.close();
        return lists;
    }

    public long getCount() {//统计所有记录数
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select count(*) from t_groupInfo ", null);
        cursor.moveToFirst();
        db.close();
        return cursor.getLong(0);
    }
}
