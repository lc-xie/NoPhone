package com.stephen.nophone;

import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Chronometer;

/**
 * Created by stephen on 18-5-21.
 * 重写Chronometer，自动添加OnChronometerTickListener
 * 但是这里当控件不可见时，后台计时停止
 */

public class MyChronometer extends Chronometer {

    private Context context;

    private final static String TIME_OUT="time_out";
    private final static String TAG="MyChronometer";

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        //失去焦点时，会设置visible为false，并调用updateRunning方法，导致计数线程停止

    }

    public MyChronometer(Context context) {
        super(context);
        this.context= context;
        setOnChronometerTickListener(chronometerTickListener);
    }

    public MyChronometer(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context= context;
        setOnChronometerTickListener(chronometerTickListener);
    }

    public MyChronometer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context= context;
        setOnChronometerTickListener(chronometerTickListener);
    }

    public MyChronometer(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context= context;
        setOnChronometerTickListener(chronometerTickListener);
    }

    private OnChronometerTickListener chronometerTickListener=new OnChronometerTickListener() {
        @Override
        public void onChronometerTick(Chronometer chronometer) {
            long time= SystemClock.elapsedRealtime()-chronometer.getBase();
            Log.d(TAG,"chronometer changed!      "+time);
            Intent intent=new Intent();
            intent.setAction(TIME_OUT);
            if (time>=20*1000){
                Log.d(TAG,"send broadcast:"+time);
                context.sendBroadcast(intent);
            }
        }
    };

    @Override
    public void setOnChronometerTickListener(OnChronometerTickListener listener) {
        super.setOnChronometerTickListener(listener);
    }
}
