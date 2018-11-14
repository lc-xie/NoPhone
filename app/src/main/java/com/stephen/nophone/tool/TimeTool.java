package com.stephen.nophone.tool;

import android.content.Context;

import java.util.Calendar;

/**
 * Created by stephen on 18-10-30.
 */

public class TimeTool {

    // 判断现在是否在睡眠时间
    public static boolean isSleepTime(Context context) {
        //判断是否到了睡眠时间
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);//注意使用24h制
        int minute = calendar.get(Calendar.MINUTE);
        int h = SPTool.getInstance(context).getSleepTimeHour();
        int m = SPTool.getInstance(context).getSleepTimeMinute();
        if (h <= 24 && h > 5) {
            return (hour < 5 && hour >= 0) || hour > h || (h == hour && minute > m);
        } else {
            //设置的睡眠时间在凌晨24点到5点之间
            return hour > h && hour <= 5;
        }
    }

    //判断现在是否可以更改睡眠时间
    public static boolean allowChangeSleepTime(Context context) {
        //判断是否到了睡眠时间
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);//注意使用24h制
        int minute = calendar.get(Calendar.MINUTE);
        int h = SPTool.getInstance(context).getSleepTimeHour();
        int m = SPTool.getInstance(context).getSleepTimeMinute();
        if (h <= 24 && h > 5) {
            return (hour < 5 && hour >= 0) || hour > h || (h == hour && minute > m);
        } else {
            //设置的睡眠时间在凌晨24点到5点之间
            return hour > h && hour <= 5;
        }
    }

    //将毫秒转换成hh mm ss
    public static String formatTime(long m) {
        int ss =(int) (m / 1000);
        if (ss < 60) return String.valueOf(ss) + "s";
        int mm = ss / 60;
        ss %= 60;
        if (mm < 60) return mm + "m " + ss + "s";
        int hh = mm / 60;
        mm %=  60;
        return hh + "h " + mm + "m " + ss + "s";
    }

}
