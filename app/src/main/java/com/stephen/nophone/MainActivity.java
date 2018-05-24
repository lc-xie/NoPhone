package com.stephen.nophone;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.PowerManager;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private ImageView setting;

    private static TextView noteText;//提示信息

    public static MyChronometer chronometer;//计时组件
    public static long recordingTime = 0;// 记录下来的总时间
    public static int todayDate;//今天的日期

    private Vibrator vibrator;

    private MyBroadcastReceiver myBroadcastReceiver;//广播接收器

    private final static int DELAY_TIME=60*1000;//每分钟run一次

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Calendar calendar = Calendar.getInstance();
        todayDate= calendar.get(Calendar.DAY_OF_MONTH);
        init();
        Log.d(Data.MA_TAG,"onCreate");
        startChronometer();
    }

    public void init(){
        //震动控件初始化
        vibrator=(Vibrator)getSystemService(VIBRATOR_SERVICE);
        //注册广播
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        intentFilter.addAction(Intent.ACTION_USER_PRESENT);
        intentFilter.addAction(Data.TIME_OUT);
        myBroadcastReceiver=new MyBroadcastReceiver(vibrator,getApplicationContext());
        registerReceiver(myBroadcastReceiver,intentFilter);
        //初始化控件
        setting=(ImageView) findViewById(R.id.setting);
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 18-5-20 进入设置页面
            }
        });
        noteText=(TextView)findViewById(R.id.note_text);
        chronometer=(MyChronometer) findViewById(R.id.chronometer);
    }

    /**
     * 设置noteText的内容
     */
    public static void changeNote(String s){
        noteText.setText(s);
    }

    /**
     * 开启计时器计时
     */
    public static void startChronometer(){
        chronometer.setBase(SystemClock.elapsedRealtime()-recordingTime);
        chronometer.start();
    }

    /**
     * 暂停计时
     */
    public static void pauseChronometer(){
        chronometer.stop();
        recordingTime=SystemClock.elapsedRealtime()-chronometer.getBase();
    }

    /**
     * 停止计时
     */
    public static void stopChronometer(){
        chronometer.stop();
        recordingTime=0;
    }

    private Handler countHander=new Handler();
    private Runnable countRunnable=new Runnable() {
        @Override
        public void run() {
            if (Data.ifTimeOut) {
                countHander.postDelayed(countRunnable, DELAY_TIME);

            }
        }
    };


    @Override
    protected void onStop() {
        super.onStop();
        Log.d(Data.MA_TAG,"onStop");
        if (!Data.ifTimeOut){
            Toast.makeText(getApplicationContext(),"您今日的余额已不足，请及时充值！",Toast.LENGTH_SHORT).show();
            return;
        }
        PowerManager powerManager = (PowerManager) this
                .getSystemService(Context.POWER_SERVICE);
        if (powerManager.isScreenOn()){
            countHander.postDelayed(countRunnable,DELAY_TIME);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(Data.MA_TAG,"onDestroy");
        unregisterReceiver(myBroadcastReceiver);
        vibrator.cancel();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //按返回键回到桌面，而不是退出activity
        if(keyCode==KeyEvent.KEYCODE_BACK){
            Intent home = new Intent(Intent.ACTION_MAIN);
            home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            home.addCategory(Intent.CATEGORY_HOME);
            startActivity(home);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
