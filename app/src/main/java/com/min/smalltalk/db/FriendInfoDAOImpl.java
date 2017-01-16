package com.min.smalltalk.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.min.smalltalk.bean.FriendInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Min on 2016/12/15.
 */

public class FriendInfoDAOImpl {
    private DBOpenHelper dbOpenHelper;
    private SQLiteDatabase db;

    public FriendInfoDAOImpl(Context context) {
        this.dbOpenHelper = new DBOpenHelper(context, "talk.db", null, 2);
    }

    public void save(FriendInfo friendInfo) {// 插入记录
        db = dbOpenHelper.getWritableDatabase();// 取得数据库操作
        db.execSQL("insert into t_friendInfo (myId,userId,userName,userPortraitUri,displayName,phone,email) values(?,?,?,?,?,?,?)",
                new Object[] {friendInfo.getMyId(), friendInfo.getUserId(),friendInfo.getName(),friendInfo.getPortraitUri(),
                        friendInfo.getDisplayName(),friendInfo.getPhone(),friendInfo.getEmail()});
        db.close();  //关闭数据库操作
    }

    public void delete(String id) {// 删除纪录
        db = dbOpenHelper.getWritableDatabase();
        db.execSQL("delete from t_friendInfo where myId=?", new Object[] { id.toString() });
        db.close();
    }
    public void deleteOne(String id) {// 删除一条纪录
        db = dbOpenHelper.getWritableDatabase();
        db.execSQL("delete from t_friendInfo where userId=?", new Object[] { id.toString() });
        db.close();
    }

    public void update(FriendInfo friendInfo) {// 修改纪录
        db = dbOpenHelper.getWritableDatabase();
        db.execSQL("update t_friendInfo set displayName=? where" + " id=?",
                new Object[] { friendInfo.getDisplayName(),friendInfo.getUserId() });
        db.close();
    }

    public FriendInfo find(String id) {// 根据ID查找纪录
        FriendInfo friendInfo = null;
        db = dbOpenHelper.getReadableDatabase();
        // 用游标Cursor接收从数据库检索到的数据
        Cursor cursor = db.rawQuery("select * from t_friendInfo where userId=?", new String[] { id.toString() });
        if (cursor.moveToFirst()) {// 依次取出数据
            friendInfo = new FriendInfo();
            friendInfo.setUserId(cursor.getString(cursor.getColumnIndex("userId")));
            friendInfo.setName(cursor.getString(cursor.getColumnIndex("userName")));
            friendInfo.setPortraitUri(cursor.getString(cursor.getColumnIndex("userPortraitUri")));
            friendInfo.setDisplayName(cursor.getString(cursor.getColumnIndex("displayName")));

        }
        db.close();
        return friendInfo;
    }

    public List<FriendInfo> findAll(String myId) {// 查询所有记录
        List<FriendInfo> lists = new ArrayList<FriendInfo>();
        FriendInfo friendInfo = null;
        db = dbOpenHelper.getReadableDatabase();
        // Cursor cursor=db.rawQuery("select * from t_users limit ?,?", new
        // String[]{offset.toString(),maxLength.toString()});
        // //这里支持类型MYSQL的limit分页操作

        Cursor cursor = db.rawQuery("select * from t_friendInfo where myId=? ", new String[]{myId.toString()});
        while (cursor.moveToNext()) {
            friendInfo = new FriendInfo();
            friendInfo.setMyId(cursor.getString(cursor.getColumnIndex("myId")));
            friendInfo.setUserId(cursor.getString(cursor.getColumnIndex("userId")));
            friendInfo.setName(cursor.getString(cursor.getColumnIndex("userName")));
            friendInfo.setPortraitUri(cursor.getString(cursor.getColumnIndex("userPortraitUri")));
            friendInfo.setDisplayName(cursor.getString(cursor.getColumnIndex("displayName")));
            friendInfo.setPhone(cursor.getString(cursor.getColumnIndex("phone")));
            friendInfo.setEmail(cursor.getString(cursor.getColumnIndex("email")));
            lists.add(friendInfo);
        }
        db.close();
        return lists;
    }

    public long getCount() {//统计所有记录数
        db = dbOpenHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select count(*) from t_friendInfo ", null);
        cursor.moveToFirst();
        db.close();
        return cursor.getLong(0);
    }
}
