package com.stephen.nophone.myview;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.util.Log;

import com.stephen.nophone.R;
import com.stephen.nophone.sql.DetailRecord;
import com.stephen.nophone.sql.SQLTool;
import com.stephen.nophone.tool.Data;
import com.stephen.nophone.tool.FileTool;
import com.stephen.nophone.tool.SPTool;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by stephen on 18-11-1.
 * 自定义计时器
 */

public class AutoChronometer extends AppCompatTextView {

    private static final String TAG = "AutoChronometer";

    private long mBase;
    private Context context;

    private boolean isRunning;
    private OnChronometerTickListener mOnChronometerTickListener;

    /**
     * A callback that notifies when the chronometer has incremented on its own.
     */
    public interface OnChronometerTickListener {

        /**
         * Notification that the chronometer has changed.
         */
        void onChronometerTick(AutoChronometer AutoChronometer);

    }
    
    public AutoChronometer(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public AutoChronometer(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public AutoChronometer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    private void init() {
        setText(getResources().getString(R.string.default_time));
        mOnChronometerTickListener = new OnChronometerTickListener() {
            @Override
            public void onChronometerTick(AutoChronometer autoChronometer) {
                long time = System.currentTimeMillis() - autoChronometer.getBase();
                // 更新notification上的时间
                //Log.d(TAG, "OnChronometerTickListener");
                Intent notificationIntent = new Intent(Data.TIME_REFRESH);
                notificationIntent.putExtra("time", getText());
                context.sendBroadcast(notificationIntent);
                long currentTime = time + SPTool.getInstance(context).readRecordingTime();
                // 判断是否到达可使用时间
                if (currentTime >= SPTool.getInstance(context).getUseTime() * 60 * 1000) {
                    Intent intent = new Intent(Data.TIME_OUT);
                    Log.d(TAG, "send broadcast:" + time / 1000);
                    context.sendBroadcast(intent);
                    SPTool.getInstance(context).setIsTimeFinished(true);
                    stop();
                }
            }
        };
    }

    public void setBase(long base) {
        this.mBase = base;
    }

    public long getBase() {
        return mBase;
    }

    public void start() {
        //判断日期是否改变
        if (Calendar.getInstance().get(Calendar.DAY_OF_MONTH) != SPTool.getInstance(context).readTodayDate()) {
            // 第二天打开app，MyBroadcastReceiver未接收到日期变更的广播，导致记录未置零
            Intent dataChange = new Intent(Intent.ACTION_DATE_CHANGED);
            context.sendBroadcast(dataChange);
            return;// 在广播接收器中，收到日期改变的广播后，置零之后会再次启动计时器（如果屏幕是亮的）
        }
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss", Locale.CHINA);
        long curMillis = System.currentTimeMillis();
        Date curDate = new Date(curMillis);//获取当前时间
        Log.e(TAG, "start: " + curDate.toString());
        String time = formatter.format(curDate);
        FileTool.getFileTool().writeRecordFile1("[start - " + time + "]");
        setBase(curMillis);
        isRunning = true;
        post(runnable);
    }

    public void stop() {
        SimpleDateFormat dataFormat = new SimpleDateFormat("yyyyMMdd", Locale.CHINA);
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss", Locale.CHINA);
        long curMillis = System.currentTimeMillis();
        Date curDate = new Date(curMillis);//获取当前时间
        String data = dataFormat.format(curDate);
        long duration = curMillis - this.getBase();
        SQLTool sqlTool = SQLTool.getSQLTool(context);
        DetailRecord detailRecord = new DetailRecord(data, timeFormat.format(getBase()), timeFormat.format(curMillis), duration);
        sqlTool.insertRecord(detailRecord);
        isRunning = false;
        removeCallbacks(runnable);
        // 更新sp中的使用时间
        writeCurrentUseTime(duration);
    }

    public void writeCurrentUseTime(long duration) {
        // 计时器stop时，自动将最新花费的时间写入sp中
        long lastRecordingTime = SPTool.getInstance(context).readRecordingTime();
        SPTool.getInstance(context).writeRecordingTime(duration + lastRecordingTime);
    }

    void dispatchChronometerTick() {
        if (mOnChronometerTickListener != null) {
            mOnChronometerTickListener.onChronometerTick(this);
        }
    }

    private synchronized void updateText(long now) {
        long seconds = SPTool.getInstance(context).readRecordingTime() + now - mBase;
        if (seconds <= 0) {
            setText(getResources().getString(R.string.default_time));
            return;
        }
        seconds /= 1000;
        long minutes = seconds / 60;
        seconds %= 60;
        int hour = (int) minutes / 60;
        minutes %= 60;

        StringBuilder stringBuilder = new StringBuilder();
        if (seconds < 10) {
            stringBuilder.insert(0, "0" + seconds);
        } else stringBuilder.insert(0, seconds);
        if (minutes < 10) {
            stringBuilder.insert(0, "0" + minutes + ":");
        } else stringBuilder.insert(0, minutes + ":");
        if (hour != 0) {
            if (hour < 10) {
                stringBuilder.insert(0, "0" + hour + ":");
            } else stringBuilder.insert(0, hour + ":");
        }
        setText(stringBuilder.toString());
    }


    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (isRunning) {
                updateText(System.currentTimeMillis());
                dispatchChronometerTick();
                postDelayed(runnable, 1000);
            }
        }
    };

}
