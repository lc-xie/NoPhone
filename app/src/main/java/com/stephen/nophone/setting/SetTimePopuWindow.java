package com.stephen.nophone.setting;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;

import com.stephen.nophone.R;

/**
 * Created by stephen on 17-8-16.
 * Copied by stephen on 18-5-25.
 * 用户自定义每日可以使用的时长
 * 这里以PopupWindow的形式出现
 */

public class SetTimePopuWindow extends PopupWindow {
    private View popupView;
    private EditText editText;

    public SetTimePopuWindow(Context context, View.OnClickListener onClickListener) {
        super(context);
        LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        popupView=inflater.inflate(R.layout.popup_set_time,null);
        //初始化控件
        Button ok, cancel;
        ok =popupView.findViewById(R.id.popup_set_time_ok);
        cancel =popupView.findViewById(R.id.popup_set_time_cancel);
        editText=popupView.findViewById(R.id.popup_set_time_edit_text);
        editText.selectAll();
        ok.setOnClickListener(onClickListener);
        cancel.setOnClickListener(onClickListener);

        this.setContentView(popupView);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setFocusable(true);
        this.setOutsideTouchable(false);
        //this.setAnimationStyle(R.style.DialogAnimation);
        //实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0x000000);
        //设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
    }

    /**
     * 获取输入的时间长度
     * @return int
     */
    public int getEditText(){
        return Integer.parseInt(editText.getText().toString());
    }
}
