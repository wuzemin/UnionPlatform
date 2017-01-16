package com.min.smalltalk.db;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Min on 2016/12/15.
 */

public class DBOpenHelper  extends SQLiteOpenHelper {
    /**
     *
     * @param context 上下文
     * @param name 数据库名
     * @param factory 可选的数据库游标工厂类，当查询(query)被提交时，该对象会被调用来实例化一个游标。默认为null。
     * @param version 数据库版本号
     */
    public DBOpenHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DBOpenHelper(Context context, String name, CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    // 覆写onCreate方法，当数据库创建时就用SQL命令创建一个表
    @Override
    public void onCreate(SQLiteDatabase db) {
        // 创建一个t_users表，id主键，自动增长，字符类型的username和pass;
        //群信息
        db.execSQL("create table if not exists t_groupInfo(id integer primary key autoincrement," +
                "userId varchar(200), groupId varchar(200),groupName varchar(200),groupPortraitUri varchar(200),role varchar(200) )");
        //群成员
        db.execSQL("create table if not exists t_groupMember(id integer primary key autoincrement," +
                "groupId varchar(200),groupName varchar(200),groupPortraitUri varchar(200)," +
                "userId varchar(200),userName varchar(200), userPortraitUri varchar(200), role varchar(200) )");
        //好友
        db.execSQL("create table if not exists t_friendInfo(id integer primary key autoincrement," +
                "myId varchar(200),userId varchar(200),userName varchar(200), userPortraitUri varchar(200)," +
                "displayName varchar(200),phone varchar(200),email varchar(200) )");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS t_groupInfo");
        db.execSQL("DROP TABLE IF EXISTS t_groupMember");
        db.execSQL("DROP TABLE IF EXISTS t_friendInfo");
        onCreate(db);

    }
}
