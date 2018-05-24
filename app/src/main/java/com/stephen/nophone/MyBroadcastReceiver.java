package com.stephen.nophone;

import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;

/**
 * Created by stephen on 18-5-20
 * 广播接收器.
 */

public class MyBroadcastReceiver extends BroadcastReceiver {

    private Vibrator vibrator;
    private Context context;
    private ComponentName componentName;


    public MyBroadcastReceiver(Vibrator vibrator,Context context) {
        super();
        this.vibrator=vibrator;
        this.context=context;
    }

    //锁屏
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what==1){
                vibrator.vibrate(400);
                lockPhone(context);
            }
        }
    };

    //10s
    class TimeThread extends Thread{
        @Override
        public void run() {
            try {
                Thread.sleep(10*1000);
                Message message=new Message();
                message.what=1;
                handler.sendMessage(message);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Data.TIME_OUT)){
            Log.d(Data.MBR_TAG,"time out");
            MainActivity.stopChronometer();
            Data.ifTimeOut=false;
            Intent startActivity=new Intent(context,MainActivity.class);
            context.startActivity(startActivity);
            MainActivity.changeNote(Data.NO_TIME_TIP);
            //vibrator.vibrate(new long[]{100,500,200,800},-1);
            new TimeThread().start();
        }
        if (intent.getAction()==Intent.ACTION_USER_PRESENT){
            //屏幕解锁，开启计时
            Log.d(Data.MBR_TAG,"screen on!");
            Calendar calendar=Calendar.getInstance();
            int day=calendar.get(Calendar.DAY_OF_MONTH);
            if (day!=MainActivity.todayDate){//第二天，清零
                Data.ifTimeOut=true;
                MainActivity.todayDate=day;
                MainActivity.recordingTime=0;
                MainActivity.startChronometer();
                MainActivity.chronometer.setFormat("%s");
                MainActivity.changeNote(Data.NORMAL_TIME_TIP);
                return;
            }
            if (Data.ifTimeOut) {
                MainActivity.startChronometer();
                return;
            }
            //时间用完之后，再次开锁，会自动打开NoPhone APP的mainActivity
            Intent startActivity=new Intent(context,MainActivity.class);
            context.startActivity(startActivity);
            //充值方式：手机锁屏1hour，获得5min的时间额度
            // TODO: 18-5-24 充值功能
            new TimeThread().start();
        }
        if (intent.getAction()==Intent.ACTION_SCREEN_OFF){
            //屏幕熄灭，停止计时，并将此次的时间加入到进入使用的总时间中
            //计时器已经停止
            if (!Data.ifTimeOut){
                return;
            }
            //计时器还在计时
            MainActivity.pauseChronometer();
        }
    }

    /**
     * 锁屏
     */
    private void lockPhone(Context context){
        DevicePolicyManager policyManager;
        policyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        componentName = new ComponentName(context, LockReceiver.class);
        if (policyManager.isAdminActive(componentName)) {//判断是否有权限(激活了设备管理器)
            policyManager.lockNow();// 直接锁屏
        }else{
            activeManager(context);//激活设备管理器获取权限
        }
    }
    //获取权限
    public void activeManager(Context context){
        //使用隐式意图调用系统方法来激活指定的设备管理器
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "一键锁屏");
        context.startActivity(intent);
    }

}
