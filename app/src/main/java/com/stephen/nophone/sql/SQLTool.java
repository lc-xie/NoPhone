package com.stephen.nophone.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by stephen on 18-11-6.
 * 数据库工具类
 */

public class SQLTool {
    private static final String TAG = SQLTool.class.getSimpleName();

    private MySQLiteOpenHelper sqLiteOpenHelper;

    private static volatile SQLTool tool;

    private SQLTool(Context context){
        sqLiteOpenHelper = new MySQLiteOpenHelper(context, 2);
    }

    public static SQLTool getSQLTool(Context context) {
        if (tool == null) {
            synchronized(SQLTool.class) {
                if (tool == null) {
                    tool = new SQLTool(context);
                }
            }
        }
        return tool;
    }

    // 插入一条数据
    public void insertRecord(DetailRecord record) {
        SQLiteDatabase write = sqLiteOpenHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MySQLiteOpenHelper.KEY_DATA, record.getData());
        values.put(MySQLiteOpenHelper.KEY_START_TIME, record.getStartTime());
        values.put(MySQLiteOpenHelper.KEY_END_TIME, record.getEndTime());
        values.put(MySQLiteOpenHelper.KEY_CONSUME, record.getConsume());
        write.insert(MySQLiteOpenHelper.TABLE_DETAIL_RECORD, null, values);
        values.clear();
    }

    public void logAllData() {
        SQLiteDatabase read = sqLiteOpenHelper.getReadableDatabase();
        Cursor cursor = read.query(MySQLiteOpenHelper.TABLE_DETAIL_RECORD, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex(MySQLiteOpenHelper.KEY_ID));
                int data = cursor.getInt(cursor.getColumnIndex(MySQLiteOpenHelper.KEY_DATA));
                String s = cursor.getString(cursor.getColumnIndex(MySQLiteOpenHelper.KEY_START_TIME));
                String e = cursor.getString(cursor.getColumnIndex(MySQLiteOpenHelper.KEY_END_TIME));
                long consume = cursor.getLong(cursor.getColumnIndex(MySQLiteOpenHelper.KEY_CONSUME));
                Log.e(TAG, id + "-" + data + "-" + s + "-" + e + "-" + consume);
                cursor.moveToNext();
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

}
