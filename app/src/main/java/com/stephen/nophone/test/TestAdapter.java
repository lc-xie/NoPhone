package com.stephen.nophone.test;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.stephen.nophone.R;

import java.util.List;

/**
 * Created by stephen on 18-6-5.
 */

public class TestAdapter extends RecyclerView.Adapter<TestAdapter.ViewHolder> {
    private List<String> stringList;
    private static int count=0;

    public TestAdapter(List<String> list) {
        super();
        this.stringList=list;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            textView=itemView.findViewById(R.id.rv_text);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Log.d("TestAdapter","onBindViewHolder:"+position);
        holder.textView.setText(stringList.get(position));
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d("TestAdapter","onCreateViewHolder:"+(count++));
        ViewHolder holder;
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_test_layout,parent,false);
        holder=new ViewHolder(view);
        return holder;
    }

    @Override
    public int getItemCount() {
        return stringList.size();
    }
}
