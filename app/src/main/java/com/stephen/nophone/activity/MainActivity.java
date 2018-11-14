package com.stephen.nophone.activity;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.stephen.nophone.MyApplication;
import com.stephen.nophone.tool.DialogHelper;
import com.stephen.nophone.LockReceiver;
import com.stephen.nophone.LockScreen;
import com.stephen.nophone.MyBroadcastReceiver;
import com.stephen.nophone.R;
import com.stephen.nophone.myview.AutoChronometer;
import com.stephen.nophone.service.MyService;
import com.stephen.nophone.tool.Data;
import com.stephen.nophone.tool.SPTool;

import java.util.Calendar;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    // UI组件
    private TextView noteText;//提示信息
    // 计时器有关
    private AutoChronometer chronometer;//计时组件
    // 震动服务
    private Vibrator vibrator;
    // 广播
    private MyBroadcastReceiver myBroadcastReceiver;
    // 开启服务
    private Intent serviceIntent;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLock();//首先获取锁屏权限
        setContentView(R.layout.activity_main);

        initData();
        init();
        // 开启计时
        startChronometer();
        // 开启前台服务
        serviceIntent = new Intent(this, MyService.class);
        startService(serviceIntent);
    }

    // sp初始化,创建数据库
    private void initData() {
        // 初始化SharedPreferences 实例
        SPTool spTool = SPTool.getInstance(this);
        // 判断日期是否改变（前一天关闭了app，第二天打开的情况）
        int todayData = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        if (spTool.readTodayDate() != todayData) {
            // 日期改变了，即第二天重新打开了app，计时归零
            spTool.writeTodayDate(todayData);
            spTool.writeRecordingTime(0);
            spTool.setIsTimeFinished(false);
        }
    }

    // 控件初始化，注册广播
    private void init() {
        noteText = findViewById(R.id.use_frequency_text);
        chronometer = findViewById(R.id.chronometer);
        //震动控件初始化
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        //注册广播
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        intentFilter.addAction(Intent.ACTION_USER_PRESENT);
        intentFilter.addAction(Intent.ACTION_DATE_CHANGED);
        intentFilter.addAction(Data.TIME_OUT);
        myBroadcastReceiver = new MyBroadcastReceiver(vibrator, getApplicationContext(), this);
        registerReceiver(myBroadcastReceiver, intentFilter);

        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    // 获取锁屏权限
    private void getLock() {
        LockScreen.devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        LockScreen.componentName = new ComponentName(this, LockReceiver.class);
        //判断是否有锁屏权限
        if (!LockScreen.devicePolicyManager.isAdminActive(LockScreen.componentName)) {
            Data.ifHaveAdmin = false;
            String manufacture = Build.MANUFACTURER;
            if (manufacture.contains("Meizu")) {
                DialogHelper.showDialog("Meizu", this);
            } else if (manufacture.equals("Xiaomi")) {
                DialogHelper.showDialog("xiaomi", this);
            } else {
                activeManager();
            }
        }
    }

    private void activeManager() {
        // 启动设备管理(隐式Intent) - 在AndroidManifest.xml中设定相应过滤器
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        //权限列表
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, LockScreen.componentName);
        //描述(additional explanation)
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "激活后才能使用锁屏功能哦亲^^");
        startActivityForResult(intent, 111);
    }

    /*@Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // 更新sp，防止出现应用非正常关闭而未记录时间（其实也不能完全避免，只能说是有一点点优化）
        long duration = System.currentTimeMillis() - chronometer.getBase();
        chronometer.writeCurrentUseTime(duration);
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 111 && resultCode == Activity.RESULT_OK) {
            Data.ifHaveAdmin = true;
        } else {
            Toast.makeText(MyApplication.getContext(), "获取权限失败！建议您去设置页面开启管理权限", Toast.LENGTH_SHORT).show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
        noteText.setText(String.format(getResources().getString(R.string.use_frequency_note_text), SPTool.getInstance(this).getFrequency()));
    }

    public void setFrequency(int f) {
        noteText.setText(String.format(getResources().getString(R.string.use_frequency_note_text), f));
    }

    // 开启计时器
    public void startChronometer() {
        chronometer.start();
    }

    // 停止计时器
    public void pauseChronometer() {
        chronometer.stop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_setting:
                Intent settingIntent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(settingIntent);
                break;
            case R.id.menu_history:
                Intent recordingIntent = new Intent(MainActivity.this, RecordingActivity.class);
//                startActivity(recordingIntent);
                Toast.makeText(MyApplication.getContext(), "开发中...", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myBroadcastReceiver);
        vibrator.cancel();
        stopService(serviceIntent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //按返回键回到桌面，而不是退出activity
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent home = new Intent(Intent.ACTION_MAIN);
            home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            home.addCategory(Intent.CATEGORY_HOME);
            startActivity(home);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
