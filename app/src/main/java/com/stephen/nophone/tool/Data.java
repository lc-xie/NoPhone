package com.stephen.nophone.tool;

import android.graphics.Bitmap;



/**
 * Created by stephen on 18-5-21.
 * 全局变量类
 */

public class Data {

    public static final int DEFAULT_SLEEP_TIME_HOUR = 23;
    public static final int DEFAULT_SLEEP_TIME_MINUTE = 0;
    public static final int DEFAULT_USE_FREQUENCY = 1;

    /**
     * 默认的使用时长:120min
     */
    public final static int DEFAULT_USE_TIME =120;
    /**
     * 用户自定义时长(单位为分种)，要求大于30min，小于4h(240min)
     * volatile限定,保证时间值修改的可见性
     */
    private volatile static int userSetTime;

    public static boolean ifUserTime=false;//用户是否自己设置了时间，默认未设置

    public static void setUserSetTime(int ust){
        if (ust<30){
            userSetTime=30;
        }else if (ust>240){
            userSetTime=240;
        }else {
            userSetTime = ust;
        }
    }

    /**
     * 获取每日可用时间长度
     * @return 单位：分钟
     */
    public static int getTimeLength(){
        if (ifUserTime) {
            return userSetTime;
        }
        return DEFAULT_USE_TIME;
    }

    public static String getTimeStr(){
        if (ifUserTime) {
            int h=userSetTime/60;
            int min=userSetTime%60;
            if (h>0){
                if (min<10){
                    return "0"+h+":0"+min+":00";
                }else {
                    return "0"+h+":"+min+":00";
                }
            }else {//10min-59min
                return "00:"+min+":00";
            }
        }
        return "01:00:00";
    }

    /**
     * TAG
     */
    public final static String MBR_TAG="MyBroadcastReceiver";
    public final static String MA_TAG="MainActivity";
    /**
     * 默认提示文字
     */
    public final static String NORMAL_TIME_TIP="你的时间不多了";

    /**
     * 时间用完时的提示文字
     */
    public final static String NO_TIME_TIP="您的余额已不足，请及时充值！\n(充值功能敬请期待...\n所以放下手机，滚去学习吧)";

    /**
     * 广播
     */
    public final static String TIME_OUT="time_out";//今日用时已到
    public final static String TIME_REFRESH="service_time_refresh";//更新通知处的时间
    //是否有管理员权限
    public static boolean ifHaveAdmin=false;
    /**
     * 时间是否用完标志
     * 为true表示还未用完
     */
//    public static boolean ifTimeOut=true;

    /**
     * SharedPreferences key
     */
    public static final String SP_FILE_NAME = "NoPhoneSharedPreferences";

    public final static String RECORD_TIME_KEY ="recordTime";
    public final static String TODAY_DATE_KEY ="todayDate";

    public static final String USE_TIME_KEY = "useTime";
    public static final String SLEEP_TIME_HOUR_KEY = "sleepTimeHour";
    public static final String SLEEP_TIME_MINUTE_KEY = "sleepTimeMinute";
    public static final String IS_TIME_FINSHED_KEY = "isTodayTimeFinshed";
    public static final String USE_FREQUENCY_KEY = "useFrequency";

    //自定义背景图片
    public static Bitmap bgBitmap;

}
