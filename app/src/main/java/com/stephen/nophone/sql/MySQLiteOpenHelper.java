package com.stephen.nophone.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by stephen on 18-6-2.
 * 数据库类
 * 创建和更新数据库
 * 记录使用时间段、不同app的使用情况
 */

public class MySQLiteOpenHelper extends SQLiteOpenHelper {

    private static final String TAG="MySQLiteOpenHelper";

    public final static String CREATE_TABLE_BOOK="create table(" +
            "id integer primary key autoincrement," +
            "bookName text," +
            "author text," +
            "pages integer," +
            "price real)";

    /**
     * 构造函数
     * @param context 上下文
     * @param name 数据库名称
     * @param factory 查询数据时，返回的自定义Cursor，一般为null
     * @param version 版本号，用于更新
     */
    public MySQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory
            factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_BOOK);
        Log.d(TAG,"create book table");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG,"update book table");
    }
}
