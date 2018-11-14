package com.stephen.nophone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.stephen.nophone.activity.MainActivity;

/**
 * 开机启动广播接收器
 * 开机自启动
 */
public class AutoStartReceiver extends BroadcastReceiver {

    private static final String TAG = AutoStartReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        switch (intent.getAction()) {
            case "android.intent.action.BOOT_COMPLETED":
                Intent startIntent=new Intent(context,MainActivity.class);
                context.startActivity(startIntent);
                break;
            /*case "android.intent.action.USER_PRESENT":
                // 系统解锁广播
                Log.e(TAG, "android.intent.action.USER_PRESENT");
                Intent startIntent1=new Intent(context,MainActivity.class);
                context.startActivity(startIntent1);
                break;
            case "android.intent.action.SCREEN_ON":
                // 系统解锁广播
                Log.e(TAG, "android.intent.action.SCREEN_ON");
                break;*/
        }
    }
}
