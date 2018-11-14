package com.stephen.nophone.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.stephen.nophone.tool.FileTool;

/**
 * Created by stephen on 18-6-2.
 * 数据库类
 * 创建和更新数据库
 * 记录使用时间段、不同app的使用情况
 */

public class MySQLiteOpenHelper extends SQLiteOpenHelper {

    private static final String TAG = "MySQLiteOpenHelper";
    private static final String DATABASE_NAME = FileTool.APP_STORAGE_DIR + "/database/NoPhoneDB.db"; // 数据库名(加路径)
    static final String TABLE_DETAIL_RECORD = "DetailRecord"; // 表名
    static final String KEY_ID = "id";
    static final String KEY_DATA = "data";
    static final String KEY_START_TIME = "startTime";
    static final String KEY_END_TIME = "endTime";
    static final String KEY_CONSUME = "consume";

    private final static String CREATE_TABLE_DETAIL_RECORD = "create table " + TABLE_DETAIL_RECORD +
            "(id integer primary key autoincrement," +
            "data integer," +// 20181106
            "startTime text," +// HH:mm:ss
            "endTime text," +// HH:mm:ss
            "consume long)";// 毫秒

    /**
     * 构造函数
     *
     * @param context 上下文
     * @param version 版本号，用于更新
     */
    MySQLiteOpenHelper(Context context, int version) {
        super(context, DATABASE_NAME, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_DETAIL_RECORD);
        Log.d(TAG, "create book table");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "update book table");
    }
}
