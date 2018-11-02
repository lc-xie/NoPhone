package com.stephen.nophone.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.stephen.nophone.tool.Data;
import com.stephen.nophone.LockScreen;
import com.stephen.nophone.R;
import com.stephen.nophone.tool.TimeTool;

/**
 * 全局的service，一方面用于前台显示使用时间（Notification）
 * 一方面用于判断是否进入了睡眠时间
 */

public class MyService extends Service {

    private static final String TAG = MyService.class.getSimpleName();

    // 前台通知
    private Notification notification;
    private NotificationManager notificationManager;

    // 接收时间更新的通知
    private ServiceBR serviceBR;

    // 一个计时器，防止解锁之后多次开启线程锁屏
    private boolean allowLock = true;

    public MyService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Data.TIME_REFRESH);
        serviceBR = new ServiceBR();
        registerReceiver(serviceBR, intentFilter);
        notificationManager = (NotificationManager) getSystemService(android.content.Context.NOTIFICATION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notification_layout);
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContent(remoteViews)
                .setSmallIcon(R.drawable.phone)
                .setTicker("时间提示")
                .setOngoing(true)
                .setWhen(System.currentTimeMillis());
        notification = builder.build();
        startForeground(10101, notification);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
        unregisterReceiver(serviceBR);
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // 广播接收器
    private class ServiceBR extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Data.TIME_REFRESH)) {
                notification.contentView.setTextViewText(R.id.notif_text,
                        "今日已使用：" + intent.getStringExtra("time"));
                notificationManager.notify(10101, notification);
                // 判断是否进入睡眠时间
                if (allowLock && TimeTool.isSleepTime(getApplicationContext())) {
                    Log.d(TAG, "现在是睡眠时间，开启线程锁屏");
                    allowLock = false;
                    // 在睡眠时间和第二天5点之间，解锁后直接锁屏
                    Toast.makeText(getApplicationContext(), "现在是睡眠时间哦", Toast.LENGTH_LONG).show();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(10000);
                                LockScreen.lockPhone(getApplicationContext());
                                //防止线程执行完之前，再次收到时间改变的广播，导致又开启了一个线程锁屏
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            allowLock = true;
                        }
                    }).start();
                }
            } else if (intent.getAction().equals(Data.TIME_OUT)) {
                notification.contentView.setTextViewText(R.id.notif_text, "今日时间已用完");
                notificationManager.notify(10101, notification);
            }
        }
    }

}
