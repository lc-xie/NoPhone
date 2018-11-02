package com.stephen.nophone;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.stephen.nophone.tool.Data;

/**
 * Created by stephen on 18-5-25.
 * 锁屏工具类
 */

public class LockScreen {
    //获取锁屏权限
    public static DevicePolicyManager devicePolicyManager;
    public static ComponentName componentName;

    public static void lockPhone(Context context){
        if (devicePolicyManager.isAdminActive(componentName)) {//判断是否有权限(激活了设备管理器)
            devicePolicyManager.lockNow();// 直接锁屏
            Log.d(Data.MBR_TAG,"lockPhone1");
        }else{
            Toast.makeText(context,"没有锁屏权限！",Toast.LENGTH_SHORT).show();
        }
    }
}
