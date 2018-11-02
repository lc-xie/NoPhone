package com.stephen.nophone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.stephen.nophone.activity.MainActivity;

/**
 * 开机启动广播接收器
 * 开机自启动
 */
public class AutoStartReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")){
            Intent startIntent=new Intent(context,MainActivity.class);
            context.startActivity(startIntent);
        }
    }
}
