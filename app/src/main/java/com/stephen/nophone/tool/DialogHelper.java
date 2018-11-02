package com.stephen.nophone.tool;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Created by stephen on 18-5-30.
 */

public class DialogHelper {

    public static void showDialog(String manufacture, Context context){
        String temp="";
        if (manufacture.equals("Meizu"))temp="魅族";
        if (manufacture.equals("xiaomi"))temp="小米";

        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setTitle("重要提示");
        builder.setMessage("亲爱的"+temp+"用户！\n\u3000\u3000欢迎使用NoPhone APP，为了提升您的体验，" +
                "需要您为本APP开启一项锁屏权限，请您放心，开启此权限不会对您的个人信息以及手机使用造成任何影响。" +
                "\n\u3000\u3000开启方法：打开设置--应用程序管理--找到本APP--授予锁屏权限" +
                "\n\u3000\u3000祝您使用愉快！");
        builder.setCancelable(false);
        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

}
