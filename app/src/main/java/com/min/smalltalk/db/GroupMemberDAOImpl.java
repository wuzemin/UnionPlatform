package com.min.smalltalk.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.min.smalltalk.bean.GroupMember;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Min on 2016/12/15.
 */

public class GroupMemberDAOImpl {
    private DBOpenHelper dbOpenHelper;

    public GroupMemberDAOImpl(Context context) {
        this.dbOpenHelper = new DBOpenHelper(context, "talk.db", null, 2);
    }

    public void save(GroupMember groupMember) {// 插入记录
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();// 取得数据库操作
        db.execSQL("insert into t_groupMember (userId,userName,userPortraitUri) " +
                "values(?,?,?)",
                new Object[] { groupMember.getUserId(), groupMember.getUserName(), groupMember.getUserPortraitUri() });
        db.close();  //关闭数据库操作
    }

    public void delete(String id) {// 删除纪录
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        db.execSQL("delete from t_groupMember where userId=?", new Object[] { id.toString() });
        db.close();
    }

    public void update(GroupMember groupMember) {// 修改纪录
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        db.execSQL("update t_groupMember set diaplay=? where" + " id=?",
                new Object[] { groupMember.getUserName(), groupMember.getUserId() });
        db.close();
    }

    public GroupMember find(String id) {// 根据ID查找纪录
        GroupMember groupMember = null;
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        // 用游标Cursor接收从数据库检索到的数据
        Cursor cursor = db.rawQuery("select * from t_groupMember where id=?", new String[] { id.toString() });
        if (cursor.moveToFirst()) {// 依次取出数据
            groupMember = new GroupMember();
        }
        db.close();
        return groupMember;
    }

    public List<GroupMember> findAll(String userId) {// 查询所有记录
        List<GroupMember> lists = new ArrayList<GroupMember>();
        GroupMember groupMember = null;
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        // Cursor cursor=db.rawQuery("select * from t_users limit ?,?", new
        // String[]{offset.toString(),maxLength.toString()});
        // //这里支持类型MYSQL的limit分页操作

        Cursor cursor = db.rawQuery("select * from t_groupMember where userId=?", new String[] {userId.toString()});
        while (cursor.moveToNext()) {
            groupMember = new GroupMember();
            groupMember.setUserId(cursor.getString(cursor.getColumnIndex("userId")));
            groupMember.setUserName(cursor.getString(cursor.getColumnIndex("userName")));
            groupMember.setUserPortraitUri(cursor.getString(cursor.getColumnIndex("userPortraitUri")));
            lists.add(groupMember);
        }
        db.close();
        return lists;
    }

    public long getCount() {//统计所有记录数
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select count(*) from t_groupMember ", null);
        cursor.moveToFirst();
        db.close();
        return cursor.getLong(0);
    }
}
