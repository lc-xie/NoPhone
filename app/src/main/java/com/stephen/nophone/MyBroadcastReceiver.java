package com.stephen.nophone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;


import com.stephen.nophone.activity.MainActivity;
import com.stephen.nophone.tool.Data;
import com.stephen.nophone.tool.FileTool;
import com.stephen.nophone.tool.SPTool;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static com.stephen.nophone.LockScreen.lockPhone;

/**
 * Created by stephen on 18-5-20
 * 广播接收器.
 */

public class MyBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = MyBroadcastReceiver.class.getSimpleName();

    private Vibrator vibrator;
    private Context context;
    private PowerManager powerManager;
    private WeakReference<MainActivity> activity;
    private MyHandler handler = new MyHandler(this);

    public MyBroadcastReceiver(Vibrator vibrator, Context context, MainActivity mainActivity) {
        super();
        this.vibrator = vibrator;
        this.context = context;
        activity = new WeakReference<>(mainActivity);
        powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
    }

    //用于锁屏的handler，使用内部静态类，避免内存泄漏
    private static class MyHandler extends Handler {

        private final WeakReference<MyBroadcastReceiver> brWeakReference;

        private MyHandler(MyBroadcastReceiver br) {
            super();
            brWeakReference = new WeakReference<>(br);
        }

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                //10s锁屏，当屏幕亮着时才会触发
                if (brWeakReference.get().powerManager.isScreenOn()) {
                    lockPhone(brWeakReference.get().context);
                    brWeakReference.get().vibrator.vibrate(400);
                }
            }
        }
    }

    //等待线程
    class TimeThread extends Thread {

        int waitingTime;

        private TimeThread(int time) {
            super();
            this.waitingTime = time;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(waitingTime * 1000);
                Message message = new Message();
                message.what = 1;
                handler.sendMessage(message);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        switch (intent.getAction()) {
            case Data.TIME_OUT:
                timeOutAction();
                break;
            case Intent.ACTION_USER_PRESENT:
                screenOnAction();
                break;
            case Intent.ACTION_SCREEN_OFF:
                screenOffAction();
                break;
            case Intent.ACTION_DATE_CHANGED:
                dateChangedAction();
                break;
        }
    }

    // 今日时间用完，停止计时，如果屏幕是亮着的（一般是亮的），锁屏
    private void timeOutAction() {
        Log.d(Data.MBR_TAG, "今日可用时间已使用完");
        activity.get().pauseChronometer();
        // 屏幕是亮着的，在桌面时直接打开应用
        if (powerManager.isScreenOn()) {
            vibrator.vibrate(new long[]{100, 300, 100, 400}, -1);
            Toast.makeText(this.context, "今日时间已用完，10s后将锁屏！", Toast.LENGTH_LONG).show();
            Intent startActivity = new Intent(context, MainActivity.class);
            this.context.startActivity(startActivity);
            new TimeThread(10).start();
        }
    }

    // 屏幕解锁
    private void screenOnAction() {
        Log.d(TAG, "屏幕解锁!");
        // 增加使用次数
        SPTool.getInstance(context).addFrequency();
        activity.get().setFrequency(SPTool.getInstance(context).getFrequency());

        // 今日可用时间未使用完,可以继续使用手机，计时开始
        if (!SPTool.getInstance(context).getIsTimeFinished()) {
            activity.get().startChronometer();
            return;
        }

        // 时间用完之后，再次开锁，会自动打开NoPhone APP的mainActivity
        Intent startActivity = new Intent(context, MainActivity.class);
        context.startActivity(startActivity);
        Toast.makeText(this.context, "今日时间已用完！", Toast.LENGTH_LONG).show();
        // 充值方式：手机锁屏1hour，获得5min的时间额度
        // TODO: 18-5-24 充值功能
        new TimeThread(10).start();
    }

    private void screenOffAction() {
        Log.d(TAG, "锁屏!");
        activity.get().pauseChronometer();
    }

    /**
     * 收到日期改变的广播
     * 停止计时，清零recordingTime，设置isTimeFinished，修改日期
     * 屏幕如果是亮的，再次启动计时
     */
    private void dateChangedAction() {
        activity.get().pauseChronometer();
        SPTool.getInstance(context).writeRecordingTime(0);
        SPTool.getInstance(context).cleanFrequency();
        SPTool.getInstance(context).setIsTimeFinished(false);
        SPTool.getInstance(context).writeTodayDate(Calendar.DAY_OF_MONTH);
        activity.get().setFrequency(SPTool.getInstance(context).getFrequency());
        if (powerManager.isScreenOn()) {
            activity.get().startChronometer();
        }
    }
}
