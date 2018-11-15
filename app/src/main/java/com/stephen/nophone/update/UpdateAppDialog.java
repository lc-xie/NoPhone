package com.stephen.nophone.update;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.stephen.nophone.MyApplication;
import com.stephen.nophone.R;

/**
 * Created by stephen on 18-11-15.
 * 升级app的弹窗
 */

public class UpdateAppDialog extends android.support.v4.app.DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater dialogInflater=getActivity().getLayoutInflater();
        View licensesView=dialogInflater.inflate(R.layout.fragment_update,null);

        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity())
                .setView(licensesView)
                .setTitle("APP更新")
                .setNeutralButton("更新",new UpdateListener())
                .setNegativeButton("稍后提醒", null);

        return builder.create();
    }

    private class UpdateListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            // 更新
            String url = "https://fir.im/nophone";
            openBrowser(MyApplication.getContext(), url);
        }
    }

    // 打开浏览器下载文件
    public static void openBrowser(Context context, String url){
        final Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        // 注意此处的判断intent.resolveActivity()可以返回显示该Intent的Activity对应的组件名
        // 官方解释 : Name of the component implementing an activity that can display
        // the intent
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            final ComponentName componentName = intent.resolveActivity(context.getPackageManager());
            // 打印Log   ComponentName到底是什么
            Log.d("componentName = ", componentName.getClassName());
            context.startActivity(Intent.createChooser(intent, "请选择浏览器"));
        } else {
            Toast.makeText(context.getApplicationContext(), "请下载浏览器", Toast.LENGTH_SHORT).show();
        }
    }

}
