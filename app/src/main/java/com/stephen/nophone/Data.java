package com.stephen.nophone;

/**
 * Created by stephen on 18-5-21.
 */

public class Data {
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
    public final static String TIME_OUT="time_out";
    /**
     * 时间是否用完标志
     * 为true表示还未用完
     */
    public static boolean ifTimeOut=true;

}
