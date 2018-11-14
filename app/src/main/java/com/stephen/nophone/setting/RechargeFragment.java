package com.stephen.nophone.setting;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import com.stephen.nophone.R;

/**
 * Created by stephen on 18-6-6.
 * 充值功能介绍页面
 */

public class RechargeFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater dialogInflater=getActivity().getLayoutInflater();
        View licensesView=dialogInflater.inflate(R.layout.fragment_recharge,null);
        String title = getArguments().getString("title");
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity())
                .setView(licensesView)
                .setTitle(title)
                .setNeutralButton("ok",null);

        return builder.create();
    }
}
