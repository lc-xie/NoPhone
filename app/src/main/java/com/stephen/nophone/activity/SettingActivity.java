package com.stephen.nophone.activity;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.stephen.nophone.setting.AboutUsFragment;
import com.stephen.nophone.setting.RechargeFragment;
import com.stephen.nophone.setting.SetTimePopuWindow;
import com.stephen.nophone.sql.SQLTool;
import com.stephen.nophone.tool.Data;
import com.stephen.nophone.tool.DialogHelper;
import com.stephen.nophone.LockReceiver;
import com.stephen.nophone.LockScreen;
import com.stephen.nophone.R;
import com.stephen.nophone.tool.SPTool;
import com.stephen.nophone.tool.TimeTool;

import static com.stephen.nophone.tool.Data.*;

public class SettingActivity extends AppCompatActivity
        implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private SPTool spTool;

    private TextView showTimeText;
    private TextView showSleepTimeText;
    private Switch adminSwitch;

    private SetTimePopuWindow setTimePopuWindow;
    private PopupOnClick popupOnClick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        spTool = SPTool.getInstance(this);
        spTool.registerOnSharedPreferenceChangeListener(new SharedPreferencesChangeListener());
        init();
        showTimeText.setText(String.format(getResources().getString(R.string.show_use_time), spTool.getUseTime()));
        showSleepTimeText.setText(getSleepTimeStr());
        if (!LockScreen.devicePolicyManager.isAdminActive(LockScreen.componentName)) {
            adminSwitch.setChecked(false);
        } else adminSwitch.setChecked(true);
    }

    private class SharedPreferencesChangeListener implements SharedPreferences.OnSharedPreferenceChangeListener {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals(USE_TIME_KEY)) {
                showTimeText.setText(String.format(getResources().getString(R.string.show_use_time), spTool.getUseTime()));
            } else {
                showSleepTimeText.setText(getSleepTimeStr());
            }
        }
    }

    //控件初始化
    public void init() {
        SQLTool.getSQLTool(this)/*.logAllData()*/;
        ImageView setTimeImage, setSleepTimeImage, setBgImage, aboutUsImage, rechargeImage;
        setBgImage = findViewById(R.id.set_bg_go);
        setSleepTimeImage = findViewById(R.id.set_sleep_time_go);
        setTimeImage = findViewById(R.id.set_use_time_go);
        aboutUsImage = findViewById(R.id.frag_main_go_3);
        rechargeImage = findViewById(R.id.frag_main_go_4);
        showTimeText = findViewById(R.id.frag_main_show_time);
        showSleepTimeText = findViewById(R.id.set_sleep_time_show);
        adminSwitch = findViewById(R.id.frag_main_switch);
        adminSwitch.setChecked(Data.ifHaveAdmin);
        adminSwitch.setOnCheckedChangeListener(this);
        setTimeImage.setOnClickListener(this);
        setSleepTimeImage.setOnClickListener(this);
        setBgImage.setOnClickListener(this);
        aboutUsImage.setOnClickListener(this);
        rechargeImage.setOnClickListener(this);
        // 返回键
        LinearLayout backLinearLayout = findViewById(R.id.linear_back);
        backLinearLayout.setOnClickListener(this);

        /*manufactureText=findViewById(R.id.frag_main_admin_switch);
        manufactureText.setText(Build.MANUFACTURER);*/

        popupOnClick = new PopupOnClick();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {//开启了设备管理权限
            getLock();
        } else {//关闭设备管理权限
            cancelLock();
        }
    }

    /**
     * 获取锁屏权限
     */
    private void getLock() {
        LockScreen.devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        LockScreen.componentName = new ComponentName(this, LockReceiver.class);
        //判断是否有锁屏权限
        if (!LockScreen.devicePolicyManager.isAdminActive(LockScreen.componentName)) {
            String manufacture = Build.MANUFACTURER;
            if (manufacture.contains("Meizu")) {
                adminSwitch.setChecked(false);
                DialogHelper.showDialog("Meizu", this);
            } else if (manufacture.equals("Xiaomi")) {
                adminSwitch.setChecked(false);
                DialogHelper.showDialog("xiaomi", this);
            } else {
                activeManager();
            }
        }
    }

    /**
     * 取消管理权限
     */
    private void cancelLock() {
        Log.d("SettingActivity", "cancelLock!");
        LockScreen.devicePolicyManager.removeActiveAdmin(LockScreen.componentName);
    }

    private void activeManager() {
        // 启动设备管理(隐式Intent) - 在AndroidManifest.xml中设定相应过滤器
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        //权限列表
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, LockScreen.componentName);
        //描述(additional explanation)
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "激活后才能使用锁屏功能哦亲^^");
        startActivityForResult(intent, 112);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 112 && resultCode == Activity.RESULT_OK) {
            Data.ifHaveAdmin = true;
            adminSwitch.setChecked(true);
        } else {
            Data.ifHaveAdmin = false;
            adminSwitch.setChecked(false);
            Toast.makeText(this, "获取管理(锁屏)权限失败，建议您开启管理权限",
                    Toast.LENGTH_SHORT).show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.set_use_time_go:
                setUseTime();
                break;
            case R.id.set_sleep_time_go:
                setSleepTime();
                break;
            case R.id.set_bg_go:
                setBg();
                break;
            case R.id.frag_main_go_3:
                showAboutUs();
                break;
            case R.id.frag_main_go_4:
                showRecharge();
                break;
            case R.id.linear_back:
                finish();
                break;
        }
    }

    private void setUseTime() {
        setTimePopuWindow = new SetTimePopuWindow(getApplicationContext(), popupOnClick);
        setTimePopuWindow.showAtLocation(getWindow().getDecorView(), Gravity.CENTER, 0, 0);
        //设置背景模糊，需要使用getActivity().getWindow(),所以需要在adapter构造器中传入fragment实例
        setWindowAlpha(0.6f);
        //bottomPopupWindow消失后恢复fragment透明度
        //并判断是否点击了确定按钮
        //若点击了，就执行重命名操作
        setTimePopuWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                setWindowAlpha(1f);
            }
        });
    }

    private void setSleepTime() {
        //睡眠时间前30minute内不能更改，睡眠时间内不能更改
        if (TimeTool.allowChangeSleepTime(getApplicationContext())) {
            Toast.makeText(getApplicationContext(), "现在不能更改睡眠时间哦～", Toast.LENGTH_LONG).show();
            return;
        }
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                //睡眠时间必须在凌晨2点之前
                if (hourOfDay >= 2 && hourOfDay < 12) {
                    Toast.makeText(getApplicationContext(), "睡眠时间不能超过凌晨2点～", Toast.LENGTH_SHORT).show();
                    hourOfDay = 2;
                }
                spTool.setSleepTime(hourOfDay, minute);
            }
        }, 23, 0, true);
        timePickerDialog.show();
    }

    //获取当前设置的睡眠时间
    private String getSleepTimeStr() {
        int h = spTool.getSleepTimeHour();
        int m = spTool.getSleepTimeMinute();
        String hStr, mStr;
        if (h < 10) hStr = "0" + h;
        else hStr = String.valueOf(h);
        if (m < 10) mStr = "0" + m;
        else mStr = String.valueOf(m);
        return hStr + ":" + mStr;
    }

    //设置fragment透明度
    private void setWindowAlpha(float f) {
        WindowManager.LayoutParams lp = this.getWindow().getAttributes();
        lp.alpha = f; //0.0-1.0
        this.getWindow().setAttributes(lp);
    }

    private class PopupOnClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.popup_set_time_cancel:
                    setTimePopuWindow.dismiss();
                    break;
                case R.id.popup_set_time_ok:
                    //设置新的时长
                    spTool.setUseTime(Integer.parseInt(String.valueOf(setTimePopuWindow.getEditText())));
                    setTimePopuWindow.dismiss();
                    Toast.makeText(getApplicationContext(), "设置时长成功："
                            + spTool.getUseTime() + "分钟！", Toast.LENGTH_SHORT).show();
                    showTimeText.setText(String.format(getResources().getString(R.string.show_use_time), spTool.getUseTime()));
                    break;
            }
        }
    }

    //region 设置背景
    private void setBg() {
        Toast.makeText(getApplicationContext(), "开发中...敬请期待！", Toast.LENGTH_SHORT).show();
    }

    private void showAboutUs() {
        AboutUsFragment aboutUsFragment = new AboutUsFragment();
        aboutUsFragment.show(getSupportFragmentManager(), "tag_about_us");
    }

    private void showRecharge() {
        RechargeFragment rechargeFragment = new RechargeFragment();
        rechargeFragment.show(getSupportFragmentManager(), "tag_recharge");
    }
    //endregion
}
