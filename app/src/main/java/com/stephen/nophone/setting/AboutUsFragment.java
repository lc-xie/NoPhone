package com.stephen.nophone.setting;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import com.stephen.nophone.R;

/**
 * Created by stephen on 17-9-27.
 * show the liscense of this app
 * Copied on 18-5-25
 */

public class AboutUsFragment extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater dialogInflater=getActivity().getLayoutInflater();
        View licensesView=dialogInflater.inflate(R.layout.fragment_about_us,null);

        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity())
                .setView(licensesView)
                .setTitle("About Us")
                .setNeutralButton("ok",null);

        return builder.create();
    }
}
