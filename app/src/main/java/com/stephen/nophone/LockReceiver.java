package com.stephen.nophone;

import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by stephen on 18-5-22.
 * 这里不能直接使用DeviceAdminReceiver，否则无法锁屏。为什么？？？？？？？？？？？？？
 */

public class LockReceiver extends DeviceAdminReceiver {

    @Override
    public void onEnabled(Context context, Intent intent) {
        super.onEnabled(context, intent);
        Toast.makeText(context,"已获取管理(锁屏)权限，祝您使用愉快！",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDisabled(Context context, Intent intent) {
        super.onDisabled(context, intent);
        Toast.makeText(context,"已取消管理权限！建议您开启管理(锁屏)权限～",Toast.LENGTH_SHORT).show();
    }
}
