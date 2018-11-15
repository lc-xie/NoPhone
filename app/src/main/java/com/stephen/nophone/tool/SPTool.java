package com.stephen.nophone.tool;

import android.content.Context;
import android.content.SharedPreferences;

import com.stephen.nophone.MyApplication;
import com.stephen.nophone.R;

import java.util.Calendar;

import static android.content.Context.MODE_PRIVATE;
import static com.stephen.nophone.tool.Data.*;

/**
 * Created by stephen on 18-10-29.
 * sharedPreferences 工具类
 * 单例模式---全局维护一个sp
 * sp存储数据如下
 *      int 每日使用时长(30-180)
 *      int 睡眠时间（分小时和分钟存储）(0-23,0-59)
 *      long 今日已经使用的时间
 *      int 今天的日期(1-31)
 *      boolean 今日时间是否已经使用完
 */

public class SPTool {

    private static volatile SPTool tool;
    private static SharedPreferences sharedPreferences;

    private SPTool(Context context) {
        sharedPreferences = context.getApplicationContext().getSharedPreferences(
                Data.SP_FILE_NAME, MODE_PRIVATE);
        initSP();
    }

    public static SPTool getInstance(Context context) {
        if (tool == null) {
            synchronized (SPTool.class) {
                if (tool == null) {
                    tool = new SPTool(context);
                }
            }
        }
        return tool;
    }

    private void initSP() {
        if (sharedPreferences.getInt(USE_TIME_KEY, 0) <= 0) {
            //第一次使用，给sp赋默认值
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(USE_TIME_KEY, DEFAULT_USE_TIME);
            editor.putInt(USE_FREQUENCY_KEY, DEFAULT_USE_FREQUENCY);
            editor.putInt(SLEEP_TIME_HOUR_KEY, DEFAULT_SLEEP_TIME_HOUR);
            editor.putInt(SLEEP_TIME_MINUTE_KEY, DEFAULT_SLEEP_TIME_MINUTE);
            editor.putLong(RECORD_TIME_KEY,0);
            editor.putInt(TODAY_DATE_KEY, Calendar.DAY_OF_MONTH);
            editor.commit();
        }
    }

    public void registerOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener);
    }

    //设置每天使用时长
    public void setUseTime(int time) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(USE_TIME_KEY, time);
        editor.apply();
    }

    // 获取每天的使用时长
    public int getUseTime() {
        return sharedPreferences.getInt(USE_TIME_KEY, 0);
    }

    // 增加一次使用次数
    public void addFrequency() {
        int cur = sharedPreferences.getInt(USE_FREQUENCY_KEY, 1);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(USE_FREQUENCY_KEY, cur + 1);
        editor.apply();
    }

    // 使用次数置零
    public void cleanFrequency() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(USE_FREQUENCY_KEY, 0);
        editor.apply();
    }

    // 获取当前的使用次数
    public int getFrequency() {
        return sharedPreferences.getInt(USE_FREQUENCY_KEY, 1);
    }

    //设置早睡时间
    public void setSleepTime(int h, int m) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(SLEEP_TIME_HOUR_KEY, h);
        editor.putInt(SLEEP_TIME_MINUTE_KEY, m);
        editor.apply();
    }

    //获取设置的早睡时间-时
    public int getSleepTimeHour() {
        return sharedPreferences.getInt(SLEEP_TIME_HOUR_KEY, 0);
    }

    //获取设置的早睡时间-分
    public int getSleepTimeMinute() {
        return sharedPreferences.getInt(SLEEP_TIME_MINUTE_KEY, 0);
    }

    // 获取今日时间是否已经使用完的标志
    public boolean getIsTimeFinished() {
        return sharedPreferences.getBoolean(IS_TIME_FINSHED_KEY, false);
    }

    // 设置今日时间是否已经使用完
    public void setIsTimeFinished(boolean b) {
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putBoolean(IS_TIME_FINSHED_KEY,b);
        editor.apply();
    }

    //写入今天的recordingTime
    public void writeRecordingTime(long recordingTime){
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putLong(RECORD_TIME_KEY,recordingTime);
        editor.apply();
    }
    //写入今天的日期
    public void writeTodayDate(int date){
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putInt(TODAY_DATE_KEY,date);
        editor.apply();
    }
    //读取今天的recordingTime
    public long readRecordingTime(){
        return sharedPreferences.getLong(RECORD_TIME_KEY,0);
    }
    //读取当前保存的日期
    public int readTodayDate(){
        return sharedPreferences.getInt(TODAY_DATE_KEY,0);
    }

}
