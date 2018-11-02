package com.stephen.nophone.myview;

import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Chronometer;

import com.stephen.nophone.tool.Data;
import com.stephen.nophone.tool.SPTool;

/**
 * Created by stephen on 18-5-21.
 * 重写Chronometer，自动添加OnChronometerTickListener
 * 注意：activity stop后，chronometer会停止计时，即listener无响应
 */

public class MyChronometer extends Chronometer {
    private boolean isCounting;//是否正在计时
    private Context context;

    private final static String TAG = MyChronometer.class.getSimpleName();

    public MyChronometer(Context context) {
        super(context);
        this.context = context;
        setOnChronometerTickListener(chronometerTickListener);
    }

    public MyChronometer(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        setOnChronometerTickListener(chronometerTickListener);
    }

    public MyChronometer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        setOnChronometerTickListener(chronometerTickListener);
    }

    private OnChronometerTickListener chronometerTickListener = new OnChronometerTickListener() {
        @Override
        public void onChronometerTick(Chronometer chronometer) {
            long time = SystemClock.elapsedRealtime() - chronometer.getBase();
            //Log.d(TAG,"chronometer changed!      "+time/1000);
            //更新notification上的时间
            Log.d(TAG, "OnChronometerTickListener");
            Intent notificationIntent = new Intent(Data.TIME_REFRESH);
            notificationIntent.putExtra("time", getText());
            context.sendBroadcast(notificationIntent);
            //判断是否到达可使用时间
            if (time >= SPTool.getInstance(context).getUseTime() * 60 * 1000) {
                Intent intent = new Intent(Data.TIME_OUT);
                Log.d(TAG, "send broadcast:" + time / 1000);
                context.sendBroadcast(intent);
                SPTool.getInstance(context).setIsTimeFinished(true);
                stop();
            }
        }
    };

    @Override
    public void start() {
        //super.start();
        Log.d(TAG, "计时器开始计时");
        isCounting = true;
    }

    @Override
    public void stop() {
        Log.d(TAG, "计时器停止计时");
        super.stop();
        isCounting = false;
        // 计时器stop时，自动将最新花费的时间写入sp中
        long duration = SystemClock.elapsedRealtime() - this.getBase();
        SPTool.getInstance(context).writeRecordingTime(duration);
    }

    @Override
    public void setOnChronometerTickListener(OnChronometerTickListener listener) {
        super.setOnChronometerTickListener(listener);
    }

    public boolean getIsCounting() {
        return this.isCounting;
    }
}
